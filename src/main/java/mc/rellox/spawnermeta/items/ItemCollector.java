package mc.rellox.spawnermeta.items;

import mc.rellox.spawnermeta.SpawnerMeta;
import mc.rellox.spawnermeta.configuration.Language;
import mc.rellox.spawnermeta.configuration.Settings;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ClickEvent.Action;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.*;
import java.util.stream.Stream;

public class ItemCollector implements Listener {

    private static final Map<UUID, ItemCollector> ITEMS = new HashMap<>();
    private static boolean running;

    public static void add(Player player, ItemStack item) {
        if(Settings.settings.breaking_drop_on_ground) return;
        ItemCollector collector = ITEMS.computeIfAbsent(player.getUniqueId(), k -> new ItemCollector(player));
        collector.add(item);
    }

    public static boolean exists(Player player) {
        return get(player) != null;
    }

    public static void execute(Player player) {
        if(Settings.settings.breaking_drop_on_ground) return;
        ItemCollector collector = ITEMS.get(player.getUniqueId());
        if(collector == null) return;
        if(collector.get(true)) {
            unregister(player);
            return;
        }
        collector.remind();
        run();
    }

    private static void run() {
        if(running) return;
        running = true;
        SpawnerMeta.scheduler().runTimer(task -> {
            ITEMS.values().removeIf(collector -> !collector.tick());
            if(!(running = !ITEMS.isEmpty())) task.cancel();
        }, 1, 1);
    }

    private static void send(Player player, int ticks) {
        @SuppressWarnings("deprecation")
        BaseComponent[] text = TextComponent.fromLegacyText(Language.get("Items.spawner-drop.alert",
                "seconds", ticks / 20).text());
        ComponentBuilder builder = new ComponentBuilder("");
        Stream.of(text).forEach(builder::append);
        builder.event(new ClickEvent(Action.RUN_COMMAND, "/" + Settings.settings.command_drops));
        player.spigot().sendMessage(builder.create());
    }

    public static void unregister(Player player) {
        ITEMS.remove(player.getUniqueId());
    }

    public static ItemCollector get(Player player) {
        if(Settings.settings.breaking_drop_on_ground) return null;
        return ITEMS.get(player.getUniqueId());
    }

    private final Player player;
    private final List<ItemStack> items;

    private int ticks;

    public ItemCollector(Player player) {
        this.player = player;
        this.items = new ArrayList<>();
    }

    private boolean tick() {
        if(--ticks <= 0) {
            player.sendMessage(Language.get("Items.spawner-drop.cleared").text());
            player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 2f, 1f);
            return false;
        }
        if(ticks == Settings.settings.items_remind_ticks) remind();
        return true;
    }

    private void remind() {
        player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_XYLOPHONE, 2f, 0.5f);
        send(player, ticks);
    }

    private void add(ItemStack item) {
        items.add(item);
        ticks = Settings.settings.items_taking_ticks;
    }

    public boolean get(boolean silent) {
        PlayerInventory inventory = player.getInventory();
        int free = ItemMatcher.free(player);
        if(free < items.size()) {
            if(!silent) {
                player.sendMessage(Language.get("Inventory.insufficient-space").text());
                player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 2f, 1f);
            }
            return false;
        }
        inventory.addItem(items.toArray(ItemStack[]::new));
        if(!silent)
            player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 2f, 2f);
        unregister(player);
        return true;
    }

}
