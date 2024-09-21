package mc.rellox.spawnermeta.hook.setup;

import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import mc.rellox.spawnermeta.SpawnerMeta;
import mc.rellox.spawnermeta.api.spawner.IVirtual;
import mc.rellox.spawnermeta.spawner.type.SpawnerType;
import mc.rellox.spawnermeta.utility.DataManager;
import mc.rellox.spawnermeta.utility.reflect.Reflect.RF;
import net.brcdev.shopgui.ShopGuiPlusApi;
import net.brcdev.shopgui.event.ShopGUIPlusPostEnableEvent;
import net.brcdev.shopgui.exception.api.ExternalSpawnerProviderNameConflictException;
import net.brcdev.shopgui.spawner.external.provider.ExternalSpawnerProvider;

public class SetupShopGUI {
	
	public static void load() {
		Bukkit.getPluginManager().registerEvents(new ShopGUIPlusHook(), SpawnerMeta.instance());
	}
	
	public static class ShopGUIPlusHook implements Listener {

		@EventHandler
		public void onEnable(ShopGUIPlusPostEnableEvent event) {
			try {
				ShopGuiPlusApi.registerSpawnerProvider(new SpawnerMetaSpawnerProvider());
			} catch (ExternalSpawnerProviderNameConflictException e) {
				// ignore
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
			IVirtual virtual = IVirtual.of(item);
			return virtual == null ? null : virtual.getType().entity();
		}
		
	}

}
