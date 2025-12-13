package mc.rellox.spawnermeta.utility.adapter;

import org.bukkit.Chunk;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;

public interface BukkitPlatformAdapter {

    BlockState getState(Block block);

    boolean isSolid(Block block);

    BlockState[] getTileEntities(Chunk chunk);
}
