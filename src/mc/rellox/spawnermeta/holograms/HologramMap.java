package mc.rellox.spawnermeta.holograms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.Player;

import mc.rellox.spawnermeta.configuration.Settings;
import mc.rellox.spawnermeta.utils.DataManager;

public final class HologramMap {
	
	private final Map<Long, List<Hologram>> holograms;
	
	public HologramMap() {
		this.holograms = new HashMap<>();
	}
	
	public void clear() {
		holograms.values().forEach(h -> h.forEach(Hologram::destroy));
		holograms.clear();
	}
	
	public void update(Block block) {
		long at = at(block.getX() >> 4, block.getZ() >> 4);
		List<Hologram> hs = holograms.get(at);
		if(hs == null) add(block);
		else {
			hs.stream()
				.filter(h -> h.is(block))
				.findFirst()
				.ifPresentOrElse(Hologram::update, () -> add(block));
		}
	}

	public void spawn(Player player) {
		holograms.values().forEach(l -> l.forEach(h -> h.spawn(player)));
	}
	
	public void add(Block block) {
		if(block.getType() != Material.SPAWNER) return;
		if(Settings.settings.holograms_show_natural == false
				&& DataManager.isPlaced(block) == false) return;
		long at = at(block.getX() >> 4, block.getZ() >> 4);
		List<Hologram> hs = holograms.get(at);
		if(hs == null) holograms.put(at, hs = new ArrayList<>(1));
		hs.add(new Hologram(block));
	}
	
	public void remove(Block block) {
		long at = at(block.getX() >> 4, block.getZ() >> 4);
		List<Hologram> hs = holograms.get(at);
		if(hs == null) return;
		Hologram h;
		Iterator<Hologram> it = hs.iterator();
		while(it.hasNext() == true) {
			if((h = it.next()).block.equals(block) == true) {
				h.destroy();
				it.remove();
				return;
			}
		}
		if(hs.isEmpty() == true) holograms.remove(at);
	}
	
	public void load(Chunk chunk) {
		List<Block> spawners = new ArrayList<>();
		for(BlockState state : chunk.getTileEntities()) {
			if(state instanceof CreatureSpawner) {
				if(Settings.settings.holograms_show_natural == false
						&& DataManager.isPlaced(state.getBlock()) == false) continue;
				spawners.add(state.getBlock());
			}
		}
		if(spawners.isEmpty() == true) return;
		long at = at(chunk.getX(), chunk.getZ());
		List<Hologram> hs = spawners.stream()
				.map(Hologram::new)
				.collect(Collectors.toList());
		holograms.put(at, hs);
	}
	
	public void unload(Chunk chunk) {
		long at = at(chunk.getX(), chunk.getZ());
		List<Hologram> hs = holograms.remove(at);
		if(hs == null) return;
		for(Hologram h : hs) h.destroy();
	}
	
	private long at(int x, int z) {
		return (((long) x) << 32) | (z & 0xffffffffL);
	}

}
