package mc.rellox.spawnermeta.views;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import mc.rellox.spawnermeta.SpawnerMeta;
import mc.rellox.spawnermeta.utils.Reflections.RF;
import mc.rellox.spawnermeta.utils.Utils;

public final class SpawnerViewLayout {

	private static File lf;
	public static FileConfiguration layout;
	
	private static final Slot[] DEFAULT;
	static {
		int i = 0;
		DEFAULT = new Slot[] {
			new Slot(SlotType.STATS, true, i++, Material.NETHER_STAR, true),
			new Slot(SlotType.BACKGROUND, false, i++, Material.CYAN_STAINED_GLASS_PANE, false),
			new Slot(SlotType.BACKGROUND, false, i++, Material.CYAN_STAINED_GLASS_PANE, false),
			new Slot(SlotType.BACKGROUND, false, i++, Material.CYAN_STAINED_GLASS_PANE, false),
			new Slot(SlotType.BACKGROUND, false, i++, Material.CYAN_STAINED_GLASS_PANE, false),
			new Slot(SlotType.BACKGROUND, false, i++, Material.CYAN_STAINED_GLASS_PANE, false),
			new Slot(SlotType.BACKGROUND, false, i++, Material.CYAN_STAINED_GLASS_PANE, false),
			new Slot(SlotType.BACKGROUND, false, i++, Material.CYAN_STAINED_GLASS_PANE, false),
			new Slot(SlotType.BACKGROUND, false, i++, Material.CYAN_STAINED_GLASS_PANE, false),
			new Slot(SlotType.BACKGROUND, false, i++, Material.CYAN_STAINED_GLASS_PANE, false),
			new Slot(SlotType.BACKGROUND, false, i++, Material.CYAN_STAINED_GLASS_PANE, false),
			new Slot(SlotType.UPGRADE_RANGE, true, i++, Material.COMPASS, true),
			new Slot(SlotType.BACKGROUND, false, i++, Material.CYAN_STAINED_GLASS_PANE, false),
			new Slot(SlotType.UPGRADE_DELAY, true, i++, Material.CLOCK, true),
			new Slot(SlotType.BACKGROUND, false, i++, Material.CYAN_STAINED_GLASS_PANE, false),
			new Slot(SlotType.UPGRADE_AMOUNT, true, i++, Material.GOLD_INGOT, true),
			new Slot(SlotType.BACKGROUND, false, i++, Material.CYAN_STAINED_GLASS_PANE, false),
			new Slot(SlotType.BACKGROUND, false, i++, Material.CYAN_STAINED_GLASS_PANE, false),
			new Slot(SlotType.BACKGROUND, false, i++, Material.CYAN_STAINED_GLASS_PANE, false),
			new Slot(SlotType.BACKGROUND, false, i++, Material.CYAN_STAINED_GLASS_PANE, false),
			new Slot(SlotType.BACKGROUND, false, i++, Material.CYAN_STAINED_GLASS_PANE, false),
			new Slot(SlotType.BACKGROUND, false, i++, Material.CYAN_STAINED_GLASS_PANE, false),
			new Slot(SlotType.BACKGROUND, false, i++, Material.CYAN_STAINED_GLASS_PANE, false),
			new Slot(SlotType.BACKGROUND, false, i++, Material.CYAN_STAINED_GLASS_PANE, false),
			new Slot(SlotType.BACKGROUND, false, i++, Material.CYAN_STAINED_GLASS_PANE, false),
			new Slot(SlotType.BACKGROUND, false, i++, Material.CYAN_STAINED_GLASS_PANE, false),
			new Slot(SlotType.CHARGES, true, i++, Material.COAL, true)
		};
	}
	public static final Slot[] LAYOUT = new Slot[27];
	public static int charges;
	public static Material background;
	
	public static void initialize() {
		lf = new File(SpawnerMeta.instance().getDataFolder(), "layout.yml");
		if(lf.getParentFile().exists() == false) lf.getParentFile().mkdirs();
		if(lf.exists() == true) layout = YamlConfiguration.loadConfiguration(lf);
		else {
			try {
				lf.createNewFile();
			} catch(IOException e) {}
			layout = YamlConfiguration.loadConfiguration(lf);
		}
		setDefaultLayout();
		new BukkitRunnable() {
			@Override
			public void run() {
				setSlots();
				for(Slot s : LAYOUT) {
					if(s.t == SlotType.BACKGROUND) {
						background = s.m;
						break;
					}
				}
			}
		}.runTaskLater(SpawnerMeta.instance(), 5);
	}

	public static void saveLayout() {
		try {
			layout.save(lf);
		} catch(IOException e) {}
	}
	
	public static final class WL {
		
		public static boolean is(SlotType type, int o) {
			return Stream.of(LAYOUT).anyMatch(s -> s.t == type && s.i == o);
		}
	}
	
	public static void updateLayout() {
		for(int i = 0; i < 27; i++) LAYOUT[i].i = i;
	}
	
	public static void setEditor(Inventory v) {
		for(int i = 0; i < 27; i++) v.setItem(i, slot(LAYOUT[i]));
		for(int i = 27; i < 36; i++) v.setItem(i, x());
		v.setItem(31, background());
	}
	
