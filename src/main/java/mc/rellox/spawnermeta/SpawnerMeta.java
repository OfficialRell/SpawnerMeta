package mc.rellox.spawnermeta;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import com.tcoded.folialib.FoliaLib;
import com.tcoded.folialib.impl.PlatformScheduler;

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
import mc.rellox.spawnermeta.utility.Utility;
import mc.rellox.spawnermeta.version.Version;

public final class SpawnerMeta extends JavaPlugin {
	
	public static final double PLUGIN_VERSION = 25.1;
	
	private static SpawnerMeta plugin;

	private static FoliaLib foliaLib;

	private static PlatformScheduler scheduler;

	private static boolean loaded;
    
    private APIInstance api;
    
    @Override
    public void onLoad() {
		loaded = Version.version != null;
		
		if(loaded == true) {
			plugin = this;
			foliaLib = new FoliaLib(this);
			scheduler = foliaLib.getScheduler();
			this.api = new APIRegistry();
		}
    }

	@Override
	public void onEnable() {
		if(Bukkit.getPluginManager().getPlugin("SpawnerLegacy") != null) {
			loaded = false;
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}
		
		if(loaded == true) {
			Text.logLoad();
			Utility.check(74188, s -> {
				if(Utility.isDouble(s) == false) return;
				double v = Double.parseDouble(s);
				if(v > PLUGIN_VERSION) Text.logOutdated(v);
			});
			HookRegistry.load();
			Configuration.initialize();
			CommandManager.initialize();
			DataManager.initialize();
			ShopRegistry.initialize();
			EventListeners.initialize();
			GeneratorRegistry.initialize();
			SpawningManager.initialize();
			LocationRegistry.initialize();
			initializeMetrics();
		} else {
			Text.logFail("failed to load, invalid server version!");
			Bukkit.getPluginManager().disablePlugin(this);
		}
	}
	
	@Override
	public void onDisable() {
		if(loaded == true) {
			Text.logUnload();
			GeneratorRegistry.clear();
			LocationRegistry.clear();
		}
	}
	
	/**
	 * @return Instance of this plugin
	 */

	public static SpawnerMeta instance() {
		return plugin;
	}

	/**
	 * @return Instance of FoliaLib
	 */

	public static FoliaLib foliaLib() {
		return foliaLib;
	}

	/**
	 * @return Instance of FoliaLib Scheduler
	 */

	public static PlatformScheduler scheduler() {
		return scheduler;
	}

	/**
	 * @return SpawnerMeta API
	 */
	
	public APIInstance getAPI() {
		return api;
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
