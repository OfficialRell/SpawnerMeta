package mc.rellox.spawnermeta.hook;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

import com.bgsoftware.wildstacker.WildStackerPlugin;
import com.bgsoftware.wildstacker.api.objects.StackedEntity;

import mc.rellox.spawnermeta.configuration.Settings;
import mc.rellox.spawnermeta.events.EventRegistry;
import mc.rellox.spawnermeta.spawner.SpawnerSpawning;

public class HookWildStacker implements HookInstance<WildStackerPlugin> {
	
	private WildStackerPlugin wild;
	
	private final Map<Block, StackedEntity> linked = new HashMap<>();

	@Override
	public WildStackerPlugin get() {
		return wild;
	}

	@Override
	public boolean exists() {
		return wild != null;
	}
	
	public void unlink(Block block) {
		linked.remove(block);
	}

	@Override
	public void load() {
		wild = (WildStackerPlugin) Bukkit.getPluginManager().getPlugin("WildStacker");
	}
	
	public List<Entity> combine(Block block, EntityType type, SpawnerSpawning spread, int count, CreatureSpawner cs) {
		List<Entity> affected = new ArrayList<>(1);
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
				if(nearby.getLivingEntity().getType() == type)
					linked.put(block, link = nearby);
			}
		}
		x: if(link != null) {
			LivingEntity le = link.getLivingEntity();
			if(le.isDead() == true) {
				linked.values().removeIf(se -> se.getLivingEntity().isDead());
				break x;
			}
			int a = wild.getSettings().linkedEntitiesMaxDistance;
			if(le.getWorld().equals(at.getWorld()) == false
					|| le.getLocation().distanceSquared(at) > a * a) break x;
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
			return affected;
		}
		
		List<LivingEntity> near = block.getWorld()
				.getLivingEntities().stream()
				.filter(e -> e.getType() == type)
				.filter(e -> e.getLocation().distanceSquared(at) <= 100)
				.collect(Collectors.toList());
		if(link != null) near.remove(link.getLivingEntity());

//		System.out.println(" - nearby entities: " + near.size());
		
		if(near.isEmpty() == true) {
//			System.out.println("  - near empty, creating");
			StackedEntity stacked = create(type, spread, count, affected, cs);
			linked.put(block, stacked);
		} else if(near.size() == 1) {
//			System.out.println("  - only 1 near");
			LivingEntity entity = near.get(0);
			StackedEntity stacked = wild.getSystemManager().getStackedEntity(entity);
			affected.add(entity);
			EventRegistry.particle(entity.getLocation());
			int a = stacked.getStackAmount() + count;
			if(a <= stacked.getStackLimit()) {
//				System.out.println("   - stacking to it");
				stacked.setStackAmount(a, true);
				linked.put(block, stacked);
			} else {
				int f = a - stacked.getStackLimit();
//				System.out.println("   - creating new: " + f);
				stacked.setStackAmount(stacked.getStackLimit(), true);
				stacked = create(type, spread, f, affected, cs);
				linked.put(block, stacked);
			}
			if(Settings.settings.modify_stacked_entities == true)
				EventRegistry.modify(entity);
		} else {
//			System.out.println("  - many near");
			int i = 0, l = count;
			while(true) {
//				System.out.println("  - loop" + l);
				x: if(i >= near.size()) {
//					System.out.println("   - create new: " + l);
					StackedEntity stacked = create(type, spread, l, affected, cs);
					linked.put(block, stacked);
					break;
				} else {
					LivingEntity entity = near.get(i);
					StackedEntity stacked = wild.getSystemManager().getStackedEntity(entity);
					int c = stacked.getStackAmount(), m = stacked.getStackLimit();
					if(c == m) break x;
					int a = c + l, f = a - m;
					affected.add(entity);
					EventRegistry.particle(entity.getLocation());
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
					if(Settings.settings.modify_stacked_entities == true)
						EventRegistry.modify(entity);
				}
				i++;
			}
		}
		return affected;
	}

	private StackedEntity create(EntityType type, SpawnerSpawning spread, int count, List<Entity> affected, CreatureSpawner cs) {
		LivingEntity entity = (LivingEntity) EventRegistry.spawn(spread.get(), type, EventRegistry.function(cs));
		StackedEntity stacked = wild.getSystemManager().getStackedEntity(entity);
		stacked.setStackAmount(count, true);
		affected.add(entity);
		EventRegistry.particle(entity.getLocation());
		return stacked;
	}

}
