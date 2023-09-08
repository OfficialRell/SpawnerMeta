package mc.rellox.spawnermeta.hook;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import net.flares.flaretokens.TMMobCoinsPlugin;
import net.flares.flaretokens.API.TokensPlayer;

public final class HookFlareTokens implements HookInstance<TMMobCoinsPlugin> {
	
	private TMMobCoinsPlugin plugin;
	private ICurrency currency;

	@Override
	public TMMobCoinsPlugin get() {
		return this.plugin;
	}
	
	public ICurrency currency() {
		return this.currency;
	}
	
	@Override
	public boolean exists() {
		return plugin != null;
	}

	@Override
	public void load() {
		if((this.plugin = fetch()) == null) return;
		this.currency = new ICurrency() {
			@Override
			public void remove(Player player, int a) {
				TokensPlayer.warpPlayer(player).removeTokens(a);
			}
			@Override
			public boolean has(Player player, int a) {
				return get(player) >= a;
			}
			@Override
			public int get(Player player) {
				return TokensPlayer.warpPlayer(player).getTokens();
			}
			@Override
			public void add(Player player, int a) {
				TokensPlayer.warpPlayer(player).giveTokens(a);
			}
		};
		
	}
	
	private static TMMobCoinsPlugin fetch() {
		return (TMMobCoinsPlugin) Bukkit.getServer().getPluginManager().getPlugin("FlareTokens");
	}

}
