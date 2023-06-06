package mc.rellox.spawnermeta.hook.setup;

import java.util.List;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import com.bgsoftware.wildtools.api.hooks.DropsProvider;
import com.bgsoftware.wildtools.handlers.ProvidersHandler;

import mc.rellox.spawnermeta.SpawnerMeta;
import mc.rellox.spawnermeta.api.spawner.Spawner;
import mc.rellox.spawnermeta.utils.Reflections.RF;

public class SetupWildTools {
	
	public static void load() {
		new BukkitRunnable() {
			@Override
			public void run() {
				ProvidersHandler providers = SpawnerMeta.WILD_TOOLS.get().getProviders();
				try {
					RF.access(providers, "dropsProviders", List.class).field().clear();
				} catch (Exception x) {
					RF.debug(x);
				}
				providers.addDropsProvider(new SpawnerMetaDropsProvider());
			}
		};
	}
	
	private static class SpawnerMetaDropsProvider implements DropsProvider {
		
		@Override
		public boolean isSpawnersOnly() {
			return true;
		}
		
		@Override
		public List<ItemStack> getBlockDrops(Player player, Block block) {
			return Spawner.of(block).toItems();
		}
	}
}
