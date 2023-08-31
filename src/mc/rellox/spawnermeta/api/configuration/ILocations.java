package mc.rellox.spawnermeta.api.configuration;

import java.util.Set;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public interface ILocations {
	
	/**
	 * Loads all values from the file if not loaded already.
	 */
	
	void load();
	
	/**
	 * @return {@code true} if the player is online, otherwise {@code false}
	 */
	
	boolean online();
	
	/**
	 * If this file has not been used in the last 5 minutes then
	 *  {@code false} is returned, otherwise {@code true}.
	 * 
	 * @return {@code true} if this file is in use
	 */
	
	boolean using();
	
	/**
	 * Refreshes the use time to disable this file from unloading.
	 */
	
	void use();
	
	/**
	 * @return Id of the player
	 */
	
	UUID id();
	
	/**
	 * @return Player object or {@code null} if offline
	 */
	
	Player player();
	
	/**
	 * Returns an unmodifiable set of spawner locations in the specified world.
	 * 
	 * @param world - world
	 * @return Set of locations
	 */
	
	Set<Location> get(World world);
	
	/**
	 * Returns an unmodifiable set of all spawner locations.
	 * 
	 * @return Set of locations
	 */
	
	Set<Location> all();
	
	/**
	 * Returns an unmodifiable set of all trusted player IDs.
	 * <p> Trusted players can manipulate with spawners the same as their owner.
	 * 
	 * @return Set of player IDs
	 */
	
	Set<UUID> trusted();
	
	/**
	 * Tried to find the UUID from the player name.
	 * 
	 * @param name - trusted player name
	 * @return UUID of the player or {@code null} if no player found
	 */
	
	UUID trusted(String name);
	
	/**
	 * @param id - player id
	 * @return {@code true} if this player is trusted
	 */
	
	boolean trusts(UUID id);
	
	/**
	 * @param player - player
	 * @return {@code true} if this player is trusted
	 */
	
	boolean trusts(Player player);
	
	/**
	 * Adds this player to the trust list.
	 * 
	 * @param id - player id
	 * @return {@code true} if this player was not trusted before, otherwise {@code false}
	 */
	
	boolean trust(UUID id);
	
	/**
	 * Adds this player to the trust list.
	 * 
	 * @param player - player
	 * @return {@code true} if this player was not trusted before, otherwise {@code false}
	 */
	
	boolean trust(Player player);
	
	/**
	 * Removes this player from the trust list.
	 * 
	 * @param id - player id
	 * @return {@code true} if this player was trusted before, otherwise {@code false}
	 */
	
	boolean untrust(UUID id);
	
	/**
	 * Removes this player from the trust list.
	 * 
	 * @param player - player
	 * @return {@code true} if this player was trusted before, otherwise {@code false}
	 */
	
	boolean untrust(Player player);
	
	/**
	 * Clears all trusted players.
	 * 
	 * @return Amount of removed trusted players
	 */
	
	int untrust();
	
	/**
	 * Removes the specified spawner location.
	 * 
	 * @param block - spawner block
	 * @return {@code true} if this location did not exists
	 */
	
	boolean remove(Block block);
	
	/**
	 * Adds the specified spawner location.
	 * 
	 * @param block - spawner block
	 * @return {@code true} if this location was added, otherwise {@code false}
	 */
	
	boolean add(Block block);
	
	/**
	 * @return Amount of spawner locations in the specified world
	 */
	
	int amount(World world);
	
	/**
	 * @return Amount of all spawner locations
	 */
	
	int amount();
	
	/**
	 * Clears spawner locations in the specified world.
	 * 
	 * @param world - world
	 * @return Amount of cleared spawner locations
	 */
	
	int clear(World world);
	
	/**
	 * Clears all spawner locations.
	 * 
	 * @return Amount of cleared spawner locations
	 */
	
	int clear();
	
	/**
	 * Checks and removes any invalid spawner locations in the specified world.
	 * 
	 * @param world - world
	 * @return Amount of invalid spawner locations removed
	 */
	
	int validate(World world);
	
	/**
	 * Checks and removes any invalid spawner locations.
	 * 
	 * @return Amount of invalid spawner locations removed
	 */
	
	int validate();
	
}
