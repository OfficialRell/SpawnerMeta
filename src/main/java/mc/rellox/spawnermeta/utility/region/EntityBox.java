package mc.rellox.spawnermeta.utility.region;

import java.util.function.IntSupplier;

import org.bukkit.Location;
import org.bukkit.block.Block;

import mc.rellox.spawnermeta.api.spawner.requirement.ErrorCounter.ErrorSubmit;
import mc.rellox.spawnermeta.api.spawner.requirement.IRequirements;

public abstract class EntityBox {
	
	public static EntityBox single() {
		return new EntityBoxSingle();
	}
	
	public static EntityBox box(int x, int y, int z) {
		if(x == 1 && y == 1 && z == 1) return single();
		if(x == 1 && y > 1 && z == 1) return new EntityBoxHigh(y);
		return new EntityBoxLarge(x, y, z);
	}
	
	public static EntityBox multibox(IntSupplier multiplier) {
		return new EntityMulitbox(multiplier);
	}
	
	private static class EntityMulitbox extends EntityBoxSingle implements IEntityMulitbox {
		
		private final IntSupplier multiplier;

		protected EntityMulitbox(IntSupplier multiplier) {
			super();
			this.multiplier = multiplier;
		}
		
		@Override
		public EntityBox box()  {
			return multiply(multiplier.getAsInt());
		}
		
	}
	
	public final int x, y, z;
	
	protected EntityBox(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	@Override
	public String toString() {
		return "EntityBox[x:" + x + ", y:" + y + ", z:" + z + "]";
	}
	
	public int volume() {
		return x * y * z;
	}
	
	public int maximum() {
		return Math.max(x, z);
	}
	
	public EntityBox multiply(int a) {
		return (a <= 1 ? this : new EntityBoxLarge(x * a, y * a, z * a));
	}
	
	public abstract Location check(Block block, IRequirements requirements, ErrorSubmit submit);
	
	private static class EntityBoxSingle extends EntityBox {

		private EntityBoxSingle() {
			super(1, 1, 1);
		}

		@Override
		public Location check(Block block, IRequirements requirements, ErrorSubmit submit) {
			boolean l = requirements.light().is(block) == false;
			if(requirements.environment().is(block) == false) {
				submit.environment();
				if(l == true) submit.light();
			} else if(l == true) submit.lighted();
			if(requirements.ground().is(block.getRelative(0, -1, 0)) == false) submit.ground();
			submit.submit();
			return submit.valid() ? block.getLocation().add(0.5, 0, 0.5) : null;
		}
		
	}
	
	private static class EntityBoxHigh extends EntityBox {

		private EntityBoxHigh(int y) {
			super(1, y, 1);
		}

		@Override
		public Location check(Block block, IRequirements requirements, ErrorSubmit submit) {
			if(requirements.ground().is(block.getRelative(0, -1, 0)) == false) submit.ground();
			boolean e = false, l = false;
			int i = 0;
			do {
				Block relative = block.getRelative(0, i, 0);
				if(requirements.environment().is(relative) == false) e = true;
				if(requirements.light().is(relative) == false) l = true;
			} while(++i < y);
			if(e == true) {
				submit.environment();
				if(l == true) submit.light();
			} else if(l == true) submit.lighted();
			submit.submit();
			return submit.valid() ? block.getLocation().add(0.5, 0, 0.5) : null;
		}
		
	}
	
	private static class EntityBoxLarge extends EntityBox {

		private EntityBoxLarge(int x, int y, int z) {
			super(x, y, z);
		}

		@Override
		public Location check(Block block, IRequirements requirements, ErrorSubmit submit) {
			boolean e = false, l = false;
			int ix = 0, iy, iz;
			Block b;
			do {
				iy = 0;
				do {
					iz = 0;
					do {
						b = block.getRelative(ix, iy, iz);
						if(iy == 0 && requirements.ground().is(b.getRelative(0, -1, 0)) == false) submit.ground();
						if(requirements.environment().is(b) == false) e = true;
						if(requirements.light().is(b) == false) l = true;
					} while(++iz < z);
				} while(++iy < y);
			} while(++ix < x);
			if(e == true) {
				submit.environment();
				if(l == true) submit.light();
			} else if(l == true) submit.lighted();
			submit.submit();
			return submit.valid() ? block.getLocation().add(x * 0.5, 0, z * 0.5) : null;
		}
		
	}

}
