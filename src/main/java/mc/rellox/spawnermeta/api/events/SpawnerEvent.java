package mc.rellox.spawnermeta.api.events;

public abstract class SpawnerEvent implements IEvent {
	
	private boolean cancelled;

	public final void cancel(boolean cancel) {
		cancelled = cancel;
	}

	public final boolean cancelled() {
		return cancelled;
	}

}
