package mc.rellox.spawnermeta.holograms;

import mc.rellox.spawnermeta.api.spawner.IGenerator;
import mc.rellox.spawnermeta.configuration.Language;
import mc.rellox.spawnermeta.configuration.Settings;
import mc.rellox.spawnermeta.text.content.Content;

public class Warning extends AbstractHologram {

	public Warning(IGenerator generator, boolean above) {
		super(generator, above, Settings.settings.holograms_warning_radius);
	}

	@Override
	public Content title() {
		return Language.get("Holograms.warning");
	}

}
