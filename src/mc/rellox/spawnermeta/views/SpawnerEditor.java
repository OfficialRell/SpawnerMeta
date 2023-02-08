package mc.rellox.spawnermeta.views;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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

import mc.rellox.spawnermeta.SpawnerMeta;
import mc.rellox.spawnermeta.utils.Utils;
import mc.rellox.spawnermeta.views.SpawnerViewLayout.Slot;

public class SpawnerEditor implements Listener {
	
	private static Player editor;
	private static SpawnerEditor edit;
	
	public static void open(Player player) {
		if(editor != null && edit != null) {
			if(editor.isOnline() == false) editor = null;
			else {
				Inventory t = editor.getOpenInventory().getTopInventory();
				if(t == null) editor = null;
				else if(t.equals(edit.v) == false) editor = null;
				else {
					player.sendMessage(ChatColor.DARK_RED + "(!) " + ChatColor.GOLD + "There already is someone editing!");
					player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 2f, 1f);
					return;
				}
			}
		}
		edit = new SpawnerEditor(editor = player);
		player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_XYLOPHONE, 2f, 2f);
		player.sendMessage(ChatColor.DARK_GREEN + "(!) " + ChatColor.DARK_AQUA + "Opening Spawner GUI Editor!");
	}
	
	private final Player player;
	private final Inventory v;
	
	private SpawnerEditor(Player player) {
		this.player = player;
		this.v = Bukkit.createInventory(null, 36, "Spawner GUI Editor");
		SpawnerViewLayout.setEditor(v);
		Bukkit.getPluginManager().registerEvents(this, SpawnerMeta.instance());
		player.openInventory(v);
	}
	
	@EventHandler
	private void onClick(InventoryClickEvent event) {
		if(check() == false) return;
		if(player.equals(event.getWhoClicked()) == false) return;
		Inventory c = event.getClickedInventory();
		if(c == null || c.equals(v) == false) return;
		event.setCancelled(true);
		int s = event.getSlot();
		ClickType t = event.getClick();
		if(s >= 0 && s < 27) {
			Slot l = SpawnerViewLayout.LAYOUT[s];
			if(l.c == false) return;
			else {
				if(t == ClickType.RIGHT) {
					if(s == 26) return;
					SpawnerViewLayout.LAYOUT[s] = SpawnerViewLayout.LAYOUT[s + 1];
					SpawnerViewLayout.LAYOUT[s + 1] = l;
					SpawnerViewLayout.updateLayout();
					player.playSound(player.getEyeLocation(), Sound.ENTITY_ITEM_FRAME_ROTATE_ITEM, 2f, 1.5f);
				} else if(t == ClickType.LEFT) {
					if(s == 0) return;
					SpawnerViewLayout.LAYOUT[s] = SpawnerViewLayout.LAYOUT[s - 1];
					SpawnerViewLayout.LAYOUT[s - 1] = l;
					SpawnerViewLayout.updateLayout();
					player.playSound(player.getEyeLocation(), Sound.ENTITY_ITEM_FRAME_ROTATE_ITEM, 2f, 1.5f);
				} else if(t == ClickType.MIDDLE) {
					ItemStack cu = event.getCursor();
					if(Utils.nulled(cu) == true || cu.getType() == l.m) return;
					l.m = cu.getType();
					player.playSound(player.getEyeLocation(), Sound.ENTITY_ITEM_FRAME_ADD_ITEM, 2f, 1.25f);
				} else if(t == ClickType.SHIFT_LEFT || t == ClickType.SHIFT_RIGHT) {
					l.g = !l.g;
					player.playSound(player.getEyeLocation(), l.g ? Sound.BLOCK_BEACON_ACTIVATE : Sound.BLOCK_BEACON_DEACTIVATE, 0.75f, 2f);
				}
			}
		} else if(s == 31) {
			ItemStack item = event.getCursor();
			if(t.isShiftClick() == true) {
				if(SpawnerViewLayout.background == null) return;
				SpawnerViewLayout.background = null;
				SpawnerViewLayout.background_model = 0;
			} else {
				if(Utils.nulled(item) == true || item.getType() == SpawnerViewLayout.background) return;
				SpawnerViewLayout.background = item.getType();
				if(item.hasItemMeta() == true)
					SpawnerViewLayout.background_model = item.getItemMeta().getCustomModelData();
			}
			SpawnerViewLayout.setBackground();
			player.playSound(player.getEyeLocation(), Sound.ENTITY_ITEM_FRAME_ADD_ITEM, 2f, 1.25f);
		}
		SpawnerViewLayout.setEditor(v);
	}

	@EventHandler
	private void onClose(InventoryCloseEvent event) {
		if(event.getPlayer().equals(player) == false) return;
		player.sendMessage(ChatColor.DARK_GREEN + "(!) " + ChatColor.DARK_AQUA + "Saving and closing editor!");
		player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_XYLOPHONE, 0.75f, 0f);
		unregister();
	}
	
	private boolean check() {
		boolean o = player.isOnline();
		if(o == false) unregister();
		return o;
	}
	
	private void unregister() {
		HandlerList.unregisterAll(this);
		edit = null;
		editor = null;
		SpawnerViewLayout.saveSlots();
	}

}
