package mc.rellox.spawnermeta.configuration.file;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.bukkit.Material;

import mc.rellox.spawnermeta.api.spawner.requirement.ILight;
import mc.rellox.spawnermeta.api.spawner.requirement.IMaterial;
import mc.rellox.spawnermeta.api.spawner.requirement.IRequirements;
import mc.rellox.spawnermeta.api.spawner.requirement.IRequirements.Builder;
import mc.rellox.spawnermeta.configuration.AbstractFile;
import mc.rellox.spawnermeta.spawner.type.SpawnerType;
import mc.rellox.spawnermeta.text.Text;
import mc.rellox.spawnermeta.utility.reflect.Reflect.RF;

public class RequirementFile extends AbstractFile {
	
	private final Map<SpawnerType, IRequirements> map = new HashMap<>();

	public RequirementFile() {
		super("requirements");
	}

	@Override
	protected void initialize() {
		
		String air = "AIR", water = "WATER", grass = "GRASS_BLOCK",
				sand = "SAND", red = "RED_SAND", snow = "SNOW_BLOCK",
				solid = "SOLID";
		
		put(SpawnerType.ALLAY, "", air, "");
		put(SpawnerType.AXOLOTL, "", water, "");
		put(SpawnerType.BAT, "-3", air, "");
		put(SpawnerType.BEE, "9+", air, "");
		put(SpawnerType.BLAZE, "-11", air, "");
		put(SpawnerType.CAMEL, "9+", air, List.of(sand, red));
		put(SpawnerType.CAT, "9+", air, solid);
		put(SpawnerType.CAVE_SPIDER, "0", air, solid);
		put(SpawnerType.CHICKEN, "9+", air, grass);
		put(SpawnerType.COD, "", water, "");
		put(SpawnerType.COW, "9+", air, grass);
		put(SpawnerType.CREEPER, "0", air, solid);
		put(SpawnerType.DOLPHIN, "", water, "");
		put(SpawnerType.DONKEY, "9+", air, grass);
		put(SpawnerType.DROWNED, "0", water, "");
		put(SpawnerType.ELDER_GUARDIAN, "0", water, "");
		put(SpawnerType.ENDER_DRAGON, "0+", air, "");
		put(SpawnerType.ENDERMAN, "0", air, "");
		put(SpawnerType.ENDERMITE, "0", air, "");
		put(SpawnerType.EVOKER, "0", air, "");
		put(SpawnerType.FOX, "9+", air, List.of(grass, snow, "PODZOL"));
		put(SpawnerType.FROG, "9+", air, solid);
		put(SpawnerType.GHAST, "", air, "");
		put(SpawnerType.GIANT, "0", air, "");
		put(SpawnerType.GLOW_SQUID, "", water, "");
		put(SpawnerType.GOAT, "9+", air, List.of("STONE", "ANDESITE", "DIORITE",
				"GRANITE", "SNOW", snow));
		put(SpawnerType.GUARDIAN, "0", water, "");
		put(SpawnerType.HOGLIN, "0", air, List.of(solid, "~NETHER_WART_BLOCK", "~SHROOMLIGHT"));
		put(SpawnerType.HORSE, "9+", air, grass);
		put(SpawnerType.HUSK, "0", air, List.of(sand, red));
		put(SpawnerType.ILLUSIONER, "0", air, "");
		put(SpawnerType.IRON_GOLEM, "", air, "");
		put(SpawnerType.LLAMA, "7+", air, grass);
		put(SpawnerType.MAGMA_CUBE, "", air, "");
		put(SpawnerType.MULE, "9+", air, grass);
		put(SpawnerType.MUSHROOM_COW, "9+", air, "MYCELIUM");
		put(SpawnerType.OCELOT, "9+", air, grass);
		put(SpawnerType.PANDA, "9+", air, grass);
		put(SpawnerType.PARROT, "9+", air, "");
		put(SpawnerType.PHANTOM, "0", air, "");
		put(SpawnerType.PIG, "9+", air, grass);
		put(SpawnerType.PIG_ZOMBIE, "-11", air, "");
		put(SpawnerType.PIGLIN, "-11", air, "");
		put(SpawnerType.PIGLIN_BRUTE, "0", air, "");
		put(SpawnerType.PILLAGER, "-8", air, "");
		put(SpawnerType.POLAR_BEAR, "7+", air, List.of(grass, "ICE", "BLUE_ICE"));
		put(SpawnerType.PUFFERFISH, "", water, "");
		put(SpawnerType.RABBIT, "9+", air, List.of(grass, sand, red, snow));
		put(SpawnerType.RAVAGER, "0", air, "");
		put(SpawnerType.SALMON, "", water, "");
		put(SpawnerType.SHEEP, "9+", air, grass);
		put(SpawnerType.SHULKER, "0", air, "");
		put(SpawnerType.SILVERFISH, "0", air, "");
		put(SpawnerType.SKELETON, "0", air, "");
		put(SpawnerType.SKELETON_HORSE, "", air, solid);
		put(SpawnerType.SLIME, "-7", air, "");
		put(SpawnerType.SNIFFER, "9+", air, grass);
		put(SpawnerType.SNOWMAN, "", air, "");
		put(SpawnerType.SPIDER, "0", air, "");
		put(SpawnerType.SQUID, "", water, "");
		put(SpawnerType.STRAY, "0", air, "");
		put(SpawnerType.STRIDER, "", air, "LAVA");
		put(SpawnerType.TADPOLE, "", water, "");
		put(SpawnerType.TRADER_LLAMA, "7+", air, solid);
		put(SpawnerType.TROPICAL_FISH, "", water, "");
		put(SpawnerType.TURTLE, "9+", air, sand);
		put(SpawnerType.VEX, "0", air, "");
		put(SpawnerType.VILLAGER, "9+", air, solid);
		put(SpawnerType.VINDICATOR, "0", air, "");
		put(SpawnerType.WANDERING_TRADER, "7+", air, solid);
		put(SpawnerType.WARDEN, "0", air, "");
		put(SpawnerType.WITCH, "0", air, "");
		put(SpawnerType.WITHER, "0", air, "");
		put(SpawnerType.WITHER_SKELETON, "-7", air, "");
		put(SpawnerType.WOLF, "7+", air, grass);
		put(SpawnerType.ZOGLIN, "", air, "");
		put(SpawnerType.ZOMBIE, "0", air, "");
		put(SpawnerType.ZOMBIE_HORSE, "", air, "");
		put(SpawnerType.ZOMBIE_VILLAGER, "0", air, solid);
		put(SpawnerType.ZOMBIFIED_PIGLIN, "0", air, "");
		
		List.of("ARMOR_STAND", "BOAT", "CHEST_BOAT", "EXPERIENCE_BOTTLE", "EXPERIENCE_ORB",
				"MINECART", "MINECART_CHEST", "MINECART_COMMAND", "MINECART_FURNACE",
				"MINECART_HOPPER", "MINECART_SPAWNER", "MINECART_TNT")
		.forEach(type -> put(type, "", air, ""));
		
		file.options().copyDefaults(true);
		
		header("In this file you can change all",
				"spawner spawn requirements:",
				"# light level",
				"    (sky or block light level)",
				"# environment type",
				"    (block types in which mobs spawn)",
				"# ground type",
				"    (block types on which mobs spawn)",
				"",
				"Light level can only be in range [0 - 15]",
				"Syntax:",
				"  5-10 (light level from 5 to 10)",
				"  -8 (light level from 0 to 8)",
				"  9+ (light level from 9 to 15)",
				"  11 (only light level 11)",
				"",
				"Environment and ground can be a single type",
				"or multiple:",
				"# environment: 'WATER' (single)",
				"# ground: (multiple)",
				"  - 'STONE'",
				"  - 'GRASS_BLOCK'",
				"  - 'DIRT'",
				"  - ...",
				"",
				"Built-in values:",
				"# SOLID (all solid block types)",
				"# AIR (air and insta-break block types)",
				"",
				"Syntax:",
				"  'STONE' (to include this block type)",
				"  '~STONE' (to exclude this block type)",
				"",
				"Note, that environment should always be",
				"a non-solid (AIR, WATER...), otherwise entities",
				"will spawn in solid block (STONE, DIRT...)",
				"");
		
		save();
		
		read();
	}
	
