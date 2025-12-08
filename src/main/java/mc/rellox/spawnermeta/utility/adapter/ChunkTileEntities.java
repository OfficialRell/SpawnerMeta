package mc.rellox.spawnermeta.utility.adapter;

import org.bukkit.Chunk;
import org.bukkit.block.BlockState;

import javax.annotation.Nullable;
import java.lang.reflect.Method;

public final class ChunkTileEntities {
    private ChunkTileEntities() {}

    private static final @Nullable Method CHUNK_GET_TILES_BOOLEAN;

    static {
        @Nullable Method getTileEntities;
        try {
            getTileEntities = org.bukkit.Chunk.class.getMethod("getTileEntities", boolean.class);
        } catch (NoSuchMethodException e) {
            getTileEntities = null;
        }
        CHUNK_GET_TILES_BOOLEAN = getTileEntities;
    }

    public static BlockState[] getTileEntities(Chunk chunk) {
        if (CHUNK_GET_TILES_BOOLEAN != null) {
            try {
                return (BlockState[]) CHUNK_GET_TILES_BOOLEAN.invoke(chunk, false);
            } catch (ReflectiveOperationException ignored) {}
        }
        return chunk.getTileEntities();
    }

}
