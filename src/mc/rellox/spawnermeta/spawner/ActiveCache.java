package mc.rellox.spawnermeta.spawner;

import java.util.UUID;

import mc.rellox.spawnermeta.api.spawner.ICache;
import mc.rellox.spawnermeta.api.spawner.ISpawner;
import mc.rellox.spawnermeta.hook.HookRegistry;
import mc.rellox.spawnermeta.spawner.type.SpawnerType;

public class ActiveCache implements ICache {
	
	private final ISpawner spawner;
	
	private SpawnerType type;
	private int stack;
	private int charges;
	private int spawnable;
	private boolean empty;
	private boolean enabled;
	private UUID owner;
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
		owner = spawner.getOwnerID();
		natural = owner == null;
		
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
		return !natural;
	}
	
	@Override
	public UUID owner() {
		return owner;
	}

	@Override
	public int range() {
		return attributes[0];
	}

	@Override
	public int delay() {
		if(HookRegistry.SUPERIOR_SKYBLOCK_2.exists() == true)
			return HookRegistry.SUPERIOR_SKYBLOCK_2.delay_upgrade(spawner.block(), attributes[1]);
		return attributes[1];
	}

	@Override
	public int amount() {
		return attributes[2];
	}

}
