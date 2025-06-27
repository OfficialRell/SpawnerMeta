package mc.rellox.spawnermeta.spawner.type;

public enum StackingType {
	
	INFINITE,//(LanguageFile::stacking_stacked_infinite),
	FINITE;//(LanguageFile::stacking_stacked_finite);
	
//	private final Function<Integer, String> f;
//	
//	private StackingType(Function<Integer, String> f) {
//		this.f = f;
//	}
	
//	public abstract String text(int i) {
//		return f.apply(i);
//	}
	
	public static StackingType of(String s, StackingType d) {
		try {
			return valueOf(s);
		} catch (Exception e) {}
		return d;
	}

}
