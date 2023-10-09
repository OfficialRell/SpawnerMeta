package mc.rellox.spawnermeta.spawner.requirement;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.block.Block;

import mc.rellox.spawnermeta.api.spawner.ISpawner;
import mc.rellox.spawnermeta.api.spawner.IGenerator;
import mc.rellox.spawnermeta.api.spawner.location.IFinder;
import mc.rellox.spawnermeta.api.spawner.requirement.ErrorCounter;
import mc.rellox.spawnermeta.api.spawner.requirement.IRequirements;
import mc.rellox.spawnermeta.configuration.Configuration.CF;
import mc.rellox.spawnermeta.configuration.Settings;
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
		int radius_h = Settings.settings.radius_horizontal;
		int radius_v = Settings.settings.radius_vertical;
		int r_h = radius_h * 2 + 1;
		int r_v = radius_v * 2 + 1;
		errors = new ErrorCounter(r_h * r_v * r_h);
		ISpawner spawner = generator.spawner();
		EntityBox box = spawner.getType().box();
		List<Location> list = new ArrayList<>();
		Block block = spawner.block();
		Location l;
		int ix = -radius_h, iy, iz;
		do {
			iy = -radius_v;
			do {
				iz = -radius_h;
				do {
					if((l = box.check(block.getRelative(ix, iy, iz),
							requirements, errors.submit())) != null) list.add(l);
				} while(++iz <= radius_h);
			} while(++iy <= radius_v);
		} while(++ix <= radius_h);
		errors.found = list.isEmpty() == false;
		return list;
	}

	@Override
	public ErrorCounter errors() throws IllegalStateException {
		if(errors == null) throw new IllegalStateException("Method find() must be ran first");
		var last = errors;
		errors = null;
		return last;
	}
	
}
