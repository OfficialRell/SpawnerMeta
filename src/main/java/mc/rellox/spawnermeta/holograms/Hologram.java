package mc.rellox.spawnermeta.holograms;

import mc.rellox.spawnermeta.api.spawner.IGenerator;
import mc.rellox.spawnermeta.configuration.Language;
import mc.rellox.spawnermeta.configuration.Settings;
import mc.rellox.spawnermeta.spawner.type.SpawnerType;
import mc.rellox.spawnermeta.text.content.Content;

public class Hologram extends AbstractHologram {
	
	public Hologram(IGenerator generator) {
		super(generator, false, Settings.settings.holograms_regular_radius);
	}
	
	@Override
	public Content title() {
		SpawnerType type = generator.cache().type();
		String r = type == SpawnerType.EMPTY ? "empty" : "regular";
		int stack = generator.cache().stack();
		return stack > 1 ? Language.get("Holograms." + r + ".multiple",
				"name", type, "stack", stack)
				: Language.get("Holograms." + r + ".single",
						"name", type);
	}

}
