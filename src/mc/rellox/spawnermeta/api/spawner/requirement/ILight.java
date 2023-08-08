package mc.rellox.spawnermeta.api.spawner.requirement;

import org.bukkit.block.Block;

public interface ILight {

	static ILight empty = new ILight() {
		@Override
		public byte minimum() {
			return 0;
		}
		@Override
		public byte maximum() {
			return 15;
		}
	};
	
	static ILight of(int min, int max) {
		if(min == max) return of(min);
		return new ILight() {
			final byte minimum = (byte) min,
					maximum = (byte) max;
			@Override
			public byte minimum() {
				return minimum;
			}
			@Override
			public byte maximum() {
				return maximum;
			}
		};
	}
	
	static ILight of(int v) {
		return new ILight() {
			final byte value = (byte) v;
			@Override
			public byte minimum() {
				return value;
			}
			@Override
			public byte maximum() {
				return value;
			}
		};
	}

	byte minimum();
	
	byte maximum();
	
	default boolean is(Block block) {
		int b = Math.max(block.getLightLevel(), block.getLightFromSky());
		return b >= minimum() && b <= maximum();
	}

}
