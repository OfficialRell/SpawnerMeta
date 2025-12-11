package mc.rellox.spawnermeta.utility.adapter;

import org.bukkit.Chunk;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;

public final class PaperAdapter implements BukkitPlatformAdapter {

    private final MethodHandle mh_getState;
    private final MethodHandle mh_isSolid;
    private final MethodHandle mh_getTileEntities;

    public PaperAdapter() throws Exception {
        MethodHandles.Lookup lookup = MethodHandles.lookup();

        // Block#getState(boolean)
        Method getState = Block.class.getMethod("getState", boolean.class);
        mh_getState = lookup.unreflect(getState);

        // Block#isSolid()
        Method isSolid = Block.class.getMethod("isSolid");
        mh_isSolid = lookup.unreflect(isSolid);

        // Chunk#getTileEntities(boolean)
        Method getTileEntities = Chunk.class.getMethod("getTileEntities", boolean.class);
        mh_getTileEntities = lookup.unreflect(getTileEntities);
    }

    @Override
    public BlockState getState(Block block) {
        try {
            return (BlockState) mh_getState.invoke(block, false);
        } catch (Throwable t) {
            return block.getState(); // fallback
        }
    }

    @Override
    public boolean isSolid(Block block) {
        try {
            return (boolean) mh_isSolid.invoke(block);
        } catch (Throwable t) {
            return block.getType().isSolid(); // fallback
        }
    }

    @Override
    public BlockState[] getTileEntities(Chunk chunk) {
        try {
            return (BlockState[]) mh_getTileEntities.invoke(chunk, false);
        } catch (Throwable t) {
            return chunk.getTileEntities(); // fallback
        }
    }
}
