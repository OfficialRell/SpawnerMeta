package mc.rellox.spawnermeta.api.events;

import org.bukkit.entity.Player;

import mc.rellox.spawnermeta.api.spawner.IGenerator;

public class SpawnerOpenEvent extends SpawnerPlayerEvent implements IGeneratorEvent {
	
	private final IGenerator generator;

	public SpawnerOpenEvent(Player player, IGenerator generator) {
		super(player);
		this.generator = generator;
	}
	
	public IGenerator getGenerator() {
		return generator;
	}


}
