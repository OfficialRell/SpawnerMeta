package mc.rellox.spawnermeta.spawner.generator;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.scheduler.BukkitRunnable;

import mc.rellox.spawnermeta.SpawnerMeta;
import mc.rellox.spawnermeta.api.spawner.IGenerator;
import mc.rellox.spawnermeta.api.spawner.ISpawner;
import mc.rellox.spawnermeta.api.spawner.location.Pos;
import mc.rellox.spawnermeta.configuration.Settings;
import mc.rellox.spawnermeta.spawner.ActiveGenerator;

public class SpawnerWorld {
	
	public final World world;
	private final Map<Pos, IGenerator> spawners;
	private final List<IGenerator> queue;
	private final Set<Chunk> chunks;
	
	public SpawnerWorld(World world) {
		this.world = world;
		this.spawners = new HashMap<>();
		this.queue = new LinkedList<>();;
		this.chunks = new HashSet<>();
	}
	
	public void load() {
		Stream.of(world.getLoadedChunks()).forEach(this::load);
	}
	
	public void load(Chunk chunk) {
		load(chunk, false);
	}
	
	public void load(Chunk chunk, boolean delayed) {
		if(delayed == true) {
			delay();
			chunks.add(chunk);
			return;
		}
		Stream.of(chunk.getTileEntities())
		.filter(CreatureSpawner.class::isInstance)
		.map(BlockState::getBlock)
		.filter(block -> Settings.settings.ignored(block) == false)
		.map(ISpawner::of)
		.map(ActiveGenerator::new)
		.forEach(queue::add);
	}
	
	private void delay() {
		if(chunks.isEmpty() == false) return;
		new BukkitRunnable() {
			@Override
			public void run() {
				try {
					Iterator<Chunk> it = chunks.iterator();
					if(it.hasNext() == true) {
						Chunk next = it.next();
						if(chunks.contains(next) == true) it.remove();
						load(next);
					}
				} catch (Exception e) {}
			}
		}.runTaskTimer(SpawnerMeta.instance(), 1, 5);
	}
	
	public void unload(Chunk chunk) {
		Stream.of(chunk.getTileEntities())
		.filter(CreatureSpawner.class::isInstance)
		.map(BlockState::getBlock)
		.map(Pos::of)
		.map(spawners::get)
		.filter(g -> g != null)
		.forEach(g -> g.remove(false));
		if(Settings.settings.delayed_chunk_loading == true) {
			try {
				if(chunks.isEmpty() == false)
					Bukkit.getScheduler().runTask(SpawnerMeta.instance(),
							() -> chunks.remove(chunk));
			} catch (Exception e) {}
		}
	}
	
	public void clear() {
		spawners.values().forEach(IGenerator::clear);
		spawners.clear();
	}
	
	public int active() {
		return spawners.size();
	}
	
	public void update() {
		spawners.values().forEach(IGenerator::update);
	}
	
	public void control() {
		spawners.values().forEach(IGenerator::control);
	}
	
	public void tick() {
		if(queue.isEmpty() == false) {
			queue.forEach(this::put);
			queue.clear();
		}
		spawners.values().forEach(IGenerator::tick);
	}
	
	public void reduce() {
		Iterator<IGenerator> it = spawners.values().iterator();
		while(it.hasNext() == true) {
			IGenerator next = it.next();
			if(next.active() == false || next.present() == false) {
				next.clear();
				it.remove();
			}
		}
	}
	
	public void put(Block block) {
		put(new ActiveGenerator(ISpawner.of(block)));
	}
	
	private void put(IGenerator generator) {
		IGenerator last = spawners.put(generator.position(), generator);
		if(last != null) last.clear();
	}
	
	public IGenerator get(Block block) {
		IGenerator generator = spawners.get(Pos.of(block));
		if(generator == null) {
			if(block.getType() == Material.SPAWNER) put(block);
		} else if(generator.active() == false) return null;
		return generator;
	}
	
	public IGenerator raw(Block block) {
		return spawners.get(Pos.of(block));
	}

}
