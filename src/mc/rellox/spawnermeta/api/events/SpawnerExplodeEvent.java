package mc.rellox.spawnermeta.api.events;

import org.bukkit.block.Block;

import mc.rellox.spawnermeta.api.spawner.Spawner;

public class SpawnerExplodeEvent extends SpawnerEvent {
	
	private final Block block;
	public final ExplosionType explosion;
	private final boolean[] bs;
	
	public SpawnerExplodeEvent(Block block, ExplosionType explosion, boolean[] bs) {
		this.block = block;
		this.explosion = explosion;
		this.bs = bs;
	}
	
	public Spawner getSpawner() {
		return Spawner.of(block);
	}
	
	public boolean canBreakOwned() {
		return bs[0];
	}
	
	public void setBreakOwned(boolean b) {
		bs[0] = b;
	}
	
	public boolean canDropOwned() {
		return bs[1];
	}
	
	public void setDropOwned(boolean b) {
		bs[1] = b;
	}
	
	public boolean canBreakNatural() {
		return bs[2];
	}
	
	public void setBreakNatural(boolean b) {
		bs[2] = b;
	}
	
	public boolean canDropNatural() {
		return bs[3];
	}
	
	public void setDropNatural(boolean b) {
		bs[3] = b;
	}
	
	public enum ExplosionType {
		
		TNT, CREEPERS, FIREBALLS, END_CRYSTALS;

	}

}
