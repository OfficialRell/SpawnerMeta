package mc.rellox.spawnermeta.shop;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import mc.rellox.spawnermeta.SpawnerMeta;
import mc.rellox.spawnermeta.spawner.type.SpawnerType;
import mc.rellox.spawnermeta.utility.reflect.Reflect.RF;
import mc.rellox.spawnermeta.version.Version;
import mc.rellox.spawnermeta.version.Version.VersionType;

public final class ShopRegistry {
	
	private static File lf;
	private static FileConfiguration file;
	
	private static SpawnerShopBuy shop_buy;
	private static SpawnerShopSell shop_sell;
	private static ShopSelection shop_selection;
	
	public static int first, second, third;
	public static Material buy_next, buy_prev, buy_page;
	
	private static final Map<String, PermissionHolder>
		BUY_PERMISSIONS = new HashMap<>(), SELL_PERMISSIONS = new HashMap<>();
	
	public static void initialize() {
		initializeConfig();
		loadValues();
		loadBuy();
		loadSell();
		loadSelection();
		loadPermissions();
	}
	
	private static void loadValues() {
		first = file.getInt("Settings.Buy.Amount.First");
		second = file.getInt("Settings.Buy.Amount.Second");
		third = file.getInt("Settings.Buy.Amount.Third");
		buy_next = RF.enumerate(Material.class, file.getString("Settings.Buy.Items.Next"), Material.SPECTRAL_ARROW);
		buy_prev = RF.enumerate(Material.class, file.getString("Settings.Buy.Items.Previous"), Material.SPECTRAL_ARROW);
		buy_page = RF.enumerate(Material.class, file.getString("Settings.Buy.Items.Page"), Material.PAPER);
	}
	
	private static void loadBuy() {
		if(shop_buy != null) shop_buy.unregister();
		shop_buy = null;
		if(file.getBoolean("Settings.Buy.Enabled") == false) return;
		int rows = file.getInt("Settings.Buy.Rows");
		if(rows <= 1 && rows > 6) {
			log("Unable to create buy shop, invalid row amount (" + rows + ")! "
					+ "Rows must be greater than 1 and less or equal than 6!");
			return;
		}
		List<BuyData> list = new ArrayList<>();
		for(SpawnerType type : SpawnerType.values()) {
			if(type.exists() == false || file.getBoolean("Shop.Buy." + type.name() + ".Toggle") == false) continue;
			int cost = file.getInt("Shop.Buy." + type.name() + ".Cost");
			if(cost <= 0) {
				log("Unable to create buy shop, invalid spawner (" + type.formated() + ") cost (" + cost + ")!");
				continue;
			}
			list.add(new BuyData(type, cost));
		}
		if(list.isEmpty() == true) {
			log("Unable to create buy shop, no spawners to sell!");
			return;
		}
		Material filler = RF.enumerate(Material.class, file.getString("Settings.Buy.Filler"));
		if(filler == null) {
			log("Unable to create buy shop, invalid inventory filler!");
			return;
		}
		boolean[] bs = {
				file.getBoolean("Settings.Buy.Buyable.First"), file.getBoolean("Settings.Buy.Buyable.Second"),
				file.getBoolean("Settings.Buy.Buyable.Third"), file.getBoolean("Settings.Buy.Buyable.Maximum")
		};
		boolean f = false;
		for(boolean b : bs) if((f |= b) == true) break;
		if(f == false) {
			log("Unable to create buy shop, shop must allow at least one buying option!");
			return;
		}
		order(list);
		shop_buy = new SpawnerShopBuy(filler, rows, bs, list.toArray(BuyData[]::new));
	}
	
	private static void order(List<BuyData> list) {
		List<SpawnerType> order = RF.enumerates(SpawnerType.class, file.getStringList("Settings.Buy.Order"));
		if(order.isEmpty() == true) return;
		List<BuyData> newest = new ArrayList<>();
		for(SpawnerType  type : order) {
			for(int i = 0; i < list.size(); i++) {
				BuyData bd = list.get(i);
				if(bd.type() == type) {
					newest.add(bd);
					list.remove(i);
					break;
				}
			}
		}
		list.addAll(0, newest);
	}
	
