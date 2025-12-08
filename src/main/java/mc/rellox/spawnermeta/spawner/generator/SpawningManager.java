package mc.rellox.spawnermeta.spawner.generator;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

import mc.rellox.spawnermeta.utility.adapter.BlockSnapshot;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.attribute.Attributable;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.EnderDragon.Phase;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Slime;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.SpawnerSpawnEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.util.Consumer;

import mc.rellox.spawnermeta.api.spawner.ISpawner;
import mc.rellox.spawnermeta.api.spawner.location.ISelector;
import mc.rellox.spawnermeta.configuration.Settings;
import mc.rellox.spawnermeta.hook.HookRegistry;
import mc.rellox.spawnermeta.spawner.type.SpawnerType;
import mc.rellox.spawnermeta.utility.Utility;
import mc.rellox.spawnermeta.utility.reflect.Reflect.RF;
import mc.rellox.spawnermeta.utility.reflect.type.Invoker;
import mc.rellox.spawnermeta.version.Version;
import mc.rellox.spawnermeta.version.Version.VersionType;

@SuppressWarnings("deprecation")
public final class SpawningManager {
	
	public static void initialize() {}
	
	public static List<Entity> spawn(ISpawner spawner, SpawnerType type, ISelector selector, int count) {
		List<Entity> entities;
		try {
			Settings s = Settings.settings;
			
			Block block = spawner.block();

			Invoker<Entity> invoker = spawner(block);
			
			boolean r = switch(type) {
			case SLIME, MAGMA_CUBE -> true;
			default -> false;
			};
			
			final Consumer<Entity> modifier, m = modifier(spawner);
			
			if(r == true) {
				modifier = e -> {
					m.accept(e);
					if(e instanceof Slime slime)
						slime.setSize(s.slime_size.getAsInt());
				};
			} else if(type == SpawnerType.EXPERIENCE_ORB) {
				modifier = e -> {
					m.accept(e);
					if(e instanceof ExperienceOrb orb) orb.setExperience(1);
				};
			} else modifier = m;

			Class<?> clazz = type.entity().getEntityClass();
			if(LivingEntity.class.isAssignableFrom(clazz) == true
					&& HookRegistry.WILD_STACKER.exists() == true
					&& HookRegistry.WILD_STACKER.stacking(type, spawner.center()) == true) {
				entities = HookRegistry.WILD_STACKER.combine(spawner, type, selector, count);
			} else {
				entities = new ArrayList<>(count);
				for(int i = 0; i < count; i++) {
					Location at = selector.get();
					Entity entity = invoker.invoke(at, clazz, modifier, s.spawn_reason);
					if(entity == null || entity.isValid() == false) continue;
					entities.add(entity);
					particle(at);
				}
			}
			return entities;
		} catch(Exception e) {
			RF.debug(e);
		}
		return new ArrayList<>();
	}
	
	public static Entity spawn(ISpawner spawner, SpawnerType type, ISelector selector) {
		try {
			Settings s = Settings.settings;
			
			Block block = spawner.block();
			
			Invoker<Entity> invoker = spawner(block);
			
			int r = switch(type) {
			case SLIME, MAGMA_CUBE -> s.slime_size.getAsInt();
			default -> 0;
			};
			
			final Consumer<Entity> modifier, m = modifier(spawner);
			
			if(r > 0) modifier = e -> {
				m.accept(e);
				if(e instanceof Slime slime) slime.setSize(r);
			};
			else modifier = m;

			Location at = selector.get();
			
			Class<?> clazz = type.entity().getEntityClass();
			
			Entity entity = invoker.invoke(at, clazz, modifier, s.spawn_reason);
			
			if(entity != null) particle(at);
			
			return entity;
		} catch(Exception e) {
			RF.debug(e);
		}
		return null;
	}

	private static Invoker<Entity> spawner(Block block) {
		return RF.order(block.getWorld(), "spawn",
				Location.class, Class.class, Version.version.high(VersionType.v_20_2)
				? java.util.function.Consumer.class : org.bukkit.util.Consumer.class, SpawnReason.class)
				.as(Entity.class);
	}

