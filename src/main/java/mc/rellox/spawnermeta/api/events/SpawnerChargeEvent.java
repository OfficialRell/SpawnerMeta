package mc.rellox.spawnermeta.api.events;

import java.util.Optional;

import org.bukkit.entity.Player;

import mc.rellox.spawnermeta.api.spawner.IGenerator;
import mc.rellox.spawnermeta.prices.Price;

public class SpawnerChargeEvent extends SpawnerModifyEvent implements IPriceEvent {
	
	public int charges;
	
	private Price price;

	public SpawnerChargeEvent(Player player, IGenerator generator, Price price, int charges) {
		super(player, ModifyType.CHARGE, generator);
		this.price = price;
		this.charges = charges;
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
