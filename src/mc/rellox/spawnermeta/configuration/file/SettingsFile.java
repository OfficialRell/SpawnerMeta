package mc.rellox.spawnermeta.configuration.file;

import java.util.List;
import java.util.stream.Stream;

import org.bukkit.Material;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;

import mc.rellox.spawnermeta.api.events.SpawnerExplodeEvent.ExplosionType;
import mc.rellox.spawnermeta.configuration.AbstractFile;
import mc.rellox.spawnermeta.configuration.Configuration.CF;
import mc.rellox.spawnermeta.configuration.Settings;
import mc.rellox.spawnermeta.prices.IncreaseType;
import mc.rellox.spawnermeta.prices.PriceType;
import mc.rellox.spawnermeta.spawner.type.SpawnerType;
import mc.rellox.spawnermeta.spawner.type.UpgradeType;

public class SettingsFile extends AbstractFile {
	
	private static final int version = 11;

	public SettingsFile() {
		super("configuration");
	}

	@Override
	protected void initialize() {
		CF.version = file.getInt("Configuration-version");

		if(CF.version < 2) {
			copy("Modifiers.breaking.enabled", "Modifiers.breaking.ignore-permission");
			delete("Modifiers.spawner-item");
		}
		if(CF.version < 3) {
			copy("Modifiers.holograms.enabled", "Modifiers.holograms.regular.enabled");
			copy("Modifiers.holograms.show-natural", "Modifiers.holograms.regular.show-natural");
			copy("Spawners.nearby-entity-limit", "Spawners.nearby-entities.limit");
		}
		if(CF.version < 4) {
			copy("Modifiers.breaking.permissions", "Modifiers.breaking.chance-permissions");
			int limit = getInteger("Modifiers.stacking.spawner-limit");
			copy("Modifiers.stacking.spawner-limit", "Modifiers.stacking.spawner-limit.natural");
			if(limit <= 0) limit = 16;
			hold("Modifiers.stacking.spawner-limit.owned", limit);
			hold("Modifiers.stacking.spawner-limit.not-owned", limit);
			copy("Commands.spawner-view", "Commands.spawner-view.label");
			copy("Commands.spawner-shop", "Commands.spawner-shop.label");
			copy("Commands.spawner-drops", "Commands.spawner-drops.label");
			copy("Commands.spawner-locations", "Commands.spawner-locations.label");
			delete("Spawner-version");
		}
		if(CF.version < 5) {
			copy("Spawners.checking-ticks", "Spawners.validation-interval");
		}
		if(CF.version < 6) {
			int r = getInteger("Spawners.spawning-radius");
			if(r <= 0) r = 3;
			hold("Spawners.spawning-radius.horizontal", r);
			hold("Spawners.spawning-radius.vertical", r);
		}
		if(CF.version < 8) {
			copy("Spawners.kill-entities-on-spawn", "Spawners.instant-kill.enabled");
			copy("Spawners.drop-xp-when-instant-kill", "Spawners.instant-kill.drop-xp");
		}
		if(CF.version < 10) {
			copy("Spawners.required-redstone-power", "Spawners.redstone-power.required");
		}
		if(CF.version < 11) {
			delete("Spawners.delayed-chunk-loading");
		}

		file.addDefault("Debug-errors", true);
		
		file.addDefault("Spawners.values.DEFAULT.range", 16);
		file.addDefault("Spawners.values.DEFAULT.delay", 500);
		file.addDefault("Spawners.values.DEFAULT.amount", 4);
		file.addDefault("Spawners.value-increase.DEFAULT.range", 4);
		file.addDefault("Spawners.value-increase.DEFAULT.delay", -75);
		file.addDefault("Spawners.value-increase.DEFAULT.amount", 1);
		
		file.addDefault("Spawners.spawning-type", "SPREAD");
		file.addDefault("Spawners.spawning-radius.horizontal", 3);
		file.addDefault("Spawners.spawning-radius.vertical", 3);

		file.addDefault("Spawners.ticking-interval", 1);
		file.addDefault("Spawners.validation-interval", 100);
		file.addDefault("Spawners.checking-interval", 1);
		file.addDefault("Spawners.check-if-present.enabled", true);
		file.addDefault("Spawners.check-if-present.interval", 1200);
		file.addDefault("Spawners.tick-until-zero", false);
		file.addDefault("Spawners.delay-offset", 5);
		
		file.addDefault("Spawners.reset-spawner-values", false);
		
		file.addDefault("Spawners.switching", false);
		
		file.addDefault("Spawners.empty.enabled", false);
		file.addDefault("Spawners.empty.destroy-eggs.when-removing", false);
		file.addDefault("Spawners.empty.destroy-eggs.when-breaking", false);
		file.addDefault("Spawners.empty.store-eggs-inside", false);
		file.addDefault("Spawners.empty.egg-removing-verify", false);
		file.addDefault("Spawners.empty.remove-from-regular", false);
		
		file.addDefault("Spawners.disabled-spawners", List.of());
		file.addDefault("Spawners.ignored-spawners", List.of());
		file.addDefault("Spawners.ignore-natural", false);
		file.addDefault("Spawners.disabled-worlds", List.of());
		file.addDefault("Spawners.ignored-worlds", List.of());
		file.addDefault("Spawners.spawning-particles", true);
		file.addDefault("Spawners.owner-warning-particles", true);
		file.addDefault("Spawners.disable-item-spawners", false);
		file.addDefault("Spawners.warning-particles", true);
		
		file.addDefault("Spawners.spawning-reason", SpawnReason.SPAWNER.name());
		
		file.addDefault("Spawners.allow-renaming", true);
		
		file.addDefault("Spawners.nearby-entities.limit", 8);
		file.addDefault("Spawners.nearby-entities.reduce", false);
		
		file.addDefault("Spawners.instant-kill.enabled", false);
		file.addDefault("Spawners.instant-kill.drop-xp", true);
		file.addDefault("Spawners.instant-kill.death-animation", true);
		
		file.addDefault("Spawners.redstone-power.required", 0);
		file.addDefault("Spawners.redstone-power.ignore-natural", true);
		
		file.addDefault("Spawners.default-slime-size", 0);
		
		file.addDefault("Events.cancel-spawning-event", true);
		file.addDefault("Events.send-spawner-event", false);
		file.addDefault("Events.cancel-break-event", true);
		file.addDefault("Events.ignore-break-event", false);
		file.addDefault("Events.check-island-kick", true);
		
		file.addDefault("Items.taking-ticks", 60 * 20);
		file.addDefault("Items.taking-remind-ticks", 30 * 20);
		
		file.addDefault("Modifiers.holograms.regular.enabled", false);
		file.addDefault("Modifiers.holograms.regular.show-natural", false);
		file.addDefault("Modifiers.holograms.regular.radius", 32);
		file.addDefault("Modifiers.holograms.warning.enabled", true);
		file.addDefault("Modifiers.holograms.warning.radius", 32);
		file.addDefault("Modifiers.holograms.height", 0);
		
		file.addDefault("Modifiers.upgrade-interface.enabled", true);
		UpgradeType.stream().forEach(type -> {
			file.addDefault("Modifiers.upgrades.upgradeable.DEFAULT." + type.lower(), true);
			file.addDefault("Modifiers.upgrades.levels.DEFAULT." + type.lower(), 5);
			file.addDefault("Modifiers.upgrades.prices.DEFAULT." + type.lower(), 100);
			file.addDefault("Modifiers.upgrades.price-increase.DEFAULT." + type.lower(), 50);
		});
		file.addDefault("Modifiers.upgrades.price-increase-type", IncreaseType.ADDITION.name());
		
		file.addDefault("Modifiers.charges.enabled", false);
		file.addDefault("Modifiers.charges.comparision", false);
		file.addDefault("Modifiers.charges.consume.DEFAULT", 1);
		file.addDefault("Modifiers.charges.requires-as-minimum.DEFAULT", 1);
		file.addDefault("Modifiers.charges.allow-stacking", false);
		file.addDefault("Modifiers.charges.ignore-natural", true);
		file.addDefault("Modifiers.charges.buy-amount.first", 16);
		file.addDefault("Modifiers.charges.buy-amount.second", 128);
		file.addDefault("Modifiers.charges.prices.DEFAULT", 2);
		file.addDefault("Modifiers.charges.ignore-levels", false);
		
		file.addDefault("Modifiers.changing.enabled", false);
		file.addDefault("Modifiers.changing.use-price", false);
		file.addDefault("Modifiers.changing.prices.DEFAULT", 100);
		file.addDefault("Modifiers.changing.material-type.EXAMPLE", "IRON_INGOT");
		file.addDefault("Modifiers.changing.deny.from", List.of());
		file.addDefault("Modifiers.changing.deny.to", List.of());
		file.addDefault("Modifiers.changing.reset-upgrades.regular", false);
		file.addDefault("Modifiers.changing.reset-upgrades.empty", false);
		
		file.addDefault("Modifiers.placing.enabled", true);
		file.addDefault("Modifiers.placing.use-price", false);
		file.addDefault("Modifiers.placing.prices.DEFAULT", 100);
		
		file.addDefault("Modifiers.stacking.enabled", false);
		file.addDefault("Modifiers.stacking.use-price", false);
		file.addDefault("Modifiers.stacking.stack-all", false);
		file.addDefault("Modifiers.stacking.ticks-per", 5);
		file.addDefault("Modifiers.stacking.disabled-types", List.of());
		file.addDefault("Modifiers.stacking.prices.DEFAULT", 100);
		file.addDefault("Modifiers.stacking.spawner-limit.natural", 16);
		file.addDefault("Modifiers.stacking.spawner-limit.owned", 16);
		file.addDefault("Modifiers.stacking.ignore-limit", true);
		file.addDefault("Modifiers.stacking.when-nearby.enabled", false);
		file.addDefault("Modifiers.stacking.when-nearby.radius", 8);
		file.addDefault("Modifiers.stacking.when-nearby.particles", true);
		file.addDefault("Modifiers.stacking.limit-permissions.example", 32);
		file.addDefault("Modifiers.stacking.affected-by-permissions.natural", true);
		file.addDefault("Modifiers.stacking.affected-by-permissions.owned", true);
		file.addDefault("Modifiers.stacking.affected-by-permissions.not-owned", false);

		file.addDefault("Modifiers.breaking.unbreakable", false);
		file.addDefault("Modifiers.breaking.ignore-permission", false);
		file.addDefault("Modifiers.breaking.use-price", false);
		file.addDefault("Modifiers.breaking.prices.DEFAULT", 100);
		file.addDefault("Modifiers.breaking.dropping-chance", 100);
		file.addDefault("Modifiers.breaking.chance-changing.owned", "+0");
		file.addDefault("Modifiers.breaking.chance-changing.not-owned", "+0");
		file.addDefault("Modifiers.breaking.chance-changing.natural", "+0");
		file.addDefault("Modifiers.breaking.drop-on-ground", true);
		file.addDefault("Modifiers.breaking.cancel-if-full", false);
		file.addDefault("Modifiers.breaking.silk-requirement.enabled", true);
		file.addDefault("Modifiers.breaking.silk-requirement.level", 1);
		file.addDefault("Modifiers.breaking.silk-requirement.break-owned", true);
		file.addDefault("Modifiers.breaking.silk-requirement.break-natural", true);
		file.addDefault("Modifiers.breaking.silk-requirement.destroy-on-fail", true);
		file.addDefault("Modifiers.breaking.enable-durability", false);
		file.addDefault("Modifiers.breaking.durability-to-remove", 1);
		file.addDefault("Modifiers.breaking.chance-permissions.example", 100);
		
		file.addDefault("Modifiers.breaking.xp-on-failure", isNew() ? 20 : 0);
		file.addDefault("Modifiers.breaking.show-owner", false);

		file.addDefault("Modifiers.silent-entities",
				List.of(SpawnerType.ENDER_DRAGON.name(), SpawnerType.WITHER.name()));
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

		file.addDefault("Modifiers.players.owned.spawn-if-online", false);
		file.addDefault("Modifiers.players.owned.offline-time-limit", 0);
		file.addDefault("Modifiers.players.owned.offline-ignore-list", List.of());
		file.addDefault("Modifiers.players.owned.ignore-limit", true);
		file.addDefault("Modifiers.players.owned.spawner-limit", 16);
		file.addDefault("Modifiers.players.owned.can-break", true);
		file.addDefault("Modifiers.players.owned.can-stack", true);
		file.addDefault("Modifiers.players.owned.can-change", true);
		file.addDefault("Modifiers.players.owned.can-open", true);
		file.addDefault("Modifiers.players.owned.can-upgrade", true);
		file.addDefault("Modifiers.players.owned.limit-permissions.example", 32);
		
		file.addDefault("Modifiers.players.natural.can-break", true);
		file.addDefault("Modifiers.players.natural.can-stack", true);
		file.addDefault("Modifiers.players.natural.can-change", true);
		file.addDefault("Modifiers.players.natural.can-open", true);
		file.addDefault("Modifiers.players.natural.can-upgrade", true);
		file.addDefault("Modifiers.players.trusted.can-break", true);
		file.addDefault("Modifiers.players.trusted.can-stack", true);
		file.addDefault("Modifiers.players.trusted.can-change", true);
		file.addDefault("Modifiers.players.trusted.can-open", true);
		file.addDefault("Modifiers.players.trusted.can-upgrade", true);

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
		
		file.addDefault("Commands.spawner-view.label", "spawnerview");
		file.addDefault("Commands.spawner-view.aliases", List.of());
		file.addDefault("Commands.spawner-shop.label", "spawnershop");
		file.addDefault("Commands.spawner-shop.aliases", List.of());
		file.addDefault("Commands.spawner-drops.label", "spawnerdrops");
		file.addDefault("Commands.spawner-drops.aliases", List.of());
		file.addDefault("Commands.spawner-locations.label", "spawnerlocations");
		file.addDefault("Commands.spawner-locations.aliases", List.of());
		file.addDefault("Commands.spawner-trust.label", "spawnertrust");
		file.addDefault("Commands.spawner-trust.aliases", List.of());
		
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
		
		file.addDefault("Prices.format.use-delimiter", false);
		file.addDefault("Prices.format.delimiter", ",");
		file.addDefault("Prices.format.use-abbreviations", false);
		file.addDefault("Prices.format.abbreviations", List.of("k", "m", "b", "t"));
		
		file.set("Configuration-version", version);

		file.options().copyDefaults(true);
		
		header("In this file you can configure all plugin values.",
				"To reload this file do /sm update configuration");
		
		if(isNew() == true) {
			save();
			create();
		}
		
		Commenter c = commenter();
		if(c != null) {
			c.comment("Spawners.disabled-spawners",
					"List of disabled spawners.",
					"Players will not be able to place, break, change",
					"  interact or do any other modifications",
					"  to disabled spawners.",
					"Disabled spawners do not spawn any entities.");
			c.comment("Spawners.ignored-spawners",
					"List of ignored spawners.",
					"Ignored spawner will stay, work and spawn the same",
					"  as in vanilla Minecraft.");
			c.comment("Spawners.ignore-natural",
					"If true then all natural spawners will not be",
					"  converted as custom ones. They will work as in",
					"  vanilla Minecraft.");
			c.comment("Spawners.disabled-worlds",
					"List of disabled worlds.",
					"In these worlds spawners will not spawn.");
			c.comment("Spawners.ignored-worlds",
					"List of ignored worlds.",
					"In these worlds spawners will work",
					"  as in vanilla Minecraft.");
			c.comment("Spawners.values",
					"Spawner values define spawner upgrade attributes.",
					"  range - required player distance (in blocks)",
					"  delay - spawning delay (in ticks) (1 second = 20 ticks)",
					"  amount - entity amount when spawning",
					"Values can also be set as ranged (2-5, 100-200...).");
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
			c.comment("Spawners.spawning-radius.horizontal", "Horizontal (x and z) entity spawning radius.");
			c.comment("Spawners.spawning-radius.vertical", "Vertical (y) entity spawning radius.");
			c.comment("Spawners.ticking-interval",
					"Amount of ticks between each spawner tick.",
					"Note! This is the main interval, meaning",
					"  other intervals as 'checking-interval'",
					"  and 'validation-interval' depend on this",
					"  interval.",
					"Suggested interval: [1-5]");
			c.comment("Spawners.validation-interval",
					"Interval between each spawner condition",
					"  check and hologram updates.",
					"The smaller the value, the more impact on",
					"  server performance.",
					"Note! This interval will only increase every",
					"  spawner ticking interval, meaning total",
					"  interval ticks are:",
					"    'ticking-interval' * 'validation-interval'",
					"Suggested interval: [20-200]",
					"  (but do not use large values if",
					"  'ticking-interval' is large)");
			c.comment("Spawners.checking-interval",
					"Interval between each spawner validation.",
					"Higher value will increase server performance",
					"  but spawners will be validated less often.",
					"Note! This interval will only increase every",
					"  spawner ticking interval, meaning total",
					"  interval ticks are:",
					"    'ticking-interval' * 'checking-interval'",
					"Suggested interval: [1-20]",
					"  (but do not use large values if",
					"  'ticking-interval' is large)");
			c.comment("Spawners.check-if-present",
					"This checks if the spawner is in a loaded chunk",
					" or is still a spawner type block.");
			c.comment("Spawners.check-if-present.enabled",
					"If spawner checking is enabled.");
			c.comment("Spawners.check-if-present.interval",
					"Interval between each spawner check.",
					"Higher value will increase server performance",
					"  but spawners will be validated less often.");
			c.comment("Spawners.tick-until-zero",
					"If spawner time should tick until it reaches zero.",
					"This will cause the spawner to tick even if",
					"  there are no nearby players, but will spawn",
					"  entities only when a player comes nearby.");
			c.comment("Spawners.delay-offset",
					"Delay offset when the spawner resets its spawn",
					"  delay.",
					"This value is a percentage, meaning by default it",
					"  will offset the delay by 5% of the maximum delay.",
					"Interval [0-99]");
			c.comment("Spawners.reset-spawner-values",
					"If true after spawner unloading all spawner",
					"  values will be reset to vanilla values.",
					"Useful if you want to remove SpawnerMeta and",
					"  make all spawners work as vanilla after.");
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
			c.comment("Spawners.empty.remove-from-regular",
					"Allows players to remove spawn eggs from",
					"  regular spawners, turing the spawner into",
					"  an empty spawner.");
			c.comment("Spawners.spawning-particles",
					"Should there be particles when",
					"  an entity spawns.");
			c.comment("Spawners.disable-item-spawners",
					"If true item spawners will not spawn any items,",
					"  otherwise will spawn as in vanilla.",
					"This plugin does not affect item spawners.");
			c.comment("Spawners.warning-particles",
					"Are warning particles shown.");
			c.comment("Spawners.owner-warning-particles",
					"Should there be particles when the spawner owner",
					"  is offline.",
					"Only work if 'spawn-if-online' is false.");
			c.comment("Spawners.spawning-reason",
					"The entity spawn reason.",
					"Changing this might help with other",
					"  plugins, such as Multiverse.",
					"By default this is set to SPAWNER,",
					"  but setting to CUSTOM might work",
					"  for some plugins.",
					"Possible spawn reasons:",
					"  https://hub.spigotmc.org/javadocs/spigot/org/bukkit/event/entity/CreatureSpawnEvent.SpawnReason.html");
			c.comment("Spawners.allow-renaming",
					"If spawners can be renamed in an anvil.");
			c.comment("Spawners.nearby-entities.limit",
					"Spawner will not spawn any entities if",
					"  the amount of entities in the spawn",
					"  radius has reached this limit.");
			c.comment("Spawners.nearby-entities.reduce",
					"If true then the amount of entities that",
					"  will spawn is reduced not to pass",
					"  the nearby limit.",
					"E.g. if spawning amount is 4 and limit is 8,",
					"  and nearby amount is 6, then the spawning",
					"  amount is reduced to 2.");
			c.comment("Spawners.instant-kill.enabled",
					"Will entities be killed when they spawn.");
			c.comment("Spawners.instant-kill.drop-xp",
					"Will entities drop xp when killed.",
					"Only applies when instant-kill is enabled.",
					"For xp to drop a player has to be in a",
					"  32 block radius.");
			c.comment("Spawners.instant-kill.death-animation",
					"Should the death animation be displayed.");
			c.comment("Spawners.redstone-power.required",
					"The required redstone power for this spawner",
					"  to spawn. [0-15]",
					"Set 0 to ignore.");
			c.comment("Spawners.redstone-power.ignore-natural",
					"If true then natural spawners will not",
					"  require redstone power to spawn.");
			c.comment("Spawners.default-slime-size",
					"What size slimes and magma cubes spawners will spawn.",
					"If the value is 0 then the size will vary (1, 2 or 4).");
			c.comment("Spawners.predicate.if-operator-online",
					"Spawners will only spawn if at least one",
					"  server operator (OP) is online.");
			c.comment("Events.cancel-spawning-event", "Is entity spawning event cancelled.");
			c.comment("Events.send-spawner-event",
					"Is entity spawning event sent for each new entity.",
					"This option might be useful for some plugins.");
			c.comment("Events.cancel-break-event",
					"Should spawner break event be cancelled.",
					"Useful for other plugins that register spawner locations.");
			c.comment("Events.ignore-break-event",
					"Should spawner break event be fully ignored.",
					"This means that SpawnerMeta will not add custom spawner",
					"  breaking and all spawners will break as in vanilla.",
					"Useful for custom plugins that require this event.");
			c.comment("Events.check-island-kick",
					"If true players that are kicked from an island",
					"  will receive all their owned spawner.");
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
			c.comment("Modifiers.charges.comparison",
					"Charge comparison mode.",
					"true  → Uses `<` (the warning is triggered only if the charges are strictly less than the required minimum).",
					"false → Uses `<=` (the warning is triggered if the charges are less than or equal to the required minimum).");
			c.comment("Modifiers.charges.consume.DEFAULT",
					"Amount of charges consumed when a spawner spawns.",
					"For specific entities:",
					"  <entity>: <charges>",
					"Replace <entity> with the specific entity name.",
					"Default: 1 charges consumed for all spawners.",
					"If you want a mob to consume charges, you must write the mob name in uppercase.",
					"Example: COW: 10 (10 charges consumed when the cow spawner spawns)");
			c.comment("Modifiers.charges.requires-as-minimum.DEFAULT",
					"Amount of charges required for the spawner to function.",
					"For specific entities:",
					"  <entity>: <charges> ",
					"Replace <entity> with the specific entity name.",
					"Default: 1 charges required for all spawners.",
					"If you want a mob to require charges, you must write the mob name in uppercase.",
					"Example: COW: 10 (10 charges required for the cow spawner to function)");
			c.comment("Modifiers.charges.allow-stacking",
					"Will spawners with different charge amount be stacked.",
					"If true, players will be able stack spawners which",
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
			c.comment("Modifiers.charges.ignore-levels",
					"If true, price will stay the same and will",
					"  ignore spawner level, otherwise price,",
					"  will be multiplied by the spawner level.");
			c.comment("Modifiers.holograms.regular.enabled",
					"Are spawner holograms enabled.",
					"Holograms are rendered over spawners, showing",
					"  their entity type and stack size.");
			c.comment("Modifiers.holograms.regular.show-natural", "Are holograms rendered on natural spawners.");
			c.comment("Modifiers.holograms.regular.radius", "Regualar hologram radius.");
			c.comment("Modifiers.holograms.warning.enabled",
					"Are warning holograms enabled.",
					"Warnings show if a spawner is unable",
					"  to spawn any entities.");
			c.comment("Modifiers.holograms.warning.radius", "Warning hologram radius.");
			c.comment("Modifiers.holograms.height", "Height of the hologram.");
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
			c.comment("Modifiers.changing.deny.to",
					"List of entity types that cannot be set",
					"  to a spawn.");
			c.comment("Modifiers.changing.deny.from",
					"List of entity types that cannot be changed",
					"  to a different one.");
			c.comment("Modifiers.changing.reset-upgrades",
					"Resets all upgrades back to level 1",
					"  when changing if set to true.",
					"Empty spawners will reset when adding",
					"  or removing eggs.");
			c.comment("Modifiers.changing.material-type",
					"Default changing material type.",
					"For specific entities:",
					"  <entity>: <material>",
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
			c.comment("Modifiers.stacking.disabled-types",
					"List of entities that cannot be stacked",
					"  when stacking spawners.");
			c.comment("Modifiers.stacking.use-price", "Does stacking cost.");
			c.comment("Modifiers.stacking.stack-all",
					"When stacking to a spawner all the items",
					"  in player's hand will be stacked to it.",
					"If false then players will stack spawners",
					"  individually.");
			c.comment("Modifiers.stacking.prices.DEFAULT",
					"Default stacking price.",
					"For specific entities:",
					"  <entity>: <price>",
					"Replace <entity> with the specific entity name.");
			c.comment("Modifiers.stacking.spawner-limit.natural", "Stacking limit for natural spawners.");
			c.comment("Modifiers.stacking.spawner-limit.owned", "Stacking limit for owned spawners.");
			c.comment("Modifiers.stacking.ignore-limit", "Is stacking limit ignored.");
			c.comment("Modifiers.stacking.when-nearby.enabled",
					"Player will be able to place same-type",
					"  spawners near another spawner and",
					"  it will be stacked to it if this",
					"  is set to true.");
			c.comment("Modifiers.stacking.when-nearby.radius",
					"Radius in which the nearest same-type spawner",
					"  will be searched.",
					"Radius interval: [1; 16]");
			c.comment("Modifiers.stacking.when-nearby.particles",
					"If a particle beam to the stacked spawner",
					"  will be shown.");
			c.comment("Modifiers.stacking.limit-permissions",
					"Permissions with specific stacking limits can be created.",
					"Permission layout:",
					"  limit-permissions:",
					"    <name>: <limit>",
					"Replace <name> with the specific permission name.",
					"  [ spawnermeta.stacking.permission.<name> ]");
			c.comment("Modifiers.stacking.limit-permissions.example",
					"This is an example permission, does not work in game.",
					"Player with permission (spawnermeta.stacking.permission.example)",
					"  will be able to stack spawners to 32.");
			c.comment("Modifiers.stacking.affected-by-permissions",
					"These options are only used if limit-permissions are used.");
			c.comment("Modifiers.stacking.affected-by-permissions.natural",
					"Does stacking limit bypass natural spawners.");
			c.comment("Modifiers.stacking.affected-by-permissions.owned",
					"Does stacking limit bypass owned spawners.");
			c.comment("Modifiers.stacking.affected-by-permissions.not-owned",
					"Does stacking limit bypass other player owned spawners.");
			c.comment("Modifiers.breaking.unbreakable",
					"Is this spawner unbreakable.",
					"Players only with permission will",
					"  be able to break spawners.",
					"  (spawnermeta.unbreakable.bypass) - this",
					"  permission is set to false by default.");
			c.comment("Modifiers.breaking.ignore-permission",
					"Will spawner breaking permission be ignored.",
					"  (spawnermeta.break)",
					"Note, this option may be removed in future,",
					"  instead use 'unbreakable' option.");
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
			c.comment("Modifiers.breaking.cancel-if-full",
					"If enabled players will not be able to break the spawner",
					"  if their inventory is full.",
					"Only works if 'drop-on-ground' is false.");
			c.comment("Modifiers.breaking.show-owner",
					"If set to true then when a player fails",
					"  to break another player's owned spawner",
					"  their name will be shown in the chat.");
			c.comment("Modifiers.breaking.chance-permissions",
					"Permissions with specific dropping chance can be created.",
					"Permission layout:",
					"  chance-permissions:",
					"    <name>: <chance>",
					"Replace <name> with the specific permission name.",
					"  [ spawnermeta.breaking.permission.<name> ]");
			c.comment("Modifiers.breaking.chance-permissions.example",
					"This is an example permission, does not work in game.",
					"Player with permission (spawnermeta.breaking.permission.example)",
					"  will be able to break spawners with a 100% chance.");
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
			c.comment("Modifiers.silent-entities",
					"List of entities that will be silent",
					"  after spawning.");
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
			c.comment("Modifiers.players.owned.spawn-if-online",
					"If true then it will only spawn entities if",
					"  the owner of that spawner is online,",
					"  otherwise the owner will be ignored and",
					"  the spawner will spawn whenever there is",
					"  a player nearby.");
			c.comment("Modifiers.players.owned.offline-time-limit",
					"The time (in minutes) that a player can be offline",
					"  for the spawner to still be active, after the time",
					"  has passed the spawner becomes inacitve for other users.",
					"This option only works if 'spawn-if-online' is true.");
			c.comment("Modifiers.players.owned.offline-ignore-list",
					"List of player UUIDs, that will be ignored if",
					"  the option 'spawn-if-online' is enabled, meaning",
					"  those player spawners will spawn even if they",
					"  are offline.",
					"Useful for spawners in safe zones that need to",
					"  always active.");
			c.comment("Modifiers.players.owned.ignore-limit",
					"Is player owner spawner limit enabled.",
					"If false, each player will only have a specific",
					"  amount of spawner they can place.");
			c.comment("Modifiers.players.owned.spawner-limit", "Player owned spawner limit.");
			c.comment("Modifiers.players.owned.can-break", "Can players break other player owned spawners.");
			c.comment("Modifiers.players.owned.can-stack", "Can players stack other player owned spawners.");
			c.comment("Modifiers.players.owned.can-change", "Can players change other player owned spawners.");
			c.comment("Modifiers.players.owned.can-open", "Can players open other player owned spawners.");
			c.comment("Modifiers.players.owned.can-upgrade", "Can players upgrade other player owned spawners.");
			c.comment("Modifiers.players.owned.limit-permissions",
					"Permissions with specific ownership limits can be created.",
					"Permission layout:",
					"  limit-permissions:",
					"    <name>: <limit>",
					"Replace <name> with the specific permission name.",
					"  [ spawnermeta.ownership.permission.<name> ]");
			c.comment("Modifiers.players.owned.limit-permissions.example",
					"This is an example permission, does not work in game.",
					"Player with permission (spawnermeta.ownership.permission.example)",
					"  will be able to place 32 spawners.");
			c.comment("Modifiers.players.natural.can-break", "Can players break natural spawners.");
			c.comment("Modifiers.players.natural.can-stack", "Can players stack natural spawners.");
			c.comment("Modifiers.players.natural.can-change", "Can players change natural spawners.");
			c.comment("Modifiers.players.natural.can-open", "Can players open natural spawners.");
			c.comment("Modifiers.players.natural.can-upgrade", "Can players upgrade natural spawners.");
			c.comment("Modifiers.players.trusted.can-break", "Can trusted players break trustee's spawners.");
			c.comment("Modifiers.players.trusted.can-stack", "Can trusted players stack trustee's spawners.");
			c.comment("Modifiers.players.trusted.can-change", "Can trusted players change trustee's spawners.");
			c.comment("Modifiers.players.trusted.can-open", "Can trusted players open trustee's spawners.");
			c.comment("Modifiers.players.trusted.can-upgrade", "Can trusted players upgrade trustee's spawners.");
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
			c.comment("Commands.spawner-view.label", "Command label for spawner view.");
			c.comment("Commands.spawner-view.aliases", "Command aliases for spawner view.");
			c.comment("Commands.spawner-shop.label", "Command label for spawner shop.");
			c.comment("Commands.spawner-shop.aliases", "Command aliases for spawner shop.");
			c.comment("Commands.spawner-drops.label", "Command label for spawner drops.");
			c.comment("Commands.spawner-drops.aliases", "Command aliases for spawner drops.");
			c.comment("Commands.spawner-locations.label", "Command label for spawner locations.");
			c.comment("Commands.spawner-locations.aliases", "Command aliases for spawner locations.");
			c.comment("Commands.spawner-trust.label", "Command label for spawner trust.");
			c.comment("Commands.spawner-trust.aliases", "Command aliases for spawner trust.");
			c.comment("Prices",
					"Price types:",
					"  EXPERIENCE - experience points",
					"  LEVELS - experience levels",
					"  ECONOMY or MONEY - vault economy",
					"  FLARE_TOKENS - flare tokens",
					"  PLAYER_POINTS - player points",
					"  MATERIAL or ITEM - items",
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
			
			c.comment("Prices.format.use-delimiter",
					"If true numbers will use",
					"  a delimiter.",
					"1000 -> 1,000");
			c.comment("Prices.format.delimiter",
					"Delimiter will be used for every",
					"  thousand in a number.",
					"Delimiter can be anything, even an empty space.");
			c.comment("Prices.format.use-abbreviations",
					"If true numbers will be abbreviated.",
					"Only the first 2 numbers will be shown,",
					"  meaning 1234 will show as 1.2k, and",
					"  all numbers will be rounded up.",
					"1 000 -> 1k",
					"2 500 000 -> 2.5m",
					"...");
			c.comment("Prices.format.abbreviations",
					"Abbreviations for each number thousand.",
					"List:",
					"- thousands",
					"- millions",
					"- billions",
					"- trillions",
					"- ...",
					"You can change and extend this list.",
					"Note, that the list order matters.");
			
			c.comment("Configuration-version", "Version of this configuration file.",
					"Should not be changed.");
		}
		
		save();
		
		Settings.reload();
	}

}
