package mc.rellox.spawnermeta.hook.setup;

import com.bgsoftware.superiorskyblock.api.events.IslandKickEvent;
import com.bgsoftware.superiorskyblock.api.events.IslandWorthCalculatedEvent;
import com.bgsoftware.superiorskyblock.api.island.Island;
import com.bgsoftware.superiorskyblock.api.key.Key;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import mc.rellox.spawnermeta.SpawnerMeta;
import mc.rellox.spawnermeta.api.spawner.ISpawner;
import mc.rellox.spawnermeta.configuration.Settings;
import mc.rellox.spawnermeta.configuration.location.LocationRegistry;
import mc.rellox.spawnermeta.items.ItemMatcher;
import mc.rellox.spawnermeta.spawner.type.SpawnerType;
import mc.rellox.spawnermeta.utility.reflect.Reflect.RF;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SetupSuperiorSkyblock2 implements Listener {

    private final Map<UUID, BigDecimal> last_bonus = new ConcurrentHashMap<>();
    private final Map<UUID, Map<Location, Integer>> last_stacks = new ConcurrentHashMap<>();

    public static SetupSuperiorSkyblock2 load() {
        var listener = new SetupSuperiorSkyblock2();
        SpawnerMeta.scheduler().runNextTick(task -> {
            if(!Settings.settings.check_island_kick) return;
            Bukkit.getPluginManager().registerEvents(listener, SpawnerMeta.instance());
        });
        return listener;
    }

    @EventHandler
    private void onPlayerKick(IslandKickEvent event) {
        try {
            SuperiorPlayer kicked = event.getTarget();
            Player player = kicked.asPlayer();
            if(player != null && player.isOnline()) {
                var data = LocationRegistry.get(player);
                data.all().stream()
                        .map(Location::getBlock)
                        .map(ISpawner::of)
                        .forEach(spawner -> {
                            spawner.toItems()
                                    .forEach(item -> ItemMatcher.add(player, item));
                            spawner.block().setType(Material.AIR);
                        });
            } else {
                var data = LocationRegistry.get(kicked.getUniqueId());
                data.all().stream()
                        .map(Location::getBlock)
                        .map(ISpawner::of)
                        .forEach(spawner -> {
                            data.store(spawner.toData());
                            spawner.block().setType(Material.AIR);
                        });
            }
        } catch(Exception e) {
            RF.debug(e);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onIslandWorthCalculated(IslandWorthCalculatedEvent event) {
        Island island = event.getIsland();
        if(island == null) return;

        UUID islandId = island.getUniqueId();

        BigDecimal bonus = bonus(island);
        BigDecimal last = last_bonus.getOrDefault(islandId, BigDecimal.ZERO);
        BigDecimal difference = bonus.subtract(last);

        if(difference.compareTo(BigDecimal.ZERO) != 0) {
            BigDecimal worth = island.getBonusWorth();
            island.setBonusWorth(worth.add(difference));
            last_bonus.put(islandId, bonus);
        } else {
            last_bonus.put(islandId, bonus);
        }
    }

    private BigDecimal bonus(Island island) {
        BigDecimal total = BigDecimal.ZERO;
        Map<Location, Integer> stacks = new HashMap<>();

        for(World world : Bukkit.getWorlds()) {
            List<Chunk> chunks = island.getAllChunks();
            if(chunks == null || chunks.isEmpty())
                continue;

            for(Chunk chunk : chunks) {
                if(!chunk.isLoaded() || !chunk.getWorld().equals(world))
                    continue;

                for(BlockState state : chunk.getTileEntities()) {
                    if(!(state instanceof CreatureSpawner)) continue;

                    var generator = SpawnerMeta.API().getGenerator(state.getBlock());
                    if(generator == null) continue;

                    Block block = generator.block();
                    Location loc = block.getLocation();
                    if(!island.isInsideRange(loc)) continue;

                    var type = generator.cache().type();
                    int stack = generator.cache().stack();

                    int extra = stack - 1;

                    stacks.put(loc, stack);

                    if(extra > 0) {
                        double value = Settings.settings.hook_ssb2_values.get(type);
                        BigDecimal bonus = BigDecimal.valueOf(value * extra);
                        total = total.add(bonus);
                    }

                    update(island, block, type, stack);
                }
            }
        }

        last_stacks.put(island.getUniqueId(), stacks);

        return total;
    }

    public void update(Island island, Block block, SpawnerType type, int stack) {
        Key key = Key.of("SPAWNER:" + type.name());

        Location loc = block.getLocation();
        UUID id = island.getUniqueId();
        Map<Location, Integer> last = last_stacks.get(id);

        int previous = last == null ? 1 : last.getOrDefault(loc, 1);
        int difference = stack - previous;
        if(difference == 0) return;

        if(difference > 0) island.handleBlockPlace(key, difference);
        else island.handleBlockBreak(key, -difference);
    }

}
