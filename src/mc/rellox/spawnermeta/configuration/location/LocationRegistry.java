package mc.rellox.spawnermeta.configuration.location;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import mc.rellox.spawnermeta.SpawnerMeta;
import mc.rellox.spawnermeta.api.configuration.IData;
import mc.rellox.spawnermeta.api.configuration.IPlayerData;
import mc.rellox.spawnermeta.api.spawner.ISpawner;
import mc.rellox.spawnermeta.utility.reflect.Reflect.RF;

public final class LocationRegistry implements Listener {
	
	protected static final File parent = new File(SpawnerMeta.instance().getDataFolder(), "spawners");
	static {
		parent.mkdirs();
	}
	
	private static final Pattern uuid_validation =
			Pattern.compile("[0-9a-fA-F]{8}-([0-9a-fA-F]{4}-){3}[0-9a-fA-F]{12}");
	
	private static final Map<UUID, IPlayerData> LOCATIONS = new HashMap<>();
	
	protected static final List<IData<?>> EXTERNA_DATA = new ArrayList<>();
	
	public static void initialize() {
		convert();
		SpawnerMeta.scheduler().runTimer(() -> {
			var it = LOCATIONS.values().iterator();
			while(it.hasNext() == true) {
				IPlayerData il = it.next();
				if(il.using() == true) continue;
				if(il instanceof LocationFile file) {
					if(EXTERNA_DATA.isEmpty() == false) file.saveExternal();
					file.cached = false;
				}
				il.infinite(false);
				it.remove();
			}
		}, 20 * 60, 20 * 60);
		Bukkit.getPluginManager().registerEvents(new LocationRegistry(), SpawnerMeta.instance());
	}
	
	/**
	 * Saves all player data.
	 */
	
	public static void clear() {
		LOCATIONS.values().forEach(il -> {
			if(il instanceof LocationFile file) file.update();
		});
		LOCATIONS.clear();
	}
	
	/**
	 * @param player - player
	 * @return {@code true} if the player file exists
	 */
	
	public static boolean exists(Player player) {
		return exists(player.getUniqueId());
	}
	
	/**
	 * @param id - player id
	 * @return {@code true} if the player file exists
	 */
	
	public static boolean exists(UUID id) {
		File file = new File(parent, id.toString() + ".yml");
		return file.exists() == true;
	}
	
	/**
	 * Submits the data parser.
	 * 
	 * @param data - data parser
	 */
	
	public static void submit(IData<?> data) {
		if(EXTERNA_DATA.stream()
				.map(IData::id)
				.anyMatch(data.id()::equals) == true)
			throw new IllegalArgumentException("Data parser with this id ("
					+ data.id() + ") already exists");
		EXTERNA_DATA.add(data);
	}
	
	/**
	 * @return List of player names that has a player file
	 */
	
	public static List<String> names() {
		String[] list = parent.list();
		if(list == null || list.length <= 0) return List.of();
		List<String> names = new ArrayList<>();
		for(String file : list) {
			file = file.replace(".yml", "");
			if(uuid_validation.matcher(file).matches() == false) continue;
			try {
				UUID id = UUID.fromString(file);
				String name = Bukkit.getOfflinePlayer(id).getName();
				if(name != null) names.add(name);
			} catch (Exception e) {
				RF.debug(e);
			}
		}
		return names;
	}
	
	/**
	 * Tries to find the player file by the player name.
	 * Can be {@code null}.
	 * 
	 * @param player - player name
	 * @return Player file
	 */
	
	public static IPlayerData find(String player) {
		String[] list = parent.list();
		if(list == null || list.length <= 0) return null;
		for(String file : list) {
			file = file.replace(".yml", "");
			if(uuid_validation.matcher(file).matches() == false) continue;
			try {
				UUID id = UUID.fromString(file);
				String name = Bukkit.getOfflinePlayer(id).getName();
				if(name != null && name.equalsIgnoreCase(player) == true)
					return get(id);
			} catch (Exception e) {
				RF.debug(e);
			}
		}
		return null;
	}
	
	/**
	 * Returns the player file by the spawner owner. Creates a new if doesn't exist.
	 * Can be {@code null}.
	 * 
	 * @param block - spawner block
	 * @return Player file
	 */
	
	public static IPlayerData get(Block block) {
		UUID id = ISpawner.of(block).getOwnerID();
		return id == null ? null : get(id);
	}
	
	/**
	 * Returns the player file. Creates a new if doesn't exist.
	 * Never {@code null}.
	 * 
	 * @param player - player
	 * @return Player file
	 */
	
	public static IPlayerData get(Player player) {
		return get(player.getUniqueId());
	}
	
	/**
	 * Returns the player file. Creates a new if doesn't exist.
	 * Never {@code null}.
	 * 
	 * @param id - player id
	 * @return Player file
	 */
	
	public static IPlayerData get(UUID id) {
		IPlayerData il = LOCATIONS.get(id);
		if(il == null) {
			if(Bukkit.getOfflinePlayer(id).getName() == null)
				throw new IllegalArgumentException("No player with this UUID (" + id.toString() + ") has played before");
			LOCATIONS.put(id, il = new LocationFile(id));
		}
		il.use();
		il.load();
		return il;
	}
	
	/**
	 * Returns the player file only if it has been already created, otherwise {@code null}.
	 * 
	 * @param player - player
	 * @return Player file
	 */
	
	public static IPlayerData raw(Player player) {
		return raw(player.getUniqueId());
	}
	
	/**
	 * Returns the player file only if it has been already created, otherwise {@code null}.
	 * 
	 * @param id - player id
	 * @return Player file
	 */
	
	public static IPlayerData raw(UUID id) {
		IPlayerData il = LOCATIONS.get(id);
		if(il != null) return il;
		if(exists(id) == false) return null;
		return get(id);
	}
	
	/**
	 * Returns the player file only if it is loaded.
	 * 
	 * @param id - player id
	 * @return Player file
	 */
	
	public static IPlayerData loaded(UUID id) {
		return LOCATIONS.get(id);
	}
	
	/**
	 * Adds the spawner block to the player's spawner location list.
	 * 
	 * @param player - player
	 * @param block - spawner block
	 */
	
	public static void add(Player player, Block block) {
		get(player).add(block);
	}
	
	/**
	 * Removes the spawner from the player's spawner location list.
	 * Only if the spawner is owned by a player.
	 * 
	 * @param block - spawner block
	 */
	
	public static void remove(Block block) {
		IPlayerData il = get(block);
		if(il != null) il.remove(block);
	}
	
	/**
	 * @param owner - owner
	 * @param player - player
	 * @return {@code true} if the player is a trusted player
	 */
	
	public static boolean trusted(UUID owner, Player player) {
		IPlayerData il = raw(owner);
		return il == null ? false : il.trusts(player);
	}
	
	@EventHandler
	private final void onQuit(PlayerQuitEvent event) {
		UUID id = event.getPlayer().getUniqueId();
		IPlayerData il = LOCATIONS.get(id);
		if(il == null) return;
		il.infinite(false);
	}
	
	private static void convert() {
		SpawnerMeta.scheduler().runLater(() -> {
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
		}, 20);
	}

}
