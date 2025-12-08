package mc.rellox.spawnermeta.utility.adapter;

import org.bukkit.block.Block;
import org.bukkit.block.BlockState;

import javax.annotation.Nullable;
import java.lang.reflect.Method;

public final class BlockSnapshot {
    private BlockSnapshot() {}

    private static final @Nullable Method BLOCK_GET_STATE_BOOLEAN;

    static {
        @Nullable Method getState;
        try {
            getState = org.bukkit.block.Block.class.getMethod("getState", boolean.class);
        } catch (NoSuchMethodException e) {
            getState = null;
        }
        BLOCK_GET_STATE_BOOLEAN = getState;
    }

    public static BlockState getBlockState(Block block) {
        // Paper: Get state without snapshot.
        if (BLOCK_GET_STATE_BOOLEAN != null) {
            try {
                return (BlockState) BLOCK_GET_STATE_BOOLEAN.invoke(block, false);
            } catch (ReflectiveOperationException ignored) {}
        }
        return block.getState();
    }

}
