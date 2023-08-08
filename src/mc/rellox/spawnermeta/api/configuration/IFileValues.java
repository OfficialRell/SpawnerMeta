package mc.rellox.spawnermeta.api.configuration;

import java.util.List;

import org.bukkit.configuration.ConfigurationSection;

public interface IFileValues {

	/**
	 * @return File or file section
	 */
	
	ConfigurationSection file();
	
	/**
	 * @param path - path
	 * @return Integer value
	 */
	
	default int getInteger(String path) {
		return file().getInt(path);
	}
	
	/**
	 * @param path - path
	 * @return Double value
	 */
	
	default double getDouble(String path) {
		return file().getDouble(path);
	}
	
	/**
	 * @param path - path
	 * @return String value
	 */
	
	default String getString(String path) {
		return file().getString(path);
	}
	
	/**
	 * @param path - path
	 * @return Boolean value
	 */
	
	default boolean getBoolean(String path) {
		return file().getBoolean(path);
	}
	
	/**
	 * @param path - path
	 * @return String list value
	 */
	
	default List<String> getStrings(String path) {
		return file().getStringList(path);
	}
	
	/**
	 * @param path - path
	 * @param min - minimum value
	 * @param max - maximum value
	 * @return Safe integer value
	 */
	
	default int getInteger(String path, int min, int max) {
		int i = file().getInt(path);
		return i < min ? min : i > max ? max : i;
	}
	
	/**
	 * @param path - path
	 * @param min - minimum value
	 * @param max - maximum value
	 * @return Safe double value
	 */
	
	default double getDouble(String path, double min, double max) {
		double i = file().getDouble(path);
		return i < min ? min : i > max ? max : i;
	}
	
}
