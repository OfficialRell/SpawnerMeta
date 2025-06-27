package mc.rellox.spawnermeta.api.events;

import org.bukkit.entity.Player;

import mc.rellox.spawnermeta.api.spawner.IGenerator;

public class SpawnerModifyEvent extends SpawnerPlayerEvent implements IGeneratorEvent {
	
	public final ModifyType type;
	private final IGenerator generator;
	
	public SpawnerModifyEvent(Player player, ModifyType type, IGenerator generator) {
		super(player);
		this.type = type;
		this.generator = generator;
	}
	
	public final IGenerator getGenerator() {
		return generator;
	}
	
	public static enum ModifyType {
		
		SWITCH, UPGRADE, CHARGE;

	}

}
