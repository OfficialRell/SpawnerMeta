package mc.rellox.spawnermeta.items;

import mc.rellox.spawnermeta.text.Text;
import mc.rellox.spawnermeta.text.content.Content;
import mc.rellox.spawnermeta.text.content.ContentParser;
import mc.rellox.spawnermeta.utility.Utility;
import mc.rellox.spawnermeta.utility.reflect.Reflect.RF;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.IntStream;

@SuppressWarnings("deprecation")
public final class ItemMatcher {

    public static final ItemMatcher DEFAULT = new ItemMatcher(Material.GOLD_INGOT);

    public static boolean has(Player player, ItemStack item, int amount) {
        if(Utility.op(player)) return true;
        PlayerInventory inventory = player.getInventory();
        int similar = IntStream.range(0, 36).map(i -> {
            ItemStack slot = inventory.getItem(i);
            if(!item.isSimilar(slot) || slot == null) return 0;
            return slot.getAmount();
        }).sum();
        return similar >= amount;
    }

    public static int free(Player player) {
        PlayerInventory inventory = player.getInventory();
        return IntStream.range(0, 36)
                .map(i -> Utility.nulled(inventory.getItem(i)) ? 1 : 0)
                .sum();
    }

    public static void add(Player player, ItemStack item) {
        PlayerInventory inventory = player.getInventory();
        var skipped = inventory.addItem(item);
        if(!skipped.isEmpty()) {
            for(ItemStack stack : skipped.values()) {
                Item drop = player.getWorld().dropItem(player.getLocation(), stack);
                drop.setVelocity(new Vector());
            }
        }
    }

    public static void remove(Player player, ItemStack item, int amount) {
        if(Utility.op(player)) return;
        ItemStack clone = item.clone();
        clone.setAmount(amount);
        player.getInventory().removeItem(clone);
    }

    public static void parse(ItemStack item, FileConfiguration file, String path) {
        if(item == null) return;
        file.set(path + ".material", item.getType().name());
        ItemMeta meta = item.getItemMeta();
        if(meta != null) {
            if(meta.hasDisplayName()) file.set(path + ".name", meta.getDisplayName());
            if(meta.hasCustomModelData()) file.set(path + ".model", meta.getCustomModelData());
        }
    }

    public static ItemMatcher from(FileConfiguration file, String path) {
        if(file == null) return DEFAULT;
        Material material = RF.enumerate(Material.class, file.getString(path + ".material"));
        if(material == null) return DEFAULT;
        ItemMatcher matcher = new ItemMatcher(material);
        if(file.isString(path + ".name")) {
            String name = file.getString(path + ".name");
            if(name == null || name.isEmpty()) return DEFAULT;
            matcher.name(ContentParser.parse(name));
        }
        if(file.isList(path + ".lore")) {
            List<String> lore = file.getStringList(path + ".lore");
            if(lore.isEmpty()) return DEFAULT;
            matcher.lore(ContentParser.parse(lore));
        }
        if(file.isInt(path + ".model")) {
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

    private ItemMatcher match(MatchData data) {
        matchers.add(data);
        return this;
    }

    protected ItemMatcher model(final int model) {
        match(new MatchData() {
            @Override
            public boolean match(ItemMeta meta) {
                return meta.getCustomModelData() == model;
            }

            @Override
            public void modify(ItemMeta meta) {
                meta.setCustomModelData(model);
            }
        });
        return this;
    }

    protected ItemMatcher name(Content name) {
        match(new MatchData() {
            final String stripped = ChatColor.stripColor(name.text());

            @Override
            public boolean match(ItemMeta meta) {
                String in = meta.getDisplayName();
                return ChatColor.stripColor(in).equals(stripped);
            }

            @Override
            public void modify(ItemMeta meta) {
                meta.setDisplayName(name.text());
            }
        });
        return this;
    }

    protected ItemMatcher lore(List<Content> lore) {
        match(new MatchData() {
            final List<String> stripped = lore.stream()
                    .map(Content::text)
                    .map(ChatColor::stripColor)
                    .toList();

            @Override
            public boolean match(ItemMeta meta) {
                List<String> list = meta.getLore();
                if(list == null || list.isEmpty()) return false;
                if(list.size() != lore.size()) return false;
                return IntStream.range(0, stripped.size())
                        .allMatch(i -> ChatColor.stripColor(list.get(i)).equals(stripped.get(i)));
            }

            @Override
            public void modify(ItemMeta meta) {
                meta.setLore(Text.toText(lore));
            }
        });
        return this;
    }

    public boolean match(ItemStack item) {
        if(item == null || item.getType() != material) return false;
        ItemMeta meta = item.getItemMeta();
        return meta != null && matchers.stream().allMatch(m -> m.match(meta));
    }

    public ItemStack refund() {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if(meta == null) return item;
        matchers.forEach(data -> data.modify(meta));
        item.setItemMeta(meta);
        return item;
    }

    protected interface MatchData {

        boolean match(ItemMeta meta);

        void modify(ItemMeta meta);

    }

}
