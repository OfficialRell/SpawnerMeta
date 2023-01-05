package mc.rellox.spawnermeta.utils;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

public abstract class EntityBox {
	
	public static EntityBox box(int x, int y, int z) {
		if(x == 1 && y == 1 && z == 1) return new EntityBoundsSingle();
		if(x == 1 && y > 1 && z == 1) return new EntityBoundsHigh(y);
		return new EntityBoundsLarge(x, y, z);
	}
	
	public final int x, y, z;
	
	protected EntityBox(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public EntityBox multiply(int a) {
		return (a <= 1 ? this : new EntityBoundsLarge(x * a, y * a, z * a));
	}
	
	public abstract Location check(Block at, Block block, boolean grass);
	
	private static class EntityBoundsSingle extends EntityBox {

		private EntityBoundsSingle() {
			super(1, 1, 1);
		}

		@Override
		public Location check(Block at, Block block, boolean grass) {
			if(grass == true && grass(block.getRelative(0, -1, 0)) == false) return null;
			if(free(at, block) == false) return null;
			return block.getLocation().add(0.5, 0, 0.5);
		}
		
	}
	
	private static class EntityBoundsHigh extends EntityBox {

		private EntityBoundsHigh(int y) {
			super(1, y, 1);
		}

		@Override
		public Location check(Block at, Block block, boolean grass) {
			if(grass == true && grass(block.getRelative(0, -1, 0)) == false) return null;
			int i = 0;
			do {
				if(free(at, block.getRelative(0, i, 0)) == false) return null;
			} while(++i < y);
			return block.getLocation().add(0.5, 0, 0.5);
		}
		
	}
	
	private static class EntityBoundsLarge extends EntityBox {

		private EntityBoundsLarge(int x, int y, int z) {
			super(x, y, z);
		}

		@Override
		public Location check(Block at, Block block, boolean grass) {
			int ix = 0, iy, iz;
			Block b;
			do {
				iy = 0;
				do {
					iz = 0;
					do {
						b = block.getRelative(ix, iy, iz);
						if(iy == 0 && grass == true && grass(block.getRelative(ix, -1, iz)) == false) return null;
						if(free(at, b) == false) return null;
					} while(++iz < z);
				} while(++iy < y);
			} while(++ix < x);
			return block.getLocation().add(x * 0.5, 0, z * 0.5);
		}
		
	}
	
	private static boolean grass(Block block) {
		return switch(block.getType()) {
		case GRASS_BLOCK, DIRT, COARSE_DIRT -> true;
		default -> false;
		};
	}
	
	private static boolean free(Block block, Block other) {
		Material type = other.getType();
		if(block.getType() == type) return true;
		return type.isSolid() == false;
	}

}
