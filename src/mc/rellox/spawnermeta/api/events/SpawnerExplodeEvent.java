package mc.rellox.spawnermeta.api.events;

import mc.rellox.spawnermeta.api.spawner.IGenerator;

public class SpawnerExplodeEvent extends SpawnerEvent implements IGeneratorEvent {
	
	private final IGenerator generator;
	public final ExplosionType explosion;
	private final boolean[] bs;
	
	public SpawnerExplodeEvent(IGenerator generator, ExplosionType explosion, boolean[] bs) {
		this.generator = generator;
		this.explosion = explosion;
		this.bs = bs;
	}
	
	public final IGenerator getGenerator() {
		return generator;
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
