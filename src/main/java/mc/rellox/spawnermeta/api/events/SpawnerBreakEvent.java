package mc.rellox.spawnermeta.api.events;

import org.bukkit.entity.Player;

import mc.rellox.spawnermeta.api.spawner.IGenerator;
import mc.rellox.spawnermeta.prices.Price;

public class SpawnerBreakEvent extends SpawnerInteractEvent {
	
	public double chance;

	public SpawnerBreakEvent(Player player, IGenerator generator, Price price, double chance) {
		super(player, generator, BlockAction.BREAK, price);
		this.chance = chance;
	}

}
