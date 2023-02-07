package mc.rellox.spawnermeta.hook;

import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;

import net.milkbowl.vault.economy.Economy;

public final class HookEconomy implements HookInstance<Economy> {
	
	private Economy economy;

	@Override
	public Economy get() {
		return this.economy;
	}
	
	@Override
	public boolean exists() {
		return economy != null;
	}

	@Override
	public void load() {
		this.economy = economy();
	}
	
	private static Economy economy() {
		if(Bukkit.getServer().getPluginManager().getPlugin("Vault") == null) return null;
		RegisteredServiceProvider<Economy> provider = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
		return provider == null ? null : provider.getProvider();
	}

}
