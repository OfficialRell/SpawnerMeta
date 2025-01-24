package mc.rellox.spawnermeta.hook.setup;

import java.util.List;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.bgsoftware.wildtools.api.hooks.DropsProvider;

import mc.rellox.spawnermeta.SpawnerMeta;
import mc.rellox.spawnermeta.api.spawner.ISpawner;
import mc.rellox.spawnermeta.hook.HookRegistry;
import mc.rellox.spawnermeta.utility.reflect.Reflect.RF;

public class SetupWildTools {
	
	public static void load() {
		SpawnerMeta.scheduler().runLater(() -> {
			var providers = HookRegistry.WILD_TOOLS.get().getProviders();
			try {
				RF.fetch(providers, "dropsProviders", List.class).clear();
			} catch (Exception x) {
				RF.debug(x);
			}
			providers.addDropsProvider(new SpawnerMetaDropsProvider());
		}, 1);
	}
	
	private static class SpawnerMetaDropsProvider implements DropsProvider {
		
		@Override
		public boolean isSpawnersOnly() {
			return true;
		}
		
		@Override
		public List<ItemStack> getBlockDrops(Player player, Block block) {
			return ISpawner.of(block).toItems();
		}
	}
}
