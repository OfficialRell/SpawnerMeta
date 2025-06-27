package mc.rellox.spawnermeta.api.spawner;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import mc.rellox.spawnermeta.spawner.type.SpawnerType;
import mc.rellox.spawnermeta.utility.DataManager;

public interface IVirtual {
	
	public static IVirtual of(ItemStack item) {
		return of(item, false);
	}
	
	public static IVirtual of(ItemStack item, boolean nullable) {
		return DataManager.getSpawnerItem(item, nullable);
	}
	
	public static IVirtual of(Block block) {
		return DataManager.getSpawnerItem(block);
	}
	
	/**
	 * @param other - virtual spawner to compare with
	 * @return {@code true} if virtual spawners are exactly equal, otherwise {@code false}
	 */
	
	boolean exact(IVirtual other);
	
	/**
	 * @return Spawner type of this virtual spawner
	 */
	
	SpawnerType getType();
	
	/**
	 * @return Upgrade levels of this virtual spawner
	 */
	
	int[] getUpgradeLevels();
	
	/**
	 * @return Spawner charges of this virtual spawner
	 */
	
	int getCharges();
	
	/**
	 * @return Spawnable entity limit of this virtual spawner
	 */
	
	int getSpawnable();
	
	/**
	 * @return {@code true} if this virtual spawner is empty, otherwise {@code false}
	 */
	
	boolean isEmpty();
	
	/**
	 * @param a - amount
	 * @return Virtual spawner item stack
	 */
	
	ItemStack getItem(int a);
	
	/**
	 * @return Virtual spawner item stack
	 */
	
	default ItemStack getItem() {
		return getItem(1);
	}
	
	default void place(Block block) {
		place(null, block);
	}
	
	default void place(Player owner, Block block) {
		block.setType(Material.SPAWNER);
		DataManager.setNewSpawner(owner, block, getType(), getUpgradeLevels(),
				getCharges(), getSpawnable(), isEmpty());
	}

}
