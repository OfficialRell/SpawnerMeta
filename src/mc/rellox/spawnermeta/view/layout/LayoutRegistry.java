package mc.rellox.spawnermeta.view.layout;

import java.util.List;
import java.util.stream.Stream;

import org.bukkit.Material;

import mc.rellox.spawnermeta.api.view.layout.IBackground;
import mc.rellox.spawnermeta.api.view.layout.ILayout;
import mc.rellox.spawnermeta.api.view.layout.ISlot;
import mc.rellox.spawnermeta.api.view.layout.SlotField;
import mc.rellox.spawnermeta.configuration.Configuration.CF;
import mc.rellox.spawnermeta.text.Text;
import mc.rellox.spawnermeta.utility.reflect.Reflect.RF;

public final class LayoutRegistry {
	
	public static final ILayout DEFAULT_UPGRADES;
	static {
		var background = new ActiveBackground(SlotField.upgrade_background,
				Material.CYAN_STAINED_GLASS_PANE, false, 0);
		var l = DEFAULT_UPGRADES = new ActiveLayout(background, 3);
		Stream.of(new ActiveSlot(SlotField.upgrade_stats, 0, Material.NETHER_STAR, null, true, 0),
				new ActiveSlot(SlotField.upgrade_range, 11, Material.COMPASS, Material.BARRIER, true, 0),
				new ActiveSlot(SlotField.upgrade_delay, 13, Material.CLOCK, Material.BARRIER, true, 0),
				new ActiveSlot(SlotField.upgrade_amount, 15, Material.GOLD_INGOT, Material.BARRIER, true, 0),
				new ActiveSlot(SlotField.upgrade_charges, 26, Material.CHARCOAL, null, true, 0))
		.forEach(slot -> l.set(slot.field(), slot));
	}
	
	private static ILayout upgrades;
	
	public static void initialize() {
		var f = CF.y;
		try {
			var path = "Upgrade-layout.";
			int rows = f.getInteger(path + "rows", 1, 6);
			IBackground background = background();
			var l = new ActiveLayout(background, rows);
			Stream.of(SlotField.upgrade_stats, SlotField.upgrade_range,
					SlotField.upgrade_delay, SlotField.upgrade_amount,
					SlotField.upgrade_charges)
			.map(LayoutRegistry::slot)
			.filter(s -> s != null)
			.forEach(s -> l.set(s.field(), s));
			upgrades = l;
		} catch (Exception e) {
			if(e instanceof SlotBuildException) {
				Text.failure("Error occured while loading upgrade inventory layout: #0 (Using default)",
						e.getMessage());
			} else RF.debug(e);
			upgrades = DEFAULT_UPGRADES;
		}
	}
	
	private static IBackground background() {
		var f = CF.y;
		var path = "Upgrade-layout.background.";
		String name = f.getString(path + "material");
		boolean empty = name != null && name.equalsIgnoreCase("EMPTY") == true;
		var material = empty == true ? null : RF.enumerate(Material.class, name);
		if(material == null && empty == false) throw new SlotBuildException("background material cannot be null");
		boolean glint = f.getBoolean(path + "glint");
		int model = f.getInteger(path + "model");
		return new ActiveBackground(SlotField.upgrade_background,
				material, glint, model);
	}
	
	private static ISlot slot(SlotField field) {
		var f = CF.y;
		var path = "Upgrade-layout." + field.defines() + ".";
		List<Integer> list = f.file().getIntegerList(path + "slots");
		if(list.isEmpty() == true) return null;
		int[] slots = list.stream()
				.mapToInt(Integer::intValue)
				.toArray();
		var material = RF.enumerate(Material.class, f.getString(path + "material"));
		if(material == null) throw new SlotBuildException(field.defines() + " slot material cannot be null");
		boolean glint = f.getBoolean(path + "glint");
		int model = f.getInteger(path + "model");
		Material deny = null;
		if(field.deny == true) {
			deny = RF.enumerate(Material.class, f.getString(path + "deny-material"));
			if(deny == null) throw new SlotBuildException(field.defines() + " slot deny material cannot be null");
		}
		return new ActiveSlot(field, slots, material, deny, glint, model);
	}
	
	public static ILayout upgrades() {
		return upgrades;
	}
	
	@SuppressWarnings("serial")
	private static class SlotBuildException extends NullPointerException {
		
		public SlotBuildException(String error) {
			super(error);
		}
		
	}

}
