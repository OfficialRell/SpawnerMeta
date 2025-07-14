package mc.rellox.spawnermeta.api.events;

import org.bukkit.entity.Player;

public abstract class SpawnerPlayerEvent extends SpawnerEvent {
	
	protected final Player player;

	public SpawnerPlayerEvent(Player player) {
		this.player = player;
	}
	
	public final Player getPlayer() {
		return player;
	}

}
