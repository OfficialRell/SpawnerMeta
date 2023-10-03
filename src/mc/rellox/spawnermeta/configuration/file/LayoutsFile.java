package mc.rellox.spawnermeta.configuration.file;

import java.io.File;
import java.util.Set;

import org.bukkit.configuration.file.YamlConfiguration;

import mc.rellox.spawnermeta.SpawnerMeta;
import mc.rellox.spawnermeta.api.view.layout.ISlot;
import mc.rellox.spawnermeta.configuration.AbstractFile;
import mc.rellox.spawnermeta.utility.Utils;
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
		
		if(file.isSet("Upgrade-layout") == false) {
			file.addDefault("Upgrade-layout.rows", 3);
			var l = LayoutRegistry.DEFAULT_UPGRADES;
			for(ISlot s : l.all())
				if(s instanceof ActiveSlot as)
					as.save(file, "Upgrade-layout");
			if(l.background() instanceof ActiveBackground ab) ab.save(file, "Upgrade-layout");
		}

		file.options().copyDefaults(true);
		
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
				int i = Utils.isInteger(key) ? Integer.parseInt(key) : -1;
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
