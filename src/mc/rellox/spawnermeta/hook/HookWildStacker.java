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

public class HookWildStacker implements HookInstance<WildStacker> {
	
	private WildStacker plugin;
	
	private final Map<Block, StackedEntity> linked = new HashMap<>();

	@Override
	public WildStacker get() {
		return plugin;
	}

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
	
	public List<Entity> combine(ISpawner spawner, SpawnerType type, ISelector selector, int count) {
		List<Entity> affected = new ArrayList<>(1);
		Block block = spawner.block();
		EntityType et = type.entity();
		Location at = block.getLocation().add(0.5, 0.5, 0.5);
		StackedEntity link = linked.get(block);
//		System.out.println();
		if(link == null) {
//			System.out.println("none linked");
			Block other = linked.keySet().stream()
					.filter(b -> b.getWorld().equals(block.getWorld()))
					.filter(b -> b.getLocation().distanceSquared(at) <= 16)
					.findFirst()
					.orElse(null);
			if(other != null) {
//				System.out.println("- found other");
				StackedEntity nearby = linked.get(other);
				if(nearby.getLivingEntity().getType() == et)
					linked.put(block, link = nearby);
			}
		}
		x: if(link != null) {
			LivingEntity le = link.getLivingEntity();
			if(le.isDead() == true) {
//				System.out.println("- linked dead");
				linked.values().removeIf(se -> se.getLivingEntity().isDead());
				break x;
			}
			int a = 32 * 32;
			if(le.getWorld().equals(at.getWorld()) == false
					|| le.getLocation().distanceSquared(at) > a) break x;
			int s = link.getStackAmount(), m = link.getStackLimit();
			if(s >= m) break x;
			int t = s + count;
//			System.out.println("- set linked to " + t);
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
				.map(LivingEntity.class::cast)
				.collect(Collectors.toList());
		if(link != null) near.remove(link.getLivingEntity());

//		System.out.println(" - nearby entities: " + near.size());
		
		if(near.isEmpty() == true) {
//			System.out.println("  - near empty, creating");
			StackedEntity stacked = create(spawner, type, selector, count, affected);
			linked.put(block, stacked);
		} else if(near.size() == 1) {
//			System.out.println("  - only 1 near");
			LivingEntity entity = near.get(0);
			StackedEntity stacked = plugin.getSystemManager().getStackedEntity(entity);
			affected.add(entity);
			SpawningManager.particle(entity.getLocation());
			int a = stacked.getStackAmount() + count;
			if(a <= stacked.getStackLimit()) {
//				System.out.println("   - stacking to it");
				stacked.setStackAmount(a, true);
				linked.put(block, stacked);
			} else {
				int f = a - stacked.getStackLimit();
//				System.out.println("   - creating new: " + f);
				stacked.setStackAmount(stacked.getStackLimit(), true);
				stacked = create(spawner, type, selector, f, affected);
				linked.put(block, stacked);
			}
			modify(entity);
		} else {
//			System.out.println("  - many near");
			int i = 0, l = count;
			while(true) {
//				System.out.println("  - loop" + l);
				x: if(i >= near.size()) {
//					System.out.println("   - create new: " + l);
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
//						System.out.println("   - stacking all");
						stacked.setStackAmount(a, true);
						linked.put(block, stacked);
						break;
					} else {
						stacked.setStackAmount(m, true);
						linked.remove(block);
						l = f;
//						System.out.println("   - stacking to limit, left: " + l);
					}
					modify(entity);
				}
				i++;
			}
		}
		return affected;
	}
	
	private void modify(Entity entity) {
		if(Settings.settings.modify_stacked_entities == true)
			SpawningManager.modify(entity);
	}

	private StackedEntity create(ISpawner spawner, SpawnerType type, ISelector selector, int count,
			List<Entity> affected) {
		LivingEntity entity = (LivingEntity) SpawningManager.spawn(spawner, type, selector);
		StackedEntity stacked = plugin.getSystemManager().getStackedEntity(entity);
		stacked.setStackAmount(count, true);
		affected.add(entity);
		return stacked;
	}

}
