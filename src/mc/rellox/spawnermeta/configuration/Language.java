package mc.rellox.spawnermeta.configuration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import mc.rellox.spawnermeta.SpawnerMeta;
import mc.rellox.spawnermeta.spawner.SpawnerType;
import mc.rellox.spawnermeta.text.Text;
import mc.rellox.spawnermeta.text.content.Content;
import mc.rellox.spawnermeta.text.content.Content.Variables;
import mc.rellox.spawnermeta.text.content.ContentParser;

public final class Language {
	
	private static final Language language = new Language();
	
	public static void initialize() {
		language.load();
	}
	
	private File f;
	protected FileConfiguration file;
	
	private final Map<String, List<Content>> text = new HashMap<>();
	private final List<String> keys = new ArrayList<>();
	
	@SuppressWarnings("deprecation")
	private void load() {
		f = new File(SpawnerMeta.instance().getDataFolder(), "language.yml");
		if(f.getParentFile().exists() == false) f.getParentFile().mkdirs();
		if(f.exists() == false) {
			try {
				f.createNewFile();
			} catch(IOException e) {}
		}
		file = YamlConfiguration.loadConfiguration(f);
		
		convertLegacy();
		
		text.clear();
		put("Inventory.upgrades.purchase.range", "<#00ff00>(!) <#80ff00>Upgraded <#00ffff-#008080><!italic>range <#80ff00>to level %level%");
		put("Inventory.upgrades.purchase.delay", "<#00ff00>(!) <#80ff00>Upgraded <#ffff00-#ff8000><!italic>delay <#80ff00>to level %level%");
		put("Inventory.upgrades.purchase.amount", "<#00ff00>(!) <#80ff00>Upgraded <#ff00ff-#800080><!italic>amount <#80ff00>to level %level%");
		put("Inventory.upgrades.name", "Spawner");
		put("Inventory.upgrades.items.upgrade.name.range", "<#00ffff-#008080>-= Range %level% =-");
		put("Inventory.upgrades.items.upgrade.name.delay", "<#ffff00-#ff8000>-= Delay %level% =-");
		put("Inventory.upgrades.items.upgrade.name.amount", "<#ff00ff-#800080>-= Amount %level% =-");
		put("Inventory.upgrades.items.upgrade.help", "<#808080><!italic>Click to upgrade!");
		put("Inventory.upgrades.items.upgrade.info.range", List.of());
		put("Inventory.upgrades.items.upgrade.info.delay", List.of());
		put("Inventory.upgrades.items.upgrade.info.amount", List.of());
		put("Inventory.upgrades.items.upgrade.current.range", "<#bfbfbf>Current range: <#80ffff-#00ffff><!italic>%value% Blocks");
		put("Inventory.upgrades.items.upgrade.current.delay", "<#bfbfbf>Current delay: <#ffff80-#ffff00><!italic>%value% Seconds");
		put("Inventory.upgrades.items.upgrade.current.amount", "<#bfbfbf>Current amount: <#ff80ff-#ff00ff><!italic>%value% Entities");
		put("Inventory.upgrades.items.upgrade.next.range", "<#bfbfbf>Next range: <#80ffff-#00ffff><!italic>%value% Blocks");
		put("Inventory.upgrades.items.upgrade.next.delay", "<#bfbfbf>Next delay: <#ffff80-#ffff00><!italic>%value% Seconds");
		put("Inventory.upgrades.items.upgrade.next.amount", "<#bfbfbf>Next amount: <#ff80ff-#ff00ff><!italic>%value% Entities");
		put("Inventory.upgrades.items.upgrade.maximum-reached", "<#008000>Maximum level has been reached!");
		put("Inventory.upgrades.items.upgrade.price", "<#ffffff>Price: <#00bf00><!italic>%price%");
		put("Inventory.upgrades.items.disabled-upgrade.name.range", "<#00ffff-#008080>-= Range =-");
		put("Inventory.upgrades.items.disabled-upgrade.name.delay", "<#ffff00-#ff8000>-= Delay =-");
		put("Inventory.upgrades.items.disabled-upgrade.name.amount", "<#ff00ff-#800080>-= Amount =-");
		put("Inventory.upgrades.items.disabled-upgrade.help", "<#800000>Cannot be upgraded!");
		put("Inventory.upgrades.items.disabled-upgrade.current.range", "<#bfbfbf><!italic>Current range: <#80ffff-#00ffff>%value% Blocks");
		put("Inventory.upgrades.items.disabled-upgrade.current.delay", "<#bfbfbf><!italic>Current delay: <#ffff80-#ffff00>%value% Seconds");
		put("Inventory.upgrades.items.disabled-upgrade.current.amount", "<#bfbfbf><!italic>Current amount: <#ff80ff-#ff00ff>%value% Entities");
		put("Inventory.upgrades.items.stats.name", "<#bfffff-#00ffff>-= %type% Spawner =-");
		put("Inventory.upgrades.items.stats.disabled", "<#ff0000>DISABLED <#bfbfbf><!italic>(Click to enable)");
		put("Inventory.upgrades.items.stats.enabled", "<#00ff00>ENABLED <#bfbfbf><!italic>(Click to disable)");
		put("Inventory.upgrades.items.stats.empty", "<#bfbfbf>Shift-right-click on this spawner to empty it.");
		put("Inventory.upgrades.items.stats.location", "<#bfbfbf>Location: <#ff0000>%x%<#808080>, <#ff0000>%y%<#808080>, <#ff0000>%z%");
		put("Inventory.upgrades.items.stats.stacking.infinite", "<#bfbfbf>Stacked: <#bfff00>%stack% Spawner");
		put("Inventory.upgrades.items.stats.stacking.finite", "<#bfbfbf>Stacked: <#bfff00>%stack%/%limit% Spawners");
		put("Inventory.upgrades.items.stats.spawnable", "<#bfbfbf>Spawnable Entities: <#ffff00-#ffbf00>%spawnable%");
		put("Inventory.upgrades.items.stats.charges.insufficient", "<#800000>Unable to spawn! <#bfbfbf>(Empty charges)");
		put("Inventory.upgrades.items.charges.name", "<#ff0080-#ff0000>Spawning Charges: <#00ffff>%charges%");
		put("Inventory.upgrades.items.charges.purchase.first", "<#bfbfbf>Left-Click to purchase <#ffff00>%charges% charges <#808080><!italic>(%price%)");
		put("Inventory.upgrades.items.charges.purchase.second", "<#bfbfbf>Right-Click to purchase <#ff8000>%charges% charges <#808080><!italic>(%price%)");
		put("Inventory.upgrades.items.charges.purchase.all", "<#bfbfbf>Shift-Click to purchase <#ff0000>%charges% charges <#808080><!italic>(%price%)");
		put("Inventory.upgrades.charges.purchase", "<#00ff00>(!) <#00ffff>You bought %charges% spawner charges");
		put("Inventory.upgrades.disabled-upgrade", "<#800000>(!) <#ff8000>You cannot upgrade this!");
		put("Spawners.item.regular.name", "<#bfffff-#00ffff>Spawner <#ffff00-#ffaa00>(%type%)");
		put("Spawners.item.empty.name", "<#ff8000><Empty> <#bfffff-#00ffff>Spawner");
		put("Spawners.item.empty-stored.name", "<#ff8000><Empty : %type%> <#bfffff-#00ffff>Spawner");
		put("Spawners.item.header", "<#ffffff>Upgrades:");
		put("Spawners.item.upgrade.range", "<#808080>- <#00ffff-#008080><!italic>Range <#00ffff>%level%");
		put("Spawners.item.upgrade.delay", "<#808080>- <#ffff00-#ff8000><!italic>Delay <#00ffff>%level%");
		put("Spawners.item.upgrade.amount", "<#808080>- <#ff00ff-#800080><!italic>Amount <#00ffff>%level%");
		put("Spawners.item.charges", "<#ff0080-#ff0000>Charges: <#00ffff>%charges%");
		put("Spawners.item.spawnable", "<#ffff00-#ff8000>Spawnable Entities: <#00ffff>%spawnable%");
		put("Spawners.item.info", List.of());
		put("Inventory.spawner-view.name", "<#000000>All Spawners");
		put("Inventory.spawner-view.items.name", "<#ffff00>-=[ <#00ffff>%type% Spawner<#ffff00> ]=-");
		put("Inventory.spawner-view.items.price", "<#808080>- <#bfbfbf>Price: <#ffffff><!italic>%price%");
		put("Inventory.spawner-view.items.price-increase", "<#808080>- <#bfbfbf>Price Increase: <#ffffff><!italic>%increase%");
		put("Inventory.spawner-view.items.maximum-level", "<#808080>- <#bfbfbf>Maximum Level: <#ffffff><!italic>%level%");
		put("Inventory.spawner-view.items.spawnable", "<#bfbfbf>Spawnable Entities: <#ffff00-#ff8000>%spawnable%");
		put("Inventory.spawner-view.items.page.current", "<#00ffff>Page %page%");
		put("Inventory.spawner-view.items.page.next", "<#ff8000>Next Page");
		put("Inventory.spawner-view.items.page.previous", "<#ff8000>Previous Page");
		put("Inventory.spawner-view.items.page.previous", "<#ff8000>Previous Page");
		put("Inventory.spawner-view.permission", "<#ff8000>Previous Page");
		put("Prices.experience.insufficient", "Not enough experience!");
		put("Prices.type.experience.amount", "%amount% Experience");
		put("Prices.type.levels.insufficient", "Not enough experience levels!");
		put("Prices.type.levels.amount", "%amount% Experience Levels");
		put("Prices.type.material.insufficient", "Not enough materials!");
		put("Prices.type.material.amount", "%amount% × %material%");
		put("Prices.type.economy.insufficient", "Insufficient funds!");
		put("Prices.type.economy.amount", "$%amount%");
		put("Prices.insufficient", "<#800000>(!) <#ff8000>%insufficient% <#bfbfbf>[Missing %price%]");
		put("Spawners.placing.permission", "<#800000>(!) <#ff8000>You do not have a permission to place this!");
		put("Spawners.breaking.success", "<#00ff00>(!) <#00ffff>Spawner successfully mined!");
		put("Spawners.breaking.failure", "<#800000>(!) <#008080>Spawner failed to mine!");
		put("Spawners.breaking.permission", "<#800000>(!) <#ff8000>You do not have a permission to break this!");
		put("Spawners.hologram.empty.single", "<#ff8000><Empty> <#bfffff-#00ffff>Spawner");
		put("Spawners.hologram.empty.multiple", "<#ffff00>%stack% <#bfbfbf>× <#ff8000><Empty> <#bfffff-#00ffff>Spawner");
		put("Spawners.hologram.regular.single", "<#bfffff-#00ffff>%name% Spawner");
		put("Spawners.hologram.regular.multiple", "<#ffff00>%stack% <#bfbfbf>× <#bfffff-#00ffff>%name% Spawner");
		put("Inventory.buy-shop.name", "<#000000>Spawner Shop <#808080>(<#ff8000>%page_current%<#808080>/<#ff8000>%page_total%<#808080>)");
		put("Inventory.buy-shop.items.page.current", "<#00ffff>Page %page%");
		put("Inventory.buy-shop.items.page.next", "<#ff8000>Next Page");
		put("Inventory.buy-shop.items.page.previous", "<#ff8000>Previous Page");
		put("Inventory.buy-shop.items.spawner.name", "<#bfffff-#00ffff>Spawner <#ffff00-#ffaa00>(%type%)");
		put("Inventory.buy-shop.items.spawner.price", "<#ffffff>Price: <#00bf00><!italic>%price%");
		put("Inventory.buy-shop.items.spawner.purchase.first", "<#bfbfbf>Left-click to purchase %amount%");
		put("Inventory.buy-shop.items.spawner.purchase.second", "<#bfbfbf>Right-click to purchase %amount%");
		put("Inventory.buy-shop.items.spawner.purchase.third", "<#bfbfbf>Shift-left-click to purchase %amount%");
		put("Inventory.buy-shop.items.spawner.purchase.all", "<#bfbfbf>Shift-right-click to purchase maximum");
		put("Inventory.buy-shop.purchase.success", "<#008000>(!) <#00ffff>Purchased <#ffff00-#ffaa00>%amount% × %type%<#00ffff> Spawner(s)!");
		put("Inventory.buy-shop.permission.opening", "<#800000>(!) <#ff8000>You do not have a permission to open this!");
		put("Inventory.buy-shop.permission.purchase", "<#800000>(!) <#ff8000>You do not have a permission to purchase this!");
		put("Inventory.sell-shop.name", "<#000000>Spawners Selling");
		put("Inventory.sell-shop.accept", "<#00ff00>Sell");
		put("Inventory.sell-shop.cancel", "<#ff0000>Close");
		put("Inventory.sell-shop.items.selling.name", "<#00ffff>Selling for:");
		put("Inventory.sell-shop.items.selling.price", "<#bfbfbf>- <#00bf00>%price%");
		put("Inventory.sell-shop.selling.success", "<#008000>(!) <#00ffff>Successfully sold spawners for:");
		put("Inventory.sell-shop.selling.empty", "<#800000>(!) <#ff8000>Nothing to sell!");
		put("Inventory.sell-shop.selling.unable", "<#800000>(!) <#ff8000>Unable to sell that!");
		put("Inventory.sell-shop.disabled", "<#800000>(!) <#008080>Spawner shop has been disabled!");
		put("Inventory.sell-shop.permission.opening", "<#800000>(!) <#ff8000>You do not have a permission to open this!");
		put("Inventory.sell-shop.permission.selling", "<#800000>(!) <#ff8000>You do not have a permission to sell this!");
		put("Inventory.select-shop.name", "<#000000>Spawners Selling");
		put("Inventory.select-shop.buy-shop", "<#00ffff>Click to purchase spawners");
		put("Inventory.select-shop.sell-shop", "<#00ffff>Click to sell spawners");
		put("Inventory.select-shop.permission.opening", "<#800000>(!) <#ff8000>You do not have a permission to open this!");
		put("Inventory.insufficient-space", "<#800000>(!) <#ff8000>You do not have enough space in your inventory!");
		put("Items.spawner-drop.alert", "<#00ffff>You have <#ffff00>%seconds% seconds <#00ffff>to take your spawner items! <#ffffff>(click or /spawnerdrops)");
		put("Items.spawner-drop.cleared", "<#800000>(!) <#ff8000>Your spawner drops disappeared, was not taken in time!");
		put("Items.spawner-drop.try-breaking", "<#800000>(!) <#ff8000>Cannot break spawners while you have not taken previously dropped items!");
		put("Items.spawner-drop.empty", "<#800000>(!) <#ff8000>No items to give!");
		put("Spawners.stacking.stacked.infinite", "<#008000>(!) <#00ffff>Spawners have been stacked! <#ffff00>(%stack% Stacked)");
		put("Spawners.stacking.stacked.finite", "<#008000>(!) <#00ffff>Spawners have been stacked! <#ffff00>(%stack%/%limit% Stacked)");
		put("Spawners.stacking.unequal-spawner", "<#800000>(!) <#ff8000>Spawners must be the same to stack!");
		put("Spawners.stacking.limit-reached", "<#800000>(!) <#ff8000>This spawner has reached its stacking limit!");
		put("Spawners.stacking.nearby.none-match", "<#800000>(!) <#ff8000>Unable to find any nearby spawner to stack to that matches!");
		put("Spawners.stacking.permission", "<#800000>(!) <#ff8000>You do not have a permission to stack this!");
		put("Spawners.chunks.limit-reached", "<#800000>(!) <#ff8000>This chunk has reached its spawner limit!");
		put("Spawners.ownership.limit.place", "<#008000>(!) <#00ffff>Spawner placed <#ff8000>(<#ffff00>%placed%<#ff8000>/<#ffff00>%limit%<#ff8000>)");
		put("Spawners.ownership.limit.reached", "<#800000>(!) <#ff8000>You have reach your spawner limit! <#bfbfbf>(%limit%)");
		put("Spawners.ownership.stacking.warning", "<#800000>(!) <#ff8000>You cannot stack a spawner that you do not own!");
		put("Spawners.ownership.breaking.warning", "<#800000>(!) <#ff8000>You cannot break a spawner that you do not own!");
		put("Spawners.ownership.upgrading.warning", "<#800000>(!) <#ff8000>You cannot upgrade a spawner that you do not own!");
		put("Spawners.ownership.opening.warning", "<#800000>(!) <#ff8000>You cannot open a spawner that you do not own!");
		put("Spawners.ownership.changing.warning", "<#800000>(!) <#ff8000>You cannot change a spawner that you do not own!");
		put("Spawners.ownership.show-owner", "<#008000>(!) <#ffff00>This spawner is owner by <#00ffff>%player%");
		put("Spawners.natural.changing.warning", "<#800000>(!) <#ff8000>You cannot change a natural spawner!");
		put("Spawners.natural.breaking.warning", "<#800000>(!) <#ff8000>You cannot break a natural spawner!");
		put("Spawners.natural.stacking.warning", "<#800000>(!) <#ff8000>You cannot stack a natural spawner!");
		put("Spawners.natural.opening.warning", "<#800000>(!) <#ff8000>You cannot open a natural spawner!");
		put("Spawners.natural.upgrading.warning", "<#800000>(!) <#ff8000>You cannot upgrade a natural spawner!");
		put("Spawners.changing.type-changed", "<#008000>(!) <#00ffff>Spawner type set to <#ffff00>%type%");
		put("Spawners.changing.same-type", "<#800000>(!) <#ff8000>You cannot set the same entity type!");
		put("Spawners.changing.permission", "<#800000>(!) <#ff8000>You do not have a permission to use this!");
		put("Spawners.changing.eggs.insufficient", "<#800000>(!) <#ff8000>Not enough spawn eggs <#bfbfbf>(Requires %required%)");
		put("Spawners.charges.lose-by-stacking", "<#800000>(!) <#ff8000>Lost <#ffff00>%charges% charge(s) <#ff8000>when stacking!");
		put("Spawners.upgrades.disabled", "<#800000>(!) <#ff8000>You cannot upgrade this!");
		put("Spawners.upgrades.permission.opening", "<#800000>(!) <#ff8000>You do not have a permission to open this!");
		put("Spawners.upgrades.permission.purchase", "<#800000>(!) <#ff8000>You do not have a permission to upgrade this!");
		put("Spawners.empty.disabled", "<#800000>(!) <#008080>Empty spawners are disabled!");
		put("Spawners.empty.try-open", "<#800000>(!) <#008080>Cannot open empty spawners!");
		put("Spawners.empty.hand-full", "<#800000>(!) <#008080>You must have an empty hand to remove spawner egg(s)!");
		put("Spawners.empty.verify-removing.first", "<#ff8000>(!) <#ffff00>Left click to verify removing eggs from this spawner!");
		put("Spawners.empty.verify-removing.try-again", "<#800000>(!) <#008080>You first have to sneak and right click the empty spawner!");
		put("Spawners.view.empty", "<#800000>(!) <#008080>Nothing to view!");
		put("Spawners.view.disabled", "<#800000>(!) <#008080>Spawner viewing is disabled!");
		put("Inventory.upgrades.items.stats.lore", List.of());
		put("Spawners.give.success", "<#008000>(!) <#008080>Added <#00ffff>%amount% <#008080>× <#00ffff>%type% Spawner <#008080>to your inventory!");
		
		Stream.of(SpawnerType.values())
			.filter(SpawnerType::regular)
			.forEach(type -> put("Entities.name." + type.name(), type.text().text()));
		
		file.options().copyDefaults(true);
		
		file.options().header("""
				
				Language file has been updated!
				
				Legacy colors (&a&1&b...) are no longer available.
				
				New text formatting:
				
				Color format:
				  <#123abc>
				  <#ABC987>
				  ...
				
				Gradient format:
				  <#ff0000-#00ff00>
				  <#ff0000-#ffff00-#00ff00>
				  ...
				
				Modifier format:
				  bold - <!bold> or <!b>
				  italic - <!italic> or <!i>
				  underline - <!underline> or <!u>
				  strikethrough - <!strikethrough> or <!s>
				  obfuscated - <!obfuscated> or <!o>
				  
				If you find any errors or bugs, or any text shows
				  incorrectly then be sure to report it.
				
				""");
		
		save();
		
		read();

		file = null; f = null;
	}
	
