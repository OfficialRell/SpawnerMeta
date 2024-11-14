package mc.rellox.spawnermeta.spawner.generator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Material;
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
	
	private static BukkitRunnable active, offline_task;
	
	public static void initialize() {
		Bukkit.getPluginManager().registerEvents(new GeneratorRegistry(), SpawnerMeta.instance());
		load();
		retime(true);
	}
	
	public static void retime(boolean first) {
		if(active != null) active.cancel();
		active = runnable();
		active.runTaskTimer(SpawnerMeta.instance(), first ? 20 : 5, Settings.settings.ticking_interval);
		offline();
	}
	
	private static BukkitRunnable runnable() {
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
	
	private static void offline() {
		if(offline_task != null && offline_task.isCancelled() == false) offline_task.cancel();
		if(Settings.settings.owned_offline_time <= 0) return;
		(offline_task = new BukkitRunnable() {
			@Override
			public void run() {
				SPAWNERS.values().forEach(SpawnerWorld::control);
			}
		}).runTaskTimer(SpawnerMeta.instance(), 20 * 60, 20 * 60);
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
			Bukkit.getWorlds()
			.stream()
			.map(GeneratorRegistry::get)
			.filter(Objects::nonNull)
			.forEach(SpawnerWorld::load);
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
		if(Settings.inactive(world) == true) return 0;
		return get(world).active();
	}

	private static SpawnerWorld get(World world) {
		if(Settings.inactive(world) == true) return null;
		SpawnerWorld sw = SPAWNERS.get(world);
		if(sw == null) SPAWNERS.put(world, sw = new SpawnerWorld(world));
		return sw;
	}
	
	public static void put(Block block) {
		if(Settings.inactive(block.getWorld()) == true) return;
		get(block.getWorld()).put(block);
	}
	
	public static IGenerator get(Block block) {
		if(Settings.inactive(block.getWorld()) == true) return null;
		return get(block.getWorld()).get(block);
	}
	
	public static IGenerator raw(Block block) {
		if(Settings.inactive(block.getWorld()) == true) return null;
		return get(block.getWorld()).raw(block);
	}
	
	public static List<IGenerator> list(World world) {
		if(world != null) {
			SpawnerWorld sw = get(world);
			return sw == null ? new ArrayList<>()
					: new ArrayList<>(sw.spawners.values());
		}
		return SPAWNERS.values().stream()
				.flatMap(SpawnerWorld::stream)
				.collect(Collectors.toList());
	}
	
	public static void update(Block block) {
		World world = block.getWorld();
		if(Settings.inactive(world) == true) return;
		
		IGenerator generator = get(world).get(block);
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
		if(Settings.inactive(block.getWorld()) == true) return;
		IGenerator generator = get(block.getWorld()).raw(block);
		if(generator != null) generator.remove(false);
	}
	
	public static void delete(Block block) {
		if(Settings.inactive(block.getWorld()) == true) return;
		IGenerator generator = get(block.getWorld()).raw(block);
		if(generator != null) generator.remove(true);
		else block.setType(Material.AIR);
	}
	
	public static void clear() {
		SPAWNERS.values().forEach(SpawnerWorld::clear);
		SPAWNERS.clear();
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	private void onWorldLoad(WorldLoadEvent event) {
		try {
			World world = event.getWorld();
			if(Settings.inactive(world) == true) return;
			
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
			if(Settings.inactive(world) == true) return;
			
			SPAWNERS.remove(world);
		} catch (Exception e) {
			RF.debug(e);
		}
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	private void onChunkLoad(ChunkLoadEvent event) {
		try {
			World world = event.getWorld();
			if(Settings.inactive(world) == true) return;
			
			get(world).load(event.getChunk());
		} catch (Exception e) {
			RF.debug(e);
		}
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	private void onChunkUnload(ChunkUnloadEvent event) {
		try {
			World world = event.getWorld();
			if(Settings.inactive(world) == true) return;
			
			get(world).unload(event.getChunk());
		} catch (Exception e) {
			if(e.getMessage().contains("Chunk not there when requested") == true) return;
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
