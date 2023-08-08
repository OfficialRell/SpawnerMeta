package mc.rellox.spawnermeta.items;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.IntStream;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

import mc.rellox.spawnermeta.utility.Utils;
import mc.rellox.spawnermeta.utility.reflect.Reflect.RF;

public final class ItemMatcher {
	
	public static final ItemMatcher DEFAULT = new ItemMatcher(Material.GOLD_INGOT);
	
	public static boolean has(Player player, ItemStack item, int a) {
		if(Utils.op(player) == true) return true;
		PlayerInventory v = player.getInventory();
		int t = IntStream.range(0, 36).map(i -> {
			ItemStack slot = v.getItem(i);
			if(item.isSimilar(slot) == false) return 0;
			return slot.getAmount();
		}).sum();
		return t >= a;
	}
	
	public static void add(Player player, ItemStack item) {
		int f = 0, a = item.getAmount();
		PlayerInventory pi = player.getInventory();
		ItemStack slot;
		for(int i = 0; f < a && i < 36; i++) {
			if((slot = pi.getItem(i)) == null) f += 64;
			else if(slot.isSimilar(item) == true) f += 64 - slot.getAmount();
		}
		if(f >= a) pi.addItem(item);
		else {
			ItemStack give = item.clone();
			give.setAmount(f);
			ItemStack sink = item.clone();
			sink.setAmount(a - f);
			pi.addItem(give);
			Item drop = player.getWorld().dropItem(player.getLocation(), sink);
			drop.setVelocity(new Vector());
		}	
	}
	
	public static void remove(Player player, ItemStack item, int a) {
		if(Utils.op(player) == true) return;
		ItemStack r = item.clone();
		r.setAmount(a);
		player.getInventory().removeItem(r);
	}
	
	public static void parse(ItemStack item, FileConfiguration file, String path) {
		if(item == null) return;
		file.set(path + ".material", item.getType().name());
		ItemMeta meta = item.getItemMeta();
		if(meta != null) {
			if(meta.hasDisplayName() == true) file.set(path + ".name", meta.getDisplayName());
			if(meta.hasCustomModelData() == true) file.set(path + ".model", meta.getCustomModelData());
		}
	}
	
	public static ItemMatcher from(FileConfiguration file, String path) {
		if(file == null) return DEFAULT;
		Material material = RF.enumerate(Material.class, file.getString(path + ".material"));
		if(material == null) return DEFAULT;
		ItemMatcher matcher = new ItemMatcher(material);
		if(file.isString(path + ".name") == true) {
			String name = file.getString(path + ".name");
			if(name == null || name.isEmpty() == true) return DEFAULT;
			matcher.name(name);
		}
		if(file.isList(path + ".lore") == true) {
			List<String> lore = file.getStringList(path + ".lore");
			if(lore == null || lore.isEmpty() == true) return DEFAULT;
			
		}
		if(file.isInt(path + ".model") == true) {
			int model = file.getInt(path + ".model");
			matcher.model(model);
		}
		return matcher;
	}
	
	private final Material material;
	private final List<MatchData> matchers;
	
	protected ItemMatcher(Material material) {
		this.material = material;
		this.matchers = new LinkedList<>();
	}
	
	private ItemMatcher match(MatchData m) {
		matchers.add(m);
		return this;
	}
	
	protected ItemMatcher model(final int m) {
		match(new MatchData() {
			@Override
			public boolean match(ItemMeta meta) {
				return meta.getCustomModelData() == m;
			}
			@Override
			public void modify(ItemMeta meta) {
				meta.setCustomModelData(m);
			}
		});
		return this;
	}
	
	protected ItemMatcher name(String name) {
		match(new MatchData() {
			@Override
			public boolean match(ItemMeta meta) {
				return meta.getDisplayName().equals(name);
			}
			@Override
			public void modify(ItemMeta meta) {
				meta.setDisplayName(name);
			}
		});
		return this;
	}
	
	protected ItemMatcher lore(List<String> lore) {
		match(new MatchData() {
			@Override
			public boolean match(ItemMeta meta) {
				List<String> list = meta.getLore();
				if(list == null || list.isEmpty() == true) return false;
				if(list.size() != lore.size()) return false;
				return IntStream.range(0, lore.size())
						.allMatch(i -> list.get(i).equals(lore.get(i)));
			}
			@Override
			public void modify(ItemMeta meta) {
				meta.setLore(lore);
			}
		});
		return this;
	}
	
	public boolean match(ItemStack item) {
		if(item == null || item.getType() != material) return false;
		ItemMeta meta = item.getItemMeta();
		return meta == null ? false : matchers.stream().allMatch(m -> m.match(meta));
	}
	
	public ItemStack refund() {
		ItemStack item = new ItemStack(material);
		ItemMeta meta = item.getItemMeta();
		if(meta == null) return item;
		matchers.forEach(m -> m.modify(meta));
		item.setItemMeta(meta);
		return item;
	}
	
	protected interface MatchData {
		
		boolean match(ItemMeta meta);
		
		void modify(ItemMeta meta);
		
	}

}
