package mc.rellox.spawnermeta.configuration.file;

import java.io.File;
import java.util.List;
import java.util.Set;

import org.bukkit.configuration.file.YamlConfiguration;

import mc.rellox.spawnermeta.SpawnerMeta;
import mc.rellox.spawnermeta.api.view.layout.ISlot;
import mc.rellox.spawnermeta.configuration.AbstractFile;
import mc.rellox.spawnermeta.configuration.Configuration.CF;
import mc.rellox.spawnermeta.utility.Utility;
import mc.rellox.spawnermeta.utility.reflect.Reflect.RF;
import mc.rellox.spawnermeta.view.layout.ActiveBackground;
import mc.rellox.spawnermeta.view.layout.ActiveSlot;
import mc.rellox.spawnermeta.view.layout.LayoutRegistry;

public class LayoutsFile extends AbstractFile {

	public LayoutsFile() {
		super("layouts");
	}

	@Override
	protected void initialize() {
		if(CF.version() < 6) {
			var of = new File(SpawnerMeta.instance().getDataFolder(), "layout.yml");
			if(of.exists() == true) {
				try {
					var old = YamlConfiguration.loadConfiguration(of);
					convert(old);
					of.delete();
				} catch (Exception e) {
					RF.debug(e);
				}
			}
		}
		if(CF.version() < 7) {
			List<String> list = CF.s.getStrings("Items.layout.spawner-item");
			hold("Item-layout.spawner-item", list.isEmpty() ? null : list);
			list = CF.s.getStrings("Items.layout.upgrades.stat-item");
			hold("Item-layout.upgrades.stat-item", list.isEmpty() ? null : list);
			list = CF.s.getStrings("Items.layout.upgrades.upgrade-item");
			hold("Item-layout.upgrades.upgrade-item", list.isEmpty() ? null : list);
			list = CF.s.getStrings("Items.layout.upgrades.disabled-upgrade-item");
			hold("Item-layout.upgrades.disabled-upgrade-item", list.isEmpty() ? null : list);
			CF.s.clear("Items.layout");
		}
		if(CF.version() < 9) {
			List<String> list = CF.s.getStrings("Item-layout.upgrades.stat-item");
			if(list.isEmpty() == false) {
				int a;
				if(list.indexOf("INFO") == list.size() - 1) a = list.size() - 1;
				else a = list.size();
				list.add(a, "!");
				list.add(a, "OFFLINE");
				hold("Item-layout.upgrades.stat-item", list);
			}
		}
		
		Object x = file.get("Item-layout");
		if(x instanceof List list && list.isEmpty() == true) file.set("Item-layout", null); 
		
		if(file.isSet("Upgrade-layout") == false) {
			file.addDefault("Upgrade-layout.rows", 3);
			var l = LayoutRegistry.DEFAULT_UPGRADES;
			for(ISlot s : l.all())
				if(s instanceof ActiveSlot as)
					as.save(file, "Upgrade-layout");
			if(l.background() instanceof ActiveBackground ab) ab.save(file, "Upgrade-layout");
		}
		
		file.addDefault("Item-layout.spawner-item", List.of(
				"!",
				"HEADER",
				"RANGE",
				"DELAY",
				"AMOUNT",
				"!",
				"CHARGES",
				"SPAWNABLE",
				"!",
				"INFO"
				));
		file.addDefault("Item-layout.upgrades.stat-item", List.of(
				"EMPTY",
				"SWITCHING",
				"!",
				"LOCATION",
				"STACK",
				"SPAWNABLE",
				"!",
				"WARNING",
				"!",
				"OFFLINE",
				"!",
				"INFO"
				));
		file.addDefault("Item-layout.upgrades.upgrade-item", List.of(
				"HELP",
				"!",
				"INFO",
				"!",
				"CURRENT",
				"!",
				"NEXT",
				"!",
				"PRICE"
				));
		file.addDefault("Item-layout.upgrades.disabled-upgrade-item", List.of(
				"HELP",
				"!",
				"INFO",
				"!",
				"CURRENT"
				));

		file.options().copyDefaults(true);
		
		Commenter c = commenter();
		if(c != null) {
			c.comment("Item-layout",
					"In this section you can change",
					"  the order of item lore and even",
					"  delete lines.",
					"You can delete a line by deleting",
					"  the line in this file or add '!'",
					"  after the line.",
					"  E.g. 'INFO' -> 'INFO!'");
			c.comment("Item-layout.spawner-item",
					"Keys:",
					"  HEADER, RANGE, DELAY, AMOUNT",
					"  CHARGES, SPAWNABLE, INFO");
			c.comment("Item-layout.upgrades.stat-item",
					"Keys:",
					"  EMPTY, SWITCHING, LOCATION, STACK",
					"  WARNING, OFFLINE, SPAWNABLE, INFO");
			c.comment("Item-layout.upgrades.upgrade-item",
					"Keys:",
					"  HELP, INFO, CURRENT, NEXT, PRICE");
			c.comment("Item-layout.upgrades.disabled-upgrade-item",
					"Keys:",
					"  HELP, INFO, CURRENT");
		}
		
		save();
		
		LayoutRegistry.initialize();
	}
	
	private void convert(YamlConfiguration old) {
		var cs = old.getConfigurationSection("Upgrade-Layout");
		if(cs == null) return;
		boolean[] bs = {false};
		Set<String> keys = cs.getKeys(false);
		keys.forEach(key -> {
			var type = cs.getString(key + ".Type");
			if(type.equalsIgnoreCase("BACKGROUND") == true) {
				if(bs[0] == true) return;
				hold("Upgrade-layout.background.material", cs.get(key + ".Material"));
				hold("Upgrade-layout.background.glint", cs.get(key + ".Glint"));
				hold("Upgrade-layout.background.model", cs.get(key + ".Model"));
			} else {
				String n = switch(type.toUpperCase()) {
				case "STATS" -> "stats";
				case "UPGRADE_RANGE" -> "range";
				case "UPGRADE_DELAY" -> "delay";
				case "UPGRADE_AMOUNT" -> "amount";
				case "CHARGES" -> "charges";
				default -> null;
				};
				if(n == null) return;
				hold("Upgrade-layout." + n + ".material", cs.get(key + ".Material"));
				hold("Upgrade-layout." + n + ".glint", cs.get(key + ".Glint"));
				hold("Upgrade-layout." + n + ".model", cs.get(key + ".Model"));
				var slots = file.getIntegerList("Upgrade-layout." + n + ".slots");
				int i = Utility.isInteger(key) ? Integer.parseInt(key) : -1;
				if(i >= 0) {
					slots.add(i);
					hold("Upgrade-layout." + n + ".slots", slots);
				}
				if(switch (n) {
				case "range", "delay", "amount" -> true;
				default -> false;
				} == true) hold("Upgrade-layout." + n + ".deny-material", "BARRIER");
			}
		});
		int rows = old.getInt("Upgrade-Rows");
		if(rows < 1) rows = 1;
		else if(rows > 6) rows = 6;
		hold("Upgrade-layout.rows", rows);
		save();
	}

}
