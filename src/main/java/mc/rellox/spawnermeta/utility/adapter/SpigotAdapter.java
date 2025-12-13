package mc.rellox.spawnermeta.utility.adapter;

import org.bukkit.Chunk;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;

public final class SpigotAdapter implements BukkitPlatformAdapter {

    @Override
    public BlockState getState(Block block) {
        return block.getState();
    }

    @Override
    public boolean isSolid(Block block) {
        return block.getType().isSolid();
    }

    @Override
    public BlockState[] getTileEntities(Chunk chunk) {
        return chunk.getTileEntities();
    }
}
