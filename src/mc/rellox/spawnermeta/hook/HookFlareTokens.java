package mc.rellox.spawnermeta.hook;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import mc.rellox.spawnermeta.utility.reflect.Reflect.RF;

public final class HookFlareTokens implements HookInstance {
	
	private JavaPlugin plugin;
	private ICurrency currency;
	
	public ICurrency currency() {
		return this.currency;
	}
	
	@Override
	public boolean exists() {
		return plugin != null;
	}
	
	@Override
	public String message() {
		return "FlareTokens has been found!";
	}

	@Override
	public void load() {
		if((this.plugin = fetch()) == null) return;
		this.currency = new ICurrency() {
			@Override
			public void remove(Player player, int a) {
				RF.order(t(player), "removeTokens", int.class).invoke(a);
			}
			@Override
			public boolean has(Player player, int a) {
				return get(player) >= a;
			}
			@Override
			public int get(Player player) {
				return RF.order(t(player), "getTokens")
						.as(int.class)
						.invoke(0);
			}
			@Override
			public void add(Player player, int a) {
				RF.order(t(player), "giveTokens", int.class).invoke(a);
			}
			private Object t(Player player) {
				Class<?> c = RF.get("net.flares.flaretokens.API.TokensPlayer");
				return RF.order(c, "warpPlayer", Player.class).invoke(player);
			}
		};
		
	}
	
	private static JavaPlugin fetch() {
		return (JavaPlugin) Bukkit.getServer().getPluginManager().getPlugin("FlareTokens");
	}

}
