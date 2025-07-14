package mc.rellox.spawnermeta.api.configuration;

public interface IData<T> {
	
	/**
	 * @return ID of this data provider
	 */
	
	String id();
	
	/**
	 * @param file - file
	 * @return The loaded data
	 */
	
	T load(IFile file);
	
	/**
	 * Saves the data to this file.
	 * 
	 * @param file - file
	 */
	
	void save(IFile file, Object data);

}
