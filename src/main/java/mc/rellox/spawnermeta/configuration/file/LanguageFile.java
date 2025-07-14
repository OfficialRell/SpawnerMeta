package mc.rellox.spawnermeta.configuration.file;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import mc.rellox.spawnermeta.configuration.AbstractFile;
import mc.rellox.spawnermeta.configuration.Configuration.CF;
import mc.rellox.spawnermeta.spawner.type.SpawnerType;
import mc.rellox.spawnermeta.text.content.Content;
import mc.rellox.spawnermeta.text.content.ContentParser;

public class LanguageFile extends AbstractFile {
	
	private final Map<String, List<Content>> text = new HashMap<>();
	private final List<String> keys = new ArrayList<>();

	public LanguageFile() {
		super("language");
	}

	@Override
	protected void initialize() {
		text.clear();
		
		if(CF.version() < 5) {
			copy("Inventory.upgrades", "Upgrade-GUI");
			copy("Inventory.spawner-view", "Spawner-view");
			copy("Spawners.hologram", "Holograms");
			copy("Inventory.buy-shop", "Buy-shop");
			copy("Inventory.sell-shop", "Sell-shop");
			copy("Inventory.select-shop", "Shop-select");
			copy("Spawners.item", "Spawner-item");
		}
		if(CF.version() < 6) {
			if(getStrings("Upgrade-GUI.items.upgrade.info.range").isEmpty() == true)
				delete("Upgrade-GUI.items.upgrade.info.range");
			if(getStrings("Upgrade-GUI.items.upgrade.info.delay").isEmpty() == true)
				delete("Upgrade-GUI.items.upgrade.info.delay");
			if(getStrings("Upgrade-GUI.items.upgrade.info.amount").isEmpty() == true)
				delete("Upgrade-GUI.items.upgrade.info.amount");
		}
		
		put("Upgrade-GUI.purchase.range", "<#00ff00>(!) <#80ff00>Upgraded <#00ffff-#008080><!italic>range <#80ff00>to level %level%");
		put("Upgrade-GUI.purchase.delay", "<#00ff00>(!) <#80ff00>Upgraded <#ffff00-#ff8000><!italic>delay <#80ff00>to level %level%");
		put("Upgrade-GUI.purchase.amount", "<#00ff00>(!) <#80ff00>Upgraded <#ff00ff-#800080><!italic>amount <#80ff00>to level %level%");
		
		put("Upgrade-GUI.name", "Spawner");
		
		put("Upgrade-GUI.items.upgrade.name.range", "<#00ffff-#008080>-= Range %level% =-");
		put("Upgrade-GUI.items.upgrade.name.delay", "<#ffff00-#ff8000>-= Delay %level% =-");
		put("Upgrade-GUI.items.upgrade.name.amount", "<#ff00ff-#800080>-= Amount %level% =-");
		put("Upgrade-GUI.items.upgrade.help", "<#808080><!italic>Click to upgrade!");
		put("Upgrade-GUI.items.upgrade.info.range", List.of("  <#99bfbf>Shows required player distance",
				"<#99bfbf>from the spawner to be active"));
		put("Upgrade-GUI.items.upgrade.info.delay", List.of("  <#bfbf99>Shows time between each spawning",
				"<#bfbf99>if active"));
		put("Upgrade-GUI.items.upgrade.info.amount", List.of("  <#bf99bf>Shows amount of entities that",
				"<#bf99bf>will spawn each time"));
		put("Upgrade-GUI.items.upgrade.current.range", "<#bfbfbf>Current range: <#80ffff-#00ffff><!italic>%value% Blocks");
		put("Upgrade-GUI.items.upgrade.current.delay", "<#bfbfbf>Current delay: <#ffff80-#ffff00><!italic>%value% Seconds");
		put("Upgrade-GUI.items.upgrade.current.amount", "<#bfbfbf>Current amount: <#ff80ff-#ff00ff><!italic>%value% Entities");
		put("Upgrade-GUI.items.upgrade.next.range", "<#bfbfbf>Next range: <#80ffff-#00ffff><!italic>%value% Blocks");
		put("Upgrade-GUI.items.upgrade.next.delay", "<#bfbfbf>Next delay: <#ffff80-#ffff00><!italic>%value% Seconds");
		put("Upgrade-GUI.items.upgrade.next.amount", "<#bfbfbf>Next amount: <#ff80ff-#ff00ff><!italic>%value% Entities");
		put("Upgrade-GUI.items.upgrade.maximum-reached", "<#008000>Maximum level has been reached!");
		put("Upgrade-GUI.items.upgrade.price", "<#ffffff>Price: <#00bf00><!italic>%price%");
		
		put("Upgrade-GUI.items.disabled-upgrade.name.range", "<#00ffff-#008080>-= Range =-");
		put("Upgrade-GUI.items.disabled-upgrade.name.delay", "<#ffff00-#ff8000>-= Delay =-");
		put("Upgrade-GUI.items.disabled-upgrade.name.amount", "<#ff00ff-#800080>-= Amount =-");
		put("Upgrade-GUI.items.disabled-upgrade.help", "<#800000>Cannot be upgraded!");
		put("Upgrade-GUI.items.disabled-upgrade.current.range", "<#bfbfbf><!italic>Current range: <#80ffff-#00ffff>%value% Blocks");
		put("Upgrade-GUI.items.disabled-upgrade.current.delay", "<#bfbfbf><!italic>Current delay: <#ffff80-#ffff00>%value% Seconds");
		put("Upgrade-GUI.items.disabled-upgrade.current.amount", "<#bfbfbf><!italic>Current amount: <#ff80ff-#ff00ff>%value% Entities");
		
		put("Upgrade-GUI.items.stats.name", "<#bfffff-#00ffff>-= %type% Spawner =-");
		put("Upgrade-GUI.items.stats.disabled", "<#ff0000>DISABLED <#bfbfbf><!italic>(Click to enable)");
		put("Upgrade-GUI.items.stats.enabled", "<#00ff00>ENABLED <#bfbfbf><!italic>(Click to disable)");
		put("Upgrade-GUI.items.stats.empty", "<#bfbfbf>Shift-right-click on this spawner to empty it.");
		put("Upgrade-GUI.items.stats.location", "<#bfbfbf>Location: <#ff0000>%x%<#808080>, <#ff0000>%y%<#808080>, <#ff0000>%z%");
		put("Upgrade-GUI.items.stats.stacking.infinite", "<#bfbfbf>Stacked: <#bfff00>%stack% Spawner");
		put("Upgrade-GUI.items.stats.stacking.finite", "<#bfbfbf>Stacked: <#bfff00>%stack%/%limit% Spawners");
		put("Upgrade-GUI.items.stats.spawnable", "<#bfbfbf>Spawnable Entities: <#ffff00-#ffbf00>%spawnable%");
		put("Upgrade-GUI.items.stats.warnings.header", "<#ff0000><!underline>Spawner has %count% warning(s):");
		put("Upgrade-GUI.items.stats.warnings.light", "  <#ff8000><!italic>insufficient light level");
		put("Upgrade-GUI.items.stats.warnings.environment", "  <#ff8000><!italic>insufficient spawn space");
		put("Upgrade-GUI.items.stats.warnings.ground", "  <#ff8000><!italic>missing correct ground type");
		put("Upgrade-GUI.items.stats.warnings.charges", "  <#ff8000><!italic>out of charges");
		put("Upgrade-GUI.items.stats.warnings.power", "  <#ff8000><!italic>insufficient redstone power");
		put("Upgrade-GUI.items.stats.warnings.unknown", "  <#ff8000><!italic>invalid spawn conditions");
		put("Upgrade-GUI.items.stats.owner-offline", List.of(
				"<#ffbf00>Unable to spawn because the owner",
				"  <#ffbf00>of this spawner is offline!"));
		put("Upgrade-GUI.items.stats.lore", List.of());
		
		put("Upgrade-GUI.items.charges.name", "<#ff0080-#ff0000>Spawning Charges: <#00ffff>%charges%");
		put("Upgrade-GUI.items.charges.purchase.first", "<#bfbfbf>Left-Click to purchase <#ffff00>%charges% charges <#808080><!italic>(%price%)");
		put("Upgrade-GUI.items.charges.purchase.second", "<#bfbfbf>Right-Click to purchase <#ff8000>%charges% charges <#808080><!italic>(%price%)");
		put("Upgrade-GUI.items.charges.purchase.all", "<#bfbfbf>Shift-Click to purchase <#ff0000>%charges% charges <#808080><!italic>(%price%)");
		
		put("Upgrade-GUI.charges.purchase", "<#00ff00>(!) <#00ffff>You bought %charges% spawner charges");
		put("Upgrade-GUI.disabled-upgrade", "<#800000>(!) <#ff8000>You cannot upgrade this!");
		
		put("Spawner-item.regular.name", "<#bfffff-#00ffff>Spawner <#ffff00-#ffaa00>(%type%)");
		put("Spawner-item.empty.name", "<#ff8000><Empty> <#bfffff-#00ffff>Spawner");
		put("Spawner-item.empty-stored.name", "<#ff8000><Empty : %type%> <#bfffff-#00ffff>Spawner");
		put("Spawner-item.header", "<#ffffff>Upgrades:");
		put("Spawner-item.upgrade.range", "<#808080>- <#00ffff-#008080><!italic>Range <#00ffff>%level%");
		put("Spawner-item.upgrade.delay", "<#808080>- <#ffff00-#ff8000><!italic>Delay <#00ffff>%level%");
		put("Spawner-item.upgrade.amount", "<#808080>- <#ff00ff-#800080><!italic>Amount <#00ffff>%level%");
		put("Spawner-item.charges", "<#ff0080-#ff0000>Charges: <#00ffff>%charges%");
		put("Spawner-item.spawnable", "<#ffff00-#ff8000>Spawnable Entities: <#00ffff>%spawnable%");
		put("Spawner-item.info", List.of());
		
		put("Spawner-view.name", "<#000000>All Spawners");
		put("Spawner-view.items.name", "<#ffff00>-=[ <#00ffff>%type% Spawner<#ffff00> ]=-");
		put("Spawner-view.items.header.range", "<#00ffff-#008080><!italic>Range:");
		put("Spawner-view.items.header.delay", "<#ffff00-#ff8000><!italic>Delay:");
		put("Spawner-view.items.header.amount", "<#ff00ff-#800080><!italic>Amount:");
		put("Spawner-view.items.price", "<#808080>- <#bfbfbf>Price: <#ffffff><!italic>%price%");
		put("Spawner-view.items.price-increase", "<#808080>- <#bfbfbf>Price Increase: <#ffffff><!italic>%increase%");
		put("Spawner-view.items.maximum-level", "<#808080>- <#bfbfbf>Maximum Level: <#ffffff><!italic>%level%");
		put("Spawner-view.items.spawnable", "<#bfbfbf>Spawnable Entities: <#ffff00-#ff8000>%spawnable%");
		put("Spawner-view.items.page.current", "<#00ffff>Page %page%");
		put("Spawner-view.items.page.next", "<#ff8000>Next Page");
		put("Spawner-view.items.page.previous", "<#ff8000>Previous Page");
		put("Spawner-view.items.page.previous", "<#ff8000>Previous Page");
		put("Spawner-view.permission", "<#ff8000>Previous Page");
		
		put("Prices.type.experience.insufficient", "Not enough experience!");
		put("Prices.type.experience.amount", "%amount% Experience");
		put("Prices.type.levels.insufficient", "Not enough experience levels!");
		put("Prices.type.levels.amount", "%amount% Experience Levels");
		put("Prices.type.material.insufficient", "Not enough materials!");
		put("Prices.type.material.amount", "%amount% × %material%");
		put("Prices.type.economy.insufficient", "Insufficient funds!");
		put("Prices.type.economy.amount", "$%amount%");
		put("Prices.type.flare-tokens.insufficient", "Insufficient tokens!");
		put("Prices.type.flare-tokens.amount", "%amount% Tokens");
		put("Prices.type.player-points.insufficient", "Insufficient player points!");
		put("Prices.type.player-points.amount", "%amount% Player points");
		put("Prices.insufficient", "<#800000>(!) <#ff8000>%insufficient% <#bfbfbf>[Missing %price%]");
		
		put("Holograms.empty.single", "<#ff8000><Empty> <#bfffff-#00ffff>Spawner");
		put("Holograms.empty.multiple", "<#ffff00>%stack% <#bfbfbf>× <#ff8000><Empty> <#bfffff-#00ffff>Spawner");
		put("Holograms.regular.single", "<#bfffff-#00ffff>%name% Spawner");
		put("Holograms.regular.multiple", "<#ffff00>%stack% <#bfbfbf>× <#bfffff-#00ffff>%name% Spawner");
		put("Holograms.warning", "<#ffff00>( <#800000>!!! <#ffff00>)");
		
		put("Shop-buy.name", "<#000000>Spawner Shop <#808080>(<#ff8000>%page_current%<#808080>/<#ff8000>%page_total%<#808080>)");
		put("Shop-buy.items.page.current", "<#00ffff>Page %page%");
		put("Shop-buy.items.page.next", "<#ff8000>Next Page");
		put("Shop-buy.items.page.previous", "<#ff8000>Previous Page");
		put("Shop-buy.items.spawner.name", "<#bfffff-#00ffff>Spawner <#ffff00-#ffaa00>(%type%)");
		put("Shop-buy.items.spawner.price", "<#ffffff>Price: <#00bf00><!italic>%price%");
		put("Shop-buy.items.spawner.purchase.first", "<#bfbfbf>Left-click to purchase %amount%");
		put("Shop-buy.items.spawner.purchase.second", "<#bfbfbf>Right-click to purchase %amount%");
		put("Shop-buy.items.spawner.purchase.third", "<#bfbfbf>Shift-left-click to purchase %amount%");
		put("Shop-buy.items.spawner.purchase.all", "<#bfbfbf>Shift-right-click to purchase maximum");
		put("Shop-buy.purchase.success", "<#008000>(!) <#00ffff>Purchased <#ffff00-#ffaa00>%amount% × %type%<#00ffff> Spawner(s)!");
		put("Shop-buy.permission.opening", "<#800000>(!) <#ff8000>You do not have a permission to open this!");
		put("Shop-buy.permission.purchase", "<#800000>(!) <#ff8000>You do not have a permission to purchase this!");
		
		put("Shop-sell.name", "<#000000>Spawners Selling");
		put("Shop-sell.accept", "<#00ff00>Sell");
		put("Shop-sell.cancel", "<#ff0000>Close");
		put("Shop-sell.items.selling.name", "<#00ffff>Selling for:");
		put("Shop-sell.items.selling.price", "<#bfbfbf>- <#00bf00>%price%");
		put("Shop-sell.selling.success", "<#008000>(!) <#00ffff>Successfully sold spawners for:");
		put("Shop-sell.selling.empty", "<#800000>(!) <#ff8000>Nothing to sell!");
		put("Shop-sell.selling.unable", "<#800000>(!) <#ff8000>Unable to sell that!");
		put("Shop-sell.disabled", "<#800000>(!) <#008080>Spawner shop has been disabled!");
		put("Shop-sell.permission.opening", "<#800000>(!) <#ff8000>You do not have a permission to open this!");
		put("Shop-sell.permission.selling", "<#800000>(!) <#ff8000>You do not have a permission to sell this!");
		
		put("Shop-select.name", "<#000000>Spawners Selling");
		put("Shop-select.buy-shop", "<#00ffff>Click to purchase spawners");
		put("Shop-select.sell-shop", "<#00ffff>Click to sell spawners");
		
		put("Shop-select.permission.opening", "<#800000>(!) <#ff8000>You do not have a permission to open this!");
		
		put("Inventory.insufficient-space", "<#800000>(!) <#ff8000>You do not have enough space in your inventory!");
		
		put("Items.spawner-drop.alert", "<#00ffff>You have <#ffff00>%seconds% seconds <#00ffff>to take your spawner items! <#ffffff>(click or /spawnerdrops)");
		put("Items.spawner-drop.cleared", "<#800000>(!) <#ff8000>Your spawner drops disappeared, was not taken in time!");
		put("Items.spawner-drop.try-breaking", "<#800000>(!) <#ff8000>Cannot break spawners while you have not taken previously dropped items!");
		put("Items.spawner-drop.empty", "<#800000>(!) <#ff8000>No items to give!");
		
		put("Spawners.placing.permission", "<#800000>(!) <#ff8000>You do not have a permission to place this!");
		
		put("Spawners.breaking.success", "<#00ff00>(!) <#00ffff>Spawner successfully mined!");
		put("Spawners.breaking.failure", "<#800000>(!) <#008080>Spawner failed to mine!");
		put("Spawners.breaking.permission", "<#800000>(!) <#ff8000>You do not have a permission to break this!");
		
		put("Spawners.stacking.stacked.infinite", "<#008000>(!) <#00ffff>Spawners have been stacked! <#ffff00>(%stack% Stacked)");
		put("Spawners.stacking.stacked.finite", "<#008000>(!) <#00ffff>Spawners have been stacked! <#ffff00>(%stack%/%limit% Stacked)");
		put("Spawners.stacking.unequal-spawner", "<#800000>(!) <#ff8000>Spawners must be the same to stack!");
		put("Spawners.stacking.disabled-type", "<#800000>(!) <#ff8000>You cannot stack this spawner!");
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
		put("Spawners.changing.dany.from", "<#800000>(!) <#ff8000>You cannot change this entity type spawner!");
		put("Spawners.changing.dany.to", "<#800000>(!) <#ff8000>You cannot set this entity type!");
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
		
		put("Spawners.give.success", "<#008000>(!) <#008080>Added <#00ffff>%amount% <#008080>× <#00ffff>%type% Spawner <#008080>to your inventory!");
		put("Spawners.give.success-single", "<#008000>(!) <#008080>Added <#00ffff>%type% Spawner <#008080>to your inventory!");
		
		put("Locations.header", "<#00ffff>You have placed %count% spawner(s) at:");
		put("Locations.world", "  <#808080>(%world%)");
		put("Locations.position", "<#ff0080>%index%. <#c4c4c4>%x%, %y%, %z%");
		put("Locations.none-owned", "<#ff8000>You do not own any spawners!");
		
		put("Trusted.help.primary",
				List.of("<#00ff80>Usage: <#80ffff>/spawnertrust",
						"  <#80ffff>add [player] <#cccccc>- add trusted player",
						"  <#80ffff>remove [player] <#cccccc>- remove trusted player",
						"  <#80ffff>clear <#cccccc>- remove all trusted players",
						"  <#80ffff>view <#cccccc>- view all trusted players"));
		put("Trusted.help.add", "<#00ff80>Usage: <#80ffff>/spawnertrust add [player]");
		put("Trusted.help.remove", "<#00ff80>Usage: <#80ffff>/spawnertrust remove [player]");
		put("Trusted.info.unknow-player", "<#ff0000>Unknown player!");
		put("Trusted.info.already-trusted", "<#ff8000>You already trust this player!");
		put("Trusted.info.not-trusted", "<#ff8000>You already do not trust this player!");
		put("Trusted.info.empty", "<#ff8000>You do not trust any players!");
		put("Trusted.info.added", "<#00ffff>Added this player to your trust list!");
		put("Trusted.info.removed", "<#00ffff>Removed this player from your trust list!");
		put("Trusted.info.cleared", "<#00ffff>Removed %count% player(s) from your trust list!");
		put("Trusted.header", "<#00ffff>You have %count% player(s) in your trust list:");
		put("Trusted.player", "<#ff0080>%index%. <#c4c4c4>%player%");
		
		Stream.of(SpawnerType.values())
			.filter(SpawnerType::regular)
			.forEach(type -> put("Entities.name." + type.name(), type.text().text()));
		
		file.options().copyDefaults(true);
		
		header("""
				
				Language file has been updated!
				
				(!!!) Legacy colors (&a&1&b...) are NO longer available.
				
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
	}
	
	private void put(String path, Object value) {
		defaulted(path, value);
		keys.add(path);
	}

	private void read() {
		keys.forEach(key -> {
			List<Content> list;
			if(file.isString(key) == true) {
				String s = file.getString(key);
				if(s == null || s.isEmpty() == true) list = of();
				else list = of(ContentParser.parse(s));
			} else list = ContentParser.parse(file.getStringList(key));
			if(list == null || list.isEmpty() == true) return; 
			text.put(key, list);
		});
		keys.clear();
	}
	
	private static List<Content> of(Content... cs) {
		return cs == null ? new ArrayList<>()
				: Stream.of(cs).collect(Collectors.toList());
	}
	
	public List<Content> get(String key) {
		return text.get(key);
	}

}
