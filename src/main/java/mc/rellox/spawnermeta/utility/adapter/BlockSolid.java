package mc.rellox.spawnermeta.utility.adapter;

import org.bukkit.block.Block;

import javax.annotation.Nullable;
import java.lang.reflect.Method;

public final class BlockSolid {
    private BlockSolid() {}

    private static final @Nullable Method BLOCK_IS_SOLID;

    static {
        @Nullable Method isSolid;
        try {
            isSolid = org.bukkit.block.Block.class.getMethod("isSolid");
        } catch (NoSuchMethodException e) {
            isSolid = null;
        }
        BLOCK_IS_SOLID = isSolid;
    }

    public static boolean isSolid(Block block) {
        // Paper: Avoid calling getType.
        if (BLOCK_IS_SOLID != null) {
            try {
                return (boolean) BLOCK_IS_SOLID.invoke(block);
            } catch (ReflectiveOperationException ignored) {}
        }
        return block.getType().isSolid();
    }

}
