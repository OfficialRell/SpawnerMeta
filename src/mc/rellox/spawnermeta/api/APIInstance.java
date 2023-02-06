package mc.rellox.spawnermeta.api;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import mc.rellox.spawnermeta.api.events.EventExecutor;
import mc.rellox.spawnermeta.api.events.IEvent;
import mc.rellox.spawnermeta.api.spawner.Spawner;
import mc.rellox.spawnermeta.api.spawner.SpawnerBuilder;
import mc.rellox.spawnermeta.api.spawner.VirtualSpawner;
import mc.rellox.spawnermeta.spawner.SpawnerType;

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
	 * @param spawner - spawner data
	 * @return {@code true} if the spawner was placed, otherwise {@code false}
	 * 
	 * @throws NullPointerException if block or spawner is {@code null}
	 */
	
	boolean placeSpawner(Block block, Player player, VirtualSpawner spawner);
	
	/**
	 * Tries to place the spawner at the specified block.
	 * 
	 * @param block - position
	 * @param spawner - spawner data
	 * @return {@code true} if the spawner was placed, otherwise {@code false}
	 * 
	 * @throws NullPointerException if block or spawner is {@code null}
	 */
	
	default boolean placeSpawner(Block block, VirtualSpawner spawner) {
		return placeSpawner(block, null, spawner);
	}
	
	/**
	 * Returns an immutable spawner with the data from the specified item.
	 * 
	 * @param item - spawner item
	 * @return Immutable spawner or {@code null} if not a spawner
	 * 
	 * @throws NullPointerException if item is {@code null}
	 */
	
	VirtualSpawner getVirtual(ItemStack item);
	
	/**
	 * Returns an immutable spawner with the data from the specified block.
	 * 
	 * @param block - spawner block
	 * @return Immutable spawner or {@code null} if not a spawner
	 * 
	 * @throws NullPointerException if block is {@code null}
	 */
	
	VirtualSpawner getVirtual(Block block);

	/**
	 * Returns a mutable spawner with the data from the specified block.
	 * 
	 * @param block - spawner block
	 * @return Mutable spawner
	 * 
	 * @throws NullPointerException if block is {@code null}
	 */
	
	Spawner getSpawner(Block block);

	/**
	 * Returns a spawner builder with the specified spawner type;
	 * 
	 * @param type - spawner type
	 * @return Mutable spawner
	 * 
	 * @throws NullPointerException if type is {@code null}
	 */
	
	SpawnerBuilder buildSpawner(SpawnerType type);

}
