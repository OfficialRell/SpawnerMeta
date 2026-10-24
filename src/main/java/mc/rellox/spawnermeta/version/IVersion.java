package mc.rellox.spawnermeta.version;

import java.util.Collection;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public interface IVersion {
	
	void send(Collection<? extends Player> players, Object... packets);
	
	Object spawn(Object entity);
	
	Object meta(Object entity);
	
	Object destroy(Object entity);
	
	Object hologram(Location location, String name);
	
	void name(Object entity, String name);
	
	default void send(Player player, Object... packets) {
		send(List.of(player), packets);
	}
	
	default void send(Object... packets) {
		send(Bukkit.getOnlinePlayers(), packets);
	}

}
