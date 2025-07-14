package mc.rellox.spawnermeta.api.events;

public interface EventExecutor<E> {
	
	void execute(E event);

}
