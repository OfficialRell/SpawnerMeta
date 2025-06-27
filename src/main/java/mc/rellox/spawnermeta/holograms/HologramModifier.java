package mc.rellox.spawnermeta.holograms;

import java.util.Set;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import mc.rellox.spawnermeta.version.IVersion;
import mc.rellox.spawnermeta.version.Version;

public class HologramModifier {
	
	private final IVersion v;
	
	public HologramModifier() {
		this.v = Version.v;
	}
	
	public Object create(Location l, String name) {
		return v.hologram(l, name);
	}
	
	public void spawn(Player player, Object entity) {
		v.send(player, v.spawn(entity), v.meta(entity));
	}

	public void destroy(Player player, Object entity) {
		v.send(player, v.destroy(entity));
	}

	public void update(Set<Player> players, Object entity, String name) {
		v.name(entity, name);
		v.send(players, v.meta(entity));
	}

}
