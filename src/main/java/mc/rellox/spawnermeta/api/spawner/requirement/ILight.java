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
		@Override
		public byte light(Block block) {
			return (byte) Math.max(block.getLightLevel(), block.getLightFromSky());
		}
		@Override
		public boolean daylight() {
			return false;
		}
	};
	
	static ILight of(int min, int max, boolean daylight) {
		if(min == max) return of(min, daylight);
		return new ILight() {
			final byte minimum = (byte) min,
					maximum = (byte) max;
			final LightFinder finder = LightFinder.of(daylight);
			@Override
			public byte minimum() {
				return minimum;
			}
			@Override
			public byte maximum() {
				return maximum;
			}
			@Override
			public byte light(Block block) {
				return finder.get(block);
			}
			@Override
			public boolean daylight() {
				return daylight;
			}
		};
	}
	
	static ILight of(int v, boolean daylight) {
		return new ILight() {
			final byte value = (byte) v;
			final LightFinder finder = LightFinder.of(daylight);
			@Override
			public byte minimum() {
				return value;
			}
			@Override
			public byte maximum() {
				return value;
			}
			@Override
			public byte light(Block block) {
				return finder.get(block);
			}
			@Override
			public boolean daylight() {
				return daylight;
			}
		};
	}

	/**
	 * @return Minimum light value
	 */
	
	byte minimum();
	
	/**
	 * @return Maximum light value
	 */
	
	byte maximum();
	
	/**
	 * @param block - block at
	 * @return Block light level
	 */
	
	byte light(Block block);
	
	/**
	 * If daylight is {@code true} then the day time will be checked, if it is day
	 * then the appropiate light level will be removed, otherwise if it is night time
	 * then light level of 0 will be returned.
	 * 
	 * @return {@code true} if daylight checking is enabled, otherwise {@code false}
	 */
	
	boolean daylight();
	
	/**
	 * @param block - block at
	 * @return {@code true} if block light level matches the required light level
	 */
	
	default boolean is(Block block) {
		int light = light(block);
		return light >= minimum() && light <= maximum();
	}
	
	interface LightFinder {
		
		static LightFinder of(boolean daylight) {
			return daylight == true
					? block -> {
						long time = block.getWorld().getTime();
						return (byte) Math.max(block.getLightFromBlocks(),
								time > 13500 && time < 22500
								? 0 : block.getLightFromSky());
					}
					: block -> (byte) Math.max(block.getLightFromBlocks(),
							block.getLightFromSky());
		}

		byte get(Block block);
		
	}

}
