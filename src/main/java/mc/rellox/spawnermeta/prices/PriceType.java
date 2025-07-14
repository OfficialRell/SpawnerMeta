package mc.rellox.spawnermeta.prices;

public enum PriceType {
	
	EXPERIENCE(),
	LEVELS(),
	MATERIAL(),
	ECONOMY(),
	FLARE_TOKENS(),
	PLAYER_POINTS();
	
	public String key() {
		return name().replace('_', '-').toLowerCase();
	}
	
	@Override
	public String toString() {
		return name();
	}
	
	public static PriceType of(String name) {
		try {
			if(name.equalsIgnoreCase("MONEY") == true) return ECONOMY;
			if(name.equalsIgnoreCase("ITEM") == true) return MATERIAL;
			return valueOf(name.toUpperCase());
		} catch (Exception e) {}
		return EXPERIENCE;
	}

}
