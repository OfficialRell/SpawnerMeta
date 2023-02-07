package mc.rellox.spawnermeta.api.events;

import org.bukkit.entity.Player;

import mc.rellox.spawnermeta.api.spawner.Spawner;

public class SpawnerModifyEvent extends SpawnerPlayerEvent {
	
	public final ModifyType type;
	private final Spawner spawner;
	
	public SpawnerModifyEvent(Player player, ModifyType type, Spawner spawner) {
		super(player);
		this.type = type;
		this.spawner = spawner;
	}
	
	public final Spawner getSpawner() {
		return spawner;
	}
	
	public static enum ModifyType {
		
		SWITCH, UPGRADE, CHARGE;

	}

}
