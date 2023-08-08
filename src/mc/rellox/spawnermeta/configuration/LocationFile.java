package mc.rellox.spawnermeta.configuration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

import mc.rellox.spawnermeta.SpawnerMeta;
import mc.rellox.spawnermeta.spawner.generator.GeneratorRegistry;
import mc.rellox.spawnermeta.utility.DataManager;
import mc.rellox.spawnermeta.utility.Utils;
import mc.rellox.spawnermeta.utility.reflect.Reflect.RF;

public class LocationFile {
	
	private static File f;
	private static FileConfiguration file;
	
	private static boolean legacy;
	
	@SuppressWarnings("deprecation")
	public static void initialize() {
		f = new File(SpawnerMeta.instance().getDataFolder(), "locations.yml");
		if(f.getParentFile().exists() == false) f.getParentFile().mkdirs();
		if(f.exists() == false) {
			try {
				f.createNewFile();
			} catch(IOException e) {}
		}
		file = YamlConfiguration.loadConfiguration(f);
		file.options().header("This file contains all player placed spawner locations.\n"
				+ "Any invalid modifications may result in errors\n"
				+ "and unwanted spawner exploits.\n"
				+ "Location syntax must always be: [x, y, z]\n\n"
				+ "If you have used version 5.2 or lower and\n"
				+ "had enabled player spawner placing limit, than\n"
				+ "you are using legacy spawner limit.\n"
				+ "Legacy spawner limit excludes spawner locations\n"
				+ "and are stored on player. You can change the value\n"
				+ "of 'Legacy' to false to enable new spawner location\n"
				+ "saving.\n"
				+ "Warning! Using the new location saving all previously\n"
				+ "placed spawner will not be counted and be ignored,\n"
				+ "meaning all players will have 0 spawner placed, but\n"
				+ "all newly placed spawners will be saved in this file\n"
				+ "for easy spawner location finding and searshing.\n\n"
				+ "Note! Spawner locations won't be saved if legacy\n"
				+ "is set to true.");
		
		File log = new File(SpawnerMeta.instance().getDataFolder(), "log.yml");
		if(log.exists() == true) {
			legacy = true;
			FileConfiguration lc = YamlConfiguration.loadConfiguration(log);
			Set<String> keys = lc.getKeys(false);
			keys.forEach(key -> hold("Removing." + key, lc.getInt("remove_placed." + key)));
			saveFile();
			log.delete();
			
		} else legacy = false;
		file.addDefault("Legacy", legacy);
		file.options().copyDefaults(true);
		saveFile();
		LM.join();
	}
	
	public static void hold(String path, Object o) {
		file.set(path, o);
	}
	
	public static void save(String path, Object o) {
		file.set(path, o);
		saveFile();
	}
	
	public static void clear(String path) {
		file.set(path, null);
		saveFile();
	}

	public static void saveFile() {
		try {
			file.save(f);
		} catch(IOException e) {}
	}
	
	public static final class LF {
		
		public static String parse(Location loc) {
			int x = loc.getBlockX(), z = loc.getBlockZ();
			String sx = (x < 0 ? x == -1 ? "-0" : "" + (x + 1) : "" + x);
			String sz = (z < 0 ? z == -1 ? "-0" : "" + (z + 1) : "" + z);
			return "[" + sx + ", " + loc.getBlockY() + ", " + sz + "]";
		}
		
		public static List<String> names() {
			ConfigurationSection cs = file.getConfigurationSection("Locations");
			if(cs == null) return List.of();
			Set<String> keys = cs.getKeys(false);
			if(keys.isEmpty() == true) return List.of();
			try {
				return keys.stream()
						.map(UUID::fromString)
						.map(Bukkit::getOfflinePlayer)
						.map(OfflinePlayer::getName)
						.filter(o -> o != null)
						.toList();
			} catch (Exception e) {
				RF.debug(e);
			}
			return List.of();
		}
		
