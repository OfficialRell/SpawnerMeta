package mc.rellox.spawnermeta.api;

import java.util.UUID;
import java.util.function.Function;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import mc.rellox.spawnermeta.api.configuration.ILocations;
import mc.rellox.spawnermeta.api.events.EventExecutor;
import mc.rellox.spawnermeta.api.events.IEvent;
import mc.rellox.spawnermeta.api.spawner.IGenerator;
import mc.rellox.spawnermeta.api.spawner.ISpawner;
import mc.rellox.spawnermeta.api.spawner.IVirtual;
import mc.rellox.spawnermeta.api.spawner.SpawnerBuilder;
import mc.rellox.spawnermeta.spawner.type.SpawnerType;

@SuppressWarnings("removal")
public interface APIInstance {
	
	/**
	 * Registers an event of the speficied class type.
	 * 
	 * @param <E> - event class type
	 * @param c - event class
	 * @param executor - event executor
	 * 
	 * @throws NullPointerException if event class or executor is {@code null}
	 */
	
	<E extends IEvent> void register(Class<E> c, EventExecutor<E> executor);
	
	/**
	 * Sets a new silk touch provider.
	 * Function takes player who broke the spawner.
	 * 
	 * @param provider - provider
	 */
	
	void setSilkTouchProvider(Function<Player, Boolean> provider);
	
	/**
	 * @param player - player
	 * @return {@code true} if this player is holding a tool with silk touch
	 */
	
	boolean hasSilkTouch(Player player);
	
	/**
	 * Tries to breaks the spawner at the specified block.
	 * 
	 * @param block - spawner
	 * @param drop - drops the spawner item if {@code true}
	 * @param particles - spawns particles if {@code true}
	 * @return {@code true} it the spawner was broken, otherwise {@code false}
	 * 
	 * @throws NullPointerException if block is {@code null}
	 */
	
	boolean breakSpawner(Block block, boolean drop, boolean particles);
	
	/**
	 * Tries to breaks the spawner at the specified block.
	 * 
	 * @param block - spawner
	 * @param drop - drops the spawner item if {@code true}
	 * @return {@code true} it the spawner was broken, otherwise {@code false}
	 * 
	 * @throws NullPointerException if block is {@code null}
	 */
	
	default boolean breakSpawner(Block block, boolean drop) {
		return breakSpawner(block, drop, true);
	}
	
	/**
	 * Tries to breaks the spawner at the specified block.
	 * 
	 * @param block - spawner
	 * @return {@code true} it the spawner was broken, otherwise {@code false}
	 * 
	 * @throws NullPointerException if block is {@code null}
	 */
	
	default boolean breakSpawner(Block block) {
		return breakSpawner(block, true);
	}
	
	/**
	 * Tries to place the spawner at the specified block.
	 * 
	 * @param block - position
	 * @param player - spawner owner
	 * @param virtual - virtual spawner
	 * @return {@code true} if the spawner was placed, otherwise {@code false}
	 * 
	 * @throws NullPointerException if block or spawner is {@code null}
	 */
	
	boolean placeSpawner(Block block, Player player, IVirtual virtual);
	
	/**
	 * Tries to place the spawner at the specified block.
	 * 
	 * @param block - position
	 * @param virtual - virtual spawner
	 * @return {@code true} if the spawner was placed, otherwise {@code false}
	 * 
	 * @throws NullPointerException if block or spawner is {@code null}
	 */
	
	default boolean placeSpawner(Block block, IVirtual virtual) {
		return placeSpawner(block, null, virtual);
	}
	
	/**
	 * Returns an immutable spawner with the data from the specified item.
	 * 
	 * @param item - spawner item
	 * @return Immutable spawner or {@code null} if not a spawner
	 * 
	 * @throws NullPointerException if item is {@code null}
	 */
	
	IVirtual getVirtual(ItemStack item);
	
	/**
	 * Returns an immutable spawner with the data from the specified block.
	 * 
	 * @param block - spawner block
	 * @return Immutable spawner or {@code null} if not a spawner
	 * 
	 * @throws NullPointerException if block is {@code null}
	 */
	
	IVirtual getVirtual(Block block);

	/**
	 * Returns a mutable spawner with the data from the specified block.
	 * 
	 * @param block - spawner block
	 * @return Mutable spawner
	 * 
	 * @throws NullPointerException if block is {@code null}
	 */
	
	ISpawner getSpawner(Block block);
	
	/**
	 * Returns the entity generator or {@code null} if no spawner at this block.
	 * 
	 * @param block - spawner block
	 * @return Entity generator
	 * 
	 * @throws NullPointerException if block is {@code null}
	 */
	
	IGenerator getGenerator(Block block);
	
	/**
	 * Returns spawner location file of the specified player. Never {@code null}.
	 * 
	 * @param player - player
	 * @return Spawner location file
	 */
	
	default ILocations getLocations(Player player) {
		return getLocations(player.getUniqueId());
	}
	
	/**
	 * Returns spawner location file of the specified player. Never {@code null}.
	 * 
	 * @param id - player's ID
	 * @return Spawner location file
	 *
	 * @throws IllegalArgumentException thrown if the player with
	 *  the specified ID has never player before
	 */
	
	ILocations getLocations(UUID id) throws IllegalArgumentException;

	/**
	 * Returns a spawner builder with the specified spawner type.
	 * 
	 * This method is deprecated, use {@code Spawner.builder()} method instead.
	 * 
	 * @param type - spawner type
	 * @return Mutable spawner
	 * 
	 * @throws NullPointerException if type is {@code null}
	 */
	
	@Deprecated(forRemoval = true)
	SpawnerBuilder buildSpawner(SpawnerType type);

}
