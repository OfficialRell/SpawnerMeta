package mc.rellox.spawnermeta.hook;

import org.bukkit.entity.Player;

public interface ICurrency {
	
	int get(Player player);
	
	boolean has(Player player, int a);
	
	void add(Player player, int a);
	
	void remove(Player player, int a);

}