		public static List<Location> get(World world, String player) {
			ConfigurationSection cs = file.getConfigurationSection("Locations");
			if(cs == null) return List.of();
			Set<String> keys = cs.getKeys(false);
			if(keys.isEmpty() == true) return List.of();
			try {
				return keys.stream()
						.map(UUID::fromString)
						.map(Bukkit::getOfflinePlayer)
						.filter(p -> p.getName().equalsIgnoreCase(player))
						.map(OfflinePlayer::getUniqueId)
						.findFirst()
						.map(id -> get(world, id))
						.orElse(List.of());
			} catch (Exception e) {
				RF.debug(e);
			}
			return List.of();
		}
		
		public static List<Location> get(World world, Player player) {
			return get(world, player.getUniqueId());
		}
		
		public static List<Location> get(World world, UUID id) {
			List<Location> list = new ArrayList<>();
			List<String> ss = file.getStringList("Locations." + id.toString()
					+ "." + world.getName());
			if(ss.isEmpty() == true) return list;
			try {
				list = ss.stream()
						.map(s -> parse(world, s))
						.filter(l -> l != null)
						.collect(Collectors.toList());
			} catch (Exception e) {
				Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_RED + "[SM] Unable to get spawner locations: "
						+ ChatColor.AQUA + e.getMessage());
			}
			return list;
		}
		
		private static Location parse(World world, String s) {
			try {
				String[] ps = s.replace("[", "").replace("]", "").replace(" ", "").split(",");
				if(ps.length != 3) return null;
				int[] is = new int[3];
				for(int i = 0; i < 3; i++) {
					if(Utils.isInteger(ps[i]) == false) return null;
					is[i] = Integer.parseInt(ps[i]);
				}
				return new Location(world, is[0], is[1], is[2]);
			} catch (Exception e) {
				RF.debug(e);
			}
			return null;
		}
		
		public static int clear(World world, String player, boolean validate) {
			ConfigurationSection cs = file.getConfigurationSection("Locations");
			if(cs == null) return 0;
			Set<String> keys = cs.getKeys(false);
			if(keys.isEmpty() == true) return 0;
			try {
				return keys.stream()
						.map(UUID::fromString)
						.map(Bukkit::getOfflinePlayer)
						.filter(p -> p.getName().equalsIgnoreCase(player))
						.map(OfflinePlayer::getUniqueId)
						.findFirst()
						.map(id -> {
							String path = "Locations." + id.toString()
								+ "." + world.getName();
							List<String> ss = file.getStringList(path);
							if(ss.isEmpty() == true) return 0;
							if(validate == true) {
								int r = 0;
								Set<String> set = new HashSet<>(ss); // make unique
								if(ss.size() != set.size()) {
									r = ss.size() - set.size();
									ss = new ArrayList<>(set);
								}
								Iterator<String> it = ss.iterator();
								while(it.hasNext() == true) {
									if(it.next()
											.matches("\\[?-?\\d+,\\s*-?\\d+,\\s*-?\\d+\\]?") == true) continue;
									it.remove();
									r++;
								}
								it = ss.iterator();
								while(it.hasNext() == true) {
									Block block = parse(world, it.next()).getBlock();
									if(block.getType() == Material.SPAWNER) continue;
									GeneratorRegistry.remove(block);
									it.remove();
									r++;
								}
								LocationFile.save(path, ss);
								return r;
							} else {
								LocationFile.clear(path);
								ss.stream()
								.map(s -> parse(world, s))
								.filter(l -> l != null)
								.forEach(l -> {
									Block block = l.getBlock();
									if(block.getType() != Material.SPAWNER) return;
									block.setType(Material.AIR);
									GeneratorRegistry.remove(block);
								});
								return ss.size();
							}
						}).orElse(0);
			} catch (Exception e) {
				RF.debug(e);
			}
			return 0;
		}
		
