package mc.rellox.spawnermeta.spawner;

import java.util.stream.Stream;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;

import mc.rellox.spawnermeta.configuration.Language;
import mc.rellox.spawnermeta.configuration.Settings;
import mc.rellox.spawnermeta.text.content.Content;
import mc.rellox.spawnermeta.utils.EntityBox;
import mc.rellox.spawnermeta.utils.Reflections.RF;

public enum SpawnerType {
	
	EMPTY(EntityType.AREA_EFFECT_CLOUD, "EMPTY", null, true, EntityBox.box(0, 0, 0)),

	ALLAY(RF.enumerate(EntityType.class, "ALLAY"), "Allay", RF.enumerate(Material.class, "ALLAY_SPAWN_EGG"), EntityBox.box(1, 1, 1)),
	ARMOR_STAND(EntityType.ARMOR_STAND, "Armor Stand", Material.ARMOR_STAND, true, EntityBox.box(1, 2, 1)),
	AXOLOTL(RF.enumerate(EntityType.class, "AXOLOTL"), "Axolotl", RF.enumerate(Material.class, "AXOLOTL_SPAWN_EGG"), EntityBox.box(1, 1, 1)),
	BAT(EntityType.BAT, "Bat", Material.BAT_SPAWN_EGG, EntityBox.box(1, 1, 1)),
	BEE(RF.enumerate(EntityType.class, "BEE"), "Bee", RF.enumerate(Material.class, "BEE_SPAWN_EGG"), EntityBox.box(1, 1, 1)),
	BLAZE(EntityType.BLAZE, "Blaze", Material.BLAZE_SPAWN_EGG, EntityBox.box(1, 2, 1)),
	BOAT(EntityType.BOAT, "Boat", Material.OAK_BOAT, true, EntityBox.box(2, 1, 2)),
	CAT(RF.enumerate(EntityType.class, "CAT"), "Cat", RF.enumerate(Material.class, "CAT_SPAWN_EGG"), EntityBox.box(1, 1, 1)),
	CAVE_SPIDER(EntityType.CAVE_SPIDER, "Cave Spider", Material.CAVE_SPIDER_SPAWN_EGG, EntityBox.box(1, 1, 1)),
	CHEST_BOAT(RF.enumerate(EntityType.class, "CHEST_BOAT"), "Boat with Chest", null, EntityBox.box(2, 1, 2)),
	CHICKEN(EntityType.CHICKEN, "Chicken", Material.CHICKEN_SPAWN_EGG, EntityBox.box(1, 1, 1)),
	COD(EntityType.COD, "Cod", Material.COD_SPAWN_EGG, EntityBox.box(1, 1, 1)),
	COW(EntityType.COW, "Cow", Material.COW_SPAWN_EGG, EntityBox.box(1, 2, 1)),
	CREEPER(EntityType.CREEPER, "Creeper", Material.CREEPER_SPAWN_EGG, EntityBox.box(1, 2, 1)),
	DOLPHIN(EntityType.DOLPHIN, "Dolphin", Material.DOLPHIN_SPAWN_EGG, EntityBox.box(1, 1, 1)),
	DONKEY(EntityType.DONKEY, "Donkey", Material.DONKEY_SPAWN_EGG, EntityBox.box(1, 2, 1)),
	DROWNED(EntityType.DROWNED, "Drowned", Material.DROWNED_SPAWN_EGG, EntityBox.box(1, 2, 1)),
	ELDER_GUARDIAN(EntityType.ELDER_GUARDIAN, "Elder Guardian", Material.ELDER_GUARDIAN_SPAWN_EGG, EntityBox.box(2, 2, 2)),
	ENDERMAN(EntityType.ENDERMAN, "Enderman", Material.ENDERMAN_SPAWN_EGG, EntityBox.box(1, 3, 1)),
	ENDERMITE(EntityType.ENDERMITE, "Endermite", Material.ENDERMITE_SPAWN_EGG, EntityBox.box(1, 1, 1)),
	ENDER_DRAGON(EntityType.ENDER_DRAGON, "Ender Dragon", null, EntityBox.box(16, 8, 16)),
	EVOKER(EntityType.EVOKER, "Evoker", Material.EVOKER_SPAWN_EGG, EntityBox.box(1, 2, 1)),
	EXPERIENCE_BOTTLE(RF.enumerates(EntityType.class, "THROWN_EXP_BOTTLE", "EXPERIENCE_BOTTLE"), "Experience Bottle", Material.EXPERIENCE_BOTTLE, true, EntityBox.box(1, 1, 1)),
	EXPERIENCE_ORB(EntityType.EXPERIENCE_ORB, "Experience Orb", null, EntityBox.box(1, 1, 1)),
	FOX(RF.enumerate(EntityType.class, "FOX"), "Fox", RF.enumerate(Material.class, "FOX_SPAWN_EGG"), EntityBox.box(1, 1, 1)),
	FROG(RF.enumerate(EntityType.class, "FROG"), "Frog", RF.enumerate(Material.class, "FROG_SPAWN_EGG"), EntityBox.box(1, 1, 1)),
	GHAST(EntityType.GHAST, "Ghast", Material.GHAST_SPAWN_EGG, EntityBox.box(4, 4, 4)),
	GIANT(EntityType.GIANT, "Giant", null, EntityBox.box(4, 12, 4)),
	GLOW_SQUID(RF.enumerate(EntityType.class, "GLOW_SQUID"), "Glow Squid", RF.enumerate(Material.class, "GLOW_SQUID_SPAWN_EGG"), EntityBox.box(1, 1, 1)),
	GOAT(RF.enumerate(EntityType.class, "GOAT"), "Goat", RF.enumerate(Material.class, "GOAT_SPAWN_EGG"), EntityBox.box(1, 2, 1)),
	GUARDIAN(EntityType.GUARDIAN, "Guardian", Material.GUARDIAN_SPAWN_EGG, EntityBox.box(1, 1, 1)),
	HOGLIN(RF.enumerate(EntityType.class, "HOGLIN"), "Hoglin", RF.enumerate(Material.class, "HOGLIN_SPAWN_EGG"), EntityBox.box(2, 2, 2)),
	HORSE(EntityType.HORSE, "Horse", Material.HORSE_SPAWN_EGG, EntityBox.box(1, 2, 1)),
	HUSK(EntityType.HUSK, "Husk", Material.HUSK_SPAWN_EGG, EntityBox.box(1, 2, 1)),
	ILLUSIONER(EntityType.ILLUSIONER, "Illusioner", null, EntityBox.box(1, 2, 1)),
	IRON_GOLEM(EntityType.IRON_GOLEM, "Iron Golem", null, EntityBox.box(2, 3, 2)),
	LLAMA(EntityType.LLAMA, "Llama", Material.LLAMA_SPAWN_EGG, EntityBox.box(1, 2, 1)),
	MAGMA_CUBE(EntityType.MAGMA_CUBE, "Magma Cube", Material.MAGMA_CUBE_SPAWN_EGG, EntityBox.box(1, 1, 1)),
	MINECART(EntityType.MINECART, "Minecart", Material.MINECART, true, EntityBox.box(1, 1, 1)),
	MINECART_CHEST(EntityType.MINECART_CHEST, "Minecart with Chest", Material.CHEST_MINECART, true, EntityBox.box(1, 1, 1)),
	MINECART_COMMAND(EntityType.MINECART_COMMAND, "Minecart with Command Block", Material.COMMAND_BLOCK_MINECART, true, EntityBox.box(1, 1, 1)),
	MINECART_FURNACE(EntityType.MINECART_FURNACE, "Minecart with Furnace", Material.FURNACE_MINECART, true, EntityBox.box(1, 1, 1)),
	MINECART_HOPPER(EntityType.MINECART_HOPPER, "Minecart with Hopper", Material.HOPPER_MINECART, true, EntityBox.box(1, 1, 1)),
	MINECART_SPAWNER(EntityType.MINECART_MOB_SPAWNER, "Minecart with Spawner", null, EntityBox.box(1, 1, 1)),
	MINECART_TNT(EntityType.MINECART_TNT, "Minecart with TNT", Material.TNT_MINECART, true, EntityBox.box(1, 1, 1)),
	MULE(EntityType.MULE, "Mule", Material.MULE_SPAWN_EGG, EntityBox.box(1, 2, 1)),
	MUSHROOM_COW(EntityType.MUSHROOM_COW, "Mushroom Cow", Material.MOOSHROOM_SPAWN_EGG, EntityBox.box(1, 2, 1)),
	OCELOT(EntityType.OCELOT, "Ocelot", Material.OCELOT_SPAWN_EGG, EntityBox.box(1, 1, 1)),
	PANDA(RF.enumerate(EntityType.class, "PANDA"), "Panda", RF.enumerate(Material.class, "PANDA_SPAWN_EGG"), EntityBox.box(2, 2, 2)),
	PARROT(EntityType.PARROT, "Parrot", Material.PARROT_SPAWN_EGG, EntityBox.box(1, 1, 1)),
	PHANTOM(EntityType.PHANTOM, "Phantom", Material.PHANTOM_SPAWN_EGG, EntityBox.box(1, 1, 1)),
	PIG(EntityType.PIG, "Pig", Material.PIG_SPAWN_EGG, EntityBox.box(1, 1, 1)),
	PIGLIN(RF.enumerate(EntityType.class, "PIGLIN"), "Piglin", RF.enumerate(Material.class, "PIGLIN_SPAWN_EGG"), EntityBox.box(1, 2, 1)),
	PIGLIN_BRUTE(RF.enumerate(EntityType.class, "PIGLIN_BRUTE"), "Piglin Brute", RF.enumerate(Material.class, "PIGLIN_BRUTE_SPAWN_EGG"), EntityBox.box(1, 2, 1)),
	PIG_ZOMBIE(RF.enumerate(EntityType.class, "PIG_ZOMBIE"), "Pig Zombie", RF.enumerate(Material.class, "ZOMBIE_PIGMAN_SPAWN_EGG"), EntityBox.box(1, 2, 1)),
	PILLAGER(RF.enumerate(EntityType.class, "PILLAGER"), "Pillager", RF.enumerate(Material.class, "PILLAGER_SPAWN_EGG"), EntityBox.box(1, 2, 1)),
	POLAR_BEAR(EntityType.POLAR_BEAR, "Polar Bear", Material.POLAR_BEAR_SPAWN_EGG, EntityBox.box(2, 2, 2)),
	PUFFERFISH(EntityType.PUFFERFISH, "Pufferfish", Material.PUFFERFISH_SPAWN_EGG, EntityBox.box(1, 1, 1)),
	RABBIT(EntityType.RABBIT, "Rabbit", Material.RABBIT_SPAWN_EGG, EntityBox.box(1, 1, 1)),
	RAVAGER(RF.enumerate(EntityType.class, "RAVAGER"), "Ravager", RF.enumerate(Material.class, "RAVAGER_SPAWN_EGG"), EntityBox.box(2, 3, 2)),
	SALMON(EntityType.SALMON, "Salmon", Material.SALMON_SPAWN_EGG, EntityBox.box(1, 1, 1)),
	SHEEP(EntityType.SHEEP, "Sheep", Material.SHEEP_SPAWN_EGG, EntityBox.box(1, 2, 1)),
	SHULKER(EntityType.SHULKER, "Shulker", Material.SHULKER_SPAWN_EGG, EntityBox.box(1, 1, 1)),
	SILVERFISH(EntityType.SILVERFISH, "Silverfish", Material.SILVERFISH_SPAWN_EGG, EntityBox.box(1, 1, 1)),
	SKELETON(EntityType.SKELETON, "Skeleton", Material.SKELETON_SPAWN_EGG, EntityBox.box(1, 2, 1)),
	SKELETON_HORSE(EntityType.SKELETON_HORSE, "Skeleton Horse", Material.SKELETON_HORSE_SPAWN_EGG, EntityBox.box(1, 2, 1)),
	SLIME(EntityType.SLIME, "Slime", Material.SLIME_SPAWN_EGG, EntityBox.box(1, 1, 1)),
	SNOWMAN(EntityType.SNOWMAN, "Snowman", null, EntityBox.box(1, 2, 1)),
	SPIDER(EntityType.SPIDER, "Spider", Material.SPIDER_SPAWN_EGG, EntityBox.box(2, 1, 2)),
	SQUID(EntityType.SQUID, "Squid", Material.SQUID_SPAWN_EGG, EntityBox.box(1, 1, 1)),
	STRAY(EntityType.STRAY, "Stray", Material.STRAY_SPAWN_EGG, EntityBox.box(1, 2, 1)),
	STRIDER(RF.enumerate(EntityType.class, "STRIDER"), "Strider", RF.enumerate(Material.class, "STRIDER_SPAWN_EGG"), EntityBox.box(1, 2, 1)),
	TADPOLE(RF.enumerate(EntityType.class, "TADPOLE"), "Tadpole", RF.enumerate(Material.class, "TADPOLE_SPAWN_EGG"), EntityBox.box(1, 1, 1)),
	TRADER_LLAMA(RF.enumerate(EntityType.class, "TRADER_LLAMA"), "Trader Llama", RF.enumerate(Material.class, "TRADER_LLAMA_SPAWN_EGG"), EntityBox.box(1, 2, 1)),
	TROPICAL_FISH(EntityType.TROPICAL_FISH, "Tropical Fish", Material.TROPICAL_FISH_SPAWN_EGG, EntityBox.box(1, 1, 1)),
	TURTLE(EntityType.TURTLE, "Turtle", Material.TURTLE_SPAWN_EGG, EntityBox.box(2, 1, 2)),
	VEX(EntityType.VEX, "Vex", Material.VEX_SPAWN_EGG, EntityBox.box(1, 1, 1)),
	VILLAGER(EntityType.VILLAGER, "Villager", Material.VILLAGER_SPAWN_EGG, EntityBox.box(1, 2, 1)),
	VINDICATOR(EntityType.VINDICATOR, "Vindicator", Material.VINDICATOR_SPAWN_EGG, EntityBox.box(1, 2, 1)),
	WANDERING_TRADER(RF.enumerate(EntityType.class, "WANDERING_TRADER"), "Wandering Trader", RF.enumerate(Material.class, "WANDERING_TRADER_SPAWN_EGG"), EntityBox.box(1, 2, 1)),
	WARDEN(RF.enumerate(EntityType.class, "WARDEN"), "Warden", RF.enumerate(Material.class, "WARDEN_SPAWN_EGG"), EntityBox.box(1, 3, 1)),
	WITCH(EntityType.WITCH, "Witch", Material.WITCH_SPAWN_EGG, EntityBox.box(1, 2, 1)),
	WITHER(EntityType.WITHER, "Wither", null, EntityBox.box(1, 4, 1)),
	WITHER_SKELETON(EntityType.WITHER_SKELETON, "Wither Skeleton", Material.WITHER_SKELETON_SPAWN_EGG, EntityBox.box(1, 3, 1)),
	WOLF(EntityType.WOLF, "Wolf", Material.WOLF_SPAWN_EGG, EntityBox.box(1, 1, 1)),
	ZOGLIN(RF.enumerate(EntityType.class, "ZOGLIN"), "Zoglin", RF.enumerate(Material.class, "ZOGLIN_SPAWN_EGG"), EntityBox.box(2, 2, 2)),
	ZOMBIE(EntityType.ZOMBIE, "Zombie", Material.ZOMBIE_SPAWN_EGG, EntityBox.box(1, 2, 1)),
	ZOMBIE_HORSE(EntityType.ZOMBIE_HORSE, "Zombie Horse", Material.ZOMBIE_HORSE_SPAWN_EGG, EntityBox.box(1, 2, 1)),
	ZOMBIE_VILLAGER(EntityType.ZOMBIE_VILLAGER, "Zombie Villager", Material.ZOMBIE_VILLAGER_SPAWN_EGG, EntityBox.box(1, 2, 1)),
	ZOMBIFIED_PIGLIN(RF.enumerate(EntityType.class, "ZOMBIFIED_PIGLIN"), "Zombified Piglin", RF.enumerate(Material.class, "ZOMBIFIED_PIGLIN_SPAWN_EGG"), EntityBox.box(1, 2, 1));

