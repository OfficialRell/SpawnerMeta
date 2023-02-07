package mc.rellox.spawnermeta.holograms;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public interface HologramInstance {
	
	Object create(Location loc, String title);
	
	void spawn(Object hologram);
	
	void destroy(Object hologram);
	
	void update(Object hologram, String title);
	
	void spawn(Player player, Object hologram);
	
	Object nbtText(String text);
	
}
