package mc.rellox.spawnermeta.holograms;

import mc.rellox.spawnermeta.version.IVersion;
import mc.rellox.spawnermeta.version.Version;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Set;

public record HologramModifier(IVersion version) {

	public HologramModifier() {
		this(Version.v);
	}
	
	public Object create(Location location, String name) {
		return version.hologram(location, name);
	}
	
	public void spawn(Player player, Object entity) {
		version.send(player, version.spawn(entity), version.meta(entity));
	}

	public void destroy(Player player, Object entity) {
		version.send(player, version.destroy(entity));
	}

	public void update(Set<Player> players, Object entity, String name) {
		version.name(entity, name);
		version.send(players, version.meta(entity));
	}

}
