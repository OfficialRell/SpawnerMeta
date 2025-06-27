package mc.rellox.spawnermeta.api.view;

import java.util.List;

import org.bukkit.entity.Player;

import mc.rellox.spawnermeta.api.spawner.ISpawner;
import mc.rellox.spawnermeta.api.spawner.IGenerator;

public interface IUpgrades {
	
	/**
	 * @return Entity generator
	 */
	
	IGenerator generator();
	
	/**
	 * @return Spawner
	 */
	
	ISpawner spawner();
	
	/**
	 * Returns list of players who are viewing this upgrade GUI.
	 * 
	 * @return List of viewers
	 */
	
	List<Player> viewers();
	
	/**
	 * @return {@code true} if any player is using this upgrade GUI, otherwise {@code false}
	 */
	
	boolean active();
	
	/**
	 * Opens this upgrade GUI for the specified player.
	 * 
	 * @param player - player
	 */
	
	void open(Player player);
	
	/**
	 * Closes and unregisters this upgrade GUI.
	 */
	
	void close();
	
	/**
	 * Updates upgrade GUI inventory.
	 */
	
	void update();

}
