package mc.rellox.spawnermeta.shop;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

import mc.rellox.spawnermeta.SpawnerMeta;
import mc.rellox.spawnermeta.configuration.Language;

public class SpawnerShopSell implements Listener {
	
	protected final SellGroup group;
	private final List<PrivateSell> list;
	
	public SpawnerShopSell(SellGroup group) {
		this.group = group;
		this.list = new LinkedList<>();
		Bukkit.getPluginManager().registerEvents(this, SpawnerMeta.instance());
	}
	
	public void open(Player player) {
		if(player.hasPermission("spawnermeta.shop.sell.open") == false) {
			player.sendMessage(Language.get("Shop-sell.permission.opening").text());
			player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 2f, 1f);
			return;
		}
		PrivateSell p = new PrivateSell(player, this);
		p.open();
		list.add(p);
	}
	
	protected void remove(Player player) {
		list.removeIf(p -> p.player.equals(player));
	}
	
	@EventHandler
	private void onClick(InventoryClickEvent event) {
		try {
			for(PrivateSell p : list) if(p.click(event) == true) return;
		} catch (Exception e) {}
	}

	@EventHandler
	private void onClose(InventoryCloseEvent event) {
		Player player = (Player) event.getPlayer();
		list.removeIf(p -> p.close(player) == true);
	}
	
	public void unregister() {
		HandlerList.unregisterAll(this);
	}

}
