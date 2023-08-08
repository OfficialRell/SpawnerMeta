package mc.rellox.spawnermeta.api.events;

import mc.rellox.spawnermeta.api.spawner.ISpawner;
import mc.rellox.spawnermeta.api.spawner.IGenerator;

public class SpawnerPreSpawnEvent extends SpawnerEvent {
	
	private final IGenerator instance;
	
	public int count;
	public boolean bypass_checks;
	
	public SpawnerPreSpawnEvent(IGenerator instance, int count) {
		this.instance = instance;
		this.count = count;
		this.bypass_checks = false;
	}
	
	public IGenerator getInstance() {
		return instance;
	}
	
	public ISpawner getSpawner() {
		return instance.spawner();
	}

}
