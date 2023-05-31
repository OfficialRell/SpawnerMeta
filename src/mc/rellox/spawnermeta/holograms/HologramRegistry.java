package mc.rellox.spawnermeta.holograms;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;
import org.bukkit.scheduler.BukkitRunnable;

import mc.rellox.spawnermeta.SpawnerMeta;
import mc.rellox.spawnermeta.configuration.Settings;
import mc.rellox.spawnermeta.utils.Version;

public final class HologramRegistry {

	public static final HologramInstance HI;
	static {
		HI = switch(Version.version) {
		case v_14_1 -> new HologramInstance1_14();
		case v_15_1 -> new HologramInstance1_15();
		case v_16_1 -> new HologramInstance1_16_1();
		case v_16_2 -> new HologramInstance1_16_2();
		case v_16_3 -> new HologramInstance1_16_3();
		case v_17_1 -> new HologramInstance1_17();
		case v_18_1 -> new HologramInstance1_18_1();
		case v_18_2 -> new HologramInstance1_18_2();
		case v_19_1 -> new HologramInstance1_19_1();
		case v_19_2 -> new HologramInstance1_19_2();
		case v_19_3 -> new HologramInstance1_19_3();
		default -> null;
		};
	}
	
	private static final HologramLoader LOADER = new HologramLoader();
	
	public static void initialize() {
		LOADER.clear();
		LOADER.unregister();
		if(Settings.settings.holograms_enabled == true) LOADER.register();
	}
	
	public static boolean loaded() {
		return LOADER.registered == true;
	}
	
	public static void erase() {
		LOADER.clear();
	}
	
	public static void update(Block block) {
		LOADER.update(block);
	}
	
	public static void add(Block block) {
		LOADER.add(block);
	}
	
	public static void remove(Block block) {
		LOADER.remove(block);
	}
	
	public static void load(World world) {
		LOADER.load(world);
	}
	
	private static final class HologramLoader implements Listener {
		
		private final Map<World, HologramMap> map;
		private boolean registered;
		
		public HologramLoader() {
			this.map = new HashMap<>();
		}
		
		public void register() {
			if(registered == true) return;
			registered = true;
			HologramMap hm;
			for(World world : Bukkit.getWorlds()) {
				map.put(world, hm = new HologramMap());
				for(Chunk chunk : world.getLoadedChunks()) hm.load(chunk);
			}
			Bukkit.getPluginManager().registerEvents(this, SpawnerMeta.instance());
		}
		
		public void unregister() {
			if(registered == false) return;
			registered = false;
			HandlerList.unregisterAll(this);
		}
		
		public void clear() {
			if(registered == false) return;
			map.forEach((w, h) -> h.clear());
			map.clear();
		}
		
		public void update(Block block) {
			if(registered == false) return;
			HologramMap hm = map.get(block.getWorld());
			if(hm != null) hm.update(block);
		}
		
		public void add(Block block) {
			if(registered == false) return;
			HologramMap hm = map.get(block.getWorld());
			if(hm != null) hm.add(block);
		}
		
		public void remove(Block block) {
			if(registered == false) return;
			HologramMap hm = map.get(block.getWorld());
			if(hm != null) hm.remove(block);
		}
		
		public void load(World world) {
			if(registered == false || map.containsKey(world) == true) return;
			HologramMap hm = new HologramMap();
			map.put(world, hm);
			for(Chunk chunk : world.getLoadedChunks()) hm.load(chunk);
		}
		
		public void unload(World world) {
			if(registered == false || map.containsKey(world) == false) return;
			HologramMap hm = map.remove(world);
			hm.clear();
		}
		
		@EventHandler
		private void onWorldLoad(WorldLoadEvent event) {
			load(event.getWorld());
		}
		
		@EventHandler
		private void onWorldUnload(WorldUnloadEvent event) {
			unload(event.getWorld());
		}
		
		@EventHandler
		private void onLoad(ChunkLoadEvent event) {
			Chunk chunk = event.getChunk();
			HologramMap hm = map.get(chunk.getWorld());
			if(hm != null) hm.load(chunk);
		}
		
		@EventHandler
		private void onUnload(ChunkUnloadEvent event) {
			Chunk chunk = event.getChunk();
			HologramMap hm = map.get(chunk.getWorld());
			if(hm != null) hm.unload(chunk);
		}
		
		@EventHandler
		private void onJoin(PlayerJoinEvent event) {
			Player player = event.getPlayer();
			new BukkitRunnable() {
				@Override
				public void run() {
					HologramMap hm = map.get(player.getWorld());
					if(hm != null) hm.spawn(player);
				}
			}.runTaskLater(SpawnerMeta.instance(), 50);
		}
		
		@EventHandler
		private void onWorldChange(PlayerChangedWorldEvent event) {
			Player player = event.getPlayer();
			new BukkitRunnable() {
				@Override
				public void run() {
					HologramMap hm = map.get(player.getWorld());
					if(hm != null) hm.spawn(player);
				}
			}.runTaskLater(SpawnerMeta.instance(), 20);
		}
		
	}

}
