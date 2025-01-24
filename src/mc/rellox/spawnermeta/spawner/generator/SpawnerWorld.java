package mc.rellox.spawnermeta.spawner.generator;

import java.util.*;
import java.util.stream.Stream;

import mc.rellox.spawnermeta.SpawnerMeta;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;

import mc.rellox.spawnermeta.api.spawner.IGenerator;
import mc.rellox.spawnermeta.api.spawner.ISpawner;
import mc.rellox.spawnermeta.api.spawner.location.Pos;
import mc.rellox.spawnermeta.configuration.Settings;
import mc.rellox.spawnermeta.spawner.ActiveGenerator;

public class SpawnerWorld {
	
	public final World world;
	protected final Map<Pos, IGenerator> spawners;
	private final List<IGenerator> queue;
	
	public SpawnerWorld(World world) {
		this.world = world;
		this.spawners = new HashMap<>();
		this.queue = new LinkedList<>();
	}
	
	public Stream<IGenerator> stream() {
		return spawners.values().stream();
	}
	
	public void load() {
		Stream.of(world.getLoadedChunks()).forEach(this::load);
	}
	
	public void load(Chunk chunk) {
		Stream.of(chunk.getTileEntities())
		.filter(CreatureSpawner.class::isInstance)
		.map(BlockState::getBlock)
		.filter(block -> Settings.settings.ignored(block) == false)
		.map(ISpawner::of)
		.map(ActiveGenerator::new)
		.forEach(queue::add);
	}
	
	public void unload(Chunk chunk) {
		spawners.values().stream()
		.filter(g -> g.in(chunk))
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
		List<Pos> toRemove = new ArrayList<>();

		spawners.forEach((pos, generator) -> {
			SpawnerMeta.scheduler().runAtLocation(generator.block().getLocation(), task -> {
				if (!generator.active() || !generator.present()) {
					generator.clear();
					toRemove.add(pos); // Add the key (pos) to the list for removal to prevent IllegalStateException
				}
			});
		});

		// Remove invalid generators using their keys
		toRemove.forEach(spawners::remove);
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
