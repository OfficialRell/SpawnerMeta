package mc.rellox.spawnermeta.api.events;

import java.util.Optional;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import mc.rellox.spawnermeta.api.spawner.ISpawner;

public class SpawnerEmptyEvent extends SpawnerPlayerEvent {
	
	private final Block block;
	private ItemStack refund;

	public SpawnerEmptyEvent(Player player, Block block, ItemStack refund) {
		super(player);
		this.block = block;
		this.refund = refund;
	}

	public ISpawner getSpawner() {
		return ISpawner.of(block);
	}
	
	public Optional<ItemStack> getRefund() {
		return Optional.ofNullable(refund);
	}
	
	public void setRefund(ItemStack item) {
		this.refund = item;
	}
	
}
