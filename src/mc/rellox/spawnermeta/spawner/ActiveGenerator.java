package mc.rellox.spawnermeta.spawner;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.Particle.DustOptions;
import org.bukkit.block.Block;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import mc.rellox.spawnermeta.api.events.SpawnerPostSpawnEvent;
import mc.rellox.spawnermeta.api.events.SpawnerPreSpawnEvent;
import mc.rellox.spawnermeta.api.hologram.IHologram;
import mc.rellox.spawnermeta.api.region.IBox;
import mc.rellox.spawnermeta.api.spawner.ICache;
import mc.rellox.spawnermeta.api.spawner.IGenerator;
import mc.rellox.spawnermeta.api.spawner.ISpawner;
import mc.rellox.spawnermeta.api.spawner.SpawnerWarning;
import mc.rellox.spawnermeta.api.spawner.location.IFinder;
import mc.rellox.spawnermeta.api.spawner.location.ISelector;
import mc.rellox.spawnermeta.api.spawner.requirement.ErrorCounter;
import mc.rellox.spawnermeta.api.view.IUpgrades;
import mc.rellox.spawnermeta.configuration.Settings;
import mc.rellox.spawnermeta.configuration.location.LocationRegistry;
import mc.rellox.spawnermeta.events.EventRegistry;
import mc.rellox.spawnermeta.hook.HookRegistry;
import mc.rellox.spawnermeta.spawner.generator.SpawningManager;
import mc.rellox.spawnermeta.spawner.requirement.ActiveFinder;
import mc.rellox.spawnermeta.spawner.type.SpawnerType;
import mc.rellox.spawnermeta.utility.DataManager;
import mc.rellox.spawnermeta.utility.Utility;
import mc.rellox.spawnermeta.utility.reflect.Reflect.RF;
import mc.rellox.spawnermeta.utility.reflect.type.Invoker;
import mc.rellox.spawnermeta.view.ActiveUpgrades;

public class ActiveGenerator implements IGenerator {
	
	private static final DustOptions dust_warn = new DustOptions(Color.MAROON, 1.5f);
	private static final DustOptions dust_owner = new DustOptions(Color.ORANGE, 1.5f);
	
	private final ISpawner spawner;
	private final IFinder finder;
	private final ICache cache;
	
	private IBox box = IBox.empty;
	
	private int delay, ticks;
	private boolean online, rotating;
	
	private final Set<SpawnerWarning> warnings;
	
	public IUpgrades upgrades;
	private final IHologram hologram;
	private IHologram warning;
	
	private int validation = 1;
	private int checking = 1;
	
	private boolean active = true;
	
	public ActiveGenerator(ISpawner spawner) {
		DataManager.validateEmpty(spawner.block());
		
		this.spawner = spawner;
		this.cache = ICache.of(spawner);
		cache.cache();
		this.finder = new ActiveFinder(this);
		this.warnings = EnumSet.noneOf(SpawnerWarning.class);
		
		DataManager.setNewSpawner(null, spawner.block(), false);
		
		update();
		this.hologram = Settings.settings.holograms_regular_enabled == true
				&& !(Settings.settings.holograms_regular_show_natural == false
				&& cache.natural() == true) ? IHologram.hologram(this) : null;
		if(hologram != null) hologram.update();
		refresh();
		if(Settings.settings.tick_until_zero == true) ticks = 0;
	}
	
	@Override
	public ICache cache() {
		return cache;
	}
	
	@Override
	public ISpawner spawner() {
		return spawner;
	}
	
	@Override
	public Block block() {
		return spawner.block();
	}
	
	@Override
	public World world() {
		return block().getWorld();
	}
	
	@Override
	public boolean active() {
		return active;
	}
	
	@Override
	public boolean present() {
		if(Settings.settings.check_present_enabled == false) return true;
		Block block = block();
		boolean loaded = block.getWorld()
				.isChunkLoaded(block.getX() >> 4, block.getZ() >> 4);
		if(loaded == false) return false;
		return block.getType() == Material.SPAWNER;
	}
	