	private final EntityType type;
	private final String name;
	private boolean unique;
	private final Material changer;
	private final EntityBox box;

	SpawnerType(EntityType type, String name, Material changer, EntityBox box) {
		this.type = type;
		this.name = name;
		this.changer = changer;
		this.box = box;
		SpawnerManager.EGGS.put(changer, this);
	}
	
	SpawnerType(EntityType type, String name, Material changer, boolean unique, EntityBox box) {
		this(type, name, changer, box);
		this.unique = unique;
	}
	
	public boolean exists() {
		return type != null;
	}
	
	public boolean regular() {
		return exists() == true && this != EMPTY;
	}

	public boolean equals(String name) {
		return name.equalsIgnoreCase(name);
	}

	public boolean equals(EntityType type) {
		return this.type == null ? false : this.type.equals(type);
	}
	
	public EntityBox box() {
		return box;
	}
	
	public boolean unique() {
		return unique;
	}

	public EntityType entity() {
		return type;
	}

	public Content text() {
		return Content.of(name);
	}

	public Content formated() {
		return Language.or("Entities.name." + type.name(), text());
	}
	
	public Material changer() {
		return changer;
	}
	
	public boolean disabled() {
		return Settings.settings.disabled(this) == true;
	}
	
	public static SpawnerType of(String name) {
		try {
			SpawnerType type = valueOf(name.toUpperCase());
			return type.exists() == true ? type : null;
		} catch (Exception e) {
			return null;
		}
	}
	
	public static SpawnerType of(EntityType type) {
		return Stream.of(values())
				.filter(SpawnerType::regular)
				.filter(s -> s.equals(type))
				.findFirst()
				.orElse(null);
	}

}
