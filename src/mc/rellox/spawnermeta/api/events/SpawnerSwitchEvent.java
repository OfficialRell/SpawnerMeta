package mc.rellox.spawnermeta.api.events;

import org.bukkit.entity.Player;

import mc.rellox.spawnermeta.api.spawner.Spawner;

public class SpawnerSwitchEvent extends SpawnerModifyEvent {
	
	public final boolean switched;

	public SpawnerSwitchEvent(Player player, Spawner spawner, boolean switched) {
		super(player, ModifyType.SWITCH, spawner);
		this.switched = switched;
	}

}
