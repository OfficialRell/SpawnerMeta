package mc.rellox.spawnermeta.api.events;

import mc.rellox.spawnermeta.api.spawner.IGenerator;
import mc.rellox.spawnermeta.api.spawner.ISpawner;

public interface IGeneratorEvent extends IEvent {
	
	IGenerator getGenerator();
	
	default ISpawner getSpawner() {
		return getGenerator().spawner();
	}

}