	private void put(String path, Object value) {
		file.addDefault(path, value);
		keys.add(path);
	}

	private void read() {
		keys.forEach(key -> {
			List<Content> list;
			if(file.isString(key) == true) {
				String s = file.getString(key);
				if(s == null || s.isEmpty() == true) list = of();
				else list = of(ContentParser.parse(s));
			}
			else list = ContentParser.parse(file.getStringList(key));
			if(list == null || list.isEmpty() == true) return; 
			text.put(key, list);
		});
		keys.clear();
	}
	
	private static List<Content> of(Content... cs) {
		return cs == null ? new ArrayList<>()
				: Stream.of(cs).collect(Collectors.toList());
	}
	
//	private void replace(String from, String to) {
//		file.set(to, file.get(from));
//		file.set(from, null);
//	}
	
	public static Content get(String key) {
		List<Content> list = language.text.get(key);
		return list == null ? Content.empty() : list.get(0);
	}

	public static Content get(String key, String k, Object o) {
		List<Content> list = language.text.get(key);
		if(list == null) return Content.empty();
		return list.get(0).modified(Variables.with(k, o));
	}

	public static Content get(String key, Object... vs) {
		List<Content> list = language.text.get(key);
		if(list == null) return Content.empty();
		return list.get(0).modified(Variables.with(vs));
	}

