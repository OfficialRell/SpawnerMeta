package mc.rellox.spawnermeta;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import mc.rellox.spawnermeta.api.APIInstance;
import mc.rellox.spawnermeta.api.APIRegistry;
import mc.rellox.spawnermeta.commands.CommandManager;
import mc.rellox.spawnermeta.configuration.Configuration;
import mc.rellox.spawnermeta.configuration.location.LocationRegistry;
import mc.rellox.spawnermeta.events.EventListeners;
import mc.rellox.spawnermeta.hook.HookRegistry;
import mc.rellox.spawnermeta.shop.ShopRegistry;
import mc.rellox.spawnermeta.spawner.generator.GeneratorRegistry;
import mc.rellox.spawnermeta.spawner.generator.SpawningManager;
import mc.rellox.spawnermeta.text.Text;
import mc.rellox.spawnermeta.utility.DataManager;
import mc.rellox.spawnermeta.utility.Metrics;
import mc.rellox.spawnermeta.utility.Utils;
import mc.rellox.spawnermeta.version.Version;

public final class SpawnerMeta extends JavaPlugin {
	
	public static final double PLUGIN_VERSION = 21.9;
	
	private static SpawnerMeta plugin;
    
    private static boolean loaded;
    
    private APIInstance api;
    
    @Override
    public void onLoad() {
		loaded = Version.version != null;
		
		if(loaded == true) {
			plugin = this;
			this.api = new APIRegistry();
		}
    }

	@Override
	public void onEnable() {
		if(loaded == true) {
			Text.logLoad();
			Utils.check(74188, s -> {
				if(Utils.isDouble(s) == false) return;
				double v = Double.parseDouble(s);
				if(v > PLUGIN_VERSION) Text.logOutdated(v);
			});
			HookRegistry.load();
			if(HookRegistry.ECONOMY.exists() == true) Text.logInfo("Vault has been found, economy enabled!");
			if(HookRegistry.FLARE_TOKENS.exists() == true) Text.logInfo("FlareTokens has been found!");
			if(HookRegistry.WILD_STACKER.exists() == true) Text.logInfo("Wild Stacker has been found, entity stacking enabled!");
			if(HookRegistry.WILD_TOOLS.exists() == true) Text.logInfo("Wild Tools has been found, custom drop provided!");
			if(HookRegistry.SHOP_GUI.exists() == true) Text.logInfo("ShopGUI+ has been found, custom spawners provided!");
			initializeMetrics();
			Configuration.initialize();
			CommandManager.initialize();
			DataManager.initialize();
			ShopRegistry.initialize();
			EventListeners.initialize();
			GeneratorRegistry.initialize();
			SpawningManager.initialize();
			LocationRegistry.initialize();
		} else {
			Text.logFail("failed to load, invalid server version!");
			Bukkit.getPluginManager().disablePlugin(this);
		}
	}
	
	/**
	 * @return Instance of this plugin
	 */
	
	public static SpawnerMeta instance() {
		return plugin;
	}
	
	/**
	 * @return SpawnerMeta API
	 */
	
	public APIInstance getAPI() {
		return api;
	}
	
	@Override
	public void onDisable() {
		if(loaded == true) {
			Text.logUnload();
			GeneratorRegistry.clear();
		}
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		return CommandManager.onCommand(sender, command, label, args);
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		return CommandManager.onTabComplete(sender, command, alias, args);
	}
	
	private void initializeMetrics() {
		new Metrics(plugin, 8373);
	}
}
