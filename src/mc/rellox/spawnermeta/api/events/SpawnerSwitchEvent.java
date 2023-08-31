package mc.rellox.spawnermeta.api.events;

import org.bukkit.entity.Player;

import mc.rellox.spawnermeta.api.spawner.IGenerator;

public class SpawnerSwitchEvent extends SpawnerModifyEvent {
	
	public final boolean switched;

	public SpawnerSwitchEvent(Player player, IGenerator generator, boolean switched) {
		super(player, ModifyType.SWITCH, generator);
		this.switched = switched;
	}

}
