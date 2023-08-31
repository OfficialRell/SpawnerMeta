package mc.rellox.spawnermeta;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import mc.rellox.spawnermeta.api.APIInstance;
import mc.rellox.spawnermeta.api.APIRegistry;
import mc.rellox.spawnermeta.commands.CommandManager;
import mc.rellox.spawnermeta.configuration.Configuration;
import mc.rellox.spawnermeta.configuration.location.LocationRegistry;
import mc.rellox.spawnermeta.events.EventListeners;
import mc.rellox.spawnermeta.hook.HookEconomy;
import mc.rellox.spawnermeta.hook.HookShopGUI;
import mc.rellox.spawnermeta.hook.HookWildStacker;
import mc.rellox.spawnermeta.hook.HookWildTools;
import mc.rellox.spawnermeta.shop.ShopRegistry;
import mc.rellox.spawnermeta.spawner.generator.GeneratorRegistry;
import mc.rellox.spawnermeta.spawner.generator.SpawningManager;
import mc.rellox.spawnermeta.utility.DataManager;
import mc.rellox.spawnermeta.utility.Metrics;
import mc.rellox.spawnermeta.utility.Utils;
import mc.rellox.spawnermeta.version.Version;
import mc.rellox.spawnermeta.view.SpawnerViewLayout;

public final class SpawnerMeta extends JavaPlugin {
	
	public static final double PLUGIN_VERSION = 21.2;
	
	private static SpawnerMeta plugin;
	
    public static final HookEconomy ECONOMY = new HookEconomy();
    public static final HookWildStacker WILD_STACKER = new HookWildStacker();
    public static final HookWildTools WILD_TOOLS = new HookWildTools();
    public static final HookShopGUI SHOP_GUI = new HookShopGUI();
    
    private static boolean loaded;
    
    private APIInstance api;
    
    @Override
    public void onLoad() {
		loaded = Version.version != null;
		plugin = this;
    }

	@Override
	public void onEnable() {
		if(loaded == true) {
			Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_PURPLE + "[" + ChatColor.LIGHT_PURPLE + "Spawner Meta " + ChatColor.AQUA + "v"
					+ PLUGIN_VERSION + ChatColor.DARK_PURPLE + "]" + ChatColor.GREEN + " enabled!");
			Utils.check(74188, s -> {
				if(Utils.isDouble(s) == false) return;
				double v = Double.parseDouble(s);
				if(v > PLUGIN_VERSION) Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_PURPLE + "[" + ChatColor.LIGHT_PURPLE + "Spawner Meta "
						+ ChatColor.AQUA + "v" + PLUGIN_VERSION + ChatColor.DARK_PURPLE + "] "
						+ ChatColor.YELLOW + "New version is available: v" + v + "! " + ChatColor.GOLD + "To download visit: "
						+ "https://www.spigotmc.org/resources/spawnermeta.74188/");
			});
			ECONOMY.load();
			if(ECONOMY.exists() == true) Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_PURPLE + "[" + ChatColor.LIGHT_PURPLE + "SpawnerMeta"
						+ ChatColor.DARK_PURPLE + "] " + ChatColor.GRAY + "Vault has been found, economy enabled!");
			WILD_STACKER.load();
			if(WILD_STACKER.exists() == true) Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_PURPLE + "[" + ChatColor.LIGHT_PURPLE + "SpawnerMeta"
					+ ChatColor.DARK_PURPLE + "] " + ChatColor.GRAY + "Wild Stacker has been found, entity stacking enabled!");
			WILD_TOOLS.load();
			if(WILD_TOOLS.exists() == true) Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_PURPLE + "[" + ChatColor.LIGHT_PURPLE + "SpawnerMeta"
					+ ChatColor.DARK_PURPLE + "] " + ChatColor.GRAY + "Wild Tools has been found, custom drop provided!");
			SHOP_GUI.load();
			if(SHOP_GUI.exists() == true) Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_PURPLE + "[" + ChatColor.LIGHT_PURPLE + "SpawnerMeta"
					+ ChatColor.DARK_PURPLE + "] " + ChatColor.GRAY + "ShopGUI+ has been found, custom spawners provided!");
			initializeMetrics();
			Configuration.initialize();
			CommandManager.initialize();
			DataManager.initialize();
			SpawnerViewLayout.initialize();
			ShopRegistry.initialize();
			EventListeners.initialize();
			GeneratorRegistry.initialize();
			SpawningManager.initialize();
			LocationRegistry.initialize();
			
			this.api = new APIRegistry();
		} else {
			Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_PURPLE + "[" + ChatColor.LIGHT_PURPLE + "Spawner Meta " + ChatColor.AQUA + "v"
					+ PLUGIN_VERSION + ChatColor.DARK_PURPLE + "]" + ChatColor.DARK_RED + " failed to load, invalid server version!");
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
			Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_PURPLE + "[" + ChatColor.LIGHT_PURPLE + "Spawner Meta " + ChatColor.AQUA + "v"
					+ PLUGIN_VERSION + ChatColor.DARK_PURPLE + "]" + ChatColor.RED + " disabled!");
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
