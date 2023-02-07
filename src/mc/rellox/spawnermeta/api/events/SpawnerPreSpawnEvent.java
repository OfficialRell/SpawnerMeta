package mc.rellox.spawnermeta.api.events;

import org.bukkit.block.Block;

import mc.rellox.spawnermeta.api.spawner.Spawner;

public class SpawnerPreSpawnEvent extends SpawnerEvent {
	
	private final Block block;
	
	public int count;
	public boolean bypass_checks;
	
	public SpawnerPreSpawnEvent(Block block, int count) {
		this.block = block;
		this.count = count;
		this.bypass_checks = false;
	}
	
	public Spawner getSpawner() {
		return Spawner.of(block);
	}

}
