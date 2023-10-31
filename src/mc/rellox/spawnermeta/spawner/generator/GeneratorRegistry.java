package mc.rellox.spawnermeta.spawner.generator;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;
import org.bukkit.scheduler.BukkitRunnable;

import mc.rellox.spawnermeta.SpawnerMeta;
import mc.rellox.spawnermeta.api.spawner.IGenerator;
import mc.rellox.spawnermeta.configuration.Settings;
import mc.rellox.spawnermeta.utility.reflect.Reflect.RF;

public final class GeneratorRegistry implements Listener {
	
	private static final Map<World, SpawnerWorld> SPAWNERS = new HashMap<>();
	
	private static BukkitRunnable active;
	
	public static void initialize() {
		Bukkit.getPluginManager().registerEvents(new GeneratorRegistry(), SpawnerMeta.instance());
		load();
		retime(true);
	}
	
	public static void retime(boolean first) {
		if(active != null) active.cancel();
		active = runnable(first);
		active.runTaskTimer(SpawnerMeta.instance(), first ? 20 : 5, Settings.settings.ticking_interval);
	}
	
	private static BukkitRunnable runnable(boolean first) {
		return new BukkitRunnable() {
			int t = 0;
			final int f = Math.max(100, Settings.settings.check_present_interval / Settings.settings.ticking_interval);
			@Override
			public void run() {
				SPAWNERS.values().forEach(SpawnerWorld::tick);
				if(++t > f) {
					t = 0;
					SPAWNERS.values().forEach(SpawnerWorld::reduce);
				}
			}
		};
	}
	
	private static void control() {
		try {
			new BukkitRunnable() {
				@Override
				public void run() {
					SPAWNERS.values().forEach(SpawnerWorld::control);
				}
			}.runTaskLater(SpawnerMeta.instance(), 5);
		} catch (Exception e) {}
	}
	
	public static void load() {
		try {
			Bukkit.getWorlds().forEach(world -> get(world).load());
		} catch (Exception e) {
			RF.debug(e);
		}
	}
	
	public static void reload() {
		try {
			clear();
			load();
		} catch (Exception e) {
			RF.debug(e);
		}
	}
	
	public static int active(World world) {
		if(world == null) return SPAWNERS.values()
				.stream()
				.mapToInt(SpawnerWorld::active)
				.sum();
		return get(world).active();
	}
	
	private static SpawnerWorld get(World world) {
		SpawnerWorld sw = SPAWNERS.get(world);
		if(sw == null) SPAWNERS.put(world, sw = new SpawnerWorld(world));
		return sw;
	}
	
	public static void put(Block block) {
		get(block.getWorld()).put(block);
	}
	
	public static IGenerator get(Block block) {
		return get(block.getWorld()).get(block);
	}
	
	public static void update(Block block) {
		IGenerator generator = get(block.getWorld()).get(block);
		if(generator != null) {
			generator.update();
			generator.valid();
			generator.rewrite();
		}
		SpawningManager.unlink(block);
	}
	
	public static void update() {
		SPAWNERS.values().forEach(SpawnerWorld::update);
	}
	
	public static void remove(Block block) {
		IGenerator generator = get(block.getWorld()).raw(block);
		if(generator != null) generator.remove(false);
	}
	
	public static void clear() {
		SPAWNERS.values().forEach(SpawnerWorld::clear);
		SPAWNERS.clear();
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	private void onWorldLoad(WorldLoadEvent event) {
		try {
			World world = event.getWorld();
			SpawnerWorld sw = new SpawnerWorld(world);
			SPAWNERS.put(world, sw);
			sw.load();
		} catch (Exception e) {
			RF.debug(e);
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	private void onWorldUnload(WorldUnloadEvent event) {
		try {
			World world = event.getWorld();
			SPAWNERS.remove(world);
		} catch (Exception e) {
			RF.debug(e);
		}
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	private void onChunkLoad(ChunkLoadEvent event) {
		try {
			get(event.getWorld()).load(event.getChunk());
		} catch (Exception e) {
			RF.debug(e);
		}
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	private void onChunkUnload(ChunkUnloadEvent event) {
		try {
			get(event.getWorld()).unload(event.getChunk());
		} catch (Exception e) {
			RF.debug(e);
		}
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	private void onJoin(PlayerJoinEvent event) {
		control();
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	private void onQuit(PlayerQuitEvent event) {
		control();
	}
	
}
