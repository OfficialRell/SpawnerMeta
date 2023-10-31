package mc.rellox.spawnermeta.api.spawner;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import mc.rellox.spawnermeta.configuration.Settings;
import mc.rellox.spawnermeta.spawner.ActiveSpawner;
import mc.rellox.spawnermeta.spawner.ActiveVirtual;
import mc.rellox.spawnermeta.spawner.type.SpawnerType;
import mc.rellox.spawnermeta.spawner.type.UpgradeType;
import mc.rellox.spawnermeta.utility.DataManager;
import mc.rellox.spawnermeta.utility.reflect.Reflect.RF;

public interface ISpawner {
	
	// type;range;delay;amount;charges;spawnable;amount;empty
	
	static List<ItemStack> from(String data) {
		try {
			String[] ss = data.split(";");
			SpawnerType type = SpawnerType.of(ss[0]);
			int[] levels = {Integer.parseInt(ss[1]),
					Integer.parseInt(ss[2]),
					Integer.parseInt(ss[3])};
			int charges = Integer.parseInt(ss[4]);
			int spawnable = Integer.parseInt(ss[5]);
			int amount = Integer.parseInt(ss[6]);
			boolean empty = ss[7].equalsIgnoreCase("true");
			return DataManager.getSpawner(type, levels, charges, spawnable, amount, empty);
		} catch (Exception e) {
			RF.debug(e);
		}
		return List.of();
	}
	
	static ISpawner of(Block block) {
		return new ActiveSpawner(block);
	}
	
	/**
	 * @return Spawner block
	 */
	
	Block block();
	
	/**
	 * @return The world in which the spawner is in
	 */
	
	World world();
	
	/**
	 * Equivalent to {@code block().getLocation().add(0.5, 0.5, 0.5)}
	 * 
	 * @return Center location of this spawner
	 */
	
	Location center();
	
	/**
	 * @return {@code true} if this spawner is empty, otherwise {@code false}
	 */
	
	boolean isEmpty();
	
	/**
	 * Sets this spawner as empty.
	 */
	
	void setEmpty();
	
	/**
	 * @return {@code true} if this spawner is enabled, otherwise {@code false}
	 */
	
	boolean isEnabled();
	
	/**
	 * Enables or disables this spawner.
	 * 
	 * @param enabled - value
	 */
	
	void setEnabled(boolean enabled);
	
	/**
	 * @return {@code true} if this spawner is enabled, otherwise {@code false}
	 */
	
	boolean isRotating();
	
	/**
	 * Sets this spawner rotating or not.
	 * If this spawner is not rotating then no entities will be spawned.
	 * 
	 * @param rotating - value
	 */
	
	void setRotating(boolean rotating);
	
	/**
	 * @return Spawner type of this spawner
	 */
	
	SpawnerType getType();
	
	/**
	 * Sets the spawner type to this spawner.
	 */

	void setType(SpawnerType type);

	/**
	 * Use {@code getStack()} method instead
	 */
	
	@Deprecated(forRemoval = true)
	int getStackSize();
	
	/**
	 * @return Stack size of this spawner
	 */
	
	int getStack();

	/**
	 * Use {@code setStack()} method instead
	 */

	@Deprecated(forRemoval = true)
	void setStackSize(int stack);
	
	/**
	 * Sets the stack size to this spawner.
	 * 
	 * @param stack - new stack size
	 */
	
	void setStack(int stack);

	/**
	 * Use {@code getSpawnable()} method instead
	 */
	
	@Deprecated(forRemoval = true)
	int getSpawnableSize();
	
	/**
	 * @return Spawnable entity limit of this spawner
	 */
	
	int getSpawnable();

	/**
	 * Use {@code setSpawnable()} method instead
	 */

	@Deprecated(forRemoval = true)
	void setSpawnableSize(int spawnable);
	
	/**
	 * Sets the spawnable entity limit to this spawner.
	 * 
	 * @param spawnable - new spawnable entity limit
	 */
	