	public static void setBackground() {
		Stream.of(LAYOUT)
			.filter(s -> s.t == SlotType.BACKGROUND)
			.forEach(s -> s.m = background);
	}
	
	private static ItemStack background() {
		ItemStack item = new ItemStack(background == null ? Material.BARRIER : background);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(ChatColor.AQUA + "Background: " + ChatColor.GRAY
				+ (background == null ? "Air" : Utils.displayName(item)));
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES);
		List<String> lore = new ArrayList<>();
		lore.add("");
		lore.add(ChatColor.GREEN + "Click " + ChatColor.AQUA + "to swap material");
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}
	
	private static void setSlots() {
		for(int i = 0; i < 27; i++) {
			try {
				String path = "Upgrade-Layout." + i;
				LAYOUT[i] = new Slot(RF.enumerate(SlotType.class, layout.getString(path + ".Type")), 
						layout.getBoolean(path + ".Changable"), 
						i, RF.enumerate(Material.class, layout.getString(path + ".Material")),
						layout.getBoolean(path + ".Glint"));
			} catch(Exception e) {
				LAYOUT[i] = DEFAULT[i];
			}
		}
	}
	
	public static void setDefaultLayout() {
		for(int i = 0; i < 27; i++) defaultSlot(DEFAULT[i]);
		layout.options().copyDefaults(true);
		layout.options().header("In this file you can modify spawner upgrade inventory layout.\n"
				+ "After any modifications do /sm update");
		saveLayout();
	}
	
	private static void defaultSlot(Slot s) {
		if(s == null) return;
		layout.addDefault("Upgrade-Layout." + s.i + ".Type", s.t.name());
		layout.addDefault("Upgrade-Layout." + s.i + ".Changable", s.c);
		layout.addDefault("Upgrade-Layout." + s.i + ".Material", s.m.name());
		layout.addDefault("Upgrade-Layout." + s.i + ".Glint", s.g);
	}
	
	public static void resetLayout() {
		for(int i = 0; i < 27; i++) resetSlot(LAYOUT[i] = DEFAULT[i]);
	}
	
	private static void resetSlot(Slot s) {
		if(s == null) return;
		layout.set("Upgrade-Layout." + s.i + ".Type", s.t.name());
		layout.set("Upgrade-Layout." + s.i + ".Changable", s.c);
		layout.set("Upgrade-Layout." + s.i + ".Material", s.m.name());
		layout.set("Upgrade-Layout." + s.i + ".Glint", s.g);
		saveLayout();
	}
	
	public static void saveSlots() {
		for(int i = 0; i < 27; i++) saveSlot(LAYOUT[i]);
	}
	
	private static void saveSlot(Slot s) {
		if(s == null) return;
		layout.set("Upgrade-Layout." + s.i + ".Type", s.t.name());
		layout.set("Upgrade-Layout." + s.i + ".Changable", s.c);
		layout.set("Upgrade-Layout." + s.i + ".Material", s.m.name());
		layout.set("Upgrade-Layout." + s.i + ".Glint", s.g);
		saveLayout();
	}
	
	private static ItemStack slot(Slot s) {
		ItemStack item = new ItemStack(s.m);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(s.t.getName() + ChatColor.GRAY + " (" + Utils.displayName(item) + ")");
		if(s.g) meta.addEnchant(Enchantment.ARROW_DAMAGE, 0, true);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES);
		if(s.c) {
			List<String> lore = new ArrayList<>();
			lore.add("");
			lore.add(ChatColor.GREEN + "Right-click " + ChatColor.AQUA + "to shift to right " + ChatColor.GOLD + " -> ");
			lore.add(ChatColor.YELLOW + "Left-click " + ChatColor.AQUA + "to shift to left " + ChatColor.GOLD + " <- ");
			lore.add(ChatColor.GOLD + "Middle-click " + ChatColor.AQUA + "to swap material");
			lore.add(ChatColor.RED + "Shift-click " + ChatColor.AQUA + "to toggle glint");
			meta.setLore(lore);
		}
		item.setItemMeta(meta);
		return item;
	}
	
	private static ItemStack x() {
		ItemStack item = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(" ");
		item.setItemMeta(meta);
		return item;
	}
	
	public static class Slot {

		protected final SlotType t;
		protected final boolean c;
		
		protected int i;
		protected Material m;
		protected boolean g;
		
		public Slot(SlotType t, boolean c, int i, Material m, boolean g) {
			this.t = t;
			this.c = c;
			this.i = i;
			this.m = m;
			this.g = g;
		}
		
	}
	
	public static enum SlotType {
		
		BACKGROUND(ChatColor.DARK_GRAY + "Background"),
		UPGRADE_DELAY(ChatColor.GOLD + "Delay Upgrade"),
		UPGRADE_AMOUNT(ChatColor.DARK_PURPLE + "Amount Upgrade"),
		UPGRADE_RANGE(ChatColor.DARK_AQUA + "Range Upgrade"),
		CHARGES(ChatColor.AQUA + "Charges"),
		STATS(ChatColor.YELLOW + "Stats");
		
		private String name;
		
		private SlotType(String name) {
			this.name = name;
		}
		
		public String getName() {
			return this.name;
		}
		
	}

}
