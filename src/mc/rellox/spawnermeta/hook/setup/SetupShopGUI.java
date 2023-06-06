package mc.rellox.spawnermeta.hook.setup;

import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import mc.rellox.spawnermeta.SpawnerMeta;
import mc.rellox.spawnermeta.api.spawner.VirtualSpawner;
import mc.rellox.spawnermeta.spawner.SpawnerType;
import mc.rellox.spawnermeta.utils.DataManager;
import mc.rellox.spawnermeta.utils.Reflections.RF;
import net.brcdev.shopgui.ShopGuiPlusApi;
import net.brcdev.shopgui.event.ShopGUIPlusPostEnableEvent;
import net.brcdev.shopgui.spawner.external.provider.ExternalSpawnerProvider;

public class SetupShopGUI {
	
	public static void load() {
		Bukkit.getPluginManager().registerEvents(new ShopGUIPlusHook(), SpawnerMeta.instance());
	}
	
	public static class ShopGUIPlusHook implements Listener {

		@EventHandler
		public void onenable(ShopGUIPlusPostEnableEvent event) {
			try {
				ShopGuiPlusApi.registerSpawnerProvider(new SpawnerMetaSpawnerProvider());
			} catch(Exception e) {
				RF.debug(e);
			}
		}
	}
	
	public static class SpawnerMetaSpawnerProvider implements ExternalSpawnerProvider {

		@Override
		public String getName() {
			return "SpawnerMeta";
		}

		@Override
		public ItemStack getSpawnerItem(EntityType entity) {
			SpawnerType type = SpawnerType.of(entity);
			if(type == null) return null;
			return DataManager.getSpawner(type, 1);
		}

		@Override
		public EntityType getSpawnerEntityType(ItemStack item) {
			VirtualSpawner virtual = VirtualSpawner.of(item);
			return virtual == null ? null : virtual.getType().entity();
		}
		
	}

}
