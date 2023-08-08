package mc.rellox.spawnermeta.shop;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import mc.rellox.spawnermeta.SpawnerMeta;
import mc.rellox.spawnermeta.configuration.Language;
import mc.rellox.spawnermeta.prices.Group;
import mc.rellox.spawnermeta.prices.Price;

public class SpawnerShopBuy implements Listener {
	
	private final BuyData[] data;
	private final boolean[] bs;
	private final Inventory[] vs;
	private final int r0, r1;
	
	public SpawnerShopBuy(Material filler, int rows, boolean[] bs, BuyData... data) {
		this.r1 = rows * 9;
		this.r0 = r1 - 9;
		this.data = data;
		this.bs = bs;
		int l = data.length;
		int i = l > r1 ? l / r0 + 1 : 1;
		this.vs = new Inventory[i];
		for(i = 0; i < vs.length; i++) vs[i] = Bukkit.createInventory(null, r1, Language.get("Shop-buy.name",
				"page_current", i + 1, "page_total", vs.length).text());
		Bukkit.getPluginManager().registerEvents(this, SpawnerMeta.instance());
		update(filler);
	}
	
	private void update(Material filler) {
		int k = 0;
		ItemStack x = x(filler);
		if(vs.length == 1) {
			for(int i = 0; i < r1; i++) vs[0].setItem(i, x);
			do vs[0].setItem(k, data[k].info(bs)); while(++k < data.length);
		} else {
			int p = 0;
			for(Inventory v : vs) {
				for(int i = 0; i < r1; i++) v.setItem(i, x);
				int m = Math.min(k + r0, data.length);
				int r = r0 * p;
				do v.setItem(k - r, data[k].info(bs)); while(++k < m);
				if(vs.length > 1) {
					if(p < vs.length - 1) v.setItem(v.getSize() - 1, next());
					if(p > 0) v.setItem(v.getSize() - 9, previous());
					v.setItem(v.getSize() - 5, page(p + 1));
				}
				p++;
			}
		}
	}
	
	public void open(Player player) {
		open(player, 0);
	}
	
	private void open(Player player, int i) {
		if(player.hasPermission("spawnermeta.shop.buy.open") == false) {
			player.sendMessage(Language.get("Shop-buy.permission.opening").text());
			player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 2f, 1f);
			return;
		}
		if(i >= vs.length) return;
		player.openInventory(vs[i]);
		player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_XYLOPHONE, 2f, 2f);
	}
	
	@EventHandler
	private void onClick(InventoryClickEvent event) {
		try {
			Inventory c = event.getClickedInventory();
			if(c == null) return;
			int p = 0;
			for(Inventory v : vs) {
				if(v.equals(c) == false) p++;
				else {
					event.setCancelled(true);
					Player player = (Player) event.getWhoClicked();
					int s = event.getSlot();
					int o = s + p * r0;
					if(s < (vs.length > 1 ? r0 : r1) && o < data.length) {
						if(player.hasPermission("spawnermeta.shop.buy.purchase") == false) {
							player.sendMessage(Language.get("Shop-buy.permission.purchase").text());
							player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 2f, 1f);
							return;
						}
						BuyData sd = data[o];
						if(sd == null) return;
						if(ShopRegistry.canBuy(player, sd.type()) == false) {
							player.sendMessage(Language.get("Shop-buy.permission.purchase").text());
							player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 2f, 1f);
							return;
						}
						ClickType click = event.getClick();
						if(click == ClickType.SHIFT_LEFT) {
							if(bs[2] == false) return;
							sd.buy(player, 16);
						} else if(click == ClickType.SHIFT_RIGHT) {
							if(bs[3] == false) return;
							int b = Price.of(Group.shop, 0).balance(player);
							int a = b / sd.value();
							if(a <= 0) return;
							sd.buy(player, a);
						} else if(click == ClickType.LEFT) {
							if(bs[0] == false) return;
							sd.buy(player, 1);
						} else if(click == ClickType.RIGHT) {
							if(bs[1] == false) return;
							sd.buy(player, 4);
						}
					} else {
						if(s == (v.getSize() - 9) && p > 0) open(player, p - 1);
						else if(s == (v.getSize() - 1) && p < vs.length - 1) open(player, p + 1);
					}
					return;
				}
			}
		} catch (Exception e) {}
	}
	
	private ItemStack next() {
		ItemStack item = new ItemStack(ShopRegistry.buy_next);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(Language.get("Shop-buy.items.page.next").text());
		item.setItemMeta(meta);
		return item;
	}
	
	private ItemStack page(int p) {
		ItemStack item = new ItemStack(ShopRegistry.buy_page, p);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(Language.get("Shop-buy.items.page.current",
				"page", p).text());
		item.setItemMeta(meta);
		return item;
	}
	
	private ItemStack previous() {
		ItemStack item = new ItemStack(ShopRegistry.buy_prev);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(Language.get("Shop-buy.items.page.previous").text());
		item.setItemMeta(meta);
		return item;
	}
	
	private ItemStack x(Material m) {
		ItemStack item = new ItemStack(m);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(" ");
		item.setItemMeta(meta);
		return item;
	}
	
	public void unregister() {
		HandlerList.unregisterAll(this);
	}

}
