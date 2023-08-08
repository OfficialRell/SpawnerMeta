package mc.rellox.spawnermeta.api.spawner;

import org.bukkit.entity.Player;

import mc.rellox.spawnermeta.api.spawner.location.Pos;
import mc.rellox.spawnermeta.api.view.IUpgrades;

public interface IGenerator {
	
	/**
	 * @return This spawner
	 */
	
	ISpawner spawner();
	
	/**
	 * @return Spawner cache
	 */
	
	ICache cache();
	
	/**
	 * Ticks this spawner.
	 */
	
	void tick();
	
	/**
	 * Updates spawner delay, upgrades and other.
	 */
	
	void update();
	
	/**
	 * Updates hologram and warning text, if exists.
	 */
	
	void rewrite();
	
	/**
	 * Updates spawner constants.
	 */
	
	void refresh();
	
	/**
	 * Tries to spawn entities from this spawner.
	 */
	
	boolean spawn();
	
	/**
	 * Sets a new warning message for this spawner.
	 * 
	 * @param warning - warning message
	 */
	
	void warn(SpawnerWarning warning);
	
	/**
	 * @param warning - warning
	 * @return {@code true} if this spawner has this warning, otherwise {@code false}
	 */
	
	boolean warned(SpawnerWarning warning);
	
	/**
	 * @return {@code true} if this spawner has any warnings, otherwise {@code false}
	 */
	
	boolean warned();
	
	/**
	 * Checks if all spawner requirements are met. Such as, light level,
	 *  flood material, spawning space, charges and other.
	 * 
	 * @return {@code true} if this spawner is valid to spawner entities
	 */
	
	boolean valid();
	
	/**
	 * @return Spawner upgrade GUI or {@code null} if not opened
	 */
	
	IUpgrades upgrades();
	
	/**
	 * Opens upgrade GUI for the specific player.
	 * 
	 * @param player - player
	 * @return Spawner upgrade GUI
	 */
	
	void open(Player player);
	
	/**
	 * Closes and unregisters upgrade GUI.
	 */
	
	void close();
	
	/**
	 * Closes spawner upgrade GUI if active.
	 */
	
	void clear();
	
	/**
	 * @return Position of this spawner
	 */
	
	default Pos position() {
		return Pos.of(spawner().block());
	}

}
