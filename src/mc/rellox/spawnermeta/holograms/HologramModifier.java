package mc.rellox.spawnermeta.holograms;

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
	
	public void spawn(Object entity) {
		v.send(v.spawn(entity), v.meta(entity));
	}
	
	public void spawn(Player player, Object entity) {
		v.send(player, v.spawn(entity), v.meta(entity));
	}

	public void destroy(Object entity) {
		v.send(v.destroy(entity));
	}

	public void update(Object entity, String name) {
		v.name(entity, name);
		v.send(v.meta(entity));
	}

}
