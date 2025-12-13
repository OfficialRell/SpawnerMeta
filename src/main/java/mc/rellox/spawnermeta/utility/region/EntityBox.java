package mc.rellox.spawnermeta.utility.region;

import java.util.function.IntSupplier;

import mc.rellox.spawnermeta.api.spawner.requirement.ILight;
import mc.rellox.spawnermeta.api.spawner.requirement.IMaterial;
import org.bukkit.Location;
import org.bukkit.World;
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
			boolean l = !requirements.light().is(block);
			if(!requirements.environment().is(block)) {
				submit.environment();
				if(l) submit.light();
			} else if(l) submit.lighted();
			if(!requirements.ground().is(block.getRelative(0, -1, 0))) submit.ground();
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
			if(!requirements.ground().is(block.getRelative(0, -1, 0))) submit.ground();
			boolean e = false, l = false;
			int i = 0;
			do {
				Block relative = block.getRelative(0, i, 0);
				if(!requirements.environment().is(relative)) e = true;
				if(!requirements.light().is(relative)) l = true;
			} while(++i < y);
			if(e) {
				submit.environment();
				if(l) submit.light();
			} else if(l) submit.lighted();
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
            final ILight lightReq = requirements.light();
            final IMaterial groundReq = requirements.ground();
            final IMaterial envReq = requirements.environment();

            final boolean noLightReq = (lightReq == ILight.empty);
            final boolean noGroundReq = (groundReq == IMaterial.empty);
            final boolean noEnvReq = (envReq == IMaterial.empty);

            // Nothing to check at all
            if (noLightReq && noGroundReq && noEnvReq) {
                return block.getLocation().add(x * 0.5, 0, z * 0.5);
            }

            boolean groundFail = false;
            boolean envFail = false;
            boolean lightFail = false;

            final World world = block.getWorld();
            final int baseX = block.getX();
            final int baseY = block.getY();
            final int baseZ = block.getZ();

            for (int ix = 0; ix < x; ix++) {
                for (int iy = 0; iy < y; iy++) {
                    for (int iz = 0; iz < z; iz++) {

                        Block b = world.getBlockAt(baseX + ix, baseY + iy, baseZ + iz);

                        // Ground check (bottom layer only)
                        if (!noGroundReq && iy == 0 && !groundFail) {
                            Block below = world.getBlockAt(b.getX(), b.getY() - 1, b.getZ());
                            if (!groundReq.is(below)) {
                                groundFail = true;
                            }
                        }

                        // Environment check
                        if (!noEnvReq && !envFail && !envReq.is(b)) {
                            envFail = true;
                        }

                        // Light check
                        if (!noLightReq && !lightFail && !lightReq.is(b)) {
                            lightFail = true;
                        }

                        // Safe early exit
                        if (groundFail && envFail && lightFail && iy > 0) {
                            ix = x;
                            iy = y;
                            break;
                        }
                    }
                }
            }

            // Submit final errors
            if (groundFail) submit.ground();

            if (envFail) {
                submit.environment();
                if (lightFail) submit.light();
            } else if (lightFail) {
                submit.lighted();
            }

            submit.submit();

            return submit.valid()
                    ? block.getLocation().add(x * 0.5, 0, z * 0.5)
                    : null;
        }

    }

}