	@Override
	public void remove(boolean fully) {
		active = false;
		clear();
		if(fully == true) {
			Block block = block();
			LocationRegistry.remove(block);
			block.setType(Material.AIR);
			HookRegistry.SUPERIOR_SKYBLOCK_2.breaking(block);
		}
	}
	
	@Override
	public void update() {
		if(active == false) return;
		cache.cache();
		
		retime();
		
		spawner.setDelay(ticks);
		finder.update();
		int r = cache.range();
		if(box.radius() != r) box = IBox.sphere(spawner.block(), r);

		boolean emptied = cache.type() == SpawnerType.EMPTY;
		if(online == false || cache.enabled() == false
				|| emptied == true) spawner.setRotating(rotating = false);
		else spawner.setRotating(rotating);
		check();
	}
	
	private void retime() {
		delay = cache.delay() / Settings.settings.ticking_interval;
		int next = delay;
		double offset = Settings.settings.delay_offset;
		if(offset > 0) {
			int o = (int) (delay * offset);
			if(o > 0) next += Utility.random(o << 1) - o;
		}
		if(ticks <= 0 || ticks > delay) ticks = next;
	}
	
	@Override
	public void rewrite() {
		if(active == false) return;
		if(hologram != null) hologram.rewrite();
		if(warning != null) warning.rewrite();
	}
	
	@Override
	public void refresh() {
		if(active == false) return;
		update();
		valid();
		rewrite();
		control();
		if(online == false) spawner.setRotating(rotating = false);
		else check();
	}
	
	@Override
	public void control() {
		Settings s = Settings.settings;
		
		UUID id;
		if(s.owned_if_online == false
				|| cache.natural() == true
				|| (id = spawner.getOwnerID()) == null
				|| s.owned_offline_ignore.contains(id) == true) {
			online = true;
			return;
		}
		
		Player owner = Bukkit.getPlayer(id);
		if(owner != null && owner.isOnline() == true) {
			online = true;
			return;
		}
		
		int time = s.owned_offline_time;
		if(time <= 0) online = false;
		else {
			OfflinePlayer op = Bukkit.getOfflinePlayer(id);
			long last = op.getLastPlayed();
			if(last == 0) {
				online = false;
				return;
			}
			long sub = System.currentTimeMillis() - last;
			sub /= 1000; // to seconds
			sub /= 60; // to minutes
			
			online = sub <= time;
		}
	}
	
	@Override
	public int ticks() {
		return ticks;
	}
	
	@Override
	public void ticks(int ticks) {
		this.ticks = Math.max(0, ticks);
	}

	@Override
	public void tick() {
		if(--validation < 0) validation = Settings.settings.validation_interval - 1;
		if(active == false) return;
		holograms();
		if(check() == false) {
			tick_untill_zero();
			return;
		}
		if(cache.type() == SpawnerType.EMPTY) return;
		if(online == false || validate() == false) {
			if(validation == 0) spawner.setDelay(delay);
			return;
		}
		if(--ticks <= 0) {
			spawn();
			update();
		}
	}

	private void tick_untill_zero() {
		if(Settings.settings.tick_until_zero == false || ticks <= 0) return;
		ticks--;
	}
	
	private void holograms() {
		if(validation != 0 || active == false) return;
		if(hologram != null) hologram.update();
		if(warning != null) warning.update();
	}
	
	private boolean check() {
		if(--checking < 0) checking = Settings.settings.checking_interval - 1;
		if(cache.enabled() == false || cache.type() == SpawnerType.EMPTY) return false;
		if(checking != 0) return rotating;
		if(box.any(spawner.world().getPlayers()) == true) {
			if(rotating == false) spawner.setRotating(rotating = true);
			return true;
		} else {
			if(rotating == true) spawner.setRotating(rotating = false);
		}
		return false;
	}

