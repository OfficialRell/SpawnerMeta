package mc.rellox.spawnermeta.hook.setup;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import mc.rellox.spawnermeta.SpawnerMeta;
import mc.rellox.spawnermeta.api.spawner.IVirtual;
import mc.rellox.spawnermeta.configuration.Settings;
import mc.rellox.spawnermeta.spawner.type.SpawnerType;
import mc.rellox.spawnermeta.utility.DataManager;
import mc.rellox.spawnermeta.utility.reflect.Reflect.RF;
import net.brcdev.shopgui.ShopGuiPlusApi;
import net.brcdev.shopgui.event.ShopGUIPlusPostEnableEvent;
import net.brcdev.shopgui.exception.api.ExternalSpawnerProviderNameConflictException;
import net.brcdev.shopgui.provider.item.ItemProvider;
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
				ShopGuiPlusApi.registerItemProvider(new SpawnerMetaItemProvider());
			} catch (ExternalSpawnerProviderNameConflictException e) {
				// ignore
			} catch(Exception e) {
				RF.debug(e);
			}
		}
	}
	
	private static class SpawnerMetaSpawnerProvider implements ExternalSpawnerProvider {

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
	
	private static class SpawnerMetaItemProvider extends ItemProvider {

		SpawnerMetaItemProvider() {
			super("SpawnerMeta");
		}

		@Override
		public boolean isValidItem(ItemStack item) {
			if(item == null) return false;
			return DataManager.getSpawnerItem(item, true) != null;
		}

		@Override
		public ItemStack loadItem(ConfigurationSection section) {
			String type_string = section.getString("type");
            if (type_string == null) return null;

			SpawnerType type = SpawnerType.of(type_string);
			if(type == null) type = SpawnerType.PIG;

			int[] levels = null;
			if(section.isList("levels")) {
				var list = section.getIntegerList("levels");
				levels = list.stream()
						.mapToInt(Integer::intValue)
						.limit(3)
						.map(i -> i < 1 ? 1 : i)
						.toArray();
			} else if(section.isInt("level")) {
				int single = section.getInt("level", 1);
				if(single < 1) single = 1;
				levels = new int[] {single, single, single};
			}
			if(levels == null) levels = DataManager.i();

			int charges = section.getInt("charges");

			int spawnable = section.getInt("spawnable");
			if(spawnable < 1)
				spawnable = Settings.settings.spawnable_amount.get(type);

			int amount = section.getInt("amount", 1);
			if(amount < 1) amount = 1;
			boolean empty = section.getBoolean("empty");

			List<ItemStack> list = DataManager.getSpawner(type, levels, charges, spawnable, amount, empty);
			return list.get(0);
		}

		@Override
		public boolean compare(ItemStack first, ItemStack second) {
			if(first == null || second == null) return false;

			IVirtual v1 = DataManager.getSpawnerItem(first, true);
			IVirtual v2 = DataManager.getSpawnerItem(second, true);

			return v1 == null || v2 == null ? false : v1.exact(v2);
		}

	}

}
