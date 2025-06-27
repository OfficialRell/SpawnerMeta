package mc.rellox.spawnermeta.api.events;

import mc.rellox.spawnermeta.api.spawner.IGenerator;

public class SpawnerPreSpawnEvent extends SpawnerEvent implements IGeneratorEvent {
	
	private final IGenerator generator;
	
	public int count;
	public boolean bypass_checks;
	
	public SpawnerPreSpawnEvent(IGenerator generator, int count) {
		this.generator = generator;
		this.count = count;
		this.bypass_checks = false;
	}
	
	public final IGenerator getGenerator() {
		return generator;
	}

}
