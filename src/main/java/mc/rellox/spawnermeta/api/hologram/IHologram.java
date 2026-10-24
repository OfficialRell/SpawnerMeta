package mc.rellox.spawnermeta.api.hologram;

import mc.rellox.spawnermeta.api.spawner.IGenerator;
import mc.rellox.spawnermeta.holograms.HologramIdentity;
import mc.rellox.spawnermeta.holograms.HologramNull;
import mc.rellox.spawnermeta.holograms.HologramWarning;
import org.bukkit.entity.Player;

import java.util.Set;

public interface IHologram {

    IHologramHandler HANDLER = IHologramHandler.of();

    static IHologram hologram(IGenerator generator) {
        return HANDLER == null
                ? new HologramNull(generator)
                : new HologramIdentity(generator);
    }

    static IHologram warning(IGenerator generator, boolean above) {
        return HANDLER == null
                ? new HologramNull(generator)
                : new HologramWarning(generator, above);
    }

    /**
     * @return Entity generator
     */

    IGenerator generator();

    /**
     * @return Set of players who sees this spawner hologram
     */

    Set<Player> viewers();

    /**
     * Updates spawner hologram players.
     */

    void update();

    /**
     * Updates spawner hologram text.
     */

    void rewrite();

    /**
     * Shows this hologram to the specified player.
     *
     * @param player player
     */

    void show(Player player);

    /**
     * Hides this hologram from the specified player.
     *
     * @param player player
     */

    void hide(Player player);

    /**
     * Hides this hologram from all players who see it.
     */

    void clear();

}
