package mc.rellox.spawnermeta.hook;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import mc.rellox.spawnermeta.hook.setup.SetupShopGUI;
import net.brcdev.shopgui.ShopGuiPlugin;

public class HookShopGUI implements HookInstance {
	
	private ShopGuiPlugin plugin;

	@Override
	public boolean exists() {
		return plugin != null;
	}
	
	@Override
	public String message() {
		return "ShopGUI+ has been found, custom spawners provided!";
	}

	@Override
	public void load() {
		Plugin plugin = Bukkit.getPluginManager().getPlugin("ShopGUIPlus");
		if(plugin == null) return;
		this.plugin = (ShopGuiPlugin) plugin;
		SetupShopGUI.load();
	}

}
