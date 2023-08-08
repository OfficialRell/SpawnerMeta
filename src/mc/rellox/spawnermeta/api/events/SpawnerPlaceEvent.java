package mc.rellox.spawnermeta.api.events;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import mc.rellox.spawnermeta.api.spawner.IVirtual;
import mc.rellox.spawnermeta.prices.Price;

public class SpawnerPlaceEvent extends SpawnerInteractEvent {
	
	private final IVirtual item;

	public SpawnerPlaceEvent(Player player, Block block, Price price, IVirtual item) {
		super(player, block, BlockAction.PLACE, price);
		this.item = item;
	}
	
	public final IVirtual getItem() {
		return item;
	}

}