	public IRequirements get(SpawnerType type) {
		return map.getOrDefault(type, IRequirements.empty);
	}
	
	private static final Pattern p0 = Pattern.compile("\\d{1,2}"),
			p1 = Pattern.compile("\\d{1,2}-\\d{1,2}"),
			p2 = Pattern.compile("-\\d{1,2}"),
			p3 = Pattern.compile("\\d{1,2}\\+");
	
	private void read() {
		SpawnerType.stream()
		.filter(SpawnerType::regular)
		.forEach(type -> {
			String path = "Requirements." + type.name();
			
			ILight light = light(type, path + ".light");
			IMaterial environment = material(type, path + ".environment", "environment");
			IMaterial ground = material(type, path + ".ground", "ground");
			
			Builder builder = IRequirements.builder();
			builder.light = light;
			builder.environment = environment;
			builder.ground = ground;
			
			map.put(type, builder.build());
		});
	}

	protected ILight light(SpawnerType type, String path) {
		boolean error = false;
		String s = file.getString(path);
		if(s == null || s.isEmpty() == true) return ILight.empty;
		else if(p0.matcher(s).matches() == true) {
			int value = Integer.parseInt(s);
			if(value < 0 || value > 15) error = true;
			else return ILight.of(value);
		} else if(p1.matcher(s).matches() == true) {
			String[] ss = s.split("-");
			int min = Integer.parseInt(ss[0]);
			int max = Integer.parseInt(ss[1]);
			if(min < 0 || min > 15 || max < 0 || max > 15
					|| min > max) error = true;
			else return ILight.of(min, max);
		} else if(p2.matcher(s).matches() == true) {
			int value = Integer.parseInt(s.substring(1));
			if(value < 0 || value > 15) error = true;
			else return ILight.of(0, value);
		} else if(p3.matcher(s).matches() == true) {
			int value = Integer.parseInt(s.substring(0, s.length() - 1));
			if(value < 0 || value > 15) error = true;
			else return ILight.of(value, 15);
		} else error = true;
		if(error == true) Text.failure("Unable to read light requirement value (#0) for #1, ignoring!",
					s, type.name());
		return ILight.empty;
	}
	
