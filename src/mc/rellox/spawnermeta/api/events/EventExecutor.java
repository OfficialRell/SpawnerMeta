package mc.rellox.spawnermeta.api.events;

@FunctionalInterface
public interface EventExecutor<E> {
	
	void execute(E event);

}
