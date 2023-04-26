package mc.rellox.spawnermeta.configuration;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import mc.rellox.spawnermeta.SpawnerMeta;
import mc.rellox.spawnermeta.api.events.SpawnerExplodeEvent.ExplosionType;
import mc.rellox.spawnermeta.items.ItemMatcher;
import mc.rellox.spawnermeta.prices.IncreaseType;
import mc.rellox.spawnermeta.prices.PriceType;
import mc.rellox.spawnermeta.spawner.SpawnerType;
import mc.rellox.spawnermeta.spawner.UpgradeType;
import mc.rellox.spawnermeta.utils.Reflections.RF;
import mc.rellox.spawnermeta.utils.Version;
import mc.rellox.spawnermeta.utils.Version.VersionType;

public class ConfigurationFile {
	
	private File f;
	protected FileConfiguration file;
	
	private final String name;
	
	protected boolean first;
	
	public ConfigurationFile(String name) {
		this.name = name;
	}
	
	protected void initialize() {
		f = new File(SpawnerMeta.instance().getDataFolder(), name + ".yml");
		if(f.getParentFile().exists() == false) f.getParentFile().mkdirs();
		if(f.exists() == false) {
			try {
				first = true;
				f.createNewFile();
			} catch(IOException e) {}
		}
		file = YamlConfiguration.loadConfiguration(f);
		
	}
	
	public List<String> getStringList(String path) {
		return file.getStringList(path);
	}
	
	public String getString(String path) {
		return file.getString(path);
	}
	
	public boolean getBoolean(String path) {
		return file.getBoolean(path);
	}
	
	public int getInteger(String path) {
		return file.getInt(path);
	}
	
	public double getDouble(String path) {
		return file.getDouble(path);
	}
	
	public ItemMatcher getMatcher(String path) {
		return ItemMatcher.from(file, path);
	}
	
	public Set<String> getKeys(String path) {
		ConfigurationSection cs = file.getConfigurationSection(path);
		return cs == null ? new HashSet<>(0) : cs.getKeys(false);
	}
	
	public boolean is(String path) {
		return file.get(path) != null;
	}
	
	public final void hold(String path, Object o) {
		file.set(path, o);
	}
	
	public final void set(String path, Object o) {
		file.set(path, o);
		save();
	}
	
	public final void clear(String path) {
		file.set(path, null);
		save();
	}

	public final void save() {
		try {
			file.save(f);
		} catch(IOException e) {}
	}
	
	protected Commenter commenter() {
		return Version.version.high(VersionType.v_18_1) == true
				? new Commenter() : null;
	}
	
	protected class Commenter {
		
		protected void comment(String path, String... cs) {
			RF.order(file, "setComments", String.class, List.class)
				.invoke(path, List.of(cs));
		}
		
	}
	
	public static final class SettingsFile extends ConfigurationFile {
		
		private static final int version = 1;

		public SettingsFile() {
			super("configuration");
		}
		
