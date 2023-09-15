package mc.rellox.spawnermeta.spawner;

import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.block.Block;
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
import mc.rellox.spawnermeta.spawner.generator.SpawningManager;
import mc.rellox.spawnermeta.spawner.requirement.ActiveFinder;
import mc.rellox.spawnermeta.spawner.type.SpawnerType;
import mc.rellox.spawnermeta.utility.DataManager;
import mc.rellox.spawnermeta.utility.Utils;
import mc.rellox.spawnermeta.view.ActiveUpgrades;

public class ActiveGenerator implements IGenerator {
	
	private static final DustOptions dust = new DustOptions(Color.MAROON, 1.5f);
	
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
		valid();
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
	public boolean active() {
		return active;
	}
	
	@Override
	public boolean present() {
		return block().getType() == Material.SPAWNER
				&& block().getChunk().isLoaded() == true;
	}
	
	@Override
	public void remove(boolean fully) {
		active = false;
		clear();
		if(fully == true) {
			Block block = block();
			LocationRegistry.remove(block);
			block.setType(Material.AIR);
		}
	}
	
	@Override
	public void update() {
		if(active == false) return;
		cache.cache();
		
		delay = cache.delay() / Settings.settings.ticking_interval;
		if(ticks <= 0 || ticks > delay) ticks = delay;
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
		if(Settings.settings.owned_if_online == true) {
			if(cache.natural() == true) online = true;
			else {
				Player owner = spawner.getOwner();
				online = owner == null ? false : owner.isOnline();
			}
		} else online = true;
		if(online == false) spawner.setRotating(rotating = false);
		else check();
	}

	@Override
	public void tick() {
		if(--validation < 0) validation = Settings.settings.validation_interval - 1;
		if(active == false) return;
		holograms();
		if(check() == false) return;
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
	
	private void holograms() {
		if(validation != 0 || active == false) return;
		if(hologram != null) hologram.update();
		if(warning != null) warning.update();
	}
	
	private boolean check() {
		if(--checking < 0) checking = Settings.settings.checking_interval - 1;
		if(cache.enabled() == false) return false;
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
			int r = s.radius;
			int nearby = spawner.world().getNearbyEntities(spawner.center(), r, r, r,
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
		if(call.cancelled() == true) return false;
		
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
			spawner.setSpawnable(spawnable);
			if(spawnable < cache.stack()) {
				if(spawnable <= 0) clear = true;
				else {
					spawner.setStack(spawnable);
					if(hologram != null) hologram.rewrite();
				}
			}
		}
		
		if(count <= 0) {
			block.getWorld().spawnParticle(Particle.REDSTONE, block.getLocation().add(0.5, 0.5, 0.5),
					25, 0.45, 0.45, 0.45, 0.075, new DustOptions(Color.MAROON, 2.5f));
			return false;
		}

		List<Entity> entities = SpawningManager.spawn(spawner, cache.type(), ISelector.of(list), count);
		
		if(entities.isEmpty() == true) return false;
		
		if(upgrades != null) upgrades.update();
		
		EventRegistry.call(new SpawnerPostSpawnEvent(this, entities));
		
		if(s.kill_entities_on_spawn == true) {
			if(s.entities_drop_xp == true) {
				Optional<Entity> opt = block.getWorld()
						.getNearbyEntities(block.getLocation(), 32, 32, 32)
						.stream()
						.filter(n -> n instanceof Player)
						.findAny();
				entities.forEach(e -> {
					if(e instanceof LivingEntity living)
						opt.ifPresentOrElse(killer -> kill(living, killer),
								() -> kill(living, null));
				});
			} else {
				entities.forEach(e -> {
					if(e instanceof LivingEntity living) kill(living, null);
				});
			}
		}
		if(call.bypass_checks == false && s.charges_enabled == true
				&& cache.charges() < 1_000_000_000) spawner.setCharges(cache.charges() - 1);

		if(clear == true) {
			remove(true);
			block.getWorld().spawnParticle(Particle.LAVA, Utils.center(block),
					25, 0.1, 0.1, 0.1, 0);
		}
		return true;
	}
	
	private void kill(LivingEntity entity, Entity killer) {
		entity.getPassengers().forEach(e -> damage(e, killer));
		damage(entity.getVehicle(), killer);
		damage(entity, killer);
	}
	
	private void damage(Entity entity, Entity killer) {
		if(entity instanceof LivingEntity living) {
			living.damage(10_000_000, killer);
			living.setHealth(0);
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
		if(rotating == true) {
			Block block = spawner.block();
			block.getWorld().spawnParticle(Particle.REDSTONE, Utils.center(block),
					1, 0.5, 0.5, 0.5, 0, dust);
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
		if(Settings.settings.charges_enabled == true && cache.charges() <= 0)
			warn(SpawnerWarning.CHARGES);
		int power = Settings.settings.required_redstone_power;
		if(power > 0 && spawner.block().getBlockPower() < power)
			warn(SpawnerWarning.POWER);
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
	}

}
