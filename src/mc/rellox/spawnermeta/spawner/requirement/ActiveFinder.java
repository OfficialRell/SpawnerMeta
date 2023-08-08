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
	
	private final IGenerator instance;
	private IRequirements requirements;
	
	private ErrorCounter errors;
	
	public ActiveFinder(IGenerator instance) {
		this.instance = instance;
		update();
	}
	
	@Override
	public void update() {
		this.requirements = CF.r.get(instance.cache().type());
	}

	@Override
	public IRequirements requirements() {
		return requirements;
	}

	@Override
	public List<Location> find() {
		int radius = Settings.settings.radius;
		int r = radius * 2 + 1;
		errors = new ErrorCounter(r * r * r);
		ISpawner spawner = instance.spawner();
		EntityBox box = spawner.getType().box();
		List<Location> list = new ArrayList<>();
		Block block = spawner.block();
		Location l;
		int ix = -radius, iy, iz;
		do {
			iy = -radius;
			do {
				iz = -radius;
				do {
					if((l = box.check(block.getRelative(ix, iy, iz),
							requirements, errors.submit())) != null) list.add(l);
				} while(++iz <= radius);
			} while(++iy <= radius);
		} while(++ix <= radius);
		errors.found = list.isEmpty() == false;
		return list;
	}

	@Override
	public ErrorCounter errors() throws IllegalStateException {
		if(errors == null) throw new IllegalStateException("Method find() must be ran first");
		return errors;
	}
	
}