	@Override
	public boolean spawn() {
		Settings s = Settings.settings;
		if(s.spawning == false || active == false) return false;
		List<Location> list = finder.find();
		validation(finder.errors());
		if(warnings.isEmpty() == false) return false;
		
		int count = cache.stack() * cache.amount();
		if(count > s.safety_limit) count = s.safety_limit;
		
		int limit = s.nearby_limit;
		if(limit > 0) {
			int r_h = s.radius_horizontal, r_v = s.radius_vertical;
			int nearby = spawner.world().getNearbyEntities(spawner.center(), r_h, r_v, r_h,
					entity -> entity instanceof LivingEntity
					&& entity instanceof Player == false)
					.size();
			if(nearby >= limit) return false;
			if(s.nearby_reduce == true) {
				int left = limit - nearby;
				if(count > left) count = left;
			}
		}

		Block block = spawner.block();
		int chuck = s.chunk_entity_limit;
		if(chuck > 0) {
			long total = Stream.of(block.getChunk().getEntities())
					.filter(e -> e instanceof LivingEntity)
					.filter(e -> e instanceof Player == false)
					.count();
			if(total >= chuck) return false;
		}
		
		SpawnerPreSpawnEvent call = EventRegistry.call(new SpawnerPreSpawnEvent(this, count));
		if(call.cancelled() == true || count < 1) return false;
		
		count = call.count;
		
		if(call.bypass_checks == false && s.charges_enabled == true && cache.charges() <= 0) {
			boolean ignore = s.charges_ignore_natural == true && cache.natural() == true;
			if(ignore == true) warnings.remove(SpawnerWarning.CHARGES);
			else {
				warn(SpawnerWarning.CHARGES);
				return false;
			}
		}
		
		boolean clear = false;
		int spawned = -1;
		x: if(call.bypass_checks == false && s.spawnable_enabled == true) {
			int spawnable = cache.spawnable();
			if(spawnable >= 1_000_000_000) break x;
			if(spawnable <= 0) {
				clear = true;
				count = 0;
				break x;
			}
			if(spawnable < count) {
				count = spawnable;
				spawnable = 0;
			} else spawnable -= count;
			spawned = spawnable;
		}
		
		if(count <= 0) {
			block.getWorld().spawnParticle(Utility.particle_redstone, block.getLocation().add(0.5, 0.5, 0.5),
					25, 0.45, 0.45, 0.45, 0.075, new DustOptions(Color.MAROON, 2.5f));
			return false;
		}

		List<Entity> entities = SpawningManager.spawn(spawner, cache.type(), ISelector.of(list), count);
		
		if(entities.isEmpty() == true) return false;
		
		if(upgrades != null) upgrades.update();
		
		EventRegistry.call(new SpawnerPostSpawnEvent(this, entities));
		
		if(s.instant_kill_enabled == true) {
			if(s.instant_kill_drop_xp == true) {
				Entity killer = block.getWorld()
						.getNearbyEntities(block.getLocation(), 32, 32, 32)
						.stream()
						.filter(n -> n instanceof Player)
						.findAny()
						.orElse(null);
				entities.forEach(e -> {
					if(e instanceof LivingEntity living) kill(living, killer);
				});
			} else {
				entities.forEach(e -> {
					if(e instanceof LivingEntity living) kill(living, null);
				});
			}
		}

		if(call.bypass_checks == false && s.charges_enabled == true
				&& cache.charges() < 1_000_000_000) spawner.setCharges(cache.charges() - s.charges_consume.get(cache.type()));

		if(spawned >= 0) {
			spawner.setSpawnable(spawned);
			if(spawned < cache.stack()) {
				if(spawned <= 0) clear = true;
				else {
					spawner.setStack(spawned);
					if(hologram != null) hologram.rewrite();
				}
			}
		}
		
		if(clear == true) {
			remove(true);
			block.getWorld().spawnParticle(Particle.LAVA, Utility.center(block),
					25, 0.1, 0.1, 0.1, 0);
		}
		return true;
	}
	
	private void kill(LivingEntity entity, Entity killer) {
		entity.getPassengers().forEach(e -> damage(e, killer));
		damage(entity.getVehicle(), killer);
		damage(entity, killer);
	}
	
	private static final Invoker<?> _damage = RF.order(Damageable.class, "damage", double.class, Entity.class);
	
	private void damage(Entity entity, Entity killer) {
		if(entity instanceof LivingEntity living) {
			_damage.objected(living, 10_000_000, killer);
			living.setHealth(0);
			if(Settings.settings.instant_kill_death_animation == false)
				living.remove();
		}
	}

