package mc.rellox.spawnermeta.api.events;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import mc.rellox.spawnermeta.prices.Price;
import mc.rellox.spawnermeta.spawner.SpawnerType;

public class SpawnerChangeEvent extends SpawnerInteractEvent {
	
	private SpawnerType type;
	public final boolean empty;

	public SpawnerChangeEvent(Player player, Block block, Price price,
			SpawnerType type, boolean empty) {
		super(player, block, BlockAction.CHANGE, price);
		this.type = type;
		this.empty = empty;
	}
	
	public SpawnerType getNewType() {
		return type == null ? SpawnerType.PIG : type;
	}
	
	public void setNewType(SpawnerType type) {
		this.type = type;
	}


}
