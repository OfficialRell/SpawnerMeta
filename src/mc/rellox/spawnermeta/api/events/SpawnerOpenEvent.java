package mc.rellox.spawnermeta.api.events;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import mc.rellox.spawnermeta.api.spawner.ISpawner;

public class SpawnerOpenEvent extends SpawnerPlayerEvent {
	
	private final Block block;

	public SpawnerOpenEvent(Player player, Block block) {
		super(player);
		this.block = block;
	}
	
	public ISpawner getSpawner() {
		return ISpawner.of(block);
	}


}
