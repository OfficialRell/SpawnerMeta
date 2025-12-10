package mc.rellox.spawnermeta.spawner.requirement;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

import mc.rellox.spawnermeta.api.spawner.IGenerator;
import mc.rellox.spawnermeta.api.spawner.location.IFinder;
import mc.rellox.spawnermeta.api.spawner.requirement.ErrorCounter;
import mc.rellox.spawnermeta.api.spawner.requirement.IRequirements;
import mc.rellox.spawnermeta.configuration.Configuration.CF;
import mc.rellox.spawnermeta.configuration.Settings;
import mc.rellox.spawnermeta.hook.HookRegistry;
import mc.rellox.spawnermeta.utility.region.EntityBox;

public class ActiveFinder implements IFinder {
	
	private final IGenerator generator;
	private IRequirements requirements;
	
	private ErrorCounter errors;
	
	public ActiveFinder(IGenerator generator) {
		this.generator = generator;
		update();
	}
	
	@Override
	public void update() {
		this.requirements = CF.r.get(generator.cache().type());
	}

	@Override
	public IRequirements requirements() {
		return requirements;
	}

	@Override
	public List<Location> find() {
		int rx = Settings.settings.radius_horizontal;
		int ry = Settings.settings.radius_vertical;
		int qx = rx * 2 + 1, qy = ry * 2 + 1;
		
		errors = new ErrorCounter(qx * qy * qx);
		
		EntityBox box = generator.cache().type().box();
		
		List<Location> list = new ArrayList<>();
		
		Block block = generator.block();
		if(!loaded(block.getWorld(), block.getX(), block.getZ(), rx + box.maximum())) {
			errors.found = false;
			return list;
		}
		Location l;
		int ix = -rx, iy, iz;
		do {
			iy = -ry;
			do {
				iz = -rx;
				do {
					if((l = box.check(block.getRelative(ix, iy, iz),
							requirements, errors.submit())) != null) list.add(l);
				} while(++iz <= rx);
			} while(++iy <= ry);
		} while(++ix <= rx);
		
		if(HookRegistry.PLOT_SQUARED.exists())
			HookRegistry.PLOT_SQUARED.filter(generator, list);
		
		errors.found = !list.isEmpty();
		return list;
	}
	
	private boolean loaded(World world, int x, int z, int r) {
		int cx = x >> 4, cz = z >> 4;
		if(!world.isChunkLoaded(cx, cz)) return false;
		int[] is = {
				x + r, z,
				x + r, z + r,
				x + r, z - r,
				x,     z + r,
				x,     z - r,
				x - r, z,
				x - r, z + r,
				x - r, z - r
		};
		for(int i = 0, ox, oz; i < is.length; i += 2) {
			ox = is[i] >> 4;
			oz = is[i + 1] >> 4;
			if(ox == cx && oz == cz) continue;
			if(!world.isChunkLoaded(ox, oz)) return false;
		}
		return true;
	}

	@Override
	public ErrorCounter errors() throws IllegalStateException {
		if(errors == null) throw new IllegalStateException("Method find() must be ran first");
		var last = errors;
		errors = null;
		return last;
	}
	
}
