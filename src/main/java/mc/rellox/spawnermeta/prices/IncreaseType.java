package mc.rellox.spawnermeta.prices;

public enum IncreaseType {

	ADDITION() {
		@Override
		public String format(Price c) {
			return c.text().text();
		}
		@Override
		public int price(int c, int i) {
			return c + i;
		}
	},	MULTIPLICATION() {
		@Override
		public String format(Price c) {
			return c.value + "%";
		}
		@Override
		public int price(int c, int i) {
			return (int) (c * m(i));
		}
	};
	
	@Override
	public String toString() {
		return name();
	}
	
	public abstract String format(Price c);
	
	public abstract int price(int c, int i);
	
	private static double m(int i) {
		return (double) (i) * 0.01 + 1.0;
	}
	
	public static IncreaseType of(String name) {
		try {
			return valueOf(name.toUpperCase());
		} catch (Exception e) {}
		return IncreaseType.ADDITION;
	}

}