	void setSpawnable(int spawnable);
	
	/**
	 * @return {@code true} if this spawner was generated or placed using a command,
	 * otherwise {@code false}
	 */
	
	boolean isNatural();
	
	/**
	 * @return {@code true} if this spawner was placed by a player, otherwise {@code false}
	 */
	
	boolean isOwned();
	
	/**
	 * @return Owner of this spawner or {@code null} if natural or owner is not online
	 */
	
	Player getOwner();
	
	/**
	 * @return Owner UUID of this spawner or {@code null} if natural
	 */
	
	UUID getOwnerID();
	
	/**
	 * @param player - specific player
	 * @return {@code true} if this player owns this spawner, otherwise {@code false}
	 */
	
	boolean isOwner(Player player);
	
	/**
	 * @param player - specific player
	 * @param def - default value
	 * @return {@code true} if this player owns this spawner, otherwise default value
	 */
	
	boolean isOwner(Player player, boolean def);
	
	/**
	 * Sets an owner for this spawner
	 * 
	 * @param player - specific player
	 */
	
	void setOwner(Player player);
	
	/**
	 * Returns an array upgrade levels: [range, delay, amount]
	 * 
	 * @return Array of upgrade levels
	 */
	
	int[] getUpgradeLevels();
	
	/**
	 * @param type - upgrade type
	 * @return Specific upgrade level
	 */
	
	int getUpgradeLevel(UpgradeType type);
	
	/**
	 * @param levels - array of upgrade levels
	 */
	
	void setUpgradeLevels(int[] levels);
	
	/**
	 * Resets all upgrade levels.
	 */
	
	void resetUpgradeLevels();
	
	/**
	 * Returns an array upgrade attributes: [range, delay, amount]
	 * 
	 * @return Array of upgrade attributes
	 */
	
	int[] getUpgradeAttributes();
	
	/**
	 * @param type - upgrade type
	 * @return Specific upgrade attribute
	 */
	
	int getUpgradeAttribute(UpgradeType type);
	
	/**
	 * Equation: (range + delay + amount - 3) = level
	 * 
	 * @return Total spawner level
	 */
	
	int getSpawnerLevel();
	
	/**
	 * @return Spawner charges
	 */
	
	int getCharges();
	
	/**
	 * @param charges - amount of charges
	 */
	
	void setCharges(int charges);
	
	/**
	 * @param ticks - delay until this spawner will try to spawn again
	 */
	
	void setDelay(int ticks);
	
	/**
	 * Updates all spawner values
	 */
	
	void update();
	
	/**
	 * @return List of spawner items
	 */
	
	List<ItemStack> toItems();
	
	/**
	 * @return converted savable spawner data
	 */
	
	String toData();
	
	/**
	 * @param type - spawner type
	 * @return Virtual spawner builder
	 */
	
	static Builder builder(SpawnerType type) {
		return new Builder(type);
	}
	
	class Builder {
		
		private final SpawnerType type;
		private int[] levels;
		private int charges;
		private int spawnable;
		private boolean empty;
		
		public Builder(SpawnerType type) {
			this.type = Objects.requireNonNull(type, "Spawner type cannot be null");
		}
		
		public IVirtual build() {
			return new ActiveVirtual(type, levels, charges, spawnable, empty);
		}
		
		public Builder levelled(int i0, int i1, int i2) {
			int[] ms = Settings.settings.upgrades_levels.get(type);
			this.levels = new int[] {a(i0, 1, ms[0]),
					a(i1, 1, ms[1]), a(i2, 1, ms[2])};
			return this;
		}
		
		private int a(int i, int m, int x) {
			return i < m ? m : i > x ? x : i;
		}
		
		public Builder charged(int i) {
			this.charges = i < 0 ? 0 : i;
			return this;
		}
		
		public Builder spawnable(int i) {
			this.spawnable = i < 0 ? 0 : i;
			return this;
		}
		
		public Builder empty(boolean b) {
			this.empty = b;
			return this;
		}
		
	}

}
