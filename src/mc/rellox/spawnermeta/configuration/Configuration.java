package mc.rellox.spawnermeta.configuration;

import mc.rellox.spawnermeta.configuration.file.LanguageFile;
import mc.rellox.spawnermeta.configuration.file.LayoutsFile;
import mc.rellox.spawnermeta.configuration.file.RequirementFile;
import mc.rellox.spawnermeta.configuration.file.SettingsFile;

public final class Configuration {
	
	public static void initialize() {
		CF.s.load();
		CF.l.load();
		CF.r.load();
		CF.y.load();
	}
	
	public static final class CF {
		
		public static final SettingsFile s = new SettingsFile();
		public static final LanguageFile l = new LanguageFile();
		public static final RequirementFile r = new RequirementFile();
		public static final LayoutsFile y = new LayoutsFile();
		
		public static int version;
		
		public static int version() {
			return version;
		}
		
	}

}
