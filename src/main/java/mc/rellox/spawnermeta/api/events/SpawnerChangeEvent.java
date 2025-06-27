package mc.rellox.spawnermeta.api.events;

import org.bukkit.entity.Player;

import mc.rellox.spawnermeta.api.spawner.IGenerator;
import mc.rellox.spawnermeta.prices.Price;
import mc.rellox.spawnermeta.spawner.type.SpawnerType;

public class SpawnerChangeEvent extends SpawnerInteractEvent {
	
	private SpawnerType type;
	public final boolean empty;

	public SpawnerChangeEvent(Player player, IGenerator generator, Price price,
			SpawnerType type, boolean empty) {
		super(player, generator, BlockAction.CHANGE, price);
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
