package mc.rellox.spawnermeta.spawner;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import mc.rellox.spawnermeta.utils.EntityBox;
import mc.rellox.spawnermeta.utils.Utils;

public interface SpawnerSpawning {
	
	void set(Block block, Block at, EntityBox box);
	
	Location get();
	
	void clear();
	
	public static enum SpawningType {
		
		SINGLE() {
			@Override
			public SpawnerSpawning spread(final int r) {
				return new SpawnerSpawning() {
					Location loc;
					@Override
					public void set(Block block, Block at, EntityBox box) {
						boolean g = at.getRelative(0, -1, 0).getType() == Material.GRASS_BLOCK;
						List<Location> list = new ArrayList<>();
						Block b;
						for(int i = -r; i <= r; i++) {
							for(int j = -r; j <= r; j++) {
								for(int k = -r; k <= r; k++) {
									b = block.getRelative(i, j, k);
									Location l = box.check(at, b, g);
									if(l == null) continue;
									list.add(l);
								}
							}
						}
						loc = list.isEmpty() == true ? at.getLocation().add(0.5, 0, 0.5) : Utils.random(list);
					}
					@Override
					public Location get() {
						return loc;
					}
					@Override
					public void clear() {
						loc = null;
					}
				};
			}
		},
		SPREAD() {
			@Override
			public SpawnerSpawning spread(final int r) {
				return new SpawnerSpawning() {
					final List<Location> list = new ArrayList<>();
					Block at;
					@Override
					public void set(Block block, Block at, EntityBox box) {
						this.at = at;
						boolean g = at.getRelative(0, -1, 0).getType() == Material.GRASS_BLOCK;
						Block b;
						for(int i = -r; i <= r; i++) {
							for(int j = -r; j <= r; j++) {
								for(int k = -r; k <= r; k++) {
									b = block.getRelative(i, j, k);
									Location l = box.check(at, b, g);
									if(l == null) continue;
									list.add(l);
								}
							}
						}
					}
					@Override
					public Location get() {
						return list.isEmpty() == true ? at.getLocation().add(0.5, 0, 0.5) : Utils.random(list);
					}
					@Override
					public void clear() {
						list.clear();
					}
				};
			}
		};
		
		public abstract SpawnerSpawning spread(final int r);
		
		public static SpawningType of(String name) {
			try {
				return valueOf(name.toUpperCase());
			} catch (Exception e) {}
			return null;
		}

	}

}
