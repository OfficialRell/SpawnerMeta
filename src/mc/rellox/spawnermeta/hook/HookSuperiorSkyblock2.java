package mc.rellox.spawnermeta.hook;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblock;
import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;
import com.bgsoftware.superiorskyblock.api.key.Key;

import mc.rellox.spawnermeta.hook.setup.SetupSuperiorSkyblock2;

public class HookSuperiorSkyblock2 implements HookInstance {
	
	private SuperiorSkyblock plugin;

	@Override
	public boolean exists() {
		return plugin != null;
	}
	
	@Override
	public String message() {
		return "SuperiorSkyblock2 found, event support provided!";
	}

	@Override
	public void load() {
		if(Bukkit.getPluginManager().getPlugin("SuperiorSkyblock2") == null) return;
		plugin = SuperiorSkyblockAPI.getSuperiorSkyblock();
		
		SetupSuperiorSkyblock2.load();
	}
	
	public void placing(Block block) {
		if(exists() == false) return;
		var is = plugin.getGrid().getIslandAt(block.getLocation());
		if(is != null) is.handleBlockPlace(Key.of(Material.SPAWNER));
	}
	
	public void breaking(Block block) {
		if(exists() == false) return;
		var is = plugin.getGrid().getIslandAt(block.getLocation());
		if(is != null) is.handleBlockBreak(Key.of(Material.SPAWNER));
	}
	
	public int delay_upgrade(Block block, int delay) {
		if(exists() == false) return delay;
		var is = plugin.getGrid().getIslandAt(block.getLocation());
		if(is == null) return delay;
		return (int) Math.round(delay / is.getSpawnerRatesMultiplier());
	}

}
