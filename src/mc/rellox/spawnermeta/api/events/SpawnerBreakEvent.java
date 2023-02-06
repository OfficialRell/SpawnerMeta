package mc.rellox.spawnermeta.api.events;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import mc.rellox.spawnermeta.prices.Price;

public class SpawnerBreakEvent extends SpawnerInteractEvent {
	
	public double chance;

	public SpawnerBreakEvent(Player player, Block block, Price price, double chance) {
		super(player, block, BlockAction.BREAK, price);
		this.chance = chance;
	}

}
