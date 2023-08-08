package mc.rellox.spawnermeta.api.events;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import mc.rellox.spawnermeta.api.spawner.IVirtual;
import mc.rellox.spawnermeta.prices.Price;

public class SpawnerStackEvent extends SpawnerInteractEvent {
	
	private final IVirtual item;
	public final boolean direct;

	public SpawnerStackEvent(Player player, Block block, Price price,
			IVirtual item, boolean direct) {
		super(player, block, BlockAction.STACK, price);
		this.item = item;
		this.direct = direct;
	}
	
	public final IVirtual getItem() {
		return item;
	}

}
