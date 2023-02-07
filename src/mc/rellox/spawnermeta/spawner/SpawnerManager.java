package mc.rellox.spawnermeta.spawner;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import mc.rellox.spawnermeta.api.spawner.VirtualSpawner;
import mc.rellox.spawnermeta.configuration.Language;
import mc.rellox.spawnermeta.configuration.LocationFile.LF;
import mc.rellox.spawnermeta.configuration.Settings;
import mc.rellox.spawnermeta.holograms.HologramRegistry;
import mc.rellox.spawnermeta.items.ItemCollector;
import mc.rellox.spawnermeta.utils.DataManager;
import mc.rellox.spawnermeta.utils.Utils;

public final class SpawnerManager {

	protected static final Map<Material, SpawnerType> EGGS = new HashMap<>();
	
	public static SpawnerType fromEgg(Material item) {
		return EGGS.get(item);
	}

	public static boolean placeSpawner(Block block, Player player, VirtualSpawner spawner) {
		if(block == null || spawner == null) return false;
		if(block.getType() != Material.SPAWNER) block.setType(Material.SPAWNER);
		int[] l = spawner.getUpgradeLevels();
		SpawnerType type = spawner.isEmpty() == true && Settings.settings.empty_store_inside == false
				? SpawnerType.EMPTY : spawner.getType();
		DataManager.setNewSpawner(player, block, type,
				l, spawner.getCharges(), spawner.getSpawnable(), spawner.isEmpty());
		DataManager.setPlaced(block);
		if(player != null) {
			LF.add(block, player);
			if(Settings.settings.owned_ignore_limit == false)
				player.sendMessage(Language.get("Spawners.ownership.limit.place",
						"placed", LF.placed(player), "limit", Settings.settings.owned_spawner_limit).text());
		}
		if(type == SpawnerType.EMPTY) DataManager.setRotating(block, false);
		HologramRegistry.add(block);
		return true;
	}
	
	public static boolean breakSpawner(Block block, boolean drop) {
		return breakSpawner(block, drop, true);
	}
	
	public static boolean breakSpawner(Block block, boolean drop, boolean particles) {
		if(block == null || block.getType() != Material.SPAWNER) return false;
		Location loc = block.getLocation().add(0.5, 0.5, 0.5);
		if(drop == true) {
			DataManager.getSpawners(block, false).forEach(item -> {
				loc.getWorld().dropItem(loc, item).setVelocity(new Vector());
			});
			dropEggs(null, block);
		}
		LF.remove(block);
		block.setType(Material.AIR);
		if(particles == true) loc.getWorld().spawnParticle(Particle.CLOUD, loc, 25, 0.25, 0.25, 0.25, 0);
		HologramRegistry.remove(block);
		return true;
	}
	
	public static void dropEggs(Player player, Block block) {
		if(Settings.settings.empty_destroy_eggs_breaking == true
				|| Settings.settings.empty_store_inside == true) {
			block.getWorld().spawnParticle(Particle.CRIT, Utils.center(block), 10, 0, 0, 0, 0.1);
			return;
		}
		SpawnerType type = DataManager.getType(block);
		if(DataManager.isEmpty(block) == true && type != SpawnerType.EMPTY) {
			if(type.unique() == false) {
				Material mat = type.changer();
				if(mat != null) {
					if(Settings.settings.breaking_drop_on_ground == true) {
						ItemStack item = new ItemStack(mat, DataManager.getStack(block));
						block.getWorld().dropItem(block.getLocation().add(0.5, 0.5, 0.5), item)
							.setVelocity(new Vector());
					} else if(player != null) {
						int s = DataManager.getStack(block);
						while(s > 0) {
							ItemStack item = new ItemStack(mat, s >= 64 ? 64 : s);
							ItemCollector.add(player, item);
							s -= 64;
						}
					}
				}
			}
		}
	}
	
	public static int getChunkSpawnerAmount(Block block) {
		BlockState[] bs = block.getChunk().getTileEntities();
		if(bs == null) return 0;
		return (int) Stream.of(bs)
				.filter(s -> s instanceof CreatureSpawner)
				.count();
	}

}
