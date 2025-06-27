package mc.rellox.spawnermeta.api.spawner.location;

import java.util.List;

import org.bukkit.Location;

import mc.rellox.spawnermeta.api.spawner.requirement.ErrorCounter;
import mc.rellox.spawnermeta.api.spawner.requirement.IRequirements;

public interface IFinder {
	
	/**
	 * Updates spawner entity changes.
	 */
	
	void update();
	
	/**
	 * @return Spawner requirements to spawn
	 */
	
	IRequirements requirements();
	
	/**
	 * @return Returns all valid spawn locations
	 */
	
	List<Location> find();
	
	/**
	 * This method can be called only once after method {@code find()}.
	 * 
	 * @return Finder error counter 
	 * @throws IllegalStateException if method {@code find()} has not been ran
	 *  or this method is ran twice
	 */
	
	ErrorCounter errors() throws IllegalStateException;

}