	public static List<Content> list(String key) {
		List<Content> list = language.text.get(key);
		return list == null ? of() : list;
	}

	public static List<Content> list(String key, String k, Object o) {
		List<Content> list = language.text.get(key);
		if(list == null) return of(); 
		Variables v = Variables.with(k, o);
		return list.stream()
				.map(c -> c.modified(v))
				.collect(Collectors.toList());
	}

	public static List<Content> list(String key, Object... vs) {
		List<Content> list = language.text.get(key);
		if(list == null) return of(); 
		Variables v = Variables.with(vs);
		return list.stream()
				.map(c -> c.modified(v))
				.collect(Collectors.toList());
	}
	
	public static Content or(String key, Content text) {
		List<Content> list = language.text.get(key);
		return list == null ? text : list.get(0);
	}
	
	private void convertLegacy() {
		File lf = new File(SpawnerMeta.instance().getDataFolder(), "lang.yml");
		if(lf.exists() == false) return;
		FileConfiguration legacy = YamlConfiguration.loadConfiguration(lf);
		
		Mover m = new Mover(legacy, file);
		
		m.with("Upgrades.Upgrade", "Inventory.upgrades.purchase.range",
				s -> s.replace("%color_light%", "<#00ffff>").replace("%color_dark%", "<#008080>")
				.replace("%upgrade_name%", m.get("Main.Name.Range"))
				.replace("%upgrade_level%", "%level%"));
		m.with("Upgrades.Upgrade", "Inventory.upgrades.purchase.delay",
				s -> s.replace("%color_light%", "<#ffff00>").replace("%color_dark%", "<#ff8000>")
				.replace("%upgrade_name%", m.get("Main.Name.Delay"))
				.replace("%upgrade_level%", "%level%"));
		m.with("Upgrades.Upgrade", "Inventory.upgrades.purchase.amount",
				s -> s.replace("%color_light%", "<#ff00ff>").replace("%color_dark%", "<#800080>")
				.replace("%upgrade_name%", m.get("Main.Name.Amount"))
				.replace("%upgrade_level%", "%level%"));
		m.string("Upgrades.InventoryName", "Inventory.upgrades.name");
		m.with("Upgrades.UpgradeItem.Name", "Inventory.upgrades.items.upgrade.name.range",
				s -> s.replace("%color_light%", "<#00ffff>").replace("%color_dark%", "<#008080>")
				.replace("%upgrade_name%", m.get("Main.Name.Range"))
				.replace("%upgrade_level%", "%level%"));
		m.with("Upgrades.UpgradeItem.Name", "Inventory.upgrades.items.upgrade.name.delay",
				s -> s.replace("%color_light%", "<#ffff00>").replace("%color_dark%", "<#ff8000>")
				.replace("%upgrade_name%", m.get("Main.Name.Delay"))
				.replace("%upgrade_level%", "%level%"));
		m.with("Upgrades.UpgradeItem.Name", "Inventory.upgrades.items.upgrade.name.amount",
				s -> s.replace("%color_light%", "<#ff00ff>").replace("%color_dark%", "<#800080>")
				.replace("%upgrade_name%", m.get("Main.Name.Amount"))
				.replace("%upgrade_level%", "%level%"));
		m.string("Upgrades.UpgradeItem.Lore.Info", "Inventory.upgrades.items.upgrade.info");
		m.with("Upgrades.UpgradeItem.Lore.Current", "Inventory.upgrades.items.upgrade.current.range",
				s -> s.replace("%color_light%", "<#00ffff>").replace("%color_dark%", "<#008080>")
				.replace("%upgrade_name%", m.get("Main.Name.Range"))
				.replace("%measurement%", m.get("Main.Measurement.Range"))
				.replace("%upgrade_value%", "%value%"));
		m.with("Upgrades.UpgradeItem.Lore.Current", "Inventory.upgrades.items.upgrade.current.delay",
				s -> s.replace("%color_light%", "<#ffff00>").replace("%color_dark%", "<#ff8000>")
				.replace("%upgrade_name%", m.get("Main.Name.Delay"))
				.replace("%measurement%", m.get("Main.Measurement.Delay"))
				.replace("%upgrade_value%", "%value%"));
		m.with("Upgrades.UpgradeItem.Lore.Current", "Inventory.upgrades.items.upgrade.current.amount",
				s -> s.replace("%color_light%", "<#ff00ff>").replace("%color_dark%", "<#800080>")
				.replace("%upgrade_name%", m.get("Main.Name.Amount"))
				.replace("%measurement%", m.get("Main.Measurement.Amount"))
				.replace("%upgrade_value%", "%value%"));
		m.with("Upgrades.UpgradeItem.Lore.Next", "Inventory.upgrades.items.upgrade.next.range",
				s -> s.replace("%color_light%", "<#00ffff>").replace("%color_dark%", "<#008080>")
				.replace("%upgrade_name%", m.get("Main.Name.Range"))
				.replace("%measurement%", m.get("Main.Measurement.Range"))
				.replace("%upgrade_value%", "%value%"));
		m.with("Upgrades.UpgradeItem.Lore.Next", "Inventory.upgrades.items.upgrade.next.delay",
				s -> s.replace("%color_light%", "<#ffff00>").replace("%color_dark%", "<#ff8000>")
				.replace("%upgrade_name%", m.get("Main.Name.Delay"))
				.replace("%measurement%", m.get("Main.Measurement.Delay"))
				.replace("%upgrade_value%", "%value%"));
		m.with("Upgrades.UpgradeItem.Lore.Next", "Inventory.upgrades.items.upgrade.next.amount",
				s -> s.replace("%color_light%", "<#ff00ff>").replace("%color_dark%", "<#800080>")
				.replace("%upgrade_name%", m.get("Main.Name.Amount"))
				.replace("%measurement%", m.get("Main.Measurement.Amount"))
				.replace("%upgrade_value%", "%value%"));
		m.string("Upgrades.UpgradeItem.Lore.Maximum", "Inventory.upgrades.items.upgrade.maximum-reached");
		m.string("Upgrades.UpgradeItem.Lore.Price", "Inventory.upgrades.items.upgrade.price");
		m.with("Upgrades.DisabledItem.Name", "Inventory.upgrades.items.disabled-upgrade.name.range",
				s -> s.replace("%color_light%", "<#00ffff>").replace("%color_dark%", "<#008080>")
				.replace("%upgrade_name%", m.get("Main.Name.Range"))
				.replace("%upgrade_value%", "%value%"));
		m.with("Upgrades.DisabledItem.Name", "Inventory.upgrades.items.disabled-upgrade.name.delay",
				s -> s.replace("%color_light%", "<#ffff00>").replace("%color_dark%", "<#ff8000>")
				.replace("%upgrade_name%", m.get("Main.Name.Delay"))
				.replace("%upgrade_value%", "%value%"));
		m.with("Upgrades.DisabledItem.Name", "Inventory.upgrades.items.disabled-upgrade.name.amount",
				s -> s.replace("%color_light%", "<#ff00ff>").replace("%color_dark%", "<#800080>")
				.replace("%upgrade_name%", m.get("Main.Name.Amount"))
				.replace("%upgrade_value%", "%value%"));
		m.string("Upgrades.DisabledItem.Lore.Info", "Inventory.upgrades.items.disabled-upgrade.info");
		m.with("Upgrades.DisabledItem.Lore.Current", "Inventory.upgrades.items.disabled-upgrade.current.range",
				s -> s.replace("%color_light%", "<#00ffff>").replace("%color_dark%", "<#008080>")
				.replace("%upgrade_name%", m.get("Main.Name.Range"))
				.replace("%measurement%", m.get("Main.Measurement.Range"))
				.replace("%upgrade_value%", "%value%"));
		m.with("Upgrades.DisabledItem.Lore.Current", "Inventory.upgrades.items.disabled-upgrade.current.delay",
				s -> s.replace("%color_light%", "<#ffff00>").replace("%color_dark%", "<#ff8000>")
				.replace("%upgrade_name%", m.get("Main.Name.Delay"))
				.replace("%measurement%", m.get("Main.Measurement.Delay"))
				.replace("%upgrade_value%", "%value%"));
		m.with("Upgrades.DisabledItem.Lore.Current", "Inventory.upgrades.items.disabled-upgrade.current.amount",
				s -> s.replace("%color_light%", "<#ff00ff>").replace("%color_dark%", "<#800080>")
				.replace("%upgrade_name%", m.get("Main.Name.Amount"))
				.replace("%measurement%", m.get("Main.Measurement.Amount"))
				.replace("%upgrade_value%", "%value%"));
		m.string("Upgrades.StatsItem.Name", "Inventory.upgrades.items.stats.name");
		m.string("Upgrades.StatsItem.Lore.Disabled", "Inventory.upgrades.items.stats.disabled");
		m.string("Upgrades.StatsItem.Lore.Enabled", "Inventory.upgrades.items.stats.enabled");
		m.string("Upgrades.StatsItem.Lore.Empty", "Inventory.upgrades.items.stats.empty");
		m.with("Upgrades.StatsItem.Lore.Location", "Inventory.upgrades.items.stats.location",
				s -> s.replace("%loc_x%", "%x%").replace("%loc_y%", "%y%").replace("%loc_z%", "%z%"));
		m.with("Upgrades.StatsItem.Lore.Stacked.Infinite", "Inventory.upgrades.items.stats.stacking.infinite",
				s -> s.replace("%stacked%", "%stack%"));
		m.with("Upgrades.StatsItem.Lore.Stacked.Finite", "Inventory.upgrades.items.stats.stacking.finite",
				s -> s.replace("%stacked%", "%stack%"));
		m.string("Upgrades.StatsItem.Lore.Spawnable", "Inventory.upgrades.items.stats.spawnable");
		m.string("Upgrades.StatsItem.Lore.EmptyCharges", "Inventory.upgrades.items.stats.charges.insufficient");
		m.string("Upgrades.ChargesItem.Name", "Inventory.upgrades.items.charges.name");
		m.with("Upgrades.ChargesItem.Lore.Buy16", "Inventory.upgrades.items.charges.purchase.first",
				s -> s.replace(" 16 ", " %charges% ").replace(" buy ", " purchase "));
		m.with("Upgrades.ChargesItem.Lore.Buy128", "Inventory.upgrades.items.charges.purchase.second",
				s -> s.replace(" 128 ", " %charges% ").replace(" buy ", " purchase "));
		m.with("Upgrades.ChargesItem.Lore.BuyAll", "Inventory.upgrades.items.charges.purchase.all",
				s -> s.replace(" buy ", " purchase ").replace("Middle", "Shift").replace("middle", "shift"));
		m.string("Upgrades.Charges.Buy", "Inventory.upgrades.charges.purchase");
		m.string("Upgrades.DisabledUpgrade", "Inventory.upgrades.disabled-upgrade");
		m.string("Spawner.Item.Name", "Spawners.item.regular.name");
		m.string("Spawner.Item.EmptyName", "Spawners.item.empty.name");
		m.string("Spawner.Item.EmptyNameStored", "Spawners.item.empty-stored.name");
		m.string("Spawner.Item.Lore.Info", "Spawners.item.header");
		m.with("Spawner.Item.Lore.Upgrade", "Spawners.item.upgrade.range",
				s -> s.replace("%color_light%", "<#00ffff>").replace("%color_dark%", "<#008080>")
					.replace("%upgrade_name%", m.get("Main.Name.Range"))
					.replace("%upgrade_level%", "%level%"));
		m.with("Spawner.Item.Lore.Upgrade", "Spawners.item.upgrade.delay",
				s -> s.replace("%color_light%", "<#ffff00>").replace("%color_dark%", "<#ff8000>")
				.replace("%upgrade_name%", m.get("Main.Name.Delay"))
				.replace("%upgrade_level%", "%level%"));
		m.with("Spawner.Item.Lore.Upgrade", "Spawners.item.upgrade.amount",
				s -> s.replace("%color_light%", "<#ff00ff>").replace("%color_dark%", "<#800080>")
				.replace("%upgrade_name%", m.get("Main.Name.Amount"))
				.replace("%upgrade_level%", "%level%"));
		m.string("Spawner.Item.Lore.Charges", "Spawners.item.charges");
		m.string("Spawner.Item.Lore.Spawnable", "Spawners.item.spawnable");
		m.string("Spawner.View.Inventory.Name", "Inventory.spawner-view.name");
		m.string("Spawner.View.Spawner.Name", "Inventory.spawner-view.items.name");
		m.string("Spawner.View.Spawner.Lore.Price", "Inventory.spawner-view.items.price");
		m.string("Spawner.View.Spawner.Lore.Increase", "Inventory.spawner-view.items.price-increase");
		m.with("Spawner.View.Spawner.Lore.MaxLevel", "Inventory.spawner-view.items.maximum-level",
				s -> s.replace("%max_level%", "%level%"));
		m.string("Spawner.View.Spawner.Lore.Spawnable", "Inventory.spawner-view.items.spawnable");
		m.string("Spawner.View.Page.Current", "Inventory.spawner-view.items.page.current");
		m.string("Spawner.View.Page.Next", "Inventory.spawner-view.items.page.next");
		m.string("Spawner.View.Page.Previous", "Inventory.spawner-view.items.page.previous");
		m.string("Permission.Warn.View", "Inventory.spawner-view.permission");
		m.string("Price.Experience.NotEnough", "Prices.experience.insufficient");
		m.string("Price.Experience.Amount", "Prices.type.experience.amount");
		m.string("Price.Levels.NotEnough", "Prices.type.levels.insufficient");
		m.string("Price.Levels.Amount", "Prices.type.levels.amount");
		m.string("Price.Material.NotEnough", "Prices.type.material.insufficient");
		m.with("Price.Material.Amount", "Prices.type.material.amount",
				s -> s.replace("%material_name%", "%material%"));
		m.string("Price.Economy.NotEnough", "Prices.type.economy.insufficient");
		m.string("Price.Economy.Amount", "Prices.type.economy.amount");
		m.with("Upgrades.PriceNotEnough", "Prices.insufficient",
				s -> s.replace("%not_enough%", "%insufficient%"));
		m.string("Permission.Warn.Place", "Spawners.placing.permission");
		m.string("Spawner.Mine.Succeed", "Spawners.breaking.success");
		m.string("Spawner.Mine.Fail", "Spawners.breaking.failure");
		m.string("Permission.Warn.Break", "Spawners.breaking.permission");
		m.string("Spawner.Hologram.Single", "Spawners.hologram.regular.single");
		m.string("Spawner.Hologram.Multiple", "Spawners.hologram.regular.multiple");
		m.string("Shop.Buy.Inventory.Name", "Inventory.buy-shop.name");
		m.string("Shop.Buy.Inventory.Page.This", "Inventory.buy-shop.items.page.current");
		m.string("Shop.Buy.Inventory.Page.Next", "Inventory.buy-shop.items.page.next");
		m.string("Shop.Buy.Inventory.Page.Previous", "Inventory.buy-shop.items.page.previous");
		m.string("Shop.Buy.Item.Name", "Inventory.buy-shop.items.spawner.name");
		m.string("Shop.Buy.Item.Price", "Inventory.buy-shop.items.spawner.price");
		m.with("Shop.Buy.Item.Buy1", "Inventory.buy-shop.items.spawner.purchase.first",
				s -> s.replace(" 1 ", " %amount %").replace(" buy ", " purchase "));
		m.with("Shop.Buy.Item.Buy4", "Inventory.buy-shop.items.spawner.purchase.second",
				s -> s.replace(" 4 ", " %amount %").replace(" buy ", " purchase "));
		m.with("Shop.Buy.Item.Buy16", "Inventory.buy-shop.items.spawner.purchase.third",
				s -> s.replace(" 16 ", " %amount% ").replace(" buy ", " purchase "));
		m.with("Shop.Buy.Item.BuyMax", "Inventory.buy-shop.items.spawner.purchase.all",
				s -> s.replace(" buy ", " purchase "));
		m.string("Shop.Buy.Success", "Inventory.buy-shop.purchase.success");
		m.string("Permission.Warn.Shop.Open", "Inventory.buy-shop.permission.opening");
		m.string("Permission.Warn.Shop.Buy", "Inventory.buy-shop.permission.purchase");
		m.string("Shop.Sell.Inventory.Name", "Inventory.sell-shop.name");
		m.string("Shop.Sell.Item.Sell", "Inventory.sell-shop.accept");
		m.string("Shop.Sell.Item.Close", "Inventory.sell-shop.cancel");
		m.string("Shop.Sell.Info.Header", "Inventory.sell-shop.items.selling.name");
		m.string("Shop.Sell.Info.Price", "Inventory.sell-shop.items.selling.price");
		m.string("Shop.Sell.Success", "Inventory.sell-shop.selling.success");
		m.string("Shop.Sell.Empty", "Inventory.sell-shop.selling.empty");
		m.string("Shop.Sell.Unable", "Inventory.sell-shop.selling.unable");
		m.string("Shop.Warn.Disabled", "Inventory.sell-shop.disabled");
		m.string("Permission.Warn.Shop.Open", "Inventory.sell-shop.permission.opening");
		m.string("Permission.Warn.Shop.Sell", "Inventory.sell-shop.permission.selling");
		m.string("Shop.Selection.Inventory.Name", "Inventory.select-shop.name");
		m.string("Shop.Selection.Buy", "Inventory.select-shop.buy-shop");
		m.string("Shop.Selection.Sell", "Inventory.select-shop.sell-shop");
		m.string("Permission.Warn.Shop.Open", "Inventory.select-shop.permission.opening");
		m.string("Inventory.Warn.Space", "Inventory.insufficient-space");
		m.string("Items.Taking", "Items.spawner-drop.alert");
		m.string("Items.DropsCleared", "Items.spawner-drop.cleared");
		m.string("Items.TryBreaking", "Items.spawner-drop.try-breaking");
		m.string("Items.NothingToGive", "Items.spawner-drop.empty");
		m.with("Stacking.Stack", "Spawners.stacking.stacked.infinite",
				s -> (s + " " + m.get("Stacking.Stacked.Infinite")).replace("%stacked%", "%stack%"));
		m.with("Stacking.Stack", "Spawners.stacking.stacked.finite",
				s -> (s + " " + m.get("Stacking.Stacked.Finite")).replace("%stacked%", "%stack%"));
		m.string("Stacking.UnequalSpawner", "Spawners.stacking.unequal-spawner");
		m.string("Stacking.LimitReached", "Spawners.stacking.limit-reached");
		m.string("Permission.Warn.Stack", "Spawners.stacking.permission");
		m.string("Chunk.LimitReached", "Spawners.chunks.limit-reached");
		m.string("Spawner.Ownership.Limit.Current", "Spawners.ownership.limit.place");
		m.string("Spawner.Ownership.Limit.Reached", "Spawners.ownership.limit.reached");
		m.string("Spawner.Ownership.Warn.Stacking", "Spawners.ownership.stacking.warning");
		m.string("Spawner.Ownership.Warn.Breaking", "Spawners.ownership.breaking.warning");
		m.string("Spawner.Ownership.Warn.Upgrading", "Spawners.ownership.upgrading.warning");
		m.string("Spawner.Ownership.Warn.Interact", "Spawners.ownership.opening.warning");
		m.string("Spawner.Ownership.Warn.Changing", "Spawners.ownership.changing.warning");
		m.string("Natural.Warn.Changing", "Spawners.natural.changing.warning");
		m.string("Natural.Warn.Breaking", "Spawners.natural.breaking.warning");
		m.string("Natural.Warn.Stacking", "Spawners.natural.stacking.warning");
		m.string("Natural.Warn.Interact", "Spawners.natural.opening.warning");
		m.string("Natural.Warn.Upgrading", "Spawners.natural.upgrading.warning");
		m.string("EggUse.TypeChanged", "Spawners.changing.type-changed");
		m.string("EggUse.SameEntity", "Spawners.changing.same-type");
		m.string("Permission.Warn.EggUse", "Spawners.changing.permission");
		m.string("EggUse.EggsNotEnough", "Spawners.changing.eggs.insufficient");
		m.string("Upgrades.DisabledUpgrade", "Spawners.upgrades.disabled");
		m.string("Permission.Warn.Upgrade.Open", "Spawners.upgrades.permission.opening");
		m.string("Permission.Warn.Upgrade.Buy", "Spawners.upgrades.permission.purchase");
		m.string("Spawner.Empty.DisabledWarn", "Spawners.empty.disabled");
		m.string("Spawner.Empty.InteractWarn", "Spawners.empty.try-open");
		m.string("Spawner.Empty.FullHandWarn", "Spawners.empty.hand-full");
		m.string("Spawner.Empty.Verify", "Spawners.empty.verify-removing.first");
		m.string("Spawner.Empty.VerifyAgain", "Spawners.empty.verify-removing.try-again");
		m.string("Spawner.View.Warn.Empty", "Spawners.view.empty");
		m.string("Spawner.View.Warn.Disabled", "Spawners.view.disabled");
		m.with("Main.Name.Range", "Inventory.spawner-view.items.header.range",
				s -> "<#008080>" + s);
		m.with("Main.Name.Delay", "Inventory.spawner-view.items.header.delay",
				s -> "<#ff8000>" + s);
		m.with("Main.Name.Amount", "Inventory.spawner-view.items.header.amount",
				s -> "<#800080>" + s);
		m.list("Additions.Info", "Inventory.upgrades.items.stats.lore");
		
		Stream.of(SpawnerType.values())
			.filter(SpawnerType::regular)
			.forEach(type -> {
				m.string("Entities." + type.name(), "Entities.name." + type.name());
			});
		
		save();
		
		lf.delete();
	}
	
	private record Mover(FileConfiguration legacy, FileConfiguration file) {
		private void string(String previous, String next) {
			String s = legacy.getString(previous);
			if(s == null) return;
			file.set(next, Text.fromLegacy(s));
		}
		private void list(String previous, String next) {
			List<String> list = legacy.getStringList(previous);
			if(list == null) return;
			file.set(next, Text.fromLegacy(list));
		}
		private void with(String previous, String next, UnaryOperator<String> u) {
			String s = legacy.getString(previous);
			if(s == null) return;
			file.set(next, u.apply(Text.fromLegacy(s)));
		}
		private String get(String previous) {
			String s = legacy.getString(previous);
			return s == null ? "" : Text.fromLegacy(s);
		}
	}

	public final void save() {
		try {
			file.save(f);
		} catch(IOException e) {}
	}
	

}
