package mc.rellox.spawnermeta.events;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.SpawnerSpawnEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import mc.rellox.spawnermeta.SpawnerMeta;
import mc.rellox.spawnermeta.api.spawner.Spawner;
import mc.rellox.spawnermeta.configuration.Settings;
import mc.rellox.spawnermeta.holograms.HologramRegistry;
import mc.rellox.spawnermeta.utils.DataManager;
import mc.rellox.spawnermeta.utils.Messagable;
import mc.rellox.spawnermeta.utils.Reflections.RF;

public class EventListeners implements Listener {
	
	private static final List<RegistryAbstract> REGISTRIES =
			List.of(new RegistryAI(), new RegistryWorldLoad(), new RegistryLinking(),
					new RegistrySpawnerRename());
	
	public static void initialize() {
		Bukkit.getPluginManager().registerEvents(new EventListeners(), SpawnerMeta.instance());
		update();
	}
	
	public static void update() {
		REGISTRIES.forEach(RegistryAbstract::update);
	}
	
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	private void onInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		Messagable m = new Messagable(player);
		if(event.getAction() == Action.LEFT_CLICK_BLOCK) {
			EventRegistry.verify_removing(event, player, m);
			return;
		}
		if(event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
		Block block = event.getClickedBlock();
		if(event.getHand() == EquipmentSlot.OFF_HAND) {
			ItemStack item = event.getItem();
			if(item != null && item.getType().name().endsWith("_EGG") == true
					&& block.getType() == Material.SPAWNER) event.setCancelled(true);
			return;
		}
		if(block.getType() != Material.SPAWNER) {
			EventRegistry.stack_nearby(event, player, m, block);
			return;
		}
		Spawner spawner = Spawner.of(block);
		try {
			EventRegistry.interact(event, player, m, block, spawner);
		} catch (Exception e) {
			RF.debug(e);
		}
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	private void onBreak(BlockBreakEvent event) {
		try {
			Block block = event.getBlock();
			if(block.getType() != Material.SPAWNER) return;
			EventRegistry.breaking(event, block);
		} catch (Exception e) {
			RF.debug(e);
		}
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	private void onBlockExplodeByBlock(BlockExplodeEvent event) {
		Iterator<Block> it = event.blockList().iterator();
		try {
			EventRegistry.explode_block(it);
		} catch (Exception e) {
			RF.debug(e);
		}
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	private void onBlockExplodeByEntity(EntityExplodeEvent event) {
		try {
			EventRegistry.explode_entity(event);
		} catch (Exception e) {
			RF.debug(e);
		}
	}
	
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	private void onPlace(BlockPlaceEvent event) {
		if(event.isCancelled() == true) return;
		Block block = event.getBlockPlaced();
		if(block.getType() != Material.SPAWNER) return;
		try {
			EventRegistry.place(event, block);
		} catch (Exception e) {
			RF.debug(e);
		}
	}
	
	@EventHandler(priority = EventPriority.LOW)
	private void onSpawn(SpawnerSpawnEvent event) {
		Entity entity = event.getEntity();
		if(entity.getCustomName() != null) return;
		if(Settings.settings.cancel_spawning_event == true) event.setCancelled(true);
		else entity.remove();
		try {
			EventRegistry.spawn(event, entity);
		} catch(Exception e) {
			RF.debug(e);
		}
	}
	
	private static abstract class RegistryAbstract implements Listener {
		
		private boolean registered;
		
		protected void register() {
			if(registered == true) return;
			Bukkit.getPluginManager().registerEvents(this, SpawnerMeta.instance());
		}
		
		protected void unregister() {
			if(registered == false) return;
			HandlerList.unregisterAll(this);
		}
		
		public abstract void update();
		
	}
	
	private static final class RegistryAI extends RegistryAbstract {
		
		@Override
		public void update() {
			if(Settings.settings.entity_target == true) unregister();
			else register();
		}

		@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
		private void onEntityTarget(EntityTargetEvent event) {
			Entity entity = event.getEntity();
			if(DataManager.isSpawned(entity) == false) return;
			event.setCancelled(true);
		}
		
	}
	
	private static final class RegistryWorldLoad extends RegistryAbstract {
		
		@Override
		public void update() {
			if(HologramRegistry.loaded() == false) unregister();
			else register();
		}

		@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
		private void onWorldLoad(WorldLoadEvent event) {
			HologramRegistry.load(event.getWorld());
		}
		
	}
	
	private static final class RegistryLinking extends RegistryAbstract {

		@Override
		public void update() {
			if(SpawnerMeta.WILD_STACKER.exists() == false) unregister();
			else register();
		}

		@EventHandler(priority = EventPriority.HIGH)
		private void onUnloadLink(ChunkUnloadEvent event) {
			Stream.of(event.getChunk().getTileEntities())
				.filter(CreatureSpawner.class::isInstance)
				.map(BlockState::getBlock)
				.forEach(SpawnerMeta.WILD_STACKER::unlink);
		}
		
	}
	
	private static final class RegistrySpawnerRename extends RegistryAbstract {

		@Override
		public void update() {
			if(Settings.settings.allow_renaming == true) unregister();
			else register();
		}

		@EventHandler(priority = EventPriority.HIGH)
		private void onAnvilPrep(PrepareAnvilEvent event) {
			var res = event.getResult();
			if(res == null || res.getType() != Material.SPAWNER) return;
			event.setResult(null);
		}
		
	}
	
}
