package mc.rellox.spawnermeta.shop;

import mc.rellox.spawnermeta.SpawnerMeta;
import mc.rellox.spawnermeta.configuration.Language;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

import java.util.LinkedList;
import java.util.List;

public class SpawnerShopSell implements Listener {

    protected final SellGroup group;
    private final List<PrivateSell> list;

    public SpawnerShopSell(SellGroup group) {
        this.group = group;
        this.list = new LinkedList<>();
        Bukkit.getPluginManager().registerEvents(this, SpawnerMeta.instance());
    }

    public void open(Player player) {
        if(!player.hasPermission("spawnermeta.shop.sell.open")) {
            player.sendMessage(Language.get("Shop-sell.permission.opening").text());
            player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 2f, 1f);
            return;
        }
        PrivateSell sell = new PrivateSell(player, this);
        sell.open();
        list.add(sell);
    }

    protected void remove(Player player) {
        list.removeIf(sell -> sell.player.equals(player));
    }

    @EventHandler
    private void onClick(InventoryClickEvent event) {
        try {
            for(PrivateSell sell : list) if(sell.click(event)) return;
        } catch(Exception ignored) {}
    }

    @EventHandler
    private void onClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        list.removeIf(sell -> sell.close(player));
    }

    public void unregister() {
        HandlerList.unregisterAll(this);
    }

}
