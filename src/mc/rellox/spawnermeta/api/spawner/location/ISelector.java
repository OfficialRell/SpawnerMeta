package mc.rellox.spawnermeta.api.spawner.location;

import java.util.List;
import java.util.function.Supplier;

import org.bukkit.Location;

import mc.rellox.spawnermeta.configuration.Settings;
import mc.rellox.spawnermeta.utility.Utils;

public interface ISelector extends Supplier<Location> {
	
	static ISelector of(List<Location> list) {
		return Settings.settings.selection.get(list);
	}
	
	enum Selection {
		SINGLE {
			@Override
			public ISelector get(List<Location> list) {
				Location location = Utils.random(list);
				return () -> location;
			}
		}, SPREAD {
			@Override
			public ISelector get(List<Location> list) {
				return () -> Utils.random(list);
			}
		};
		
		public abstract ISelector get(List<Location> list);
	}

}
