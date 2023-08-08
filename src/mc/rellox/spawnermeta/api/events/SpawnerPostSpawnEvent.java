package mc.rellox.spawnermeta.api.events;

import java.util.List;

import org.bukkit.entity.Entity;

import mc.rellox.spawnermeta.api.spawner.ISpawner;
import mc.rellox.spawnermeta.api.spawner.IGenerator;

public class SpawnerPostSpawnEvent implements IEvent {
	
	private final IGenerator intance;
	public final List<Entity> entities;
	
	public SpawnerPostSpawnEvent(IGenerator intance, List<Entity> entities) {
		this.intance = intance;
		this.entities = entities;
	}
	
	public IGenerator getInstance() {
		return intance;	
	}
	
	public ISpawner getSpawner() {
		return intance.spawner();
	}

}
