package mc.rellox.spawnermeta.api.hologram;

import mc.rellox.spawnermeta.holograms.HologramModifier;
import mc.rellox.spawnermeta.version.Version;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Set;

public interface IHologramHandler {

    static IHologramHandler of() {
        var version = Version.instance();
        return version == null ? null : new HologramModifier(version);
    }

    Object create(Location location, String name);

    void spawn(Player player, Object entity);

    void destroy(Player player, Object entity);

    void update(Set<Player> players, Object entity, String name);

}
