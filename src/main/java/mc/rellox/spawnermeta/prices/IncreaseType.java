package mc.rellox.spawnermeta.prices;

public enum IncreaseType {

    ADDITION() {
        @Override
        public String format(Price price) {
            return price.text().text();
        }

        @Override
        public int price(int base, int increment) {
            return base + increment;
        }
    }, MULTIPLICATION() {
        @Override
        public String format(Price price) {
            return price.value + "%";
        }

        @Override
        public int price(int base, int increment) {
            return (int) (base * m(increment));
        }
    };

    @Override
    public String toString() {
        return name();
    }

    public abstract String format(Price price);

    public abstract int price(int base, int increment);

    private static double m(int i) {
        return (double) (i) * 0.01 + 1.0;
    }

    public static IncreaseType of(String name) {
        try {
            return valueOf(name.toUpperCase());
        } catch(Exception ignored) {}
        return IncreaseType.ADDITION;
    }

}
