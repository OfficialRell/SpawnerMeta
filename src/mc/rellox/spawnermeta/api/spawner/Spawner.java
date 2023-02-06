package mc.rellox.spawnermeta.api.spawner;

import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import mc.rellox.spawnermeta.spawner.SpawnerType;
import mc.rellox.spawnermeta.spawner.UpgradeType;
import mc.rellox.spawnermeta.utils.DataManager;

@FunctionalInterface
public interface Spawner {
	
	static Spawner of(Block block) {
		return () -> block;
	}
	
	/**
	 * @return Spawner block
	 */
	
	Block block();
	
	/**
	 * @return {@code true} if this spawner is empty, otherwise {@code false}
	 */
	
	default boolean isEmpty() {
		return DataManager.isEmpty(block());
	}
	
	/**
	 * Sets this spawner as empty.
	 */
	
	default void setEmpty() {
		DataManager.setEmpty(block());
	}
	
	/**
	 * @return {@code true} if this spawner is enabled, otherwise {@code false}
	 */
	
	default boolean isEnabled() {
		return DataManager.isEnabled(block());
	}
	
	/**
	 * Enables or disables this spawner.
	 * 
	 * @param b - value
	 */
	
	default void setEnabled(boolean b) {
		DataManager.setEnabled(block(), b);
	}
	
	/**
	 * @return {@code true} if this spawner is enabled, otherwise {@code false}
	 */
	
	default boolean isRotating() {
		return DataManager.isRotating(block());
	}
	
	/**
	 * Sets this spawner rotating or not.
	 * If this spawner is not rotating then no entities will be spawned.
	 * 
	 * @param b - value
	 */
	
	default void setRotating(boolean b) {
		DataManager.setRotating(block(), b);
	}
	
	/**
	 * @return Spawner type of this spawner
	 */
	
	default SpawnerType getType() {
		return DataManager.getType(block());
	}
	
	/**
	 * Sets the spawner type to this spawner.
	 */

	default void setType(SpawnerType type) {
		DataManager.setType(block(), type);
	}

	@Deprecated(forRemoval = true)
	default int getStackSize() {
		return DataManager.getStack(block());
	}
	
	/**
	 * @return Stack size of this spawner
	 */
	
	default int getStack() {
		return DataManager.getStack(block());
	}

	@Deprecated(forRemoval = true)
	default void setStackSize(int s) {
		DataManager.setStack(block(), s);
	}
	
	/**
	 * Sets the stack size to this spawner.
	 * 
	 * @param s - new stack size
	 */
	
	default void setStack(int s) {
		DataManager.setStack(block(), s);
	}
	
	@Deprecated(forRemoval = true)
	default int getSpawnableSize() {
		return DataManager.getSpawnable(block());
	}
	
	/**
	 * @return Spawnable entity limit of this spawner
	 */
	
	default int getSpawnable() {
		return DataManager.getSpawnable(block());
	}

	@Deprecated(forRemoval = true)
	default void setSpawnableSize(int s) {
		DataManager.setSpawnable(block(), s);
	}
	
	/**
	 * Sets the spawnable entity limit to this spawner.
	 * 
	 * @param s - new spawnable entity limit
	 */
	
	default void setSpawnable(int s) {
		DataManager.setSpawnable(block(), s);
	}
	
	/**
	 * @return {@code true} if this spawner was generated or placed using a command, otherwise {@code false}
	 */
	
	default boolean isNatural() {
		return isOwned() == false;
	}
	
	default boolean isOwned() {
		return DataManager.isPlaced(block());
	}
	
	default Player getOwner() {
		UUID id = DataManager.getOwner(block());
		return id == null ? null : Bukkit.getPlayer(id);
	}
	
	default boolean isOwner(Player player) {
		return isOwner(player, false);
	}
	
	default boolean isOwner(Player player, boolean def) {
		UUID id = DataManager.getOwner(block());
		return id == null ? def : id.equals(player.getUniqueId());
	}
	
	default void setOwner(Player player) {
		DataManager.setOwner(block(), player);
	}
	
	default int[] getUpgradeLevels() {
		return DataManager.getUpgradeLevels(block());
	}
	
	default int getUpgradeLevel(UpgradeType type) {
		return DataManager.getUpgradeLevels(block())[type.ordinal()];
	}
	
	default void setUpgradeLevels(int[] as) {
		DataManager.setUpgradeLevels(block(), as);
	}
	
	default int[] getUpgradeAttributes() {
		return DataManager.getUpgradeAttributes(block());
	}
	
	default int getUpgradeAttribute(UpgradeType type) {
		return DataManager.getUpgradeAttributes(block())[type.ordinal()];
	}
	
	default void setUpgradeAttributes(int[] as) {
		DataManager.setUpgradeAttributes(block(), as);
	}
	
	default int getCharges() {
		return DataManager.getCharges(block());
	}
	
	default void setCharges(int a) {
		DataManager.setCharges(block(), a);
	}
	
	default void update() {
		DataManager.updateValues(block());
	}
	
	default List<ItemStack> toItems() {
		return DataManager.getSpawners(block(), false);
	}

}
