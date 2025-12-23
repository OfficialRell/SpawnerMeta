package mc.rellox.spawnermeta.api.configuration;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.FileConfigurationOptions;

import mc.rellox.spawnermeta.utility.reflect.Reflect.RF;
import mc.rellox.spawnermeta.version.Version;
import mc.rellox.spawnermeta.version.Version.VersionType;

public interface IFile extends IFileValues {
	
	/**
	 * Creates or initializes this file.
	 */
	
	void create();
	
	/**
	 * @return The configuration file
	 */
	
	FileConfiguration file();
	
	/**
	 * @return File name
	 */
	
	String name();
	
	/**
	 * @return {@code true} if this file was just created
	 */
	
	boolean isNew();

	/**
	 * Saves all changes to the file.
	 */
	
	void save();
	
	/**
	 * Adds a default value to this file.
	 * 
	 * @param path - path
	 * @param object - object
	 */
	
	default void defaulted(String path, Object object) {
		file().addDefault(path, object);
	}
	
	/**
	 * Sets an object to the path, without saving this file.
	 * 
	 * @param path - path
	 * @param object - object to set
	 */
	
	default void hold(String path, Object object) {
		file().set(path, object);
	}
	
	/**
	 * Removes specified path, without saving this file.
	 * 
	 * @param path - path to remove
	 */
	
	default void delete(String path) {
		file().set(path, null);
	}
	
	/**
	 * Sets an object to the path and saves this file.
	 * 
	 * @param path - path
	 * @param object - object to set
	 */
	
	default void set(String path, Object object) {
		file().set(path, object);
		save();
	}
	
	/**
	 * Removes specified path and saved this file.
	 * 
	 * @param path - path to remove
	 */
	
	default void clear(String path) {
		file().set(path, null);
		save();
	}
	
	/**
	 * Copies the object from the specified path to a different path,
	 *  removing the previous path, without saving
	 * 
	 * @param from - path from
	 * @param to - path to copy
	 */
	
	default void copy(String from, String to) {
		copy(from, to, false);
	}
	
	/**
	 * Copies the object from the specified path to a different path,
	 *  removing the previous path.
	 * 
	 * @param from - path from
	 * @param to - path to copy
	 * @param save - should save
	 */
	
	default void copy(String from, String to, boolean save) {
		Object o = file().get(from);
		if(save == true) clear(from);
		else delete(from);
		if(o != null) {
			if(save == true) set(to, o);
			else hold(to, o);
		}
	}
	
	/**
	 * @param path - path
	 * @return {@code true} if this path has a value, otherwise {@code false}
	 */
	
	default boolean exists(String path) {
		return file().get(path) != null;
	}
	
	/**
	 * Get all keys from the specified path.
	 * 
	 * @param path - path
	 * @return Set of path keys
	 */
	
	default Set<String> keys(String path) {
		ConfigurationSection cs = file().getConfigurationSection(path);
		return cs == null ? new HashSet<>() : cs.getKeys(false);
	}
	
	/**
	 * Clears file memory. After using this method most other methods will throw {@link NullPointerException}.
	 */
	
	void free();
	
	default void header(String header) {
		header(header.split("\\n"));
	}
	
	/**
	 * Sets a header to this file.
	 * 
	 * @param header - file header
	 */
	
	default void header(String... header) {
		FileConfigurationOptions o = file().options();
		if(Version.version.atleast(VersionType.v_18_1) == true) {
			RF.order(o, "setHeader", List.class).invoke(List.of(header));
			RF.order(o, "parseComments", boolean.class).invoke(true);
		} else {
			RF.order(o, "header", String.class).invoke(Stream.of(header).collect(Collectors.joining("\n")));
			RF.order(o, "copyHeader", boolean.class).invoke(true);
		}
	}
	
	/**
	 * @return New commenter or {@code null} if server version is 1.17 or lower
	 */
	
	default Commenter commenter() {
		return Version.version.atleast(VersionType.v_18_1) == true
				? new Commenter(this) : null;
	}
	
	record Commenter(IFile file) {
		
		/**
		 * Adds comments to this path.
		 * 
		 * @param path - path
		 * @param cs - comments
		 */
		
		public void comment(String path, String... cs) {
			comment(path, List.of(cs));
		}
		
		/**
		 * Adds comments to this path.
		 * 
		 * @param path - path
		 * @param list - comments
		 */
		
		public void comment(String path, List<String> list) {
			RF.order(file.file(), "setComments", String.class, List.class)
				.invoke(path, list);
		}
		
	}

}
