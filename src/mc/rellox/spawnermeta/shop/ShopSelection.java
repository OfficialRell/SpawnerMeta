package mc.rellox.spawnermeta.shop;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import mc.rellox.spawnermeta.SpawnerMeta;
import mc.rellox.spawnermeta.configuration.Language;

public class ShopSelection implements Listener {
	
	private final SpawnerShopBuy buy;
	private final SpawnerShopSell sell;
	
	private final Inventory v;
	
	public ShopSelection(SpawnerShopBuy buy, SpawnerShopSell sell, Material filler, Material mbuy, Material msell) {
		this.buy = buy;
		this.sell = sell;
		this.v = Bukkit.createInventory(null, 9, Language.get("Inventory.select-shop.name").text());
		Bukkit.getPluginManager().registerEvents(this, SpawnerMeta.instance());
		update(filler, mbuy, msell);
	}
	
	public void open(Player player) {
		if(player.hasPermission("spawnermeta.shop.selection.open") == false) {
			player.sendMessage(Language.get("Inventory.select-shop.permission.opening").text());
			player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 2f, 1f);
			return;
		}
		player.openInventory(v);
		player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_XYLOPHONE, 2f, 2f);
	}
	
	@EventHandler
	private void onClick(InventoryClickEvent event) {
		if(v.equals(event.getClickedInventory()) == false) return;
		event.setCancelled(true);
		Player player = (Player) event.getWhoClicked();
		int s = event.getSlot();
		if(s == 2) buy.open(player);
		else if(s == 6) sell.open(player);
	}
	
	private void update(Material filler, Material mbuy, Material msell) {
		ItemStack x = x(filler);
		for(int i = 0; i < v.getSize(); i++) v.setItem(i, x);
		v.setItem(2, buy(mbuy));
		v.setItem(6, sell(msell));
	}
	
	private ItemStack buy(Material m) {
		ItemStack item = new ItemStack(m);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(Language.get("Inventory.select-shop.buy-shop").text());
		meta.addItemFlags(ItemFlag.values());
		meta.addEnchant(Enchantment.ARROW_DAMAGE, 0, true);
		item.setItemMeta(meta);
		return item;
	}
	
	private ItemStack sell(Material m) {
		ItemStack item = new ItemStack(m);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(Language.get("Inventory.select-shop.sell-shop").text());
		meta.addItemFlags(ItemFlag.values());
		meta.addEnchant(Enchantment.ARROW_DAMAGE, 0, true);
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
