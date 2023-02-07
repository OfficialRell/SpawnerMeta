package mc.rellox.spawnermeta.api.events;

import java.util.List;

import org.bukkit.block.Block;
import org.bukkit.entity.Entity;

import mc.rellox.spawnermeta.api.spawner.Spawner;

public class SpawnerPostSpawnEvent implements IEvent {
	
	private final Block block;
	public final List<Entity> entities;
	
	public SpawnerPostSpawnEvent(Block block, List<Entity> entities) {
		this.block = block;
		this.entities = entities;
	}
	
	public Spawner getSpawner() {
		return Spawner.of(block);
	}

}
