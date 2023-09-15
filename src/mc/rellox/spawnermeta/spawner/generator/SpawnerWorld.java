package mc.rellox.spawnermeta.spawner.generator;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;

import mc.rellox.spawnermeta.api.spawner.IGenerator;
import mc.rellox.spawnermeta.api.spawner.ISpawner;
import mc.rellox.spawnermeta.api.spawner.location.Pos;
import mc.rellox.spawnermeta.spawner.ActiveGenerator;

public class SpawnerWorld {
	
	public final World world;
	private final Map<Pos, IGenerator> spawners;
	private final List<IGenerator> queue;
	
	public SpawnerWorld(World world) {
		this.world = world;
		this.spawners = new HashMap<>();
		this.queue = new LinkedList<>();
	}
	
	public void load() {
		Stream.of(world.getLoadedChunks()).forEach(this::load);
	}
	
	public void load(Chunk chunk) {
		Stream.of(chunk.getTileEntities())
		.filter(CreatureSpawner.class::isInstance)
		.map(BlockState::getBlock)
		.map(ISpawner::of)
		.map(ActiveGenerator::new)
		.forEach(queue::add);
	}
	
	public void unload(Chunk chunk) {
		Stream.of(chunk.getTileEntities())
		.filter(CreatureSpawner.class::isInstance)
		.map(BlockState::getBlock)
		.map(Pos::of)
		.map(spawners::get)
		.filter(g -> g != null)
		.forEach(g -> g.remove(false));
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
	
	public void refresh() {
		spawners.values().forEach(IGenerator::refresh);
	}
	
	public void tick() {
		if(queue.isEmpty() == false) {
			queue.forEach(g -> spawners.put(g.position(), g));
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
		IGenerator g = new ActiveGenerator(ISpawner.of(block));
		spawners.put(g.position(), g);
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
