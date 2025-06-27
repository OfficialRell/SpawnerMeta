package mc.rellox.spawnermeta.api.spawner;

import mc.rellox.spawnermeta.api.spawner.ISpawner.Builder;
import mc.rellox.spawnermeta.spawner.type.SpawnerType;

@Deprecated(forRemoval = true)
public final class SpawnerBuilder {
	
	private final Builder builder;
	
	public SpawnerBuilder(SpawnerType type) {
		this.builder = ISpawner.builder(type);
	}
	
	public IVirtual build() {
		return builder.build();
	}
	
	public SpawnerBuilder levelled(int i0, int i1, int i2) {
		builder.levelled(i0, i1, i2);
		return this;
	}
	
	public SpawnerBuilder charged(int i) {
		builder.charged(i);
		return this;
	}
	
	public SpawnerBuilder spawnable(int i) {
		builder.spawnable(i);
		return this;
	}
	
	public SpawnerBuilder empty(boolean b) {
		builder.empty(b);
		return this;
	}

}
