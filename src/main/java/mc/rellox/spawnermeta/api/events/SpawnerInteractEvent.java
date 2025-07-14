package mc.rellox.spawnermeta.api.events;

import java.util.Optional;

import org.bukkit.entity.Player;

import mc.rellox.spawnermeta.api.spawner.IGenerator;
import mc.rellox.spawnermeta.prices.Price;

public class SpawnerInteractEvent extends SpawnerPlayerEvent implements IPriceEvent, IGeneratorEvent {
	
	private final IGenerator generator;
	public final BlockAction action;
	
	private Price price;
	
	public SpawnerInteractEvent(Player player, IGenerator generator, BlockAction action, Price price) {
		super(player);
		this.generator = generator;
		this.action = action;
		
		this.price = price;
	}
	
	public final IGenerator getGenerator() {
		return generator;
	}

	@Override
	public Optional<Price> getPrice() {
		return Optional.ofNullable(price);
	}

	@Override
	public void setPrice(Price price) {
		this.price = price;
	}
	
	public static enum BlockAction {
		
		BREAK, CHANGE, STACK;
		
	}

}
