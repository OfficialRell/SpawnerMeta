package mc.rellox.spawnermeta.spawner.type;

public enum StackingType {
	
	INFINITE,
	FINITE;
	
	public static StackingType of(String s, StackingType d) {
		try {
			return valueOf(s);
		} catch (Exception ignored) {}
		return d;
	}

}
