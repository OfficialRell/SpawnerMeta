package mc.rellox.spawnermeta.configuration.location;

import java.util.HashMap;
import java.util.Map;

import mc.rellox.spawnermeta.api.configuration.IData;
import mc.rellox.spawnermeta.api.configuration.IFile;

public class ExternalData {
	
	private final Map<String, Object> values;
	
	public ExternalData() {
		this.values = new HashMap<>();
	}
	
	public <T> T get(IData<T> data) {
		return get0(data);
	}
	
	@SuppressWarnings("unchecked")
	<T> T get0(IData<T> data) {
		return (T) values.get(data.id());
	}
	
	public <T> T set(IData<T> data, T value) {
		return set0(data, value);
	}
	
	@SuppressWarnings("unchecked")
	<T> T set0(IData<T> data, T value) {
		return (T) values.put(data.id(), value);
	}
	
	public void load(IFile file) {
		LocationRegistry.EXTERNA_DATA.forEach(data -> {
			Object value = data.load(file);
			if(value == null) values.remove(data.id());
			else values.put(data.id(), value);
		});
	}
	
	public void save(IFile file) {
		LocationRegistry.EXTERNA_DATA.forEach(data -> {
			data.save(file, values.get(data.id()));
		});
	}

}