	private IMaterial material(SpawnerType type, String path, String requirement) {
		boolean error = false;
		if(file.isString(path) == true) {
			String s = file.getString(path);
			if(s == null || s.isEmpty() == true) return IMaterial.empty;
			if(s.equalsIgnoreCase("SOLID") == true) return IMaterial.solid();
			if(s.equalsIgnoreCase("AIR") == true) return IMaterial.air();
			Material m = RF.enumerate(Material.class, s);
			if(m == null) error = true;
			else return IMaterial.of(m);
		} else {
			List<String> list = file.getStringList(path);
			Iterator<String> it = list.iterator();
			boolean solid = false;
			while(it.hasNext() == true) {
				if(it.next().equalsIgnoreCase("SOLID") == true) {
					it.remove();
					solid = true;
				}
			}
			List<Material> and = new ArrayList<>(), not = new ArrayList<>();
			if(list.isEmpty() == false) {
				for(String v : list) {
					if(v.charAt(0) == '~') {
						Material m = RF.enumerate(Material.class, v.substring(0));
						if(m == null) error = true;
						else not.add(m);
					} else {
						Material m = RF.enumerate(Material.class, v);
						if(m == null) error = true;
						else and.add(m);
					}
				}
			}
			List<IMaterial> ms = new ArrayList<>();
			if(solid == true) ms.add(IMaterial.solid());
			if(and.isEmpty() == false) ms.add(IMaterial.of(and));
			if(not.isEmpty() == false) ms.add(IMaterial.not(not));
			if(ms.isEmpty() == true) error = true;
			else return IMaterial.of(ms);
		}
		if(error == true) Text.failure("Unable to read " + requirement + " requirement value for #0, ignoring!",
					type.name());
		return IMaterial.empty;
	}
	
	private void put(SpawnerType type, String light, Object environment, Object ground) {
		if(type.exists() == true) put(type.name(), light, environment, ground);
	}
	
	private void put(String type, String light, Object environment, Object ground) {
		file.addDefault("Requirements." + type + ".light", light);
		file.addDefault("Requirements." + type + ".environment", environment);
		file.addDefault("Requirements." + type + ".ground", ground);
	}

}
