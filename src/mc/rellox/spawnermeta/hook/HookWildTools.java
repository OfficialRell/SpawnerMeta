package mc.rellox.spawnermeta.hook;

import org.bukkit.Bukkit;

import com.bgsoftware.wildtools.WildToolsPlugin;

import mc.rellox.spawnermeta.hook.setup.SetupWildTools;

public class HookWildTools implements HookInstance<WildToolsPlugin> {
	
	private WildToolsPlugin plugin;

	@Override
	public WildToolsPlugin get() {
		return plugin;
	}

	@Override
	public boolean exists() {
		return plugin != null;
	}

	@Override
	public void load() {
		plugin = (WildToolsPlugin) Bukkit.getPluginManager().getPlugin("WildTools");
		if(plugin == null) return;
		SetupWildTools.load();
	}

}
