package mc.rellox.spawnermeta.api.events;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import mc.rellox.spawnermeta.api.spawner.VirtualSpawner;
import mc.rellox.spawnermeta.prices.Price;

public class SpawnerStackEvent extends SpawnerInteractEvent {
	
	private final VirtualSpawner item;

	public SpawnerStackEvent(Player player, Block block, Price price, VirtualSpawner item) {
		super(player, block, BlockAction.STACK, price);
		this.item = item;
	}
	
	public final VirtualSpawner getItem() {
		return item;
	}

}
