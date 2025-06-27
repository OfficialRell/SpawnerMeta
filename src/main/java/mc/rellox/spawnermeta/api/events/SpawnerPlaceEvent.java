package mc.rellox.spawnermeta.api.events;

import java.util.Optional;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import mc.rellox.spawnermeta.api.spawner.IVirtual;
import mc.rellox.spawnermeta.prices.Price;

public class SpawnerPlaceEvent extends SpawnerPlayerEvent implements IPriceEvent {
	
	private final Block block;
	private final IVirtual item;
	
	private Price price;

	public SpawnerPlaceEvent(Player player, Block block, Price price, IVirtual item) {
		super(player);
		this.block = block;
		this.item = item;
		
		this.price = price;
	}
	
	public final Block getBlock() {
		return block;
	}
	
	public final IVirtual getItem() {
		return item;
	}

	@Override
	public Optional<Price> getPrice() {
		return Optional.ofNullable(price);
	}

	@Override
	public void setPrice(Price price) {
		this.price = price;
	}

}
