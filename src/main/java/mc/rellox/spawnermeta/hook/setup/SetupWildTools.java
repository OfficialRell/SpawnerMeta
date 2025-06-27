package mc.rellox.spawnermeta.hook.setup;

import java.util.List;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import com.bgsoftware.wildtools.api.WildTools;
import com.bgsoftware.wildtools.api.hooks.DropsProvider;

import mc.rellox.spawnermeta.SpawnerMeta;
import mc.rellox.spawnermeta.api.spawner.ISpawner;
import mc.rellox.spawnermeta.configuration.location.LocationRegistry;
import mc.rellox.spawnermeta.spawner.generator.GeneratorRegistry;
import mc.rellox.spawnermeta.utility.reflect.Reflect.RF;

public class SetupWildTools {
	
	public static void load(WildTools plugin) {
		new BukkitRunnable() {
			@Override
			public void run() {
				var providers = plugin.getProviders();
				try {
					RF.fetch(providers, "dropsProviders", List.class).clear();
				} catch (Exception x) {
					RF.debug(x);
				}
				providers.addDropsProvider(new SpawnerMetaDropsProvider());
			}
		}.runTaskLater(SpawnerMeta.instance(), 1);
	}
	
	private static class SpawnerMetaDropsProvider implements DropsProvider {
		
		@Override
		public boolean isSpawnersOnly() {
			return true;
		}
		
		@Override
		public List<ItemStack> getBlockDrops(Player player, Block block) {
			// Obtain spawner items
			List<ItemStack> items = ISpawner.of(block).toItems();

			// Correctly removing the spawner and its location from the registry
			LocationRegistry.remove(block);
			GeneratorRegistry.delete(block);
			
			return items;
		}
	}
}
