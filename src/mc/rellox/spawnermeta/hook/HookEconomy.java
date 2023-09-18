package mc.rellox.spawnermeta.hook;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

import net.milkbowl.vault.economy.Economy;

public final class HookEconomy implements HookInstance<Economy> {
	
	private Economy economy;
	private ICurrency currency;

	@Override
	public Economy get() {
		return this.economy;
	}
	
	public ICurrency currency() {
		return currency;
	}
	
	@Override
	public boolean exists() {
		return economy != null;
	}

	@Override
	public void load() {
		if((this.economy = fetch()) == null) return;
		currency = new ICurrency() {
			@Override
			public void remove(Player player, int a) {
				economy.withdrawPlayer(player, a);
			}
			@Override
			public boolean has(Player player, int a) {
				return economy.has(player, a);
			}
			@Override
			public int get(Player player) {
				return (int) economy.getBalance(player);
			}
			@Override
			public void add(Player player, int a) {
				economy.depositPlayer(player, a);
			}
		};
	}
	
	private static Economy fetch() {
		if(Bukkit.getServer().getPluginManager().getPlugin("Vault") == null) return null;
		RegisteredServiceProvider<Economy> provider = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
		return provider == null ? null : provider.getProvider();
	}

}
