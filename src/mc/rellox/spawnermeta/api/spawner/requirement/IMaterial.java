package mc.rellox.spawnermeta.api.spawner.requirement;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.block.Block;

@FunctionalInterface
public interface IMaterial {
	
	static IMaterial empty = block -> true;
	static IMaterial air = block -> {
		Material type = block.getType();
		return type.getHardness() <= 0
				|| type.isAir() == true;
	};
	static IMaterial solid = block -> block.getType().isSolid() == true;
	
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
				return set.contains(block.getType()) == true;
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
				return set.contains(block.getType()) == false;
			}
		};
	}
	
	static IMaterial of(List<IMaterial> list) {
		if(list.size() == 1) return list.get(0);
		return block -> list.stream().allMatch(i -> i.is(block));
	}
	
	/**
	 * @param block - block at
	 * @return {@code true} if this block matches
	 */
	
	boolean is(Block block);

}
