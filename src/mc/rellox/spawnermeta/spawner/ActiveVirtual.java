package mc.rellox.spawnermeta.spawner;

import java.util.Arrays;

import org.bukkit.inventory.ItemStack;

import mc.rellox.spawnermeta.api.spawner.IVirtual;
import mc.rellox.spawnermeta.configuration.Settings;
import mc.rellox.spawnermeta.spawner.type.SpawnerType;
import mc.rellox.spawnermeta.utility.DataManager;

public class ActiveVirtual implements IVirtual {
	
	private final SpawnerType type;
	private final int[] levels;
	private final int charges, spawnable;
	private final boolean empty;

	public ActiveVirtual(SpawnerType type, int[] levels, int charges,
			int spawnable, boolean empty) {
		this.type = type;
		this.levels = levels == null || levels.length != 3
				? new int[] {1, 1, 1} : levels;
		this.charges = charges;
		this.spawnable = spawnable;
		this.empty = empty;
	}

	@Override
	public boolean exact(IVirtual other) {
		if(type != other.getType()) return false;
		if(empty != other.isEmpty()) return false;
		if(Settings.settings.charges_enabled == true) {
			int oc = other.getCharges();
			if(Settings.settings.charges_allow_stacking == false
					&& charges != oc) return false;
			boolean a = charges >= 1_000_000_000,
					b = oc >= 1_000_000_000;
			if(a != b) return false;
		}
		return Arrays.equals(levels, other.getUpgradeLevels());
	}
	
	@Override
	public SpawnerType getType() {
		return type;
	}

	@Override
	public int[] getUpgradeLevels() {
		return levels;
	}

	@Override
	public int getCharges() {
		return charges;
	}

	@Override
	public int getSpawnable() {
		return spawnable;
	}

	@Override
	public boolean isEmpty() {
		return empty;
	}
	
	@Override
	public ItemStack getItem(int a) {
		return DataManager.getSpawner(this, a);
	}
	
}