	@Override
	public void warn(SpawnerWarning warn) {
		if(active == false) return;
		warnings.add(warn);
		if(warning == null
				&& Settings.settings.holograms_warning_enabled == true) {
			warning = IHologram.warning(this, hologram != null);
			warning.update();
		}
		if(upgrades != null) upgrades.update();
	}
	
	@Override
	public boolean warned(SpawnerWarning warn) {
		return warnings.contains(warn) == true;
	}
	
	@Override
	public boolean warned() {
		return warnings.isEmpty() == false;
	}
	
	private boolean validate() {
		if(warnings.isEmpty() == true || rotating == false || active == false) return true;
		Block block = spawner.block();
		if(online == false) {
			block.getWorld().spawnParticle(Utility.particle_redstone, Utility.center(block),
					1, 0.5, 0.5, 0.5, 0, dust_owner);
		}
		if(rotating == true && Settings.settings.warning_particles == true ) {
			block.getWorld().spawnParticle(Utility.particle_redstone, Utility.center(block),
					1, 0.5, 0.5, 0.5, 0, dust_warn);
		}
		if(validation == 0 && cache.enabled() == true) valid();
		return false;
	}
	
	@Override
	public boolean valid() {
		if(cache.type() == SpawnerType.EMPTY) {
			warnings.clear();
			if(warning != null) warning.clear();
			warning = null;
			if(upgrades != null) upgrades.update();
			return false;
		}
		finder.find();
		validation(finder.errors());
		boolean empty = warnings.isEmpty() == true;
		if(empty == true && warning != null) {
			warnings.clear();
			warning.clear();
			warning = null;
			if(upgrades != null) upgrades.update();
		}
		return empty;
	}
	
	private void validation(ErrorCounter errors) {
		warnings.clear();
		if(errors.valid() == false) {
			int a = errors.light();
			int b = errors.lighted();
			int c = errors.ground();
			int d = errors.environment();
			
			if(a > 0) {
				if(c <= 0) warn(SpawnerWarning.GROUND);
				if(a == b) warn(SpawnerWarning.LIGHT);
				if(b == d) {
					if(b > 0) warn(SpawnerWarning.LIGHT);
					else warn(SpawnerWarning.ENVIRONMENT);
				}
			} else if(b <= 0 && d <= 0) warn(SpawnerWarning.ENVIRONMENT);
			
			// safety check
			if(warnings.isEmpty() == true) warn(SpawnerWarning.UNKNOWN);
		}
		var s = Settings.settings;

		if (s.charges_enabled) {
			boolean ignore = s.charges_ignore_natural && cache.natural();
		
			boolean shouldWarn = s.charges_comparison
				? cache.charges() < s.charges_requires_as_minimum.get(cache.type())
				: cache.charges() <= s.charges_requires_as_minimum.get(cache.type());
		
			if (shouldWarn && !ignore) {
				warn(SpawnerWarning.CHARGES);
			}
		}
		

		int power = s.redstone_power_required;
		if(power > 0 && spawner.block().getBlockPower() < power
				&& !(s.redstone_power_ignore_natural == true && cache.natural() == true))
			warn(SpawnerWarning.POWER);
	}
	
	@Override
	public boolean online() {
		return online;
	}
	
	@Override
	public IUpgrades upgrades() {
		return upgrades;
	}
	
	@Override
	public void open(Player player) {
		if(upgrades == null) upgrades = new ActiveUpgrades(this);
		upgrades.open(player);
	}
	
	@Override
	public void close() {
		if(upgrades == null) return;
		if(upgrades.active() == true) upgrades.close();
		upgrades = null;
	}
	
	@Override
	public void clear() {
		if(upgrades != null) upgrades.close();
		upgrades = null;
		if(hologram != null) hologram.clear();
		if(warning != null) warning.clear();
		warning = null;
		SpawningManager.unlink(spawner.block());
		unload();
	}
	
	@Override
	public void unload() {
		if(Settings.settings.reset_spawner_values == false) return;
		spawner.reset();
	}

}
