package mc.rellox.spawnermeta.spawner.generator;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;

import mc.rellox.spawnermeta.SpawnerMeta;
import mc.rellox.spawnermeta.utility.adapter.ChunkTileEntities;
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
		this.spawners = Collections.synchronizedMap(new HashMap<>());
		this.queue = Collections.synchronizedList(new LinkedList<>());
	}
	
	public Stream<IGenerator> stream() {
		return spawners.values().stream();
	}

    public void load() {
        for (Chunk chunk : world.getLoadedChunks()) {
            load(chunk);
        }
    }

    public void load(Chunk chunk) {
		BlockState[] tileEntities = ChunkTileEntities.getTileEntities(chunk);
		for (BlockState state : tileEntities) {
			if (state instanceof CreatureSpawner) {
				Block block = state.getBlock();
				if (!Settings.settings.ignored(block)) {
					ISpawner spawner = ISpawner.of(block);
					IGenerator generator = new ActiveGenerator(spawner);
					queue.add(generator);
				}
			}
		}
	}

    public void unload(World world, Chunk chunk) {
        final int x = chunk.getX();
        final int z = chunk.getZ();

        for (IGenerator generator : spawners.values()) {
            if (generator.in(world, x, z)) {
                generator.remove(false);
            }
        }
	}

	public void clear() {
		for (IGenerator generator : spawners.values()) {
			generator.clear();
		}
		spawners.clear();
	}
	
	public int active() {
		return spawners.size();
	}
	
	public void update() {
		for (IGenerator generator : spawners.values()) {
			generator.update();
		}
	}
	
	public void control() {
		for (IGenerator generator : spawners.values()) {
			generator.control();
		}
	}
	
	public void tick() {
		if(!queue.isEmpty()) {
			for (IGenerator generator : queue) {
				put(generator);
			}
			queue.clear();
		}
        if (SpawnerMeta.foliaLib().isFolia()) {
            for (IGenerator generator : spawners.values()) {
                generator.tickFolia();
            }
        } else {
            for (IGenerator generator : spawners.values()) {
                generator.tick();
            }
        }
	}

	public void reduce() {
		List<Pos> toRemove = new ArrayList<>();

		Map<Pos, IGenerator> spawnersCopy;
		synchronized (spawners) {
			spawnersCopy = new HashMap<>(spawners);
		}

		for (Map.Entry<Pos, IGenerator> entry : spawnersCopy.entrySet()) {
			Pos pos = entry.getKey();
			IGenerator generator = entry.getValue();
			if (!generator.active() || !generator.present()) {
				generator.clear();
				toRemove.add(pos);
			}
		}

		synchronized (spawners) {
			for (Pos pos : toRemove) {
				spawners.remove(pos);
			}
		}
	}

	public int remove(boolean fully, Predicate<IGenerator> filter) {
		List<Pos> toRemove = new ArrayList<>();

		Map<Pos, IGenerator> spawnersCopy;
		synchronized (spawners) {
			spawnersCopy = new HashMap<>(spawners);
		}

		for (Map.Entry<Pos, IGenerator> entry : spawnersCopy.entrySet()) {
			Pos pos = entry.getKey();
			IGenerator generator = entry.getValue();
			if (generator.active() && filter.test(generator)) {
				generator.remove(fully);
				toRemove.add(pos);
			}
		}

		synchronized (spawners) {
			for (Pos pos : toRemove) {
				spawners.remove(pos);
			}
		}

		return toRemove.size();
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
		} else if(!generator.active()) return null;
		return generator;
	}
	
	public IGenerator raw(Block block) {
		return spawners.get(Pos.of(block));
	}

}
