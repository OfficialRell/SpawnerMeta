package mc.rellox.spawnermeta.shop;

import mc.rellox.spawnermeta.SpawnerMeta;
import mc.rellox.spawnermeta.configuration.Language;
import mc.rellox.spawnermeta.utility.Utility;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.stream.Stream;

public class ShopSelection implements Listener {

    private final SpawnerShopBuy buy;
    private final SpawnerShopSell sell;

    private final Inventory inventory;

    public ShopSelection(SpawnerShopBuy buy, SpawnerShopSell sell, Material filler, Material mbuy, Material msell) {
        this.buy = buy;
        this.sell = sell;
        this.inventory = Bukkit.createInventory(null, 9, Language.get("Shop-select.name").text());
        Bukkit.getPluginManager().registerEvents(this, SpawnerMeta.instance());
        update(filler, mbuy, msell);
    }

    public void open(Player player) {
        if(!player.hasPermission("spawnermeta.shop.selection.open")) {
            player.sendMessage(Language.get("Shop-select.permission.opening").text());
            player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 2f, 1f);
            return;
        }
        player.openInventory(inventory);
        player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_XYLOPHONE, 2f, 2f);
    }

    @EventHandler
    private void onClick(InventoryClickEvent event) {
        if(!inventory.equals(event.getClickedInventory())) return;
        event.setCancelled(true);
        Player player = (Player) event.getWhoClicked();
        int s = event.getSlot();
        if(s == 2) buy.open(player);
        else if(s == 6) sell.open(player);
    }

    private void update(Material filler, Material mbuy, Material msell) {
        ItemStack x = background(filler);
        for(int i = 0; i < inventory.getSize(); i++) inventory.setItem(i, x);
        inventory.setItem(2, buy(mbuy));
        inventory.setItem(6, sell(msell));
    }

    private ItemStack buy(Material m) {
        ItemStack item = new ItemStack(m);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(Language.get("Shop-select.buy-shop").text());
        meta.addItemFlags(Stream.of(ItemFlag.values())
                .filter(i -> i.ordinal() < 8)
                .toArray(ItemFlag[]::new));
        meta.addEnchant(Utility.enchantment_power, 1, true);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack sell(Material m) {
        ItemStack item = new ItemStack(m);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(Language.get("Shop-select.sell-shop").text());
        meta.addItemFlags(Stream.of(ItemFlag.values())
                .filter(i -> i.ordinal() < 8)
                .toArray(ItemFlag[]::new));
        meta.addEnchant(Utility.enchantment_power, 1, true);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack background(Material m) {
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
