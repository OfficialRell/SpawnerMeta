package mc.rellox.spawnermeta.spawner;

import mc.rellox.spawnermeta.api.spawner.ICache;
import mc.rellox.spawnermeta.api.spawner.ISpawner;
import mc.rellox.spawnermeta.spawner.type.SpawnerType;

public class ActiveCache implements ICache {
	
	private final ISpawner spawner;
	
	private SpawnerType type;
	private int stack;
	private int charges;
	private int spawnable;
	private boolean empty;
	private boolean enabled;
	private boolean natural;
	private int[] attributes;
	
	public ActiveCache(ISpawner spawner) {
		this.spawner = spawner;
	}

	@Override
	public void cache() {
		type = spawner.getType();
		stack = spawner.getStack();
		charges = spawner.getCharges();
		spawnable = spawner.getSpawnable();
		empty = spawner.isEmpty();
		enabled = spawner.isEnabled();
		natural = spawner.isNatural();
		attributes = spawner.getUpgradeAttributes();
	}

	@Override
	public SpawnerType type() {
		return type;
	}

	@Override
	public int stack() {
		return stack;
	}

	@Override
	public int charges() {
		return charges;
	}

	@Override
	public int spawnable() {
		return spawnable;
	}

	@Override
	public boolean empty() {
		return empty;
	}
	
	@Override
	public boolean enabled() {
		return enabled;
	}

	@Override
	public boolean natural() {
		return natural;
	}

	@Override
	public boolean owned() {
		return natural == false;
	}

	@Override
	public int range() {
		return attributes[0];
	}

	@Override
	public int delay() {
		return attributes[1];
	}

	@Override
	public int amount() {
		return attributes[2];
	}

}
