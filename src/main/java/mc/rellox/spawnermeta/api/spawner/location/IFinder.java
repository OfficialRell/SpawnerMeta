package mc.rellox.spawnermeta.api.spawner.location;

import mc.rellox.spawnermeta.api.spawner.requirement.ErrorCounter;
import mc.rellox.spawnermeta.api.spawner.requirement.IRequirements;
import org.bukkit.Location;

import java.util.List;

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
     * @throws IllegalStateException if method {@code find()} has not been run
     *                               or this method is run twice
     */

    ErrorCounter errors() throws IllegalStateException;

}
