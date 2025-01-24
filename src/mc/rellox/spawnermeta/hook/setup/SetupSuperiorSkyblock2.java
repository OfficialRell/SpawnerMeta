package mc.rellox.spawnermeta.hook.setup;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.bgsoftware.superiorskyblock.api.events.IslandKickEvent;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;

import mc.rellox.spawnermeta.SpawnerMeta;
import mc.rellox.spawnermeta.api.spawner.ISpawner;
import mc.rellox.spawnermeta.configuration.Settings;
import mc.rellox.spawnermeta.configuration.location.LocationRegistry;
import mc.rellox.spawnermeta.items.ItemMatcher;
import mc.rellox.spawnermeta.utility.reflect.Reflect.RF;

public class SetupSuperiorSkyblock2 implements Listener {

	public static void load() {
		SpawnerMeta.scheduler().runLater(() -> {
			if(Settings.settings.check_island_kick == false) return;
			Bukkit.getPluginManager().registerEvents(new SetupSuperiorSkyblock2(), SpawnerMeta.instance());
		}, 1);
	}

	@EventHandler
	private void onPlayerKick(IslandKickEvent event) {
		try {
			SuperiorPlayer kicked = event.getTarget();
			Player player = kicked.asPlayer();
			if(player != null && player.isOnline() == true) {
				var il = LocationRegistry.get(player);
				il.all().stream()
				.map(Location::getBlock)
				.map(ISpawner::of)
				.forEach(spawner -> {
					spawner.toItems()
					.forEach(item -> ItemMatcher.add(player, item));
					spawner.block().setType(Material.AIR);
				});
			} else {
				var il = LocationRegistry.get(kicked.getUniqueId());
				il.all().stream()
				.map(Location::getBlock)
				.map(ISpawner::of)
				.forEach(spawner -> {
					il.store(spawner.toData());
					spawner.block().setType(Material.AIR);
				});
			}
		} catch (Exception e) {
			RF.debug(e);
		}
	}

}
