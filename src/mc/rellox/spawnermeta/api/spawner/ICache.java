package mc.rellox.spawnermeta.api.spawner;

import mc.rellox.spawnermeta.spawner.ActiveCache;
import mc.rellox.spawnermeta.spawner.type.SpawnerType;

public interface ICache {
	
	static ICache of(ISpawner spawner) {
		return new ActiveCache(spawner);
	}
	
	void cache();
	
	SpawnerType type();
	
	int stack();
	
	int charges();
	
	int spawnable();
	
	boolean empty();
	
	boolean enabled();
	
	boolean natural();
	
	boolean owned();

}