	private static void loadSell() {
		if(shop_sell != null) shop_sell.unregister();
		shop_sell = null;
		if(file.getBoolean("Settings.Sell.Enabled") == false) return;
		int rows = file.getInt("Settings.Sell.Rows");
		if(rows <= 1 && rows > 6) {
			log("Unable to create sell shop, invalid row amount (" + rows + ")! "
					+ "Rows must be greater than 1 and less or equal than 6!");
			return;
		}
		Material filler = RF.enumerate(Material.class, file.getString("Settings.Sell.Filler"));
		if(filler == null) {
			log("Unable to create sell shop, invalid inventory filler!");
			return;
		}
		Material sell = RF.enumerate(Material.class, file.getString("Settings.Sell.Sell"));
		if(sell == null) {
			log("Unable to create sell shop, invalid sell material!");
			return;
		}
		Material close = RF.enumerate(Material.class, file.getString("Settings.Sell.Close"));
		if(close == null) {
			log("Unable to create sell shop, invalid close material!");
			return;
		}
		List<SellData> list = new ArrayList<>();
		for(SpawnerType type : SpawnerType.values()) {
			if(type.exists() == false || file.getBoolean("Shop.Sell." + type.name() + ".Toggle") == false) continue;
			int refund = file.getInt("Shop.Sell." + type.name() + ".Refund");
			if(refund <= 0) {
				log("Unable to create sell shop, refund amount must be greater than 0 (" + type.name() + ")!");
				return;
			}
			double up = file.getDouble("Shop.Sell." + type.name() + ".Upgrades");
			if(up < 0 || up > 1) {
				log("Unable to create sell shop, upgrade refund percentage must be greater or equal than 0 "
						+ "and less or equal than 1 (" + type.name() + ")!");
				return;
			}
			list.add(new SellData(type, refund, up));
		}
		SellGroup group = new SellGroup(filler, sell, close, rows, list.toArray(SellData[]::new));
		shop_sell = new SpawnerShopSell(group);
	}
	
	private static void loadSelection() {
		if(shop_buy == null || shop_sell == null) {
			shop_selection = null;
			return;
		}
		Material filler = RF.enumerate(Material.class, file.getString("Settings.Selection.Filler"));
		if(filler == null) {
			log("Unable to create selection shop, invalid inventory filler!");
			return;
		}
		Material mbuy = RF.enumerate(Material.class, file.getString("Settings.Selection.Buy"));
		if(mbuy == null) {
			log("Unable to create selection shop, invalid buy material!");
			return;
		}
		Material msell = RF.enumerate(Material.class, file.getString("Settings.Selection.Sell"));
		if(msell == null) {
			log("Unable to create selection shop, invalid sell material!");
			return;
		}
		shop_selection = new ShopSelection(shop_buy, shop_sell, filler, mbuy, msell);
	}
	
	public static boolean open(Player player) {
		if(shop_selection != null) shop_selection.open(player);
		else if(shop_buy != null) shop_buy.open(player);
		else if(shop_sell != null) shop_sell.open(player);
		else return false;
		return true;
	}
	
	private static void loadPermissions() {
		BUY_PERMISSIONS.clear();
		SELL_PERMISSIONS.clear();
		ConfigurationSection cs = file.getConfigurationSection("Permissions.buy");
		if(cs != null) {
			String path = "Permissions.buy";
			Set<String> keys = cs.getKeys(false);
			keys.forEach(key -> {
				String p = path + "." + key;
				List<String> list = file.getStringList(p + ".entities");
				if(list.isEmpty() == true) {
					log("Missing entity list for shop buy permission (" + key + ")");
					return;
				}
				Set<SpawnerType> set = list.contains("ALL") == true
						? Set.of(SpawnerType.values()) : new HashSet<>(RF.enumerates(SpawnerType.class, list));
				String sub = file.getString(p + ".sub-permission");
				BUY_PERMISSIONS.put("spawnermeta.shop.buy.permission." + key, new PermissionHolder(set, sub));
			});
		}
		cs = file.getConfigurationSection("Permissions.sell");
		if(cs != null) {
			String path = "Permissions.sell";
			Set<String> keys = cs.getKeys(false);
			keys.forEach(key -> {
				String p = path + "." + key;
				List<String> list = file.getStringList(p + ".entities");
				if(list.isEmpty() == true) {
					log("Missing entity list for shop sell permission (" + key + ")");
					return;
				}
				Set<SpawnerType> set = list.contains("ALL") == true
						? Set.of(SpawnerType.values()) : new HashSet<>(RF.enumerates(SpawnerType.class, list));
				String sub = file.getString(p + ".sub-permission");
				SELL_PERMISSIONS.put("spawnermeta.shop.sell.permission." + key, new PermissionHolder(set, sub));
			});
		}
	}
	
