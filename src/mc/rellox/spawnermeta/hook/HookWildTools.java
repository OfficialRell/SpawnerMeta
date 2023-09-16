package mc.rellox.spawnermeta.hook;

import org.bukkit.Bukkit;

import com.bgsoftware.wildtools.api.WildTools;
import com.bgsoftware.wildtools.api.WildToolsAPI;

import mc.rellox.spawnermeta.hook.setup.SetupWildTools;

public class HookWildTools implements HookInstance<WildTools> {
	
	private WildTools plugin;

	@Override
	public WildTools get() {
		return plugin;
	}

	@Override
	public boolean exists() {
		return plugin != null;
	}

	@Override
	public void load() {
		if(Bukkit.getPluginManager().getPlugin("WildTools") == null) return;
		plugin = WildToolsAPI.getWildTools();
		if(plugin == null) return;
		SetupWildTools.load();
	}

}