	private static final BiConsumer<ISpawner, Entity> modifier = (spawner, entity) -> {
		try {
			if(entity instanceof EnderDragon dragon) dragon.setPhase(Phase.CIRCLING);
			Settings s = Settings.settings;
			if(s.entity_movement == false
					&& entity instanceof Attributable le) {
				AttributeInstance at = le.getAttribute(Utility.attribute_speed);
				if(at != null) at.setBaseValue(0);
			}
			if(s.check_spawner_nerf == true && entity instanceof Mob mob) {
				Object w = RF.direct(mob.getWorld(), "getHandle");
				Object f = RF.fetch(w, "spigotConfig");
				if(RF.access(f, "nerfSpawnerMobs")
						.as(boolean.class)
						.get(false) == true) {
					Object a = RF.direct(mob, "getHandle");
					RF.access(a, "aware", boolean.class).set(false);
				}
			}
			if(s.spawn_babies == false && entity instanceof Ageable ageable) ageable.setAdult();
			if(s.spawn_with_equipment == false && entity instanceof LivingEntity a) {
				EntityEquipment e = a.getEquipment();
				e.clear();
			}
			Object o = RF.direct(entity, "getHandle");
			RF.access(o, "spawnedViaMobSpawner", boolean.class, false).set(true);
			RF.access(o, "spawnReason", SpawnReason.class, false).set(SpawnReason.SPAWNER);
			if(s.send_spawning_event == true) {
				SpawnerMetaSpawnEvent event = new SpawnerMetaSpawnEvent(entity, spawner);
				entity.getServer().getPluginManager().callEvent(event);
			}
			if(s.silent_entities.contains(spawner.getType()) == true)
				entity.setSilent(true);
		} catch (Exception e) {
			RF.debug(e);
		}
	};

	public static Consumer<Entity> modifier(ISpawner spawner) {
		return entity -> modifier.accept(spawner, entity);
	}

	public static void modify(Entity entity) {
		try {
			if(entity instanceof EnderDragon dragon) dragon.setPhase(Phase.CIRCLING);
			Settings s = Settings.settings;
			if(s.entity_movement == false
					&& entity instanceof Attributable le) {
				AttributeInstance at = le.getAttribute(Utility.attribute_speed);
				if(at != null) at.setBaseValue(0);
			}
			if(s.check_spawner_nerf == true && entity instanceof Mob mob) {
				Object w = RF.direct(mob.getWorld(), "getHandle");
				Object f = RF.fetch(w, "spigotConfig");
				if(RF.access(f, "nerfSpawnerMobs")
						.as(boolean.class)
						.get(false) == true) {
					Object a = RF.direct(mob, "getHandle");
					RF.access(a, "aware").as(boolean.class).set(false);
				}
			}
			Object o = RF.direct(entity, "getHandle");
			RF.access(o, "spawnedViaMobSpawner", boolean.class, false).set(true);
			RF.access(o, "spawnReason", SpawnReason.class, false).set(SpawnReason.SPAWNER);
		} catch (Exception e) {
			RF.debug(e);
		}
	}
	
	public static void particle(Location loc) {
		if(Settings.settings.spawning_particles == false) return;
		loc.getWorld().spawnParticle(Particle.CLOUD, loc.add(0, 0.25, 0), 5, 0.2, 0.2, 0.2, 0.1);
 	}
	
	public static void unlink(Block block) {
		if(HookRegistry.WILD_STACKER.exists() == false) return;
		HookRegistry.WILD_STACKER.unlink(block);
	}
	
	public static class SpawnerMetaSpawnEvent extends SpawnerSpawnEvent {
		
		public final ISpawner spawner;

		public SpawnerMetaSpawnEvent(Entity spawnee, ISpawner spawner) {
			super(spawnee, (CreatureSpawner) BlockSnapshot.getBlockState(spawner.block()));
			this.spawner = spawner;
		}
		
	}

}
