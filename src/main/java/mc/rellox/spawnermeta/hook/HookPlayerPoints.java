package mc.rellox.spawnermeta.hook;

import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.PlayerPointsAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public final class HookPlayerPoints implements HookInstance {
	
	private PlayerPoints plugin;
	private ICurrency currency;
	
	public ICurrency currency() {
		return currency;
	}
	
	@Override
	public boolean exists() {
		return plugin != null;
	}
	
	@Override
	public String message() {
		return "PlayerPoints has been found, player points enabled!";
	}

	@Override
	public void load() {
		if((this.plugin = fetch()) == null) return;
		currency = new ICurrency() {
			PlayerPointsAPI api = plugin.getAPI();
			@Override
			public void remove(Player player, int a) {
				api.take(player.getUniqueId(), a);
			}
			@Override
			public boolean has(Player player, int a) {
				return api.look(player.getUniqueId()) >= a;
			}
			@Override
			public int get(Player player) {
				return api.look(player.getUniqueId());
			}
			@Override
			public void add(Player player, int a) {
				api.give(player.getUniqueId(), a);
			}
		};
	}
	
	private static PlayerPoints fetch() {
		return (PlayerPoints) Bukkit.getServer()
				.getPluginManager()
				.getPlugin("PlayerPoints");
	}

}
