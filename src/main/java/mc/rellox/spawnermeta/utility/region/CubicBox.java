package mc.rellox.spawnermeta.utility.region;

import mc.rellox.spawnermeta.api.region.IBox;

public class CubicBox implements IBox {
	
	private final int mx, my, mz, xx, xy, xz, r;
	
	public CubicBox(int x, int z, int y, int r) {
		this.mx = x - r;
		this.my = y - r;
		this.mz = z - r;
		this.xx = x + r;
		this.xy = y + r;
		this.xz = z + r;
		this.r = r;
	}

	@Override
	public boolean in(int x, int y, int z) {
		return x >= mx && x <= xx
				&& y >= my && y <= xy
				&& z >= mz && z <= xz;
	}

	@Override
	public int x() {
		return mx + r;
	}

	@Override
	public int y() {
		return my + r;
	}

	@Override
	public int z() {
		return mz + r;
	}

	@Override
	public int radius() {
		return r;
	}

}
