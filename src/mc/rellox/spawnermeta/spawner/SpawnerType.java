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

	ALLAY(_e("ALLAY"), "Allay", _m("ALLAY_SPAWN_EGG"), EntityBox.single()),
	ARMOR_STAND(EntityType.ARMOR_STAND, "Armor Stand", Material.ARMOR_STAND, true, EntityBox.box(1, 2, 1)),
	AXOLOTL(_e("AXOLOTL"), "Axolotl", _m("AXOLOTL_SPAWN_EGG"), EntityBox.single()),
	BAT(EntityType.BAT, "Bat", Material.BAT_SPAWN_EGG, EntityBox.single()),
	BEE(_e("BEE"), "Bee", _m("BEE_SPAWN_EGG"), EntityBox.single()),
	BLAZE(EntityType.BLAZE, "Blaze", Material.BLAZE_SPAWN_EGG, EntityBox.box(1, 2, 1)),
	BOAT(EntityType.BOAT, "Boat", Material.OAK_BOAT, true, EntityBox.box(2, 1, 2)),
	CAT(_e("CAT"), "Cat", _m("CAT_SPAWN_EGG"), EntityBox.single()),
	CAVE_SPIDER(EntityType.CAVE_SPIDER, "Cave Spider", Material.CAVE_SPIDER_SPAWN_EGG, EntityBox.single()),
	CHEST_BOAT(_e("CHEST_BOAT"), "Boat with Chest", null, EntityBox.box(2, 1, 2)),
	CHICKEN(EntityType.CHICKEN, "Chicken", Material.CHICKEN_SPAWN_EGG, EntityBox.single()),
	COD(EntityType.COD, "Cod", Material.COD_SPAWN_EGG, EntityBox.single()),
	COW(EntityType.COW, "Cow", Material.COW_SPAWN_EGG, EntityBox.box(1, 2, 1)),
	CREEPER(EntityType.CREEPER, "Creeper", Material.CREEPER_SPAWN_EGG, EntityBox.box(1, 2, 1)),
	DOLPHIN(EntityType.DOLPHIN, "Dolphin", Material.DOLPHIN_SPAWN_EGG, EntityBox.single()),
	DONKEY(EntityType.DONKEY, "Donkey", Material.DONKEY_SPAWN_EGG, EntityBox.box(1, 2, 1)),
	DROWNED(EntityType.DROWNED, "Drowned", Material.DROWNED_SPAWN_EGG, EntityBox.box(1, 2, 1)),
	ELDER_GUARDIAN(EntityType.ELDER_GUARDIAN, "Elder Guardian", Material.ELDER_GUARDIAN_SPAWN_EGG, EntityBox.box(2, 2, 2)),
	ENDERMAN(EntityType.ENDERMAN, "Enderman", Material.ENDERMAN_SPAWN_EGG, EntityBox.box(1, 3, 1)),
	ENDERMITE(EntityType.ENDERMITE, "Endermite", Material.ENDERMITE_SPAWN_EGG, EntityBox.single()),
	ENDER_DRAGON(EntityType.ENDER_DRAGON, "Ender Dragon", null, EntityBox.box(16, 8, 16)),
	EVOKER(EntityType.EVOKER, "Evoker", Material.EVOKER_SPAWN_EGG, EntityBox.box(1, 2, 1)),
	EXPERIENCE_BOTTLE(_e("THROWN_EXP_BOTTLE", "EXPERIENCE_BOTTLE"), "Experience Bottle", Material.EXPERIENCE_BOTTLE, true, EntityBox.single()),
	EXPERIENCE_ORB(EntityType.EXPERIENCE_ORB, "Experience Orb", null, EntityBox.single()),
	FOX(_e("FOX"), "Fox", _m("FOX_SPAWN_EGG"), EntityBox.single()),
	FROG(_e("FROG"), "Frog", _m("FROG_SPAWN_EGG"), EntityBox.single()),
	GHAST(EntityType.GHAST, "Ghast", Material.GHAST_SPAWN_EGG, EntityBox.box(4, 4, 4)),
	GIANT(EntityType.GIANT, "Giant", null, EntityBox.box(4, 12, 4)),
	GLOW_SQUID(_e("GLOW_SQUID"), "Glow Squid", _m("GLOW_SQUID_SPAWN_EGG"), EntityBox.single()),
	GOAT(_e("GOAT"), "Goat", _m("GOAT_SPAWN_EGG"), EntityBox.box(1, 2, 1)),
	GUARDIAN(EntityType.GUARDIAN, "Guardian", Material.GUARDIAN_SPAWN_EGG, EntityBox.single()),
	HOGLIN(_e("HOGLIN"), "Hoglin", _m("HOGLIN_SPAWN_EGG"), EntityBox.box(2, 2, 2)),
	HORSE(EntityType.HORSE, "Horse", Material.HORSE_SPAWN_EGG, EntityBox.box(1, 2, 1)),
	HUSK(EntityType.HUSK, "Husk", Material.HUSK_SPAWN_EGG, EntityBox.box(1, 2, 1)),
	ILLUSIONER(EntityType.ILLUSIONER, "Illusioner", null, EntityBox.box(1, 2, 1)),
	IRON_GOLEM(EntityType.IRON_GOLEM, "Iron Golem", null, EntityBox.box(2, 3, 2)),
	LLAMA(EntityType.LLAMA, "Llama", Material.LLAMA_SPAWN_EGG, EntityBox.box(1, 2, 1)),
	MAGMA_CUBE(EntityType.MAGMA_CUBE, "Magma Cube", Material.MAGMA_CUBE_SPAWN_EGG, EntityBox.single()),
	MINECART(EntityType.MINECART, "Minecart", Material.MINECART, true, EntityBox.single()),
	MINECART_CHEST(EntityType.MINECART_CHEST, "Minecart with Chest", Material.CHEST_MINECART, true, EntityBox.single()),
	MINECART_COMMAND(EntityType.MINECART_COMMAND, "Minecart with Command Block", Material.COMMAND_BLOCK_MINECART, true, EntityBox.single()),
	MINECART_FURNACE(EntityType.MINECART_FURNACE, "Minecart with Furnace", Material.FURNACE_MINECART, true, EntityBox.single()),
	MINECART_HOPPER(EntityType.MINECART_HOPPER, "Minecart with Hopper", Material.HOPPER_MINECART, true, EntityBox.single()),
	MINECART_SPAWNER(EntityType.MINECART_MOB_SPAWNER, "Minecart with Spawner", null, EntityBox.single()),
	MINECART_TNT(EntityType.MINECART_TNT, "Minecart with TNT", Material.TNT_MINECART, true, EntityBox.single()),
	MULE(EntityType.MULE, "Mule", Material.MULE_SPAWN_EGG, EntityBox.box(1, 2, 1)),
	MUSHROOM_COW(EntityType.MUSHROOM_COW, "Mushroom Cow", Material.MOOSHROOM_SPAWN_EGG, EntityBox.box(1, 2, 1)),
	OCELOT(EntityType.OCELOT, "Ocelot", Material.OCELOT_SPAWN_EGG, EntityBox.single()),
	PANDA(_e("PANDA"), "Panda", _m("PANDA_SPAWN_EGG"), EntityBox.box(2, 2, 2)),
	PARROT(EntityType.PARROT, "Parrot", Material.PARROT_SPAWN_EGG, EntityBox.single()),
	PHANTOM(EntityType.PHANTOM, "Phantom", Material.PHANTOM_SPAWN_EGG, EntityBox.single()),
	PIG(EntityType.PIG, "Pig", Material.PIG_SPAWN_EGG, EntityBox.single()),
	PIGLIN(_e("PIGLIN"), "Piglin", _m("PIGLIN_SPAWN_EGG"), EntityBox.box(1, 2, 1)),
	PIGLIN_BRUTE(_e("PIGLIN_BRUTE"), "Piglin Brute", _m("PIGLIN_BRUTE_SPAWN_EGG"), EntityBox.box(1, 2, 1)),
	PIG_ZOMBIE(_e("PIG_ZOMBIE"), "Pig Zombie", _m("ZOMBIE_PIGMAN_SPAWN_EGG"), EntityBox.box(1, 2, 1)),
	PILLAGER(_e("PILLAGER"), "Pillager", _m("PILLAGER_SPAWN_EGG"), EntityBox.box(1, 2, 1)),
	POLAR_BEAR(EntityType.POLAR_BEAR, "Polar Bear", Material.POLAR_BEAR_SPAWN_EGG, EntityBox.box(2, 2, 2)),
	PUFFERFISH(EntityType.PUFFERFISH, "Pufferfish", Material.PUFFERFISH_SPAWN_EGG, EntityBox.single()),
	RABBIT(EntityType.RABBIT, "Rabbit", Material.RABBIT_SPAWN_EGG, EntityBox.single()),
	RAVAGER(_e("RAVAGER"), "Ravager", _m("RAVAGER_SPAWN_EGG"), EntityBox.box(2, 3, 2)),
	SALMON(EntityType.SALMON, "Salmon", Material.SALMON_SPAWN_EGG, EntityBox.single()),
	SHEEP(EntityType.SHEEP, "Sheep", Material.SHEEP_SPAWN_EGG, EntityBox.box(1, 2, 1)),
	SHULKER(EntityType.SHULKER, "Shulker", Material.SHULKER_SPAWN_EGG, EntityBox.single()),
	SILVERFISH(EntityType.SILVERFISH, "Silverfish", Material.SILVERFISH_SPAWN_EGG, EntityBox.single()),
	SKELETON(EntityType.SKELETON, "Skeleton", Material.SKELETON_SPAWN_EGG, EntityBox.box(1, 2, 1)),
	SKELETON_HORSE(EntityType.SKELETON_HORSE, "Skeleton Horse", Material.SKELETON_HORSE_SPAWN_EGG, EntityBox.box(1, 2, 1)),
	SLIME(EntityType.SLIME, "Slime", Material.SLIME_SPAWN_EGG, EntityBox.single()),
	SNOWMAN(EntityType.SNOWMAN, "Snowman", null, EntityBox.box(1, 2, 1)),
	SPIDER(EntityType.SPIDER, "Spider", Material.SPIDER_SPAWN_EGG, EntityBox.box(2, 1, 2)),
	SQUID(EntityType.SQUID, "Squid", Material.SQUID_SPAWN_EGG, EntityBox.single()),
	STRAY(EntityType.STRAY, "Stray", Material.STRAY_SPAWN_EGG, EntityBox.box(1, 2, 1)),
	STRIDER(_e("STRIDER"), "Strider", _m("STRIDER_SPAWN_EGG"), EntityBox.box(1, 2, 1)),
	TADPOLE(_e("TADPOLE"), "Tadpole", _m("TADPOLE_SPAWN_EGG"), EntityBox.single()),
	TRADER_LLAMA(_e("TRADER_LLAMA"), "Trader Llama", _m("TRADER_LLAMA_SPAWN_EGG"), EntityBox.box(1, 2, 1)),
	TROPICAL_FISH(EntityType.TROPICAL_FISH, "Tropical Fish", Material.TROPICAL_FISH_SPAWN_EGG, EntityBox.single()),
	TURTLE(EntityType.TURTLE, "Turtle", Material.TURTLE_SPAWN_EGG, EntityBox.box(2, 1, 2)),
	VEX(EntityType.VEX, "Vex", Material.VEX_SPAWN_EGG, EntityBox.single()),
	VILLAGER(EntityType.VILLAGER, "Villager", Material.VILLAGER_SPAWN_EGG, EntityBox.box(1, 2, 1)),
	VINDICATOR(EntityType.VINDICATOR, "Vindicator", Material.VINDICATOR_SPAWN_EGG, EntityBox.box(1, 2, 1)),
	WANDERING_TRADER(_e("WANDERING_TRADER"), "Wandering Trader", _m("WANDERING_TRADER_SPAWN_EGG"), EntityBox.box(1, 2, 1)),
	WARDEN(_e("WARDEN"), "Warden", _m("WARDEN_SPAWN_EGG"), EntityBox.box(1, 3, 1)),
	WITCH(EntityType.WITCH, "Witch", Material.WITCH_SPAWN_EGG, EntityBox.box(1, 2, 1)),
	WITHER(EntityType.WITHER, "Wither", null, EntityBox.box(1, 4, 1)),
	WITHER_SKELETON(EntityType.WITHER_SKELETON, "Wither Skeleton", Material.WITHER_SKELETON_SPAWN_EGG, EntityBox.box(1, 3, 1)),
	WOLF(EntityType.WOLF, "Wolf", Material.WOLF_SPAWN_EGG, EntityBox.single()),
	ZOGLIN(_e("ZOGLIN"), "Zoglin", _m("ZOGLIN_SPAWN_EGG"), EntityBox.box(2, 2, 2)),
	ZOMBIE(EntityType.ZOMBIE, "Zombie", Material.ZOMBIE_SPAWN_EGG, EntityBox.box(1, 2, 1)),
	ZOMBIE_HORSE(EntityType.ZOMBIE_HORSE, "Zombie Horse", Material.ZOMBIE_HORSE_SPAWN_EGG, EntityBox.box(1, 2, 1)),
	ZOMBIE_VILLAGER(EntityType.ZOMBIE_VILLAGER, "Zombie Villager", Material.ZOMBIE_VILLAGER_SPAWN_EGG, EntityBox.box(1, 2, 1)),
	ZOMBIFIED_PIGLIN(_e("ZOMBIFIED_PIGLIN"), "Zombified Piglin", _m("ZOMBIFIED_PIGLIN_SPAWN_EGG"), EntityBox.box(1, 2, 1));

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
	
	private static EntityType _e(String... ns) {
		return ns.length == 1 ? RF.enumerate(EntityType.class, ns[0])
				: RF.enumerates(EntityType.class, ns);
	}
	
	private static Material _m(String name) {
		return RF.enumerate(Material.class, name);
	}

}
