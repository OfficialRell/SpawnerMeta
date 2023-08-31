package mc.rellox.spawnermeta.configuration.location;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import mc.rellox.spawnermeta.SpawnerMeta;
import mc.rellox.spawnermeta.api.configuration.ILocations;
import mc.rellox.spawnermeta.api.spawner.ISpawner;
import mc.rellox.spawnermeta.utility.reflect.Reflect.RF;

public final class LocationRegistry {
	
	protected static final File parent = new File(SpawnerMeta.instance().getDataFolder(), "spawners");
	static {
		parent.mkdirs();
	}
	
	private static final Map<UUID, ILocations> LOCATIONS = new HashMap<>();
	
	public static void initialize() {
		convert();
		new BukkitRunnable() {
			@Override
			public void run() {
				var it = LOCATIONS.values().iterator();
				while(it.hasNext() == true) {
					if(it.next().using() == true) continue;
					it.remove();
				}
			}
		}.runTaskTimer(SpawnerMeta.instance(), 20 * 60, 20 * 60);
	}
	
	public static boolean exists(Player player) {
		File file = new File(parent, player.getUniqueId().toString() + ".yml");
		return file.exists() == true;
	}
	
	public static List<String> names() {
		String[] list = parent.list();
		if(list == null || list.length <= 0) return List.of();
		List<String> names = new ArrayList<>();
		for(String file : list) {
			try {
				UUID id = UUID.fromString(file.replace(".yml", ""));
				String name = Bukkit.getOfflinePlayer(id).getName();
				if(name != null) names.add(name);
			} catch (Exception e) {
				RF.debug(e);
			}
		}
		return names;
	}
	
	public static ILocations find(String player) {
		String[] list = parent.list();
		if(list == null || list.length <= 0) return null;
		for(String file : list) {
			try {
				UUID id = UUID.fromString(file.replace(".yml", ""));
				String name = Bukkit.getOfflinePlayer(id).getName();
				if(name != null && name.equalsIgnoreCase(player) == true)
					return get(id);
			} catch (Exception e) {
				RF.debug(e);
			}
		}
		return null;
	}
	
	public static ILocations get(Block block) {
		UUID id = ISpawner.of(block).getOwnerID();
		return id == null ? null : get(id);
	}
	
	public static ILocations get(Player player) {
		return get(player.getUniqueId());
	}
	
	public static ILocations get(UUID id) {
		ILocations il = LOCATIONS.get(id);
		if(il == null) {
			if(Bukkit.getOfflinePlayer(id).getName() == null)
				throw new IllegalArgumentException("No player with this UUID (" + id.toString() + ") has played before");
			LOCATIONS.put(id, il = new LocationFile(id));
		}
		il.use();
		il.load();
		return il;
	}
	
	public static ILocations raw(Player player) {
		return raw(player.getUniqueId());
	}
	
	public static ILocations raw(UUID id) {
		return LOCATIONS.get(id);
	}
	
	public static void add(Player player, Block block) {
		get(player).add(block);
	}
	
	public static void remove(Block block) {
		ILocations il = get(block);
		if(il != null) il.remove(block);
	}
	
	public static boolean trusted(UUID owner, Player player) {
		ILocations il = raw(owner);
		return il == null ? false : il.trusts(player);
	}
	
	private static void convert() {
		new BukkitRunnable() {
			@Override
			public void run() {
				File f = new File(parent.getParentFile(), "locations.yml");
				if(f.exists() == false) return;
				var file = YamlConfiguration.loadConfiguration(f);
				if(file == null) return;
				ConfigurationSection cs = file.getConfigurationSection("Locations");
				if(cs == null) return;
				Set<String> keys = cs.getKeys(false);
				keys.forEach(key -> {
					ConfigurationSection cc = cs.getConfigurationSection(key);
					if(cc == null) return;
					UUID id;
					try {
						id = UUID.fromString(key);
					} catch (Exception e) {
						RF.debug(e);
						return;
					}
					Set<String> ks = cc.getKeys(false);
					LocationFile il = new LocationFile(id);
					il.create();
					ks.forEach(k -> {
						List<String> list = cc.getStringList(k);
						il.hold("Spawners." + k, list.stream()
								.map(s -> s.replaceAll("\\[|\\]", ""))
								.distinct()
								.toList());
					});
					il.save();
					f.delete();
				});
			}
		}.runTaskLater(SpawnerMeta.instance(), 20);
	}

}
