package mc.rellox.spawnermeta.hook;

import org.bukkit.Bukkit;

import mc.rellox.spawnermeta.hook.setup.SetupShopGUI;
import net.brcdev.shopgui.ShopGuiPlugin;

public class HookShopGUI implements HookInstance<ShopGuiPlugin> {
	
	private ShopGuiPlugin plugin;

	@Override
	public ShopGuiPlugin get() {
		return plugin;
	}

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
		plugin = (ShopGuiPlugin) Bukkit.getPluginManager().getPlugin("ShopGUIPlus");
		if(plugin == null) return;
		SetupShopGUI.load();
	}

}
