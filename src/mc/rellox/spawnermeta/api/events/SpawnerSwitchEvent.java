package mc.rellox.spawnermeta.api.events;

import org.bukkit.entity.Player;

import mc.rellox.spawnermeta.api.spawner.ISpawner;

public class SpawnerSwitchEvent extends SpawnerModifyEvent {
	
	public final boolean switched;

	public SpawnerSwitchEvent(Player player, ISpawner spawner, boolean switched) {
		super(player, ModifyType.SWITCH, spawner);
		this.switched = switched;
	}

}
