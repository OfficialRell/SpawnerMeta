package mc.rellox.spawnermeta.spawner;

import java.util.stream.Stream;

import org.bukkit.Color;

public enum UpgradeType {
	
	RANGE("Range", "blocks", Color.AQUA),
	DELAY("Delay", "ticks", Color.YELLOW),
	AMOUNT("Amount", "entities", Color.PURPLE);
	
	public final String name, values;
	public final Color color;
	
	private UpgradeType(String name, String values, Color color) {
		this.name = name;
		this.values = values;
		this.color = color;
	}
	
	public String lower() {
		return name.toLowerCase();
	}
	
	public static UpgradeType of(String name) {
		return stream()
				.filter(u -> u.name.equalsIgnoreCase(name))
				.findFirst()
				.orElse(null);
	}
	
	public static UpgradeType of(int i) {
		return values()[i];
	}
	
	public static Stream<UpgradeType> stream() {
		return Stream.of(values());
	}

}