		public static int placed(Player player) {
			if(legacy == true) return LM.getPlaced(player);
			String path = "Locations." + player.getUniqueId().toString();
			ConfigurationSection cs = file.getConfigurationSection(path);
			if(cs == null) return 0;
			Set<String> keys = cs.getKeys(false);
			List<String> list;
			int i = 0;
			for(String key : keys) {
				list = file.getStringList(path + "." + key);
				i += list.size();
			}
			return i;
		}
		
		public static void add(Block block, Player player) {
			if(legacy == true) {
				LM.addPlaced(player);
				return;
			}
			World world = block.getWorld();
			Location loc = block.getLocation();
			String path = "Locations." + player.getUniqueId().toString()
					+ "." + world.getName();
			List<String> ss = file.getStringList(path);
			String s = parse(loc);
			if(ss == null || ss.isEmpty() == true) ss = List.of(s);
			else ss.add(s);
			save(path, ss);
		}
		
		public static void remove(Block block) {
			UUID owner = DataManager.getOwner(block);
			if(owner == null) return;
			if(legacy == true) {
				LM.removePlaced(block);
				return;
			}
			int t = DataManager.getStack(block);
			World world = block.getWorld();
			Location loc = block.getLocation();
			String path = "Locations." + owner.toString() + "." + world.getName();
			List<String> ss = file.getStringList(path);
			if(ss == null || ss.isEmpty() == true) return;
			String s = parse(loc);
			boolean b = false;
			while(t-- > 0) b |= ss.remove(s);
			if(b == true) save(path, ss);
		}
		
	}

	private static final class LM {
		
		public static void join() {
			if(legacy == false) return;
			class LagacyJoin implements Listener {
				@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
				private void onPlayerJoin(PlayerJoinEvent event) {
					Player player = event.getPlayer();
					int r = getPlacedToRemove(player.getUniqueId());
					if(r <= 0) return;
					removePlaced(player, r);
				}
			}
			Bukkit.getPluginManager().registerEvents(new LagacyJoin(), SpawnerMeta.instance());
		}
		
		private static final String METADATA_PLACED = "Placed_Spawners";

		public static void removePlaced(Block block) {
			UUID id = DataManager.getOwner(block);
			if(id == null) return;
			int s = DataManager.getStack(block);
			Player player = Bukkit.getPlayer(id);
			if(player == null) setPlacedToRemove(id, s);
			else removePlaced(player, s);
		}

		public static void addPlaced(Player player) {
			MetadataValue m = getPlacedData(player);
			int v = (m == null) ? 1 : (m.asInt() + 1);
			player.setMetadata(METADATA_PLACED, new FixedMetadataValue(SpawnerMeta.instance(), v));
		}

		public static int getPlaced(Player player) {
			MetadataValue m = getPlacedData(player);
			return (m == null) ? 0 : m.asInt();
		}

		public static void removePlaced(Player player, int i) {
			MetadataValue m = getPlacedData(player);
			if(m == null) return;
			int p = m.asInt() - i;
			if(p <= 0) player.removeMetadata(METADATA_PLACED, SpawnerMeta.instance());
			else player.setMetadata(METADATA_PLACED, new FixedMetadataValue(SpawnerMeta.instance(), p));
		}

		private static MetadataValue getPlacedData(Player player) {
			if(player.hasMetadata(METADATA_PLACED) == false) return null;
			List<MetadataValue> list = player.getMetadata(METADATA_PLACED);
			if(list == null || list.isEmpty() == true) return null;
			return list.get(0);
		}

		public static void setPlacedToRemove(UUID id, int i) {
			String path = "Removing." + id.toString();
			int j = file.getInt(path) + i;
			save(path, Integer.valueOf(j));
		}

		public static int getPlacedToRemove(UUID id) {
			String path = "Removing." + id.toString();
			if(file.contains(path) == false) return 0;
			int v = file.getInt(path);
			save(path, null);
			return v;
		}
	}

}
