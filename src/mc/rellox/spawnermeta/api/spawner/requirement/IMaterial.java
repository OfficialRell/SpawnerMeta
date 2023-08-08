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
	
	static IMaterial of(Material m) {
		return new IMaterial() {
			final Material material = m;
			@Override
			public boolean is(Block block) {
				return block.getType() == material;
			}
		};
	}
	
	static IMaterial of(Collection<Material> collection) {
		return new IMaterial() {
			final Set<Material> set = new HashSet<>(collection);
			@Override
			public boolean is(Block block) {
				return set.contains(block.getType()) == true;
			}
		};
	}
	
	static IMaterial air() {
		return block -> {
			Material type = block.getType();
			return type.isAir() == true
					|| type.getHardness() <= 0;
		};
	}
	
	static IMaterial solid() {
		return block -> block.getType().isSolid() == true;
	}
	
	static IMaterial not(Collection<Material> ignore) {
		return new IMaterial() {
			final Set<Material> set = new HashSet<>(ignore);
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
	
	boolean is(Block block);

}
