package mc.rellox.spawnermeta.view;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import mc.rellox.spawnermeta.SpawnerMeta;
import mc.rellox.spawnermeta.commands.CommandManager;
import mc.rellox.spawnermeta.configuration.Language;
import mc.rellox.spawnermeta.configuration.Settings;
import mc.rellox.spawnermeta.spawner.type.SpawnerType;
import mc.rellox.spawnermeta.utility.DataManager;

public final class SpawnerView implements Listener {
	
	private static final ItemStack x = x();
	private final List<SpawnerType> types = Settings.settings.spawner_view_entities;
	
	private final Player player;
	private final Inventory v;
	private int page;
	private final int total;
	
	public SpawnerView(Player player) {
		this.player = player;
		this.total = types.size() / 27;
		this.v = Bukkit.createInventory(null, 36, Language.get("Spawner-view.name").text());
		update();
		Bukkit.getPluginManager().registerEvents(this, SpawnerMeta.instance());
		
		player.openInventory(v);
		player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_XYLOPHONE, 2f, 2f);
	}
	
	private void update() {
		for(int i = 0; i < v.getSize(); i++) v.setItem(i, x);
		
		int a = page * 27, b = (Math.min(types.size(), (page + 1) * 27) - 1) % 27;
		for(int i = 0; i <= b; i++) {
			if(i + a >= types.size()) break;
			v.setItem(i, Settings.settings.view_item(types.get(i + a)));
		}
		
		if(page > 0) v.setItem(29, previous());
		if(page < total) v.setItem(33, next());
		if(total > 0) v.setItem(31, page(page + 1));
	}
	
	public void close() {
		HandlerList.unregisterAll(this);
	}
	
	@EventHandler
	private void onClick(InventoryClickEvent event) {
		if(player.isOnline() == true) {
			if(v.equals(event.getClickedInventory()) == false) return;
			if(player.equals(event.getWhoClicked()) == false) return;
			event.setCancelled(true);
			int s = event.getSlot();
			if(s == 33) {
				if(page >= total) return;
				page++;
				update();
				player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_XYLOPHONE, 2f, 2f);
			} else if(s == 29) {
				if(page <= 0) return;
				page--;
				update();
				player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_XYLOPHONE, 2f, 2f);
			} else if(player.getGameMode() == GameMode.CREATIVE && player.isOp() == true) {
				int l = (Math.min(types.size(), (page + 1) * 27) - 1) % 27;
				if(s > l) return;
				SpawnerType type = types.get(page * 27 + s);
				if(type.exists() == false) return;
				int a;
				ClickType click = event.getClick();
				if(click.isShiftClick() == true) a = 64;
				else if(click.isRightClick() == true) a = 8;
				else a = 1;
				CommandManager.success(player, "Added #0 × #1 to your inventory!", a, type.formated() + " Spawner");
				player.getInventory().addItem(DataManager.getSpawners(type, a, false, true).get(0));
				player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 1f, 2f);
			}
		} else close();
	}

	@EventHandler
	private void onClose(InventoryCloseEvent event) {
		if(v.equals(event.getInventory()) == false) return;
		close();
	}
	
	private ItemStack next() {
		ItemStack item = new ItemStack(Material.SPECTRAL_ARROW);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(Language.get("Spawner-view.items.page.next").text());
		item.setItemMeta(meta);
		return item;
	}
	
	private ItemStack page(int p) {
		ItemStack item = new ItemStack(Material.PAPER, p);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(Language.get("Spawner-view.items.page.current",
				"page", p).text());
		item.setItemMeta(meta);
		return item;
	}
	
	private ItemStack previous() {
		ItemStack item = new ItemStack(Material.SPECTRAL_ARROW);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(Language.get("Spawner-view.items.page.previous").text());
		item.setItemMeta(meta);
		return item;
	}
	
	private static ItemStack x() {
		ItemStack item = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(" ");
		item.setItemMeta(meta);
		return item;
	}

}
