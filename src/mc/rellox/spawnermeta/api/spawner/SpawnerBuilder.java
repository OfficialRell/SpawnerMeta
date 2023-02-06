package mc.rellox.spawnermeta.api.spawner;

import java.util.Objects;

import mc.rellox.spawnermeta.configuration.Settings;
import mc.rellox.spawnermeta.spawner.SpawnerType;

public final class SpawnerBuilder {
	
	private final SpawnerType type;
	private int[] levels;
	private int charges;
	private int spawnable;
	private boolean empty;
	
	public SpawnerBuilder(SpawnerType type) {
		this.type = Objects.requireNonNull(type, "Spawner type cannot be null");
	}
	
	public VirtualSpawner build() {
		return new FilledVirtualSpawner(type, levels, charges, spawnable, empty);
	}
	
	public SpawnerBuilder levelled(int i0, int i1, int i2) {
		int[] ms = Settings.settings.upgrades_levels.get(type);
		this.levels = new int[] {a(i0, 1, ms[0]),
				a(i1, 1, ms[1]), a(i2, 1, ms[2])};
		return this;
	}
	
	private int a(int i, int m, int x) {
		return i < m ? m : i > x ? x : i;
	}
	
	public SpawnerBuilder charged(int i) {
		this.charges = i < 0 ? 0 : i;
		return this;
	}
	
	public SpawnerBuilder spawnable(int i) {
		this.spawnable = i < 0 ? 0 : i;
		return this;
	}
	
	public SpawnerBuilder empty(boolean b) {
		this.empty = b;
		return this;
	}

}
