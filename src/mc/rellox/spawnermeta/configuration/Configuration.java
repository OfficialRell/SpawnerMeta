package mc.rellox.spawnermeta.configuration;

import mc.rellox.spawnermeta.configuration.ConfigurationFile.SettingsFile;

public final class Configuration {
	
	public static void initialize() {
		CF.s.initialize();
	}
	
	public static final class CF {
		
		public static final SettingsFile s = new SettingsFile();
		
	}

}