		@Override
		protected void initialize() {
			super.initialize();

			file.addDefault("Debug-errors", true);
			
			file.addDefault("Spawners.values.DEFAULT.range", 16);
			file.addDefault("Spawners.values.DEFAULT.delay", 500);
			file.addDefault("Spawners.values.DEFAULT.amount", 4);
			file.addDefault("Spawners.value-increase.DEFAULT.range", 4);
			file.addDefault("Spawners.value-increase.DEFAULT.delay", -75);
			file.addDefault("Spawners.value-increase.DEFAULT.amount", 1);
			
			file.addDefault("Spawners.spawning-type", "SINGLE");
			file.addDefault("Spawners.spawning-radius", 3);
			
			file.addDefault("Spawners.switching", false);
			
			file.addDefault("Spawners.empty.enabled", false);
			file.addDefault("Spawners.empty.destroy-eggs.when-removing", false);
			file.addDefault("Spawners.empty.destroy-eggs.when-breaking", false);
			file.addDefault("Spawners.empty.store-eggs-inside", false);
			file.addDefault("Spawners.empty.egg-removing-verify", false);
			
			file.addDefault("Spawners.disabled-spawners", List.of());
			file.addDefault("Spawners.spawning-particles", true);
			file.addDefault("Spawners.disable-item-spawners", false);
			
			file.addDefault("Spawners.allow-renaming", true);
			
			file.addDefault("Spawners.nearby-entity-limit", 6);
			
			file.addDefault("Spawners.kill-entities-on-spawn", false);
			file.addDefault("Spawners.drop-xp-when-instant-kill", true);
			
			file.addDefault("Spawners.default-slime-size", 0);
			
			file.addDefault("Events.cancel-spawning-event", true);
			file.addDefault("Events.send-spawner-event", false);
			file.addDefault("Events.cancel-break-event", true);
			
			file.addDefault("Items.taking-ticks", 60 * 20);
			file.addDefault("Items.taking-remind-ticks", 30 * 20);

			file.addDefault("Modifiers.holograms.enabled", false);
			file.addDefault("Modifiers.holograms.show-natural", false);
			
			file.addDefault("Modifiers.upgrade-interface.enabled", true);
			UpgradeType.stream().forEach(type -> {
				file.addDefault("Modifiers.upgrades.upgradeable.DEFAULT." + type.lower(), true);
				file.addDefault("Modifiers.upgrades.levels.DEFAULT." + type.lower(), 5);
				file.addDefault("Modifiers.upgrades.prices.DEFAULT." + type.lower(), 100);
				file.addDefault("Modifiers.upgrades.price-increase.DEFAULT." + type.lower(), 50);
			});
			file.addDefault("Modifiers.upgrades.price-increase-type", IncreaseType.ADDITION.name());
			
			file.addDefault("Modifiers.charges.enabled", false);
			file.addDefault("Modifiers.charges.allow-stacking", false);
			file.addDefault("Modifiers.charges.ignore-natural", true);
			file.addDefault("Modifiers.charges.buy-amount.first", 16);
			file.addDefault("Modifiers.charges.buy-amount.second", 128);
			file.addDefault("Modifiers.charges.prices.DEFAULT", 2);
			
			file.addDefault("Modifiers.changing.enabled", false);
			file.addDefault("Modifiers.changing.use-price", false);
			file.addDefault("Modifiers.changing.prices.DEFAULT", 100);
			
			file.addDefault("Modifiers.placing.enabled", true);
			file.addDefault("Modifiers.placing.use-price", false);
			file.addDefault("Modifiers.placing.prices.DEFAULT", 100);
			
			file.addDefault("Modifiers.stacking.enabled", false);
			file.addDefault("Modifiers.stacking.use-price", false);
			file.addDefault("Modifiers.stacking.ticks-per", 5);
			file.addDefault("Modifiers.stacking.prices.DEFAULT", 100);
			file.addDefault("Modifiers.stacking.spawner-limit", 16);
			file.addDefault("Modifiers.stacking.ignore-limit", true);
			
			file.addDefault("Modifiers.breaking.enabled", false);
			file.addDefault("Modifiers.breaking.use-price", false);
			file.addDefault("Modifiers.breaking.prices.DEFAULT", 100);
			file.addDefault("Modifiers.breaking.dropping-chance", 100);
			file.addDefault("Modifiers.breaking.chance-changing.owned", "+0");
			file.addDefault("Modifiers.breaking.chance-changing.not-owned", "+0");
			file.addDefault("Modifiers.breaking.chance-changing.natural", "+0");
			file.addDefault("Modifiers.breaking.drop-on-ground", true);
			file.addDefault("Modifiers.breaking.silk-requirement.enabled", true);
			file.addDefault("Modifiers.breaking.silk-requirement.level", 1);
			file.addDefault("Modifiers.breaking.silk-requirement.break-owned", true);
			file.addDefault("Modifiers.breaking.silk-requirement.break-natural", true);
			file.addDefault("Modifiers.breaking.silk-requirement.destroy-on-fail", true);
			file.addDefault("Modifiers.breaking.enable-durability", false);
			file.addDefault("Modifiers.breaking.durability-to-remove", 1);
			int xp = first ? 20 : 0;
			file.addDefault("Modifiers.breaking.xp-on-failure", xp);
			file.addDefault("Modifiers.breaking.permissions", List.of());
			file.addDefault("Modifiers.breaking.show-owner", false);
			
			Object o = file.get("Modifiers.entity-AI");
			if(o != null) {
				file.set("Modifiers.entity-target", o);
				file.set("Modifiers.entity-AI", null);
			}
			
			file.addDefault("Modifiers.entity-target", true);
			file.addDefault("Modifiers.entity-movement", true);
			file.addDefault("Modifiers.check-spawner-nerf", true);
			file.addDefault("Modifiers.spawn-babies", true);
			file.addDefault("Modifiers.spawn-with-equipment", true);
			file.addDefault("Modifiers.modify-stacked-entities", true);
			
			file.addDefault("Modifiers.safety-limit", 128);
			
			file.addDefault("Modifiers.chunk-limits.enabled", false);
			file.addDefault("Modifiers.chunk-limits.spawner-limit", 16);
			file.addDefault("Modifiers.chunk-limits.entities-in-chuck", 0);
			
			file.addDefault("Modifiers.spawner-item.show-header", true);
			file.addDefault("Modifiers.spawner-item.show-range", true);
			file.addDefault("Modifiers.spawner-item.show-delay", true);
			file.addDefault("Modifiers.spawner-item.show-amount", true);

			file.addDefault("Modifiers.players.owned.ignore-limit", true);
			file.addDefault("Modifiers.players.owned.spawner-limit", 16);
			file.addDefault("Modifiers.players.owned.can-break", true);
			file.addDefault("Modifiers.players.owned.can-stack", true);
			file.addDefault("Modifiers.players.owned.can-change", true);
			file.addDefault("Modifiers.players.owned.can-open", true);
			file.addDefault("Modifiers.players.owned.can-upgrade", true);
			file.addDefault("Modifiers.players.natural.can-break", true);
			file.addDefault("Modifiers.players.natural.can-stack", true);
			file.addDefault("Modifiers.players.natural.can-change", true);
			file.addDefault("Modifiers.players.natural.can-open", true);
			file.addDefault("Modifiers.players.natural.can-upgrade", true);

			file.addDefault("Modifiers.spawnable.enabled", false);
			file.addDefault("Modifiers.spawnable.entity-amount.DEFAULT", 5000);

			List.of(ExplosionType.values())
			.forEach(type -> {
				file.addDefault("Miscellaneous.explosions." + type.name() + ".break-spawners", true);
				file.addDefault("Miscellaneous.explosions." + type.name() + ".drop-spawners", true);
				file.addDefault("Miscellaneous.explosions." + type.name() + ".break-natural-spawners", true);
				file.addDefault("Miscellaneous.explosions." + type.name() + ".drop-natural-spawners", true);
			});

			file.addDefault("Spawner-view.enabled", true);
			file.addDefault("Spawner-view.ignore-entities", 
					Stream.of(SpawnerType.ARMOR_STAND, SpawnerType.BOAT, SpawnerType.EXPERIENCE_BOTTLE,
			SpawnerType.EXPERIENCE_ORB, SpawnerType.MINECART, SpawnerType.MINECART_CHEST,
			SpawnerType.MINECART_COMMAND, SpawnerType.MINECART_FURNACE, SpawnerType.MINECART_HOPPER,
			SpawnerType.MINECART_SPAWNER, SpawnerType.MINECART_TNT)
					.map(SpawnerType::name)
					.toList());
			
			file.addDefault("Commands.spawner-view", "spawnerview");
			file.addDefault("Commands.spawner-shop", "spawnershop");
			file.addDefault("Commands.spawner-drops", "spawnerdrops");
			
			PriceType type = PriceType.EXPERIENCE;
			file.addDefault("Prices.upgrades.price-type", type.name());
			file.addDefault("Prices.upgrades.item.material", Material.GOLD_INGOT.name());
			file.addDefault("Prices.charges.price-type", type.name());
			file.addDefault("Prices.charges.item.material", Material.GOLD_INGOT.name());
			file.addDefault("Prices.shop.price-type", type.name());
			file.addDefault("Prices.shop.item.material", Material.GOLD_INGOT.name());
			file.addDefault("Prices.placing.price-type", type.name());
			file.addDefault("Prices.placing.item.material", Material.GOLD_INGOT.name());
			file.addDefault("Prices.stacking.price-type", type.name());
			file.addDefault("Prices.stacking.item.material", Material.GOLD_INGOT.name());
			file.addDefault("Prices.breaking.price-type", type.name());
			file.addDefault("Prices.breaking.item.material", Material.GOLD_INGOT.name());
			file.addDefault("Prices.changing.price-type", type.name());
			file.addDefault("Prices.changing.item.material", Material.GOLD_INGOT.name());
			
			file.addDefault("Configuration-version", version);
			file.addDefault("Spawner-version", 0);

			file.options().copyDefaults(true);
			
			file.options().header("In this file you can configure all plugin values.\n"
					+ "To reload this file do /sm update configuration");
			file.options().copyHeader(true);
			
			if(first == true) {
				save();
				super.initialize();
			}
			
			Commenter c = commenter();
			if(c != null) {
				c.comment("Spawners.disabled-spawners",
						"List of disabled spawners.",
						"Players will not be able to place, break, change",
						"  interact or do any other modifications",
						"  to disabled spawners.",
						"Disabled spawners do not spawn any entities.");
				c.comment("Spawners.values",
						"Spawner values define spawner upgrade attributes.",
						"  range - required player distance (in blocks)",
						"  delay - spawning delay (in ticks) (1 second = 20 ticks)",
						"  amount - entity amount when spawning");
				c.comment("Spawners.values.DEFAULT",
						"Default spawner upgrade values.",
						"For specific entities:",
						"  <entity>:",
						"    range: <value>",
						"    delay: <value>",
						"    amount: <value>",
						"Replace <entity> with the specific entity name, any",
						"  unset upgrade values will use default ones.");
				c.comment("Spawners.value-increase.DEFAULT",
						"Default spawner upgrade increase values.",
						"For specific entities:",
						"  <entity>:",
						"    range: <value>",
						"    delay: <value> (should be negative)",
						"    amount: <value>",
						"Replace <entity> with the specific entity name, any",
						"  unset upgrade values will use default ones.");
				c.comment("Spawners.spawning-type",
						"Spawner spawning type.",
						"  SINGLE - spawn entities in a single spot.",
						"  SPREAD - spread entities around spawner.");
				c.comment("Spawners.spawning-radius", "Entity spawning radius.");
				c.comment("Spawners.switching",
						"Is spawner switching enabled.",
						"To switch a spawner on or off players must click",
						"  the stat item in spawner upgrading interface.");
				c.comment("Spawners.empty.enabled",
						"Are empty spawner enabled.",
						"An empty spawner can be changed at any time",
						"  with spawn eggs, when broken they will drop",
						"  the empty spawner and used spawn eggs.");
				c.comment("Spawners.empty.destroy-eggs.when-removing",
						"Are eggs destroyed when a player removes them",
						"  from a filled empty spawner.");
				c.comment("Spawners.empty.destroy-eggs.when-breaking",
						"Are eggs destroyed when a player breaks",
						"  a filled empty spawner.");
				c.comment("Spawners.empty.store-eggs-inside",
						"Are eggs kept inside the empty spawner",
						"  when broken.");
				c.comment("Spawners.empty.egg-removing-verify",
						"Enables players to double verify when",
						"  removing eggs from empty spawners.",
						"Useful if",
						"  destroy-eggs:",
						"    when-removing: true");
				c.comment("Spawners.spawning-particles",
						"Should there be particles when",
						"  an entity spawns.");
				c.comment("Spawners.disable-item-spawners",
						"Are item spawners disabled.");
				c.comment("Spawners.allow-renaming",
						"If spawners can be renamed in an anvil.");
				c.comment("Spawners.nearby-entity-limit",
						"If there are this amount of entities",
						"  in a 4 block radius of the spawner",
						"  then no entities will be spawned.");
				c.comment("Spawners.kill-entities-on-spawn",
						"Will entities be killed when they spawn.");
				c.comment("Spawners.drop-xp-when-instant-kill",
						"Will entities drop drop xp when killed.",
						"Only applies when kill-entities-on-spawn",
						"  is set to true.");
				c.comment("Spawners.default-slime-size",
						"What size slimes and magma cubes spawners will spawn.",
						"If the value is 0 then the size will vary (1-3).");
				c.comment("Events.cancel-spawning-event", "Is entity spawning event cancelled.");
				c.comment("Events.send-spawner-event",
						"Is entity spawning event sent for each new entity.",
						"This option might be useful for some plugins.");
				c.comment("Events.cancel-break-event",
						"Is spawner break event cancelled.",
						"Useful for other plugins that register spawner locations.");
				c.comment("Items.taking-ticks",
						"Amount of ticks to get back dropped items using",
						"  /spawnerdrops.",
						"Only works if drop-on-ground is disabled.");
				c.comment("Items.taking-remind-ticks",
						"Amount of ticks when a player gets a reminder to",
						"  collect their spawner items.");
				c.comment("Modifiers.upgrade-interface.enabled",
						"Is upgrade interface (GUI) enabled.",
						"If disabled, players will not be able to open it.");
				c.comment("Modifiers.upgrades.upgradeable.DEFAULT",
						"Default spawner upgradeable upgrades.",
						"For specific entities:",
						"  <entity>:",
						"    range: <true/false>",
						"    delay: <true/false>",
						"    amount: <true/false>",
						"Replace <entity> with the specific entity name, any",
						"  unset upgrade values will use default ones.");
				c.comment("Modifiers.upgrades.levels.DEFAULT",
						"Default spawner upgrade levels.",
						"For specific entities:",
						"  <entity>:",
						"    range: <level>",
						"    delay: <level>",
						"    amount: <level>",
						"Replace <entity> with the specific entity name, any",
						"  unset upgrade values will use default ones.");
				c.comment("Modifiers.upgrades.prices.DEFAULT",
						"Default spawner upgrade prices.",
						"For specific entities:",
						"  <entity>:",
						"    range: <price>",
						"    delay: <price>",
						"    amount: <price>",
						"Replace <entity> with the specific entity name, any",
						"  unset upgrade values will use default ones.");
				c.comment("Modifiers.upgrades.price-increase.DEFAULT",
						"Default spawner upgrade price increases.",
						"For specific entities:",
						"  <entity>:",
						"    range: <increase>",
						"    delay: <increase>",
						"    amount: <increase>",
						"Replace <entity> with the specific entity name, any",
						"  unset upgrade values will use default ones.");
				c.comment("Modifiers.upgrades.price-increase-type",
						"Type of price increase.",
						"ADDITION - added increase to pervious price.",
						"  [ price + increase * level ]",
						"MULTIPLICATION - multuplies pervious price by increase.",
						"  increase value is a percentage (100 = 100%)",
						"  [ price * increase ^ level ]");
				c.comment("Modifiers.charges.enabled",
						"Are charges enabled.",
						"Charges define how many times a spawner can spawn,",
						"  they are purchased by players in game.");
				c.comment("Modifiers.charges.allow-stacking",
						"Will spawners with different charge amount be stacked.",
						"If true, player will be able stack spawners which",
						"  has different amount of charges.",
						"For example, if one spawner has 10 charges and the other 8",
						"  then the stacked spawner will have 9 charges",
						"  (9 charges for each spawner to keep the balance).",
						"Player might lose charges if the stack size and charges does",
						"  not divide equally.");
				c.comment("Modifiers.charges.ignore-natural",
						"Will natural spawners spawn entities even",
						"  if it has no charges.");
				c.comment("Modifiers.charges.buy-amount.first",
						"Amount of charges players can purchase when",
						"  left-clicking.");
				c.comment("Modifiers.charges.buy-amount.second",
						"Amount of charges players can purchase when",
						"  right-clicking.");
				c.comment("Modifiers.charges.prices.DEFAULT",
						"Default price per charge.",
						"For specific entities:",
						"  <entity>: <price>",
						"Replace <entity> with the specific entity name.");
				c.comment("Modifiers.holograms.enabled",
						"Are spawner holograms enabled.",
						"Holograms are rendered over spawners, showing",
						"  their entity type and stack size.");
				c.comment("Modifiers.holograms.show-natural", "Are holograms rendered on natural spawners.");
				c.comment("Modifiers.changing.enabled",
						"Is spawner changing enabled.",
						"To change spawner type a players must shift-right-click",
						"  onto a spawner with a specific spawn egg.");
				c.comment("Modifiers.changing.use-price", "Does changing cost.");
				c.comment("Modifiers.changing.prices.DEFAULT",
						"Default changing price.",
						"For specific entities:",
						"  <entity>: <price>",
						"Replace <entity> with the specific entity name.");
				c.comment("Modifiers.placing.enabled", "Is spawner placing enabled.");
				c.comment("Modifiers.placing.use-price", "Does placing cost.");
				c.comment("Modifiers.placing.prices.DEFAULT",
						"Default placing price.",
						"For specific entities:",
						"  <entity>: <price>",
						"Replace <entity> with the specific entity name.");
				c.comment("Modifiers.stacking.enabled",
						"Is spawner stacking enabled.",
						"To stack a spawner players must shift-right-click",
						"  onto a spawner with the same spawner in their hand.");
				c.comment("Modifiers.stacking.ticks-per",
						"Ticks between each stacking.");
				c.comment("Modifiers.stacking.use-price", "Does stacking cost.");
				c.comment("Modifiers.stacking.prices.DEFAULT",
						"Default stacking price.",
						"For specific entities:",
						"  <entity>: <price>",
						"Replace <entity> with the specific entity name.");
				c.comment("Modifiers.stacking.spawner-limit", "Maximum stack size a spawner can have.");
				c.comment("Modifiers.stacking.ignore-limit", "Is stacking limit ignored.");
				c.comment("Modifiers.breaking.enabled",
						"Will players be checked if they have spawner",
						"  breaking permission (spawnermeta.break).");
				c.comment("Modifiers.breaking.use-price", "Does breaking cost.");
				c.comment("Modifiers.breaking.prices.DEFAULT",
						"Default breaking price.",
						"For specific entities:",
						"  <entity>: <price>",
						"Replace <entity> with the specific entity name.");
				c.comment("Modifiers.breaking.dropping-chance", "Chance of the spawner to be dropped.");
				c.comment("Modifiers.breaking.chance-changing",
						"Breaking chance changing.",
						"Chance changers modifies breaking change for",
						"  owned, not owned and natural spawners.",
						"Value must have '+', '-', '*' or '/'",
						"  in front of a number.",
						"(E.g. '+20', '-12.5', '*1.5', '/2'...)");
				c.comment("Modifiers.breaking.chance-changing.owned",
						"How much will the breaking chance change if",
						"  the player breaks their own spawner.");
				c.comment("Modifiers.breaking.chance-changing.not-owned",
						"How much will the breaking chance change if",
						"  the player breaks other player owned spawner.");
				c.comment("Modifiers.breaking.chance-changing.natural",
						"How much will the breaking chance change if",
						"  the player breaks a natural spawner.");
				c.comment("Modifiers.breaking.drop-on-ground",
						"Should the spawner be dropped on the ground",
						"  or automatically be teleported into player inventory.");
				c.comment("Modifiers.breaking.show-owner",
						"If set to true then when a player fails",
						"  to break another player's owned spawner",
						"  their name will be shown in the chat.");
				c.comment("Modifiers.breaking.permissions",
						"Permissions with specific dropping chance can be created.",
						"Permission layout:",
						"  permissions:",
						"    <name>: <chance>",
						"Replace <name> with the specific permission name.",
						"  [ spawnermeta.breaking.permission.<name> ]");
				c.comment("Modifiers.breaking.silk-requirement.enabled",
						"Is silk touch enchantment required",
						"  to break spawners.");
				c.comment("Modifiers.breaking.silk-requirement.level", "Minumum required silk touch enchantment level.");
				c.comment("Modifiers.breaking.silk-requirement.break-owned", "Can player owned spawners be broken with silk touch.");
				c.comment("Modifiers.breaking.silk-requirement.break-natural", "Can natural spawners be broken with silk touch.");
				c.comment("Modifiers.breaking.silk-requirement.destroy-on-fail",
						"Will the spawner be destroyed if broken",
						"  with a pickaxe that does not have",
						"  silk touch enchantment.");
				c.comment("Modifiers.breaking.can-break-natural", "Are natural spawners breakable.");
				c.comment("Modifiers.breaking.enable-durability", "Is durability loss enabled.");
				c.comment("Modifiers.breaking.xp-on-failure",
						"Amount of xp that is dropped when a player",
						"  fails to break a spawner.");
				c.comment("Modifiers.breaking.durability-to-remove",
						"Durability amount that will be removed",
						"  when a player breaks a spawner.");
				c.comment("Modifiers.entity-target",
						"Does entities target players.",
						"Entities will not attack if this is set to false.");
				c.comment("Modifiers.entity-movement",
						"Does entities have movement.",
						"Entities will not move around if this is set to false.");
				c.comment("Modifiers.check-spawner-nerf",
						"Are spawned entities aware.",
						"If this option is enable then the value",
						"  is taken from the spigot configuration",
						"  option: nerf-spawner-mobs.");
				c.comment("Modifiers.spawn-babies",
						"Are baby entities spawned.",
						"If false then all entities will be adult age.");
				c.comment("Modifiers.spawn-with-equipment",
						"Are entities spawned with equipment.",
						"If false then all entities will not have any items,",
						"  nor will drop any.");
				c.comment("Modifiers.modify-stacked-entities",
						"Will already existing entities be modified.",
						"Entities, such as, WildStacker entities.");
				c.comment("Modifiers.safety-limit",
						"Limit of how many entities can spawn at one time.",
						"To prevent any unexpected server lagging and crashing.");
				c.comment("Modifiers.chunk-limits.enabled",
						"Is spawner limit in chunks enabled.",
						"When enabled, each world chunk will have a limit",
						"  of how many spawners it can have.");
				c.comment("Modifiers.chunk-limits.spawner-limit", "Spawner limit in each chunk.");
				c.comment("Modifiers.chunk-limits.entities-in-chuck",
						"How many entities can be in a chunk,",
						"  before the spawner stops spawning.",
						"Leave it 0 to ignore this limit.");
				c.comment("Modifiers.spawner-item", "All lores that are shown on the spawner item.");
				c.comment("Modifiers.players.owned",
						"Options for player owned spawners.",
						"When a player placed down a spawner, their ID is",
						"  saved on the spawner.");
				c.comment("Modifiers.players.owned.ignore-limit",
						"Is player owner spawner limit enabled.",
						"If false, each player will only have a specific",
						"  amount of spawner they can place.");
				c.comment("Modifiers.players.owned.spawner-limit", "Player owned spawner limit.");
				c.comment("Modifiers.players.owned.ignore-limit", "Is owned spawner limit ignored.");
				c.comment("Modifiers.players.owned.can-break", "Can players break other player owned spawners.");
				c.comment("Modifiers.players.owned.can-stack", "Can players stack other player owned spawners.");
				c.comment("Modifiers.players.owned.can-change", "Can players change other player owned spawners.");
				c.comment("Modifiers.players.owned.can-open", "Can players open other player owned spawners.");
				c.comment("Modifiers.players.owned.can-upgrade", "Can players upgrade other player owned spawners.");
				c.comment("Modifiers.players.natural.can-break", "Can players break natural spawners.");
				c.comment("Modifiers.players.natural.can-stack", "Can players stack natural spawners.");
				c.comment("Modifiers.players.natural.can-change", "Can players change natural spawners.");
				c.comment("Modifiers.players.natural.can-open", "Can players open natural spawners.");
				c.comment("Modifiers.players.natural.can-upgrade", "Can players upgrade natural spawners.");
				c.comment("Modifiers.spawnable.enabled",
						"Is spawnable entity amount enabled.",
						"If true, each spawner will have an amount of how many",
						"  entity it can spawn.",
						"When spawnable entity amount reaches 0 the spawner",
						"  will be destrayed.");
				c.comment("Modifiers.spawnable.entity-amount.DEFAULT",
						"Default spawnable entity amount.",
						"For specific entities:",
						"  <entity>: <amount>",
						"Replace <entity> with the specific entity name.");
				c.comment("Miscellaneous.explosions.TNT", "Breaking options for TNT explosions.");
				c.comment("Miscellaneous.explosions.CREEPERS", "Breaking options for creeper explosions.");
				c.comment("Miscellaneous.explosions.FIREBALLS", "Breaking options for fireball explosions.");
				c.comment("Miscellaneous.explosions.END_CRYSTALS", "Breaking options for end crystal explosions.");
				c.comment("Spawner-view.enabled", "Is spawner view enabled.",
						"Spawner view can be accessed by all players",
						"  using /spawnerview");
				c.comment("Spawner-view.ignore-entities", "Entities that are excluded from spawner view.");
				c.comment("Commands.spawner-view", "Command label for spawner view.");
				c.comment("Commands.spawner-shop", "Command label for spawner shop.");
				c.comment("Commands.spawner-drops", "Command label for spawner drops.");
				c.comment("Prices",
						"Price types:",
						"  EXPERIENCE - experience points",
						"  LEVELS	- experience levels",
						"  ECONOMY - vault economy",
						"  MATERIAL - items",
						"Item format (only for MATERIAL price type):",
						"item:",
						"  material: <material>",
						"  name: <name> (checks item name, optional)",
						"  model: <model> (check item custom model data, optional)");
				c.comment("Prices.upgrades", "Price type for upgrades.");
				c.comment("Prices.charges", "Price type for charges.");
				c.comment("Prices.shop", "Price type for shop.");
				c.comment("Prices.placing", "Price type for placing.");
				c.comment("Prices.stacking", "Price type for stacking.");
				c.comment("Prices.breaking", "Price type for breaking.");
				c.comment("Prices.changing", "Price type for changing.");
				c.comment("Configuration-version", "Version of this configuration file.",
						"Should not be changed.");
				c.comment("Spawner-version", "Version of spawners in the server.",
						"By incrementing this value all spawners in the server",
						"  will be updated. Can also be done",
						"  using /sm update spawners.");
			}
			
			save();
			
			Settings.reload();
		}
		
	}

}
