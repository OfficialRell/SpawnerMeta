package mc.rellox.spawnermeta.api.spawner.requirement;

import java.util.*;

import mc.rellox.spawnermeta.utility.adapter.BlockSolid;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.data.Waterlogged;

public interface IMaterial {
	
	static IMaterial empty = block -> true;
	static IMaterial air = block -> {
		Material type = block.getType();
		return type.isAir() || (!type.isSolid() && type != Material.WATER && type != Material.LAVA);
	};
	static IMaterial solid = BlockSolid::isSolid;
	static IMaterial water = block -> {
		Material type = block.getType();
		if(type == Material.WATER) return true;
		if(type.getHardness() > 0) return false;
		if(block.getBlockData() instanceof Waterlogged) return true;
		return type == Material.SEAGRASS || type == Material.TALL_SEAGRASS
				|| type == Material.KELP_PLANT;
	};
	static IMaterial slab = block -> Tag.SLABS.isTagged(block.getType());
	static IMaterial stairs = block -> Tag.STAIRS.isTagged(block.getType());
	static IMaterial fence = block -> {
        Material type = block.getType();
        return Tag.FENCES.isTagged(type)
                || Tag.FENCE_GATES.isTagged(type)
                || Tag.WALLS.isTagged(type);
    };

	static IMaterial is(Material m) {
		return new IMaterial() {
			final Material material = m;
			@Override
			public boolean is(Block block) {
				return block.getType() == material;
			}
		};
	}
	
	static IMaterial is(Collection<Material> is) {
		if(is.size() == 1) return is(is.toArray(Material[]::new)[0]);
		return new IMaterial() {
			final Set<Material> set = new HashSet<>(is);
			@Override
			public boolean is(Block block) {
				return set.contains(block.getType());
			}
		};
	}
	
	static IMaterial not(Material m) {
		return new IMaterial() {
			final Material material = m;
			@Override
			public boolean is(Block block) {
				return block.getType() != material;
			}
		};
	}
	
	static IMaterial not(Collection<Material> not) {
		if(not.size() == 1) return not(not.toArray(Material[]::new)[0]);
		return new IMaterial() {
			final Set<Material> set = new HashSet<>(not);
			@Override
			public boolean is(Block block) {
				return !set.contains(block.getType());
			}
		};
	}
	
	static IMaterial of(List<IMaterial> list) {
		if(list.size() == 1) return list.get(0);
		return block -> list.stream().anyMatch(i -> i.is(block));
	}
	
	/**
	 * @param block - block at
	 * @return {@code true} if this block matches
	 */
	
	boolean is(Block block);

}
