package mc.rellox.spawnermeta.api.spawner.location;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

public record Pos(int x, int y, int z) {
	
	public Block block(World world) {
		return world.getBlockAt(x, y, z);
	}
	
	public Location location(World world) {
		return new Location(world, x, y, z);
	}
	
	public static Pos of(Block block) {
		return new Pos(block.getX(), block.getY(), block.getZ());
	}

}
