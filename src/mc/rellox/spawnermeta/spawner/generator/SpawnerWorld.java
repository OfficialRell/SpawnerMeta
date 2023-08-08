package mc.rellox.spawnermeta.spawner.generator;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;

import mc.rellox.spawnermeta.api.spawner.ISpawner;
import mc.rellox.spawnermeta.api.spawner.IGenerator;
import mc.rellox.spawnermeta.api.spawner.location.Pos;
import mc.rellox.spawnermeta.spawner.ActiveGenerator;
import mc.rellox.spawnermeta.utility.DataManager;

public class SpawnerWorld {
	
	public final World world;
	private final Map<Pos, IGenerator> spawners;
	
	public SpawnerWorld(World world) {
		this.world = world;
		this.spawners = new HashMap<>();
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
		.forEach(o -> spawners.put(o.position(), o));
	}
	
	public void unload(Chunk chunk) {
		Stream.of(chunk.getTileEntities())
		.filter(CreatureSpawner.class::isInstance)
		.map(BlockState::getBlock)
		.map(Pos::of)
		.forEach(spawners::remove);
	}
	
	public void clear() {
		spawners.values().forEach(IGenerator::clear);
		spawners.clear();
	}
	
	public void update() {
		spawners.values().forEach(IGenerator::update);
	}
	
	public void refresh() {
		spawners.values().forEach(IGenerator::refresh);
	}
	
	public void tick() {
		spawners.values().forEach(IGenerator::tick);
	}
	
	public IGenerator get(Block block) {
		IGenerator generator = spawners.get(Pos.of(block));
		if(generator == null && block.getType() == Material.SPAWNER) {
			DataManager.setNewSpawner(null, block, false);
			spawners.put(Pos.of(block),
					generator = new ActiveGenerator(ISpawner.of(block)));
		}
		return generator;
	}

	public IGenerator remove(Block block) {
		return spawners.remove(Pos.of(block));
	}

}
