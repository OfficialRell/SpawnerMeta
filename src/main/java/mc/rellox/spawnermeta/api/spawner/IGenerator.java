package mc.rellox.spawnermeta.api.spawner;

import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import mc.rellox.spawnermeta.api.spawner.location.Pos;
import mc.rellox.spawnermeta.api.view.IUpgrades;

public interface IGenerator {
	
	/**
	 * @return This spawner
	 */
	
	ISpawner spawner();
	
	/**
	 * @return Spawner block
	 */
	
	Block block();
	
	/**
	 * @return World the generator is in
	 */
	
	World world();
	
	/**
	 * @return Spawner cache
	 */
	
	ICache cache();
	
	/**
	 * @return {@code true} if this generator is active, otherwise {@code false}
	 */
	
	boolean active();
	
	/**
	 * @return {@code true} if this generator is a spawner type block
	 *  or is an a loaded chunk, otherwise {@code false}
	 */
	
	boolean present();
	
	/**
	 * Removes this generator. If fully is {@code true} then this generator will be
	 *  fully removed including the block and location.
	 * 
	 * @param fully - should this generator be removed fully
	 */
	
	void remove(boolean fully);
	
	/**
	 * @return Current spawner ticks
	 */
	
	int ticks();
	
	/**
	 * Sets the generator ticks. <br>
	 * This will not take ticking interval into consideration.
	 * 
	 * @param ticks - new spawner ticks
	 */
	
	void ticks(int ticks);
	
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
	 * Does all spawner updates and checks.
	 */
	
	void refresh();
	
	/**
	 * Updates spawner options that might control it.
	 */
	
	void control();
	
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
	 * floor material, spawning space, charges and other.
	 * 
	 * @return {@code true} if this spawner is valid to spawner entities
	 */
	
	boolean valid();
	
	/**
	 * Returns {@code true} if the owner of this spawner is online
	 * or if the option 'spawn-if-online' is true.
	 * 
	 * @return {@code true} or {@code false}
	 */
	
	boolean online();
	
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
	 * Resets vanilla spawner values.
	 */
	
	void unload();
	
	/**
	 * @return Position of this spawner
	 */
	
	default Pos position() {
		return Pos.of(spawner().block());
	}
	
	/**
	 * @param chunk - chunk
	 * @return {@code true} if this spawner is in the specified chunk
	 */
	
	default boolean in(Chunk chunk) {
		Block block = block();
		if(block.getWorld().equals(chunk.getWorld()) == false) return false;
		return chunk.getX() == (block.getX() >> 4) && chunk.getZ() == (block.getZ() >> 4);
	}

}
