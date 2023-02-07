package mc.rellox.spawnermeta.api.events;

import java.util.Optional;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import mc.rellox.spawnermeta.api.spawner.Spawner;
import mc.rellox.spawnermeta.prices.Price;

public class SpawnerInteractEvent extends SpawnerPlayerEvent implements IPriceEvent {
	
	private final Block block;
	public final BlockAction action;
	
	private Price price;
	
	public SpawnerInteractEvent(Player player, Block block, BlockAction action, Price price) {
		super(player);
		this.block = block;
		this.action = action;
		
		this.price = price;
	}
	
	public final Spawner getSpawner() {
		return Spawner.of(block);
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
		
		PLACE, BREAK, CHANGE, STACK;
		
	}

}