	public static boolean canBuy(Player player, SpawnerType type) {
		return permissions(player, type, BUY_PERMISSIONS);
	}
	
	public static boolean canSell(Player player, SpawnerType type) {
		return permissions(player, type, SELL_PERMISSIONS);
	}
	
	private static boolean permissions(Player player, SpawnerType type, Map<String, PermissionHolder> map) {
		if(map.isEmpty() == true) return true;
		for(Entry<String, PermissionHolder> e : map.entrySet()) {
			String perm = e.getKey();
			if(player.isPermissionSet(perm) == true && player.hasPermission(perm) == true)
				if(e.getValue().is(type, map) == true) return true;
		}
		return false;
	}
	
	private static class PermissionHolder {
		
		private final Set<SpawnerType> set;
		private final String sub;
		
		public PermissionHolder(Set<SpawnerType> list, String sub) {
			this.set = list;
			this.sub = sub;
		}
		
		public boolean is(SpawnerType type, Map<String, PermissionHolder> map) {
			if(set.contains(type) == true) return true;
			if(sub != null) {
				PermissionHolder subholder = map.get(sub);
				return subholder == null ? false : subholder.is(type, map);
			}
			return false;
		}
		
	}
	
	private static void log(String s) {
		Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_PURPLE + "[" + ChatColor.LIGHT_PURPLE + "SM" + ChatColor.DARK_PURPLE + "] "
				+ ChatColor.RED + s);
	}
	
	private static void initializeConfig() {
		lf = new File(SpawnerMeta.instance().getDataFolder(), "shop.yml");
		if(lf.getParentFile().exists() == false) lf.getParentFile().mkdirs();
		if(lf.exists() == true) file = YamlConfiguration.loadConfiguration(lf);
		else {
			try {
				lf.createNewFile();
			} catch(IOException e) {}
			file = YamlConfiguration.loadConfiguration(lf);
		}
		Map<Integer, SpawnerType[]> map = new HashMap<>();
		map.put(500, new SpawnerType[] {
				SpawnerType.AXOLOTL, SpawnerType.BAT, SpawnerType.BEE, SpawnerType.CAT, SpawnerType.CHICKEN, SpawnerType.COD,
				SpawnerType.COW, SpawnerType.DOLPHIN, SpawnerType.DONKEY, SpawnerType.ENDERMITE, SpawnerType.FOX,
				SpawnerType.GLOW_SQUID, SpawnerType.GOAT, SpawnerType.HOGLIN, SpawnerType.HORSE, SpawnerType.LLAMA,
				SpawnerType.MULE, SpawnerType.MUSHROOM_COW, SpawnerType.OCELOT, SpawnerType.PANDA, SpawnerType.PARROT, SpawnerType.PIG,
				SpawnerType.POLAR_BEAR, SpawnerType.PUFFERFISH, SpawnerType.RABBIT, SpawnerType.SALMON, SpawnerType.SHEEP, SpawnerType.SILVERFISH,
				SpawnerType.SQUID, SpawnerType.STRIDER, SpawnerType.TROPICAL_FISH, SpawnerType.TURTLE, SpawnerType.WOLF, SpawnerType.ZOGLIN
		});
		map.put(1000, new SpawnerType[] {
				SpawnerType.BLAZE, SpawnerType.CAVE_SPIDER, SpawnerType.CREEPER, SpawnerType.DROWNED, SpawnerType.ENDERMAN, SpawnerType.EVOKER,
				SpawnerType.GHAST, SpawnerType.GUARDIAN, SpawnerType.HUSK, SpawnerType.IRON_GOLEM, SpawnerType.MAGMA_CUBE, SpawnerType.PHANTOM,
				SpawnerType.PIG_ZOMBIE,
				SpawnerType.PIGLIN, SpawnerType.PIGLIN_BRUTE, SpawnerType.PILLAGER, SpawnerType.RAVAGER, SpawnerType.SHULKER, SpawnerType.SKELETON,
				SpawnerType.SLIME, SpawnerType.SPIDER, SpawnerType.STRAY, SpawnerType.VEX, SpawnerType.VINDICATOR, SpawnerType.WITCH,
				SpawnerType.WITHER_SKELETON, SpawnerType.ZOMBIE, SpawnerType.ZOMBIFIED_PIGLIN
		});
		map.forEach((i, ts) -> {
			for(SpawnerType t : ts) {
				String old_path = "Shop." + t.name();
				String new_path = "Shop.Buy." + t.name();
				if(file.isBoolean(old_path + ".Toggle") == true) {
					boolean b = file.getBoolean(old_path + ".Toggle");
					file.set(new_path + ".Toggle", b);
					file.set(old_path + ".Toggle", null);
				} else file.addDefault(new_path + ".Toggle", true);
				if(file.isInt(old_path + ".Cost") == true) {
					int c = file.getInt(old_path + ".Cost");
					file.set(new_path + ".Cost", c);
					file.set(old_path + ".Cost", null);
				} else file.addDefault(new_path + ".Cost", i);
				file.set("Shop." + t.name(), null);
			}
		});
		file.addDefault("Settings.Buy.Enabled", true);
		if(file.isInt("Rows") == true) {
			int r = file.getInt("Rows");
			file.set("Settings.Buy.Rows", r);
			file.set("Rows", null);
		} else file.addDefault("Settings.Buy.Rows", 4);
		if(file.isString("Filler") == true) {
			String s = file.getString("Filler");
			file.set("Settings.Buy.Filler", s);
			file.set("Filler", null);
		} else file.addDefault("Settings.Buy.Filler", Material.PURPLE_STAINED_GLASS_PANE.name());
		
		Object o;
		
		o = file.get("Settings.Buy.Buyable.BUY_1");
		if(o != null) {
			file.set("Settings.Buy.Buyable.First", o);
			file.set("Settings.Buy.Buyable.BUY_1", null);
		} else file.addDefault("Settings.Buy.Buyable.First", true);

		o = file.get("Settings.Buy.Buyable.BUY_4");
		if(o != null) {
			file.set("Settings.Buy.Buyable.Second", o);
			file.set("Settings.Buy.Buyable.BUY_4", null);
		} else file.addDefault("Settings.Buy.Buyable.Second", true);

		o = file.get("Settings.Buy.Buyable.BUY_16");
		if(o != null) {
			file.set("Settings.Buy.Buyable.Third", o);
			file.set("Settings.Buy.Buyable.BUY_16", null);
		} else file.addDefault("Settings.Buy.Buyable.Third", true);

		o = file.get("Settings.Buy.Buyable.BUY_MAX");
		if(o != null) {
			file.set("Settings.Buy.Buyable.Maximum", o);
			file.set("Settings.Buy.Buyable.BUY_MAX", null);
		} else file.addDefault("Settings.Buy.Buyable.Maximum", true);
		
		file.addDefault("Settings.Buy.Amount.First", 1);
		file.addDefault("Settings.Buy.Amount.Second", 4);
		file.addDefault("Settings.Buy.Amount.Third", 16);
		file.addDefault("Settings.Buy.Items.Next", Material.SPECTRAL_ARROW.name());
		file.addDefault("Settings.Buy.Items.Previous", Material.SPECTRAL_ARROW.name());
		file.addDefault("Settings.Buy.Items.Page", Material.PAPER.name());
		
		map.clear();
		map.put(250, new SpawnerType[] {
				SpawnerType.AXOLOTL, SpawnerType.BAT, SpawnerType.BEE, SpawnerType.CAT, SpawnerType.CHICKEN, SpawnerType.COD,
				SpawnerType.COW, SpawnerType.DOLPHIN, SpawnerType.DONKEY, SpawnerType.ENDERMITE, SpawnerType.FOX,
				SpawnerType.GLOW_SQUID, SpawnerType.GOAT, SpawnerType.HOGLIN, SpawnerType.HORSE, SpawnerType.LLAMA,
				SpawnerType.MULE, SpawnerType.MUSHROOM_COW, SpawnerType.OCELOT, SpawnerType.PANDA, SpawnerType.PARROT, SpawnerType.PIG,
				SpawnerType.POLAR_BEAR, SpawnerType.PUFFERFISH, SpawnerType.RABBIT, SpawnerType.SALMON, SpawnerType.SHEEP, SpawnerType.SILVERFISH,
				SpawnerType.SQUID, SpawnerType.STRIDER, SpawnerType.TROPICAL_FISH, SpawnerType.TURTLE, SpawnerType.WOLF, SpawnerType.ZOGLIN
		});
		map.put(500, new SpawnerType[] {
				SpawnerType.BLAZE, SpawnerType.CAVE_SPIDER, SpawnerType.CREEPER, SpawnerType.DROWNED, SpawnerType.ENDERMAN, SpawnerType.EVOKER,
				SpawnerType.GHAST, SpawnerType.GUARDIAN, SpawnerType.HUSK, SpawnerType.IRON_GOLEM, SpawnerType.MAGMA_CUBE, SpawnerType.PHANTOM,
				SpawnerType.PIG_ZOMBIE,
				SpawnerType.PIGLIN, SpawnerType.PIGLIN_BRUTE, SpawnerType.PILLAGER, SpawnerType.RAVAGER, SpawnerType.SHULKER, SpawnerType.SKELETON,
				SpawnerType.SLIME, SpawnerType.SPIDER, SpawnerType.STRAY, SpawnerType.VEX, SpawnerType.VINDICATOR, SpawnerType.WITCH,
				SpawnerType.WITHER_SKELETON, SpawnerType.ZOMBIE, SpawnerType.ZOMBIFIED_PIGLIN
		});
		file.addDefault("Settings.Buy.Order", new ArrayList<>());
		map.forEach((i, ts) -> {
			String path;
			for(SpawnerType t : ts) {
				path = "Shop.Sell." + t.name();
				file.addDefault(path + ".Toggle", true);
				file.addDefault(path + ".Refund", i);
				file.addDefault(path + ".Upgrades", 0.5);
			}
		});
		file.addDefault("Settings.Sell.Enabled", false);
		file.addDefault("Settings.Sell.Filler", Material.LIME_STAINED_GLASS_PANE.name());
		file.addDefault("Settings.Sell.Sell", Material.EMERALD.name());
		file.addDefault("Settings.Sell.Close", Material.REDSTONE.name());
		file.addDefault("Settings.Sell.Rows", 4);
		file.addDefault("Settings.Selection.Filler", Material.LIGHT_BLUE_STAINED_GLASS_PANE.name());
		file.addDefault("Settings.Selection.Buy", Material.GOLD_BLOCK.name());
		file.addDefault("Settings.Selection.Sell", Material.EMERALD_BLOCK.name());
		file.addDefault("Permissions.buy", new ArrayList<>());
		file.addDefault("Permissions.sell", new ArrayList<>());
		file.options().copyDefaults(true);
		
		Commenter c = commenter();
		if(c != null) {
			c.comment("Shop.Sell",
					"There are 3 options for each entity type:",
					"  Toggle - can this entity spawner be sold",
					"  Refund - the price that is refunded for the spawner",
					"  Upgrades - refunded percentage of upgrade value ( 0.5 -> 50% )");
			c.comment("Permissions.buy",
					"Shop buy permissions can be created:",
					"  <permission name>:",
					"    # List of allowed entities.",
					"    # Use ALL to allow all entities.",
					"    entities:",
					"    - PIG",
					"    - ...",
					"    # Previous permission, this permission will",
					"    # include all sub-permission entities.",
					"    sub-permission: <permission name>",
					"",
					"Final permission name - spawnermeta.shop.buy.permission.<permission name>");
			c.comment("Permissions.sell",
					"Shop sell permissions can be created:",
					"  <permission name>:",
					"    # List of allowed entities.",
					"    # Use ALL to allow all entities.",
					"    entities:",
					"    - PIG",
					"    - ...",
					"    # Previous permission, this permission will",
					"    # include all sub-permission entities.",
					"    sub-permission: <permission name>",
					"",
					"Final permission name - spawnermeta.shop.sell.permission.<permission name>");
		}
		
		save();
	}
	
	protected static Commenter commenter() {
		return Version.version.high(VersionType.v_18_1) == true
				? new Commenter() : null;
	}
	
	protected static class Commenter {
		
		protected void comment(String path, String... cs) {
			RF.order(file, "setComments", String.class, List.class)
				.invoke(path, List.of(cs));
		}
		
	}

	public static void save() {
		try {
			file.save(lf);
		} catch(IOException e) {}
	}
	
}
