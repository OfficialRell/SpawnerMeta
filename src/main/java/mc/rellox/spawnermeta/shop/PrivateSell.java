package mc.rellox.spawnermeta.shop;

import mc.rellox.spawnermeta.api.spawner.IVirtual;
import mc.rellox.spawnermeta.configuration.Language;
import mc.rellox.spawnermeta.configuration.Settings;
import mc.rellox.spawnermeta.items.ItemMatcher;
import mc.rellox.spawnermeta.prices.Group;
import mc.rellox.spawnermeta.prices.Price;
import mc.rellox.spawnermeta.spawner.type.SpawnerType;
import mc.rellox.spawnermeta.utility.Utility;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class PrivateSell {

    protected final Player player;
    private final SpawnerShopSell sell;
    private final SellGroup group;

    private final List<SpawnerItem> spawners;

    private final Inventory inventory;

    public PrivateSell(Player player, SpawnerShopSell sell) {
        this.player = player;
        this.sell = sell;
        this.group = sell.group;

        this.spawners = new ArrayList<>();

        this.inventory = group.create();

        update();
    }

    public void open() {
        player.openInventory(inventory);
        player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_XYLOPHONE, 2f, 2f);
    }

    protected boolean click(InventoryClickEvent event) {
        if(!player.equals(event.getWhoClicked())) return false;
        if(!inventory.equals(event.getInventory())) return false;
        event.setCancelled(true);
        Inventory clicked = event.getClickedInventory();
        if(clicked == null) return false;
        int slot = event.getSlot();
        if(clicked.equals(inventory)) {
            if(slot == inventory.getSize() - 6) {
                give();
                sell.remove(player);
                player.closeInventory();
                player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 2f, 1f);
            } else if(slot == inventory.getSize() - 4) {
                if(spawners.isEmpty()) {
                    player.sendMessage(Language.get("Shop-sell.selling.empty").text());
                    return true;
                }
                player.sendMessage(Language.get("Shop-sell.selling.success").text());
                Price[] cs = total();
                if(cs != null) {
                    for(Price cc : cs)
                        player.sendMessage(Language.get("Shop-sell.items.selling.price",
                                "price", cc).text());
                }
                spawners.forEach(item -> item.refund(player));
                spawners.clear();
                player.playSound(player.getEyeLocation(), Sound.BLOCK_GRINDSTONE_USE, 2f, 1.5f);
                sell.remove(player);
                player.closeInventory();
            } else if(!spawners.isEmpty()) {
                if(slot >= 9 && slot < 9 + spawners.size()) {
                    int i = slot - 9;
                    SpawnerItem item = spawners.remove(i);
                    ItemMatcher.add(player, item.item);
                    player.playSound(player.getEyeLocation(), Sound.ENTITY_ITEM_FRAME_REMOVE_ITEM, 2f, 1f);
                    update();
                }
            }
        } else {
            if(spawners.size() >= inventory.getSize() - 18) return true;
            ItemStack item = clicked.getItem(slot);
            IVirtual sd = IVirtual.of(item);
            if(sd == null) return true;
            if(sd.isEmpty()) {
                player.sendMessage(Language.get("Shop-sell.selling.unable").text());
                return true;
            }
            SellData sell = group.get(sd.getType());
            if(sell == null) {
                player.sendMessage(Language.get("Shop-sell.selling.unable").text());
                return true;
            }
            if(!ShopRegistry.canSell(player, sell.type)) {
                player.sendMessage(Language.get("Shop-sell.permission.selling").text());
                player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 2f, 1f);
                return true;
            }
            int m = item.getAmount();
            int a;
            if(event.isShiftClick()) {
                a = m;
                clicked.setItem(slot, null);
            } else {
                a = 1;
                if(m > 1) {
                    ItemStack clone = item.clone();
                    clone.setAmount(m - 1);
                    clicked.setItem(slot, clone);
                } else clicked.setItem(slot, null);
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
            if(c0.type == c1.type) return new Price[] { Price.of(Group.shop, rs[0] + rs[1]) };
            return new Price[] { c0, c1 };
        }
        return new Price[] { c0 };
    }

    private void give() {
        if(!spawners.isEmpty()) {
            spawners.forEach(si -> ItemMatcher.add(player, si.item));
            spawners.clear();
        }
    }

    protected boolean close(Player player) {
        give();
        return this.player.equals(player)
                || inventory.getViewers().isEmpty();
    }

    private void update() {
        ItemStack x = group.background();
        for(int i = 0; i < 9; i++) inventory.setItem(i, x);
        for(int i = inventory.getSize() - 9; i < inventory.getSize(); i++) inventory.setItem(i, x);
        int j = 9;
        for(SpawnerItem si : spawners) inventory.setItem(j++, si.show());
        for(int e = inventory.getSize() - 9; j < e; j++) inventory.setItem(j, null);
        inventory.setItem(inventory.getSize() - 6, close());
        inventory.setItem(inventory.getSize() - 4, sell());
    }

    private ItemStack sell() {
        ItemStack item = new ItemStack(group.sell());
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(Language.get("Shop-sell.accept").text());
        Price[] cs = total();
        if(cs != null) {
            List<String> lore = new ArrayList<>();
            lore.add("");
            lore.add(Language.get("Shop-sell.items.selling.name").text());
            for(Price c : cs)
                lore.add(Language.get("Shop-sell.items.selling.price",
                        "price", c).text());
            meta.setLore(lore);
        }
        meta.addItemFlags(Utility.ITEM_FLAGS);
        meta.addEnchant(Utility.enchantment_power, 1, true);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack close() {
        ItemStack item = new ItemStack(group.close());
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(Language.get("Shop-sell.cancel").text());
        meta.addItemFlags(Utility.ITEM_FLAGS);
        meta.addEnchant(Utility.enchantment_power, 1, true);
        item.setItemMeta(meta);
        return item;
    }

    private static class SpawnerItem {

        private final SellData sell;
        public final ItemStack item;
        public final IVirtual spawner;
        private final int amount;
        public final int[] refund;

        public SpawnerItem(SellData sell, ItemStack item, IVirtual spawner, int amount) {
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
            lore.add(Language.get("Shop-sell.items.selling.name").text());
            Price[] cs = prices();
            for(Price c : cs)
                lore.add(Language.get("Shop-sell.items.selling.price",
                        "price", c).text());
            meta.setLore(lore);
            meta.addItemFlags(Utility.ITEM_FLAGS);
            clone.setItemMeta(meta);
            return clone;
        }

        public Price[] prices() {
            Price c0 = Price.of(Group.shop, refund[0]);
            if(refund[1] > 0) {
                Price c1 = Price.of(Group.upgrades, refund[1]);
                if(c0.type == c1.type) return new Price[] { Price.of(Group.shop, c0.value + c1.value) };
                return new Price[] { c0, c1 };
            }
            return new Price[] { c0 };
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
                if(bs[i]) {
                    int c = vs[i];
                    for(int j = 1; j < ls[i]; j++) {
                        a += c;
                        c = Settings.settings.upgrade_increase_type.price(c, is[i]);
                    }
                }
            }
            return new int[] { t * amount, (int) (a * sell.up) * amount };
        }

    }

}
