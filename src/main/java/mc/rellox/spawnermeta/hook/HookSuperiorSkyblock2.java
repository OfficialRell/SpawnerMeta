package mc.rellox.spawnermeta.hook;

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblock;
import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;
import com.bgsoftware.superiorskyblock.api.island.Island;
import com.bgsoftware.superiorskyblock.api.key.Key;
import mc.rellox.spawnermeta.SpawnerMeta;
import mc.rellox.spawnermeta.api.spawner.IGenerator;
import mc.rellox.spawnermeta.hook.setup.SetupSuperiorSkyblock2;
import mc.rellox.spawnermeta.spawner.type.SpawnerType;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;

public class HookSuperiorSkyblock2 implements HookInstance {

    private SuperiorSkyblock plugin;
    private SetupSuperiorSkyblock2 setup;

    @Override
    public boolean exists() {
        return plugin != null;
    }

    @Override
    public String message() {
        return "SuperiorSkyblock2 found, event support provided!";
    }

    @Override
    public void load() {
        if(Bukkit.getPluginManager().getPlugin("SuperiorSkyblock2") == null) return;
        plugin = SuperiorSkyblockAPI.getSuperiorSkyblock();

        setup = SetupSuperiorSkyblock2.load();
    }

    public void placing(Block block, SpawnerType type) {
        if(!exists()) return;
        var is = plugin.getGrid().getIslandAt(block.getLocation());

        if(is != null) is.handleBlockPlace(Key.of("SPAWNER:" + type.name()));
    }

    public int delay_upgrade(Block block, int delay) {
        if(!exists()) return delay;
        var is = plugin.getGrid().getIslandAt(block.getLocation());
        if(is == null) return delay;
        return (int) Math.round(delay / is.getSpawnerRatesMultiplier());
    }

    public void recalculate(IGenerator generator) {
        if(!exists()) return;

        SpawnerMeta.scheduler().runLater(() -> {
            if(generator == null || !generator.active()) return;

            Block block = generator.block();
            Island island = SuperiorSkyblockAPI.getIslandAt(block.getLocation());
            if(island == null) return;

            SpawnerType type = generator.cache().type();
            int stack = generator.cache().stack();

            setup.update(island, block, type, stack);

            schedule(island);
        }, 5);
    }

    public void recalculate(IGenerator generator, SpawnerType from, SpawnerType to) {
        if(!exists()) return;

        if(generator == null || from == null || to == null) return;

        SpawnerMeta.scheduler().runLater(() -> {
            Island island = SuperiorSkyblockAPI.getIslandAt(generator.block().getLocation());
            if(island == null) return;

            Key key_from = Key.of("SPAWNER:" + from.name());

            Key key_to = Key.of("SPAWNER:" + to.name());

            int stack = generator.cache().stack();

            island.handleBlockBreak(key_from, stack);
            island.handleBlockPlace(key_to, stack);

            schedule(island);
        }, 5);
    }

    public void reschedule(Block block) {
        if(!exists()) return;

        if(block == null) return;

        Island island = SuperiorSkyblockAPI.getIslandAt(block.getLocation());
        if(island == null) return;

        schedule(island);
    }

    private void schedule(Island island) {
        SpawnerMeta.scheduler().runLater(() -> island.calcIslandWorth(null), 10L);
    }

}
