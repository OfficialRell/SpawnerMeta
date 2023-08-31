package mc.rellox.spawnermeta.api.events;

import java.util.Optional;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import mc.rellox.spawnermeta.api.spawner.IGenerator;
import mc.rellox.spawnermeta.api.spawner.ISpawner;

public class SpawnerEmptyEvent extends SpawnerPlayerEvent {
	
	private final IGenerator generator;
	private ItemStack refund;

	public SpawnerEmptyEvent(Player player, IGenerator generator, ItemStack refund) {
		super(player);
		this.generator = generator;
		this.refund = refund;
	}

	public IGenerator getGenerator() {
		return generator;
	}

	public ISpawner getSpawner() {
		return generator.spawner();
	}
	
	public Optional<ItemStack> getRefund() {
		return Optional.ofNullable(refund);
	}
	
	public void setRefund(ItemStack item) {
		this.refund = item;
	}
	
}
