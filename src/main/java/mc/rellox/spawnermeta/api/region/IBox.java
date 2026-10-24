package mc.rellox.spawnermeta.api.region;

import mc.rellox.spawnermeta.utility.region.CubicBox;
import mc.rellox.spawnermeta.utility.region.SphereBox;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public interface IBox {

    IBox empty = new IBox() {
        @Override
        public int z() {
            return 0;
        }
        @Override
        public int y() {
            return 0;
        }
        @Override
        public int x() {
            return 0;
        }
        @Override
        public int radius() {
            return 0;
        }
        @Override
        public boolean in(int x, int y, int z) {
            return false;
        }
    };

    static IBox cube(Block block, int r) {
        return cube(block.getX(), block.getY(), block.getZ(), r);
    }

    static IBox cube(int x, int y, int z, int r) {
        return new CubicBox(x, y, z, r);
    }

    static IBox sphere(Block block, int r) {
        return sphere(block.getX(), block.getY(), block.getZ(), r);
    }

    static IBox sphere(int x, int y, int z, int r) {
        return new SphereBox(x, y, z, r);
    }

    boolean in(int x, int y, int z);

    default boolean in(Player player) {
        return in(player.getLocation());
    }

    default boolean in(Block block) {
        return in(block.getX(), block.getY(), block.getZ());
    }

    default boolean in(Location loc) {
        return in(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
    }

    int x();

    int y();

    int z();

    int radius();

    default boolean any(Iterable<? extends Player> players) {
        for(Player player : players)
            if(in(player)
                    && player.getGameMode() != GameMode.SPECTATOR) return true;
        return false;
    }

}
