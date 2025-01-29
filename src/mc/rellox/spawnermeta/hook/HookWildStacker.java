package mc.rellox.spawnermeta.hook;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

import com.bgsoftware.wildstacker.api.WildStacker;
import com.bgsoftware.wildstacker.api.WildStackerAPI;
import com.bgsoftware.wildstacker.api.objects.StackedEntity;

import mc.rellox.spawnermeta.api.spawner.ISpawner;
import mc.rellox.spawnermeta.api.spawner.location.ISelector;
import mc.rellox.spawnermeta.configuration.Settings;
import mc.rellox.spawnermeta.spawner.generator.SpawningManager;
import mc.rellox.spawnermeta.spawner.type.SpawnerType;
import mc.rellox.spawnermeta.utility.Utility;
import mc.rellox.spawnermeta.utility.reflect.Reflect.RF;

public class HookWildStacker implements HookInstance {
	
	private WildStacker plugin;
	
	private final Map<Block, StackedEntity> linked = new HashMap<>();

	@Override
	public boolean exists() {
		return plugin != null;
	}
	
	public void unlink(Block block) {
		linked.remove(block);
	}

	@Override
	public void load() {
		if(Bukkit.getPluginManager().getPlugin("WildStacker") == null) return;
		plugin = WildStackerAPI.getWildStacker();
	}
	
	@Override
	public String message() {
		return "Wild Stacker has been found, entity stacking enabled!";
	}
	
	public boolean stacking(SpawnerType type, Location at) {
		try {
			Entity entity = Utility.create(at, type.entity());
			Class<?> utils = RF.get("com.bgsoftware.wildstacker.utils.entity.EntityUtils");
			return RF.order(utils, "isStackable", Entity.class)
					.as(boolean.class)
					.invoke(entity);
		} catch (Exception e) {}
		return true;
	}
	
	public List<Entity> combine(ISpawner spawner, SpawnerType type, ISelector selector, int count) {
		List<Entity> affected = new ArrayList<>(1);
		Block block = spawner.block();
		EntityType et = type.entity();
		Location at = block.getLocation().add(0.5, 0.5, 0.5);
		StackedEntity link = linked.get(block);
		if(link == null) {
			Block other = linked.keySet().stream()
					.filter(b -> b.getWorld().equals(block.getWorld()))
					.filter(b -> b.getLocation().distanceSquared(at) <= 16)
					.findFirst()
					.orElse(null);
			if(other != null) {
				StackedEntity nearby = linked.get(other);
				if(nearby.getLivingEntity().getType() == et)
					linked.put(block, link = nearby);
			}
		}
		x: if(link != null) {
			LivingEntity le = link.getLivingEntity();
			if(le.isDead() == true || inside(block, le) == false) {
				linked.values().removeIf(se -> se.getLivingEntity().isDead());
				break x;
			}
			int a = 32 * 32;
			if(le.getWorld().equals(at.getWorld()) == false
					|| le.getLocation().distanceSquared(at) > a) break x;
			int s = link.getStackAmount(), m = link.getStackLimit();
			if(s >= m) break x;
			int t = s + count;
			if(t < m) link.setStackAmount(t, true);
			else if(t == m) {
				link.setStackAmount(t, true);
				unlink(block);
			} else {
				link.setStackAmount(m, true);
				affected.add(le);
				unlink(block);
				count = t - m;
				break x;
			}
			affected.add(le);
			modify(le);
			return affected;
		}
		
		List<LivingEntity> near = block.getWorld()
				.getNearbyEntities(at, 10, 10, 10, e -> e.getType() == et)
				.stream()
				.filter(e -> inside(block, e))
				.map(LivingEntity.class::cast)
				.collect(Collectors.toList());
		if(link != null) near.remove(link.getLivingEntity());
		
		if(near.isEmpty() == true) {
			StackedEntity stacked = create(spawner, type, selector, count, affected);
			if(stacked == null) return affected;
			linked.put(block, stacked);
		} else if(near.size() == 1) {
			LivingEntity entity = near.get(0);
			StackedEntity stacked = plugin.getSystemManager().getStackedEntity(entity);
			affected.add(entity);
			SpawningManager.particle(entity.getLocation());
			int a = stacked.getStackAmount() + count;
			if(a <= stacked.getStackLimit()) {
				stacked.setStackAmount(a, true);
				linked.put(block, stacked);
			} else {
				int f = a - stacked.getStackLimit();
				stacked.setStackAmount(stacked.getStackLimit(), true);
				stacked = create(spawner, type, selector, f, affected);
				if(stacked == null) return affected;
				linked.put(block, stacked);
			}
			modify(entity);
		} else {
			int i = 0, l = count;
			while(true) {
				x: if(i >= near.size()) {
					StackedEntity stacked = create(spawner, type, selector, l, affected);
					linked.put(block, stacked);
					break;
				} else {
					LivingEntity entity = near.get(i);
					StackedEntity stacked = plugin.getSystemManager().getStackedEntity(entity);
					int c = stacked.getStackAmount(), m = stacked.getStackLimit();
					if(c == m) break x;
					int a = c + l, f = a - m;
					affected.add(entity);
					SpawningManager.particle(entity.getLocation());
					if(f < 0) {
						stacked.setStackAmount(a, true);
						linked.put(block, stacked);
						break;
					} else {
						stacked.setStackAmount(m, true);
						linked.remove(block);
						l = f;
					}
					modify(entity);
				}
				i++;
			}
		}
		return affected;
	}
	
	private boolean inside(Block block, Entity entity) {
		return HookRegistry.PLOT_SQUARED.exists() == true
				? HookRegistry.PLOT_SQUARED.inside(block, entity) : true;
	}
	
	private void modify(Entity entity) {
		if(Settings.settings.modify_stacked_entities == true)
			SpawningManager.modify(entity);
	}

	private StackedEntity create(ISpawner spawner, SpawnerType type, ISelector selector, int count,
			List<Entity> affected) {
		LivingEntity entity = (LivingEntity) SpawningManager.spawn(spawner, type, selector);
		if(entity == null) return null;
		
		StackedEntity stacked = plugin.getSystemManager().getStackedEntity(entity);
		stacked.setStackAmount(count, true);
		affected.add(entity);
		return stacked;
	}

}
