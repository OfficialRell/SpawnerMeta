package mc.rellox.spawnermeta.shop;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import mc.rellox.spawnermeta.api.spawner.VirtualSpawner;
import mc.rellox.spawnermeta.configuration.Language;
import mc.rellox.spawnermeta.configuration.Settings;
import mc.rellox.spawnermeta.items.ItemMatcher;
import mc.rellox.spawnermeta.prices.Group;
import mc.rellox.spawnermeta.prices.Price;
import mc.rellox.spawnermeta.spawner.SpawnerType;
import mc.rellox.spawnermeta.utils.DataManager;

public class PrivateSell {
	
	protected final Player player;
	private final SpawnerShopSell sell;
	private final SellGroup group;
	
	private final List<SpawnerItem> spawners;
	
	private final Inventory v;
	
	public PrivateSell(Player player, SpawnerShopSell sell) {
		this.player = player;
		this.sell = sell;
		this.group = sell.group;
		
		this.spawners = new ArrayList<>();
		
		this.v = group.create();
		
		update();
	}
	
	public void open() {
		player.openInventory(v);
		player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_XYLOPHONE, 2f, 2f);
	}
	
	protected boolean click(InventoryClickEvent event) {
		if(player.equals(event.getWhoClicked()) == false) return false;
		Inventory n = event.getInventory();
		if(v.equals(n) == false) return false;
		event.setCancelled(true);
		Inventory c = event.getClickedInventory();
		if(c == null) return false;
		int s = event.getSlot();
		if(c.equals(v) == true) {
			if(s == v.getSize() - 6) {
				give();
				sell.remove(player);
				player.closeInventory();
				player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 2f, 1f);
			} else if(s == v.getSize() - 4) {
				if(spawners.isEmpty() == true) {
					player.sendMessage(Language.get("Inventory.sell-shop.selling.empty").text());
					return true;
				}
				player.sendMessage(Language.get("Inventory.sell-shop.selling.success").text());
				Price[] cs = total();
				for(Price cc : cs) player.sendMessage(Language.get("Inventory.sell-shop.items.selling.price",
						"price", cc).text());
				spawners.forEach(si -> si.refund(player));
				spawners.clear();
				player.playSound(player.getEyeLocation(), Sound.BLOCK_GRINDSTONE_USE, 2f, 1.5f);
				sell.remove(player);
				player.closeInventory();
			} else if(spawners.isEmpty() == false) {
				if(s >= 9 && s < 9 + spawners.size()) {
					int i = s - 9;
					SpawnerItem si = spawners.remove(i);
					ItemMatcher.add(player, si.item);
					player.playSound(player.getEyeLocation(), Sound.ENTITY_ITEM_FRAME_REMOVE_ITEM, 2f, 1f);
					update();
				}
			}
		} else {
			if(spawners.size() >= v.getSize() - 18) return true;
			ItemStack item = c.getItem(s);
			VirtualSpawner sd = DataManager.getSpawnerItem(item);
			if(sd == null) return true;
			if(sd.isEmpty() == true) {
				player.sendMessage(Language.get("Inventory.sell-shop.selling.unable").text());
				return true;
			}
			SellData sell = group.get(sd.getType());
			if(sell == null) {
				player.sendMessage(Language.get("Inventory.sell-shop.selling.unable").text());
				return true;
			}
			if(ShopRegistry.canSell(player, sell.type) == false) {
				player.sendMessage(Language.get("Inventory.sell-shop.permission.selling").text());
				player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 2f, 1f);
				return true;
			}
			int m = item.getAmount();
			int a;
			if(event.isShiftClick() == true) {
				a = m;
				c.setItem(s, null);
			} else {
				a = 1;
				if(m > 1) {
					ItemStack clone = item.clone();
					clone.setAmount(m - 1);
					c.setItem(s, clone);
				} else c.setItem(s, null);
			}
			spawners.add(new SpawnerItem(sell, item.clone(), sd, a));
			player.playSound(player.getEyeLocation(), Sound.ENTITY_ITEM_FRAME_PLACE, 2f, 1.5f);
			update();
		}
		return true;
	}
	
	private Price[] total() {
		int[] rs = new int[2];
		for(SpawnerItem si : spawners) {
			rs[0] += si.refund[0];
			rs[1] += si.refund[1];
		}
		if(rs[0] <= 0) return null;
		Price c0 = Price.of(Group.shop, rs[0]);
		if(rs[1] > 0) {
			Price c1 = Price.of(Group.upgrades, rs[1]);
			if(c0.type == c1.type) return new Price[] {Price.of(Group.shop, rs[0] + rs[1])};
			return new Price[] {c0, c1};
		}
		return new Price[] {c0};
	}
	
	private void give() {
		if(spawners.isEmpty() == false) {
			spawners.forEach(si -> ItemMatcher.add(player, si.item));
			spawners.clear();
		}
	}
	
	protected boolean close(Player player) {
		give();
		return this.player.equals(player) == true
				|| v.getViewers().size() == 0;
	}
	
	private void update() {
		ItemStack x = group.x();
		for(int i = 0; i < 9; i++) v.setItem(i, x);
		for(int i = v.getSize() - 9; i < v.getSize(); i++) v.setItem(i, x);
		int j = 9;
		for(SpawnerItem si : spawners) v.setItem(j++, si.show());
		for(int e = v.getSize() - 9; j < e; j++) v.setItem(j, null);
		v.setItem(v.getSize() - 6, close());
		v.setItem(v.getSize() - 4, sell());
	}
	
	private ItemStack sell() {
		ItemStack item = new ItemStack(group.sell());
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(Language.get("Inventory.sell-shop.accept").text());
		Price[] cs = total();
		if(cs != null) {
			List<String> lore = new ArrayList<>();
			lore.add("");
			lore.add(Language.get("Inventory.sell-shop.items.selling.name").text());
			for(Price c : cs) lore.add(Language.get("Inventory.sell-shop.items.selling.price",
					"price", c).text());
			meta.setLore(lore);
		}
		meta.addItemFlags(ItemFlag.values());
		meta.addEnchant(Enchantment.ARROW_DAMAGE, 0, true);
		item.setItemMeta(meta);
		return item;
	}
	
	private ItemStack close() {
		ItemStack item = new ItemStack(group.close());
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(Language.get("Inventory.sell-shop.cancel").text());
		meta.addItemFlags(ItemFlag.values());
		meta.addEnchant(Enchantment.ARROW_DAMAGE, 0, true);
		item.setItemMeta(meta);
		return item;
	}
	
	private class SpawnerItem {
		
		private final SellData sell;
		public final ItemStack item;
		public final VirtualSpawner spawner;
		private final int amount;
		public final int[] refund;
		
		public SpawnerItem(SellData sell, ItemStack item, VirtualSpawner spawner, int amount) {
			this.sell = sell;
			this.item = item;
			item.setAmount(amount);
			this.spawner = spawner;
			this.amount = amount;
			this.refund = refund();
		}
		
		public ItemStack show() {
			ItemStack clone = item.clone();
			ItemMeta meta = clone.getItemMeta();
			List<String> lore = meta.getLore();
			if(lore == null) lore = new ArrayList<>();
			lore.add("");
			lore.add(Language.get("Inventory.sell-shop.items.selling.name").text());
			Price[] cs = prices();
			for(Price c : cs) lore.add(Language.get("Inventory.sell-shop.items.selling.price",
					"price", c).text());
			meta.setLore(lore);
			clone.setItemMeta(meta);
			return clone;
		}
		
		public Price[] prices() {
			Price c0 = Price.of(Group.shop, refund[0]);
			if(refund[1] > 0) {
				Price c1 = Price.of(Group.upgrades, refund[1]);
				if(c0.type == c1.type) return new Price[] {Price.of(Group.shop, c0.value + c1.value)};
				return new Price[] {c0, c1};
			}
			return new Price[] {c0};
		}
		
		public void refund(Player player) {
			Price[] cs = prices();
			for(Price c : cs) c.refund(player);
		}
		
		private int[] refund() {
			int t = sell.refund;
			SpawnerType type = spawner.getType();
			boolean[] bs = Settings.settings.upgrades_upgradeable.get(type);
			int[] vs = Settings.settings.upgrades_prices.get(type);
			int[] is = Settings.settings.spawner_value_increase.get(type);
			int[] ls = spawner.getUpgradeLevels();
			int a = 0;
			for(int i = 0; i < 3; i++) {
				if(bs[i] == true) {
					int c = vs[i];
					for(int j = 1; j < ls[i]; j++) {
						a += c;
						c = Settings.settings.upgrade_increase_type.price(c, is[i]);
					}
				}
			}
			return new int[] {t * amount, (int) (a * sell.up) * amount};
		}
		
	}

}
