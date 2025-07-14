package mc.rellox.spawnermeta.utility.region;

import mc.rellox.spawnermeta.api.region.IBox;

public class SphereBox implements IBox {
	
	private final int x, y, z, r, rq;
	
	public SphereBox(int x, int z, int y, int r) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.r = r;
		this.rq = r * r;
	}

	@Override
	public boolean in(int a, int b, int c) {
		int v;
		int r = (v = x - a) * v
				+ (v = y - b) * v
				+ (v = z - c) * v;
		return r > 0 ? r < rq : r > -rq;
	}

	@Override
	public int x() {
		return x;
	}

	@Override
	public int y() {
		return y;
	}

	@Override
	public int z() {
		return z;
	}

	@Override
	public int radius() {
		return r;
	}

}
