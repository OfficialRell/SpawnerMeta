package mc.rellox.spawnermeta.utility.region;

import mc.rellox.spawnermeta.api.spawner.requirement.ErrorCounter.ErrorSubmit;
import mc.rellox.spawnermeta.api.spawner.requirement.ILight;
import mc.rellox.spawnermeta.api.spawner.requirement.IMaterial;
import mc.rellox.spawnermeta.api.spawner.requirement.IRequirements;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.function.IntSupplier;

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
        public EntityBox box() {
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

    public EntityBox multiply(int size) {
        return size <= 1 ? this : new EntityBoxLarge(x * size, y * size, z * size);
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
            final ILight light = requirements.light();
            final IMaterial ground = requirements.ground();
            final IMaterial environment = requirements.environment();

            final boolean ignore_light = (light == ILight.empty);
            final boolean ignore_ground = (ground == IMaterial.empty);
            final boolean ignore_environment = (environment == IMaterial.empty);

            // Nothing to check at all
            if(ignore_light && ignore_ground && ignore_environment) {
                return block.getLocation().add(x * 0.5, 0, z * 0.5);
            }

            boolean no_light = false;
            boolean no_ground = false;
            boolean no_environment = false;

            final World world = block.getWorld();
            final int base_x = block.getX();
            final int base_y = block.getY();
            final int base_z = block.getZ();

            for(int ix = 0; ix < x; ix++) {
                for(int iy = 0; iy < y; iy++) {
                    for(int iz = 0; iz < z; iz++) {

                        Block at = world.getBlockAt(base_x + ix, base_y + iy, base_z + iz);

                        // Ground check (bottom layer only)
                        if(!ignore_ground && iy == 0 && !no_ground) {
                            Block below = world.getBlockAt(at.getX(), at.getY() - 1, at.getZ());
                            if(!ground.is(below)) {
                                no_ground = true;
                            }
                        }

                        // Environment check
                        if(!ignore_environment && !no_environment && !environment.is(at)) {
                            no_environment = true;
                        }

                        // Light check
                        if(!ignore_light && !no_light && !light.is(at)) {
                            no_light = true;
                        }

                        // Safe early exit
                        if(no_ground && no_environment && no_light && iy > 0) {
                            ix = x;
                            iy = y;
                            break;
                        }
                    }
                }
            }

            // Submit final errors
            if(no_ground) submit.ground();

            if(no_environment) {
                submit.environment();
                if(no_light) submit.light();
            } else if(no_light) {
                submit.lighted();
            }

            submit.submit();

            return submit.valid()
                    ? block.getLocation().add(x * 0.5, 0, z * 0.5)
                    : null;
        }

    }

}
