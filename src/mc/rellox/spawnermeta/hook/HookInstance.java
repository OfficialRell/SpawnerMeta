package mc.rellox.spawnermeta.hook;

public interface HookInstance<T> {
	
	T get();
	
	boolean exists();
	
	void load();

}
