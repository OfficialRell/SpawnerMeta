package mc.rellox.spawnermeta.api.events;

import org.bukkit.entity.Player;

import mc.rellox.spawnermeta.api.spawner.ISpawner;

public class SpawnerModifyEvent extends SpawnerPlayerEvent {
	
	public final ModifyType type;
	private final ISpawner spawner;
	
	public SpawnerModifyEvent(Player player, ModifyType type, ISpawner spawner) {
		super(player);
		this.type = type;
		this.spawner = spawner;
	}
	
	public final ISpawner getSpawner() {
		return spawner;
	}
	
	public static enum ModifyType {
		
		SWITCH, UPGRADE, CHARGE;

	}

}
