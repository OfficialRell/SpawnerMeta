package mc.rellox.spawnermeta.prices;

public enum PriceType {
	
	EXPERIENCE("Experience"),
	LEVELS("Levels"),
	MATERIAL("Material"),
	ECONOMY("Economy");
	
	private final String name;
	
	private PriceType(String name) {
		this.name = name;
	}
	
	public String getName() {
		return this.name;
	}
	
	@Override
	public String toString() {
		return getName();
	}
	
	public static PriceType of(String name) {
		try {
			return valueOf(name.toUpperCase());
		} catch (Exception e) {}
		return EXPERIENCE;
	}

}
