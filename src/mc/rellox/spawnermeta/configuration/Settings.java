package mc.rellox.spawnermeta.configuration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.function.IntPredicate;
import java.util.function.IntSupplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;

import mc.rellox.spawnermeta.api.configuration.IFileValues;
import mc.rellox.spawnermeta.api.events.SpawnerExplodeEvent.ExplosionType;
import mc.rellox.spawnermeta.api.spawner.IGenerator;
import mc.rellox.spawnermeta.api.spawner.location.ISelector.Selection;
import mc.rellox.spawnermeta.configuration.Configuration.CF;
import mc.rellox.spawnermeta.prices.Group;
import mc.rellox.spawnermeta.prices.IncreaseType;
import mc.rellox.spawnermeta.prices.Price;
import mc.rellox.spawnermeta.prices.PriceManager;
import mc.rellox.spawnermeta.spawner.type.SpawnerType;
import mc.rellox.spawnermeta.spawner.type.UpgradeType;
import mc.rellox.spawnermeta.text.Text;
import mc.rellox.spawnermeta.utility.DataManager;
import mc.rellox.spawnermeta.utility.Utility;
import mc.rellox.spawnermeta.utility.reflect.Reflect.RF;

public final class Settings {
	
	public static final Settings settings = new Settings();
	
	public static void reload() {
		settings.reload0();
	}
	
	public boolean debug;

	// Global spawning
	
	public boolean spawning;
	
	// Spawner values
	
	public final TripleRangeMap spawner_values;
	public final TripleIntegerMap spawner_value_increase;

	// Intervals and ticking
	
	public int ticking_interval;
	public int checking_interval;
	public int validation_interval;
	public boolean check_present_enabled;
	public int check_present_interval;
	public boolean tick_until_zero;
	public double delay_offset;
	
	// Reset spawners on unload
	
	public boolean reset_spawner_values;
	
	// Radii and selection
	
	public int radius_horizontal;
	public int radius_vertical;
	public Selection selection;
	public boolean spawner_switching;
	
	// Empty spawners
	
	public boolean empty_enabled;
	public boolean empty_destroy_eggs_removing;
	public boolean empty_destroy_eggs_breaking;
	public boolean empty_store_inside;
	public boolean empty_verify_removing;
	public boolean empty_remove_from_regular;
	
	// General spawner settings
	
	public boolean spawning_particles;
	public boolean warning_particles;
	public boolean owner_particles;
	public boolean disable_item_spawners;
	public final Set<SpawnerType> spawner_disabled;
	public final Set<SpawnerType> spawner_ignored;
	public final Set<String> world_disabled;
	public final Set<String> world_ignored;
	public boolean ignore_natural;
	
	// Spawner reason
	
	public SpawnReason spawn_reason;
	
	// Misc
	
	public boolean allow_renaming;
	
	public IntSupplier slime_size;
	public int slime_box;
	
	// Events
	
	public boolean cancel_spawning_event;
	public boolean send_spawning_event;
	public boolean cancel_break_event;
	public boolean ignore_break_event;
	public boolean check_island_kick;
	
	// Instant kill
	
	public boolean instant_kill_enabled;
	public boolean instant_kill_drop_xp;
	public boolean instant_kill_death_animation;

	// Redstone
	
	public int redstone_power_required;
	public boolean redstone_power_ignore_natural;
	
	// Nearby
	
	public int nearby_limit;
	public boolean nearby_reduce;
	
	// Items
	
	public int items_taking_ticks;
	public int items_remind_ticks;
	
	// Holograms
	
	public boolean holograms_regular_enabled;
	public boolean holograms_regular_show_natural;
	public int holograms_regular_radius;
	public boolean holograms_warning_enabled;
	public int holograms_warning_radius;
	public double holograms_height;
	
	// Upgrades
	
	public boolean upgrade_interface_enabled;

	public final TripleBooleanMap upgrades_upgradeable;
	public final TripleIntegerMap upgrades_levels;
	public final TripleIntegerMap upgrades_prices;
	public final TripleIntegerMap upgrades_price_increase;
	public IncreaseType upgrade_increase_type;
	
	// Charges

	public final SingleIntegerMap charges_price;
	public boolean charges_enabled;
	public boolean charges_comparison;
	public final SingleIntegerMap charges_consume;
	public final SingleIntegerMap charges_requires_as_minimum;
	public boolean charges_allow_stacking;
	public boolean charges_ignore_natural;
	public int charges_buy_first;
	public int charges_buy_second;
	public boolean charges_ignore_levels;
	
	// Changing
	
	public final SinglePriceMap changing_price;
	public boolean changing_enabled;
	public final Map<SpawnerType, Material> changing_materials;
	public final Set<SpawnerType> changing_deny_from;
	public final Set<SpawnerType> changing_deny_to;
	public boolean changing_reset_regular;
	public boolean changing_reset_empty;
	
	// Placing

	public final SinglePriceMap placing_price;
	public boolean placing_enabled;
	
	// Stacking

	public final SinglePriceMap stacking_price;
	public boolean stacking_stack_all;
	public boolean stacking_enabled;
	public final Set<SpawnerType> stacking_disabled_types;
	public int stacking_limit_natural;
	public int stacking_limit_owned;
	public boolean stacking_ignore_limit;
	public boolean stacking_nearby_enabled;
	public int stacking_nearby_radius;
	public boolean stacking_nearby_particles;
	public final Map<String, Integer> stacking_permissions;
	public boolean stacking_permissions_natural;
	public boolean stacking_permissions_owned;
	public boolean stacking_permissions_not_owned;
	
	public int stacking_ticks;
	
	// Breaking

	public final SinglePriceMap breaking_price;
	public boolean unbreakable;
	public boolean ignore_permission;
	public double breaking_dropping_chance;
	public ValueChanger breaking_chance_changer_owned;
	public ValueChanger breaking_chance_changer_not_owned;
	public ValueChanger breaking_chance_changer_natural;
	public boolean breaking_drop_on_ground;
	public boolean breaking_cancel_if_full;
	public boolean breaking_silk_enabled;
	public int breaking_silk_level;
	public boolean breaking_silk_break_owned;
	public boolean breaking_silk_break_natural;
	public boolean breaking_silk_destroy;
	public boolean breaking_durability_enabled;
	public int breaking_durability_to_remove;
	public int breaking_xp_on_failure;
	public final Map<String, Double> chance_permissions;
	public boolean breaking_show_owner;
	
	// Entity settings

	public final Set<SpawnerType> silent_entities;
	public boolean entity_target;
	public boolean entity_movement;
	public boolean check_spawner_nerf;
	public boolean spawn_babies;
	public boolean spawn_with_equipment;
	public boolean modify_stacked_entities;
	public int safety_limit;
	
	// Chunks
	
	public boolean chunk_enabled;
	public int chunk_limit;
	public int chunk_entity_limit;
	
	// Spawner item
	
	public boolean item_show_header;
	public boolean item_show_range;
	public boolean item_show_delay;
	public boolean item_show_amount;
	
	// Player, natural and owned spawner settings

	public boolean owned_if_online;
	public int owned_offline_time;
	public final List<UUID> owned_offline_ignore;
	public boolean owned_ignore_limit;
	public int owned_spawner_limit;
	public boolean owned_can_break;
	public boolean owned_can_stack;
	public boolean owned_can_change;
	public boolean owned_can_open;
	public boolean owned_can_upgrade;
	public final Map<String, Integer> ownership_permissions;
	public boolean natural_can_break;
	public boolean natural_can_stack;
	public boolean natural_can_change;
	public boolean natural_can_open;
	public boolean natural_can_upgrade;
	public boolean trusted_can_break;
	public boolean trusted_can_stack;
	public boolean trusted_can_change;
	public boolean trusted_can_open;
	public boolean trusted_can_upgrade;
	
	// Spawnable amount

	public boolean spawnable_enabled;
	public final SingleIntegerMap spawnable_amount;
	
	// Explosions
	
	public final Map<ExplosionType, boolean[]> explosion_types;
	
	// Spawner view
	
	public boolean spawner_view_enabled;
	public final List<SpawnerType> spawner_view_entities;
	
	// Commands
	
	public String command_view;
	public final List<String> aliases_view;
	public String command_shop;
	public final List<String> aliases_shop;
	public String command_drops;
	public final List<String> aliases_drops;
	public String command_locations;
	public final List<String> aliases_locations;
	public String command_trust;
	public final List<String> aliases_trust;
	
	// Price formatting
	
	public boolean use_delimiter;
	public char delimiter;
	public boolean use_abbreviations;
	public final List<String> abbreviations;
	
	private Settings() {
		this.spawner_values = new TripleRangeMap("Spawners.values");
		this.spawner_value_increase = new TripleIntegerMap("Spawners.value-increase");
		this.upgrades_upgradeable = new TripleBooleanMap("Modifiers.upgrades.upgradeable");
		this.upgrades_levels = new TripleIntegerMap("Modifiers.upgrades.levels");
		this.upgrades_prices = new TripleIntegerMap("Modifiers.upgrades.prices");
		this.upgrades_price_increase = new TripleIntegerMap("Modifiers.upgrades.price-increase");
		this.charges_price = new SingleIntegerMap("Modifiers.charges.prices");
		this.changing_price = new SinglePriceMap("Modifiers.changing");
		this.placing_price = new SinglePriceMap("Modifiers.placing");
		this.stacking_price = new SinglePriceMap("Modifiers.stacking");
		this.breaking_price = new SinglePriceMap("Modifiers.breaking");
		this.spawnable_amount = new SingleIntegerMap("Modifiers.spawnable.entity-amount");
		this.spawner_disabled = EnumSet.noneOf(SpawnerType.class);
		this.spawner_ignored = EnumSet.noneOf(SpawnerType.class);
		this.world_disabled = new HashSet<>();
		this.world_ignored = new HashSet<>();
		this.stacking_disabled_types = EnumSet.noneOf(SpawnerType.class);
		this.stacking_permissions = new HashMap<>(4);
		this.chance_permissions = new HashMap<>(4);
		this.ownership_permissions = new HashMap<>(4);
		this.explosion_types = new EnumMap<>(ExplosionType.class);
		this.changing_materials = new HashMap<>(4);
		this.changing_deny_from = EnumSet.noneOf(SpawnerType.class);
		this.changing_deny_to = EnumSet.noneOf(SpawnerType.class);
		this.silent_entities = EnumSet.noneOf(SpawnerType.class);
		this.spawner_view_entities = new ArrayList<>(8);
		this.owned_offline_ignore = new ArrayList<>(4);
		this.abbreviations = new ArrayList<>();
		this.aliases_view = new ArrayList<>();
		this.aliases_shop = new ArrayList<>();
		this.aliases_drops = new ArrayList<>();
		this.aliases_locations = new ArrayList<>();
		this.aliases_trust = new ArrayList<>();
		this.charges_consume = new SingleIntegerMap("Modifiers.charges.consume");
		this.charges_requires_as_minimum = new SingleIntegerMap("Modifiers.charges.requires-as-minimum");
	}
	
	protected void reload0() {
		IFileValues file = CF.s;
		
		debug = file.getBoolean("Debug-errors");

		spawning = true;
		
		ticking_interval = file.getInteger("Spawners.ticking-interval", 1, 20);
		checking_interval = file.getInteger("Spawners.checking-interval", 1, 1000);
		validation_interval = file.getInteger("Spawners.validation-interval", 1, 1000);
		check_present_enabled = file.getBoolean("Spawners.check-if-present.enabled");
		check_present_interval = file.getInteger("Spawners.check-if-present.interval", 100, 100000);
		tick_until_zero = file.getBoolean("Spawners.tick-until-zero");
		delay_offset = file.getDouble("Spawners.delay-offset");
		if(delay_offset > 99) delay_offset = 99;
		else if(delay_offset < 1) delay_offset = 1;
		delay_offset *= 0.01;
		
		reset_spawner_values = file.getBoolean("Spawners.reset-spawner-values");
		
		spawner_values.load();
		spawner_value_increase.load();
		selection = RF.enumerate(Selection.class, file.getString("Spawners.spawning-type"),
				Selection.SPREAD);
		
		radius_horizontal = file.getInteger("Spawners.spawning-radius.horizontal", 1, 8);
		radius_vertical = file.getInteger("Spawners.spawning-radius.vertical", 1, 8);
		spawner_switching = file.getBoolean("Spawners.switching");
		
		empty_enabled = file.getBoolean("Spawners.empty.enabled");
		empty_destroy_eggs_removing = file.getBoolean("Spawners.empty.destroy-eggs.when-removing");
		empty_destroy_eggs_breaking = file.getBoolean("Spawners.empty.destroy-eggs.when-breaking");
		empty_store_inside = file.getBoolean("Spawners.empty.store-eggs-inside");
		empty_verify_removing = file.getBoolean("Spawners.empty.egg-removing-verify");
		empty_remove_from_regular = file.getBoolean("Spawners.empty.remove-from-regular");
		
		spawn_reason = RF.enumerate(SpawnReason.class, file.getString("Spawners.spawning-reason"),
				SpawnReason.SPAWNER);
		
		spawning_particles = file.getBoolean("Spawners.spawning-particles");
		warning_particles = file.getBoolean("Spawners.warning-particles");
		owner_particles = file.getBoolean("Spawners.owner-warning-particles");
		disable_item_spawners = file.getBoolean("Spawners.disable-item-spawners");
		spawner_disabled.clear();
		spawner_disabled.addAll(RF.enumerates(SpawnerType.class,
				file.getStrings("Spawners.disabled-spawners")));
		spawner_ignored.clear();
		spawner_ignored.addAll(RF.enumerates(SpawnerType.class,
				file.getStrings("Spawners.ignored-spawners")));
		ignore_natural = file.getBoolean("Spawners.ignore-natural");
		world_disabled.clear();
		world_disabled.addAll(file.getStrings("Spawners.disabled-worlds"));
		world_ignored.clear();
		world_ignored.addAll(file.getStrings("Spawners.ignored-worlds"));
		
		final int z = file.getInteger("Spawners.default-slime-size", 0, 8);
		final int[] ss = {1, 2, 4};
		slime_size = z <= 0 ? () -> ss[Utility.random(3)] : () -> z;
		slime_box = z <= 0 ? 3 : (int) (0.51 * (z + 1) + 1);
		
		cancel_spawning_event = file.getBoolean("Events.cancel-spawning-event");
		send_spawning_event = file.getBoolean("Events.send-spawner-event");
		cancel_break_event = file.getBoolean("Events.cancel-break-event");
		ignore_break_event = file.getBoolean("Events.ignore-break-event");
		check_island_kick = file.getBoolean("Events.check-island-kick");
		
		instant_kill_enabled = file.getBoolean("Spawners.instant-kill.enabled");
		instant_kill_drop_xp = file.getBoolean("Spawners.instant-kill.drop-xp");
		instant_kill_death_animation = file.getBoolean("Spawners.instant-kill.death-animation");
		
		redstone_power_required = file.getInteger("Spawners.redstone-power.required", 0, 15);
		redstone_power_ignore_natural = file.getBoolean("Spawners.redstone-power.ignore-natural");
		
		allow_renaming = file.getBoolean("Spawners.allow-renaming");
		
		nearby_limit = file.getInteger("Spawners.nearby-entities.limit");
		nearby_reduce = file.getBoolean("Spawners.nearby-entities.reduce");

		items_taking_ticks = file.getInteger("Items.taking-ticks");
		items_remind_ticks = file.getInteger("Items.taking-remind-ticks");
		
		holograms_regular_enabled = file.getBoolean("Modifiers.holograms.regular.enabled");
		holograms_regular_show_natural = file.getBoolean("Modifiers.holograms.regular.show-natural");
		holograms_regular_radius = file.getInteger("Modifiers.holograms.regular.radius");
		holograms_warning_enabled = file.getBoolean("Modifiers.holograms.warning.enabled");
		holograms_warning_radius = file.getInteger("Modifiers.holograms.warning.radius");
		holograms_height = file.getDouble("Modifiers.holograms.height", -4, 4);
		
		upgrade_interface_enabled = file.getBoolean("Modifiers.upgrade-interface.enabled");
		upgrades_upgradeable.load();
		upgrades_levels.load();
		upgrades_prices.load();
		upgrades_price_increase.load();
		upgrade_increase_type = IncreaseType.of(file.getString("Modifiers.upgrades.price-increase-type"));

		charges_enabled = file.getBoolean("Modifiers.charges.enabled");
		charges_comparison = file.getBoolean("Modifiers.charges.comparison");
		charges_consume.load();
		charges_requires_as_minimum.load();
		charges_allow_stacking = file.getBoolean("Modifiers.charges.allow-stacking");
		charges_ignore_natural = file.getBoolean("Modifiers.charges.ignore-natural");
		charges_price.load();
		charges_buy_first = file.getInteger("Modifiers.charges.buy-amount.first", 1, Integer.MAX_VALUE);
		charges_buy_second = file.getInteger("Modifiers.charges.buy-amount.second", 1, Integer.MAX_VALUE);
		charges_ignore_levels = file.getBoolean("Modifiers.charges.ignore-levels");
		
		changing_enabled = file.getBoolean("Modifiers.changing.enabled");
		changing_price.load();
		changing_materials.clear();
		SpawnerType.stream().forEach(type -> {
			String name = file.getString("Modifiers.changing.material-type." + type.name());
			Material m = RF.enumerate(Material.class, name);
			if(m == null
					|| changing_materials.values().contains(m) == true) return;
			changing_materials.put(type, m);
		});
		changing_deny_from.clear();
		changing_deny_from.addAll(RF.enumerates(SpawnerType.class,
				file.getStrings("Modifiers.changing.deny.from")));
		changing_deny_to.clear();
		changing_deny_to.addAll(RF.enumerates(SpawnerType.class,
				file.getStrings("Modifiers.changing.deny.to")));
		changing_reset_regular = file.getBoolean("Modifiers.changing.reset-upgrades.regular");
		changing_reset_empty = file.getBoolean("Modifiers.changing.reset-upgrades.empty");
		
		placing_enabled = file.getBoolean("Modifiers.placing.enabled");
		placing_price.load();
		
		stacking_enabled = file.getBoolean("Modifiers.stacking.enabled");
		stacking_disabled_types.clear();
		stacking_disabled_types.addAll(RF.enumerates(SpawnerType.class,
				file.getStrings("Modifiers.stacking.disabled-types")));
		stacking_stack_all = file.getBoolean("Modifiers.stacking.stack-all");
		stacking_price.load();
		stacking_limit_natural = file.getInteger("Modifiers.stacking.spawner-limit.natural");
		stacking_limit_owned = file.getInteger("Modifiers.stacking.spawner-limit.owned");
		stacking_ignore_limit = file.getBoolean("Modifiers.stacking.ignore-limit");
		stacking_nearby_enabled = file.getBoolean("Modifiers.stacking.when-nearby.enabled");
		stacking_nearby_radius = file.getInteger("Modifiers.stacking.when-nearby.radius", 1, 16);
		stacking_nearby_particles = file.getBoolean("Modifiers.stacking.when-nearby.particles");
		stacking_permissions.clear();
		CF.s.keys("Modifiers.stacking.limit-permissions")
		.forEach(key -> {
			if(key.equals("example") == true) return;
			String perm = "spawnermeta.stacking.permission." + key;
			int limit = file.getInteger("Modifiers.stacking.limit-permissions." + key);
			stacking_permissions.put(perm, limit);
		});
		stacking_permissions_natural = file.getBoolean("Modifiers.stacking.affected-by-permissions.natural");
		stacking_permissions_owned = file.getBoolean("Modifiers.stacking.affected-by-permissions.owned");
		stacking_permissions_not_owned = file.getBoolean("Modifiers.stacking.affected-by-permissions.not-owned");
		
		stacking_ticks = file.getInteger("Modifiers.stacking.ticks-per", 0, 1000);

		unbreakable = file.getBoolean("Modifiers.breaking.unbreakable");
		ignore_permission = file.getBoolean("Modifiers.breaking.ignore-permission");
		breaking_price.load();
		breaking_dropping_chance = file.getDouble("Modifiers.breaking.dropping-chance");
		breaking_chance_changer_owned = ValueChanger.of("Modifiers.breaking.chance-changing.owned");
		breaking_chance_changer_not_owned = ValueChanger.of("Modifiers.breaking.chance-changing.not-owned");
		breaking_chance_changer_natural = ValueChanger.of("Modifiers.breaking.chance-changing.natural");
		breaking_drop_on_ground = file.getBoolean("Modifiers.breaking.drop-on-ground");
		breaking_cancel_if_full = file.getBoolean("Modifiers.breaking.cancel-if-full");
		breaking_silk_enabled = file.getBoolean("Modifiers.breaking.silk-requirement.enabled");
		breaking_silk_level = file.getInteger("Modifiers.breaking.silk-requirement.level");
		breaking_silk_break_owned = file.getBoolean("Modifiers.breaking.silk-requirement.break-owned");
		breaking_silk_break_natural = file.getBoolean("Modifiers.breaking.silk-requirement.break-natural");
		breaking_silk_destroy = file.getBoolean("Modifiers.breaking.silk-requirement.destroy-on-fail");
		breaking_durability_enabled = file.getBoolean("Modifiers.breaking.enable-durability");
		breaking_durability_to_remove = file.getInteger("Modifiers.breaking.durability-to-remove");
		breaking_xp_on_failure = file.getInteger("Modifiers.breaking.xp-on-failure");
		chance_permissions.clear();
		CF.s.keys("Modifiers.breaking.chance-permissions")
		.forEach(key -> {
			if(key.equals("example") == true) return;
			String perm = "spawnermeta.breaking.permission." + key;
			double chance = file.getDouble("Modifiers.breaking.chance-permissions." + key);
			chance_permissions.put(perm, chance);
		});
		breaking_show_owner = file.getBoolean("Modifiers.breaking.show-owner");

		silent_entities.clear();
		silent_entities.addAll(RF.enumerates(SpawnerType.class,
				file.getStrings("Modifiers.silent-entities")));
		entity_target = file.getBoolean("Modifiers.entity-target");
		entity_movement = file.getBoolean("Modifiers.entity-movement");
		check_spawner_nerf = file.getBoolean("Modifiers.check-spawner-nerf");
		spawn_babies = file.getBoolean("Modifiers.spawn-babies");
		spawn_with_equipment = file.getBoolean("Modifiers.spawn-with-equipment");
		modify_stacked_entities = file.getBoolean("Modifiers.modify-stacked-entities");
		safety_limit = file.getInteger("Modifiers.safety-limit", 16, 1024);
		
		chunk_enabled = file.getBoolean("Modifiers.chunk-limits.enabled");
		chunk_limit = file.getInteger("Modifiers.chunk-limits.spawner-limit", 1, 1024);
		chunk_entity_limit = file.getInteger("Modifiers.chunk-limits.entities-in-chuck");
		
		item_show_header = file.getBoolean("Modifiers.spawner-item.show-header");
		item_show_range = file.getBoolean("Modifiers.spawner-item.show-range");
		item_show_delay = file.getBoolean("Modifiers.spawner-item.show-delay");
		item_show_amount = file.getBoolean("Modifiers.spawner-item.show-amount");
		
		owned_if_online = file.getBoolean("Modifiers.players.owned.spawn-if-online");
		owned_offline_time = file.getInteger("Modifiers.players.owned.offline-time-limit");
		owned_offline_ignore.clear();
		owned_offline_ignore.addAll(RF.enumerates(UUID::fromString,
				file.getStrings("Modifiers.players.owned.offline-ignore-list")));
		owned_ignore_limit = file.getBoolean("Modifiers.players.owned.ignore-limit");
		owned_spawner_limit = file.getInteger("Modifiers.players.owned.spawner-limit");
		owned_can_break = file.getBoolean("Modifiers.players.owned.can-break");
		owned_can_change = file.getBoolean("Modifiers.players.owned.can-stack");
		owned_can_open = file.getBoolean("Modifiers.players.owned.can-open");
		owned_can_stack = file.getBoolean("Modifiers.players.owned.can-stack");
		owned_can_upgrade = file.getBoolean("Modifiers.players.owned.can-upgrade");
		ownership_permissions.clear();
		CF.s.keys("Modifiers.players.owned.limit-permissions")
		.forEach(key -> {
			if(key.equals("example") == true) return;
			String perm = "spawnermeta.ownership.permission." + key;
			int limit = file.getInteger("Modifiers.players.owned.limit-permissions." + key);
			ownership_permissions.put(perm, limit);
		});
		natural_can_break = file.getBoolean("Modifiers.players.natural.can-break");
		natural_can_change = file.getBoolean("Modifiers.players.natural.can-stack");
		natural_can_open = file.getBoolean("Modifiers.players.natural.can-open");
		natural_can_stack = file.getBoolean("Modifiers.players.natural.can-stack");
		natural_can_upgrade = file.getBoolean("Modifiers.players.natural.can-upgrade");
		trusted_can_break = file.getBoolean("Modifiers.players.trusted.can-break");
		trusted_can_change = file.getBoolean("Modifiers.players.trusted.can-stack");
		trusted_can_open = file.getBoolean("Modifiers.players.trusted.can-open");
		trusted_can_stack = file.getBoolean("Modifiers.players.trusted.can-stack");
		trusted_can_upgrade = file.getBoolean("Modifiers.players.trusted.can-upgrade");

		spawnable_enabled = file.getBoolean("Modifiers.spawnable.enabled");
		spawnable_amount.load();
		spawnable_amount.check(i -> i > 0, 5000, "Spawnable value must be greater than 0!");
		
		explosion_types.clear();
		Arrays.asList(ExplosionType.values()).forEach(x -> {
			boolean[] bs = {
					file.getBoolean("Miscellaneous.explosions." + x.name() + ".break-spawners"),
					file.getBoolean("Miscellaneous.explosions." + x.name() + ".drop-spawners"),
					file.getBoolean("Miscellaneous.explosions." + x.name() + ".break-natural-spawners"),
					file.getBoolean("Miscellaneous.explosions." + x.name() + ".drop-natural-spawners")
			};
			explosion_types.put(x, bs);
		});

		spawner_view_enabled = file.getBoolean("Spawner-view.enabled");
		Set<SpawnerType> list = file.getStrings("Spawner-view.ignore-entities").stream()
				.map(SpawnerType::of)
				.filter(Objects::nonNull)
				.collect(Collectors.toSet());
		spawner_view_entities.clear();
		spawner_view_entities.addAll(Stream.of(SpawnerType.values())
				.filter(SpawnerType::exists)
				.filter(s -> list.contains(s) == false)
				.collect(Collectors.toList()));
		spawner_view_entities.remove(SpawnerType.EMPTY);
		
		command_view = file.getString("Commands.spawner-view.label");
		aliases_view.clear();
		aliases_view.addAll(file.getStrings("Commands.spawner-view.aliases"));
		command_shop = file.getString("Commands.spawner-shop.label");
		aliases_shop.clear();
		aliases_shop.addAll(file.getStrings("Commands.spawner-shop.aliases"));
		command_drops = file.getString("Commands.spawner-drops.label");
		aliases_drops.clear();
		aliases_drops.addAll(file.getStrings("Commands.spawner-drops.aliases"));
		command_locations = file.getString("Commands.spawner-locations.label");
		aliases_locations.clear();
		aliases_locations.addAll(file.getStrings("Commands.spawner-locations.aliases"));
		command_trust = file.getString("Commands.spawner-trust.label");
		aliases_trust.clear();
		aliases_trust.addAll(file.getStrings("Commands.spawner-trust.aliases"));
		
		PriceManager.reload();
		
		use_delimiter = file.getBoolean("Prices.format.use-delimiter");
		delimiter = (file.getString("Prices.format.delimiter") + ",").charAt(0);
		use_abbreviations = file.getBoolean("Prices.format.use-abbreviations");
		abbreviations.clear();
		abbreviations.addAll(file.getStrings("Prices.format.abbreviations"));
	}
	
	public boolean disabled(SpawnerType type) {
		return spawner_disabled.contains(type) == true;
	}
	
	public double breaking_chance(Player player) {
		double chance = breaking_dropping_chance;
		if(chance_permissions.isEmpty() == true) return chance;
		return chance_permissions.entrySet().stream()
				.filter(e -> player.hasPermission(e.getKey()))
				.mapToDouble(Entry::getValue)
				.max()
				.orElse(chance);
	}
	
	public int stacking_limit(Player player, IGenerator generator) {
		int limit;
		if(generator.cache().natural() == true) limit = stacking_limit_natural;
		else limit = stacking_limit_owned;
		if(stacking_permissions.isEmpty() == true || player == null) return limit;
		if(generator.cache().natural() == true) {
				if(stacking_permissions_natural == false) return limit;
		} else if(generator.spawner().isOwner(player) == true) {
			if(stacking_permissions_owned == false) return limit;
		} else if(stacking_permissions_not_owned == false) return limit;
		return stacking_permissions.entrySet().stream()
				.filter(e -> player.hasPermission(e.getKey()))
				.mapToInt(Entry::getValue)
				.max()
				.orElse(limit);
	}
	
	public int owning_limit(Player player) {
		int limit = owned_spawner_limit;
		if(ownership_permissions.isEmpty() == true) return limit;
		return ownership_permissions.entrySet().stream()
				.filter(e -> player.hasPermission(e.getKey()))
				.mapToInt(Entry::getValue)
				.max()
				.orElse(limit);
	}
	
	public boolean has_silk(Player player) {
		ItemStack item = player.getInventory().getItemInMainHand();
		if(item == null) return false;
		ItemMeta meta = item.getItemMeta();
		if(meta == null) return false;
		return meta.getEnchantLevel(Enchantment.SILK_TOUCH) >= breaking_silk_level;
	}
	
	public boolean ignored(Block block) {
		if(ignore_natural == true
				&& block.getState() instanceof CreatureSpawner spawner) {
			PersistentDataContainer data = spawner.getPersistentDataContainer();
			if(data.has(DataManager.ownerKey()) == false) return true;
		}
		return ignored(DataManager.getEntity(block));
	}
	
	public boolean ignored(EntityType entity) {
		if(entity == null) return true;
		Class<? extends Entity> ec = entity.getEntityClass();
		if(ec == null) return true;
		var type = SpawnerType.ofAll(entity);
		return type == null ? true : spawner_ignored.contains(type) == true;
	}
	
	public String price(int f) {
		if(use_abbreviations == true && abbreviations.isEmpty() == false) {
			if(f < 1_000) return f + "";
			double i = 1;
			String s = abbreviations.get(0);
			for(String a : abbreviations) {
				i *= 1_000;
				if(f >= i) s = a;
				else break;
			}
			double r = f / (i * 0.001);
			if(r == (int) r) return (int) r + s;
			return String.format("%.1f", r) + s;
		} else if(use_delimiter == true) {
			return String.format("%,d", f).replace(',', delimiter);
		}
		return f + "";
	}
	
	public ItemStack view_item(SpawnerType type) {
		boolean[] upgrades = upgrades_upgradeable.get(type);
		int[] prices = upgrades_prices.get(type),
				increases = upgrades_price_increase.get(type),
				levels = upgrades_levels.get(type);
		ItemStack item = new ItemStack(Material.SPAWNER);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(Language.get("Spawner-view.items.name", "type", type).text());
		List<String> lore = new ArrayList<>();
		lore.add("");
		for(int i = 0; i < 3; i++) {
			if(upgrades[i] == false) continue;
			UpgradeType u = UpgradeType.values()[i];
			Price t = Price.of(Group.upgrades, prices[i]);
			Price n = Price.of(Group.upgrades, increases[i]);
			lore.add(Language.get("Spawner-view.items.header." + u.lower()).text());
			lore.add(Language.get("Spawner-view.items.price", "price", t).text());
			lore.add(Language.get("Spawner-view.items.price-increase",
					"increase", upgrade_increase_type.format(n)).text());
			lore.add(Language.get("Spawner-view.items.maximum-level",
					"level", levels[i]).text());
		}
		if(spawnable_enabled == true) {
			lore.add("");
			lore.add(Language.get("Spawner-view.items.spawnable",
					"spawnable", spawnable_amount.get(type)).text());
		}
		meta.setLore(lore);
		meta.addItemFlags(ItemFlag.values());
		Utility.hideCustomFlags(meta);
		item.setItemMeta(meta);
		return item;
	}
	
	public int charges_price(SpawnerType type, IGenerator generator) {
		if(charges_ignore_levels == true) return charges_price.get(type) * generator.cache().stack();
		return charges_price.get(type) * generator.cache().stack() * (generator.spawner().getSpawnerLevel() + 1);
	}
	
	public int charges_consume(SpawnerType type, IGenerator generator) {
		if(charges_ignore_levels == true) return charges_consume.get(type) * generator.cache().stack();
		return charges_consume.get(type) * generator.cache().stack() * (generator.spawner().getSpawnerLevel() + 1);
	}

	public int charges_requires_as_minimum(SpawnerType type, IGenerator generator) {
		if(charges_ignore_levels == true) return charges_requires_as_minimum.get(type) * generator.cache().stack();
		return charges_requires_as_minimum.get(type) * generator.cache().stack() * (generator.spawner().getSpawnerLevel() + 1);
	}
	
	public static boolean ignored(World world) {
		return world == null ? false : settings.world_ignored.contains(world.getName());
	}
	
	public static boolean disabled(World world) {
		return world == null ? false : settings.world_disabled.contains(world.getName());
	}
	
	public static boolean inactive(World world) {
		return ignored(world) == true || disabled(world) == true;
	}


	
	public static class TripleIntegerMap {
		
		private final String path;
		private final int[] is;
		private final Map<SpawnerType, int[]> map;
		
		public TripleIntegerMap(String path) {
			this.path = path;
			this.is = new int[3];
			this.map = new HashMap<>();
		}
		
		public int[] get(SpawnerType type) {
			return map.getOrDefault(type, is).clone();
		}
		
		public void load() {
			map.clear();
			is[0] = CF.s.file.getInt(path + ".DEFAULT.range");
			is[1] = CF.s.file.getInt(path + ".DEFAULT.delay");
			is[2] = CF.s.file.getInt(path + ".DEFAULT.amount");
			Stream.of(SpawnerType.values())
			.forEach(type -> {
				int a0 = a(path + "." + type.name() + ".range", 0);
				int a1 = a(path + "." + type.name() + ".delay", 1);
				int a2 = a(path + "." + type.name() + ".amount", 2);
				if(a0 == is[0] && a1 == is[1] && a2 == is[2]) return;
				int[] as = {a0, a1, a2};
				map.put(type, as);
			});
		}
		
		private int a(String path, int i) {
			if(CF.s.exists(path) == false) return is[i];
			int a = CF.s.file.getInt(path);
			return a == 0 ? is[i] : a;
		}
		
	}
	
	public static class TripleRangeMap {
		
		private final String path;
		private final IRange[] is;
		private final Map<SpawnerType, IRange[]> map;
		
		public TripleRangeMap(String path) {
			this.path = path;
			this.is = new IRange[3];
			this.map = new HashMap<>();
		}
		
		public IRange[] get(SpawnerType type) {
			return map.getOrDefault(type, is);
		}
		
		public void load() {
			map.clear();
			is[0] = parse(CF.s.file.getString(path + ".DEFAULT.range"), 16);
			is[1] = parse(CF.s.file.getString(path + ".DEFAULT.delay"), 500);
			is[2] = parse(CF.s.file.getString(path + ".DEFAULT.amount"), 4);
			Stream.of(SpawnerType.values())
			.forEach(type -> {
				IRange a0 = a(path + "." + type.name() + ".range", 0, 16);
				IRange a1 = a(path + "." + type.name() + ".delay", 1, 500);
				IRange a2 = a(path + "." + type.name() + ".amount", 2, 4);
				if(a0 == is[0] && a1 == is[1] && a2 == is[2]) return;
				IRange[] as = {a0, a1, a2};
				map.put(type, as);
			});
		}
		
		private IRange parse(String s, int def) {
			try {
				if(s.indexOf('-') > 0) {
					String[] ss = s.split("-");
					int minimum = Integer.parseInt(ss[0]);
					int maximum = Integer.parseInt(ss[1]);
					if(minimum > maximum) {
						Text.failure("Value maximum cannot be less than minimum (#0), using default!", s);
						return new RangeConstant(def);
					}
					if(minimum <= 0 || maximum > 1_000_000) {
						Text.failure("Spawner value out of range [1; 1 000 000] (#0), using default!", s);
						return new RangeConstant(def);
					}
					if(minimum == maximum) return new RangeConstant(minimum);
					return new RangeOf(minimum, maximum);
				}
				int value = Integer.parseInt(s);
				if(value <= 0 || value > 1_000_000) {
					Text.failure("Spawner value out of range [1; 1 000 000] (#0), using default!", s);
					return new RangeConstant(def);
				}
				return new RangeConstant(value);
			} catch (Exception e) {
				Text.failure("Unable to parse spawner value (#0), using default!", s);
				return new RangeConstant(def);
			}
		}
		
		private IRange a(String path, int i, int def) {
			if(CF.s.exists(path) == false) return is[i];
			String a = CF.s.file.getString(path);
			return a == null || a.isEmpty() == true ? is[i] : parse(a, def);
		}
		
	}
	
	public static interface IRange {
		
		int roll(int hash);
		
	}
	
	private static record RangeConstant(int roll) implements IRange {
		@Override
		public int roll(int hash) {
			return roll;
		}
	}
	
	private static record RangeOf(int minimum, int maximum) implements IRange {
		@Override
		public int roll(int hash) {
			int a = maximum - minimum + 1;
			return (hash % a) + minimum;
		}
	}
	
	public static class TripleBooleanMap {
		
		private final String path;
		private final boolean[] is;
		private final Map<SpawnerType, boolean[]> map;
		
		public TripleBooleanMap(String path) {
			this.path = path;
			this.is = new boolean[3];
			this.map = new HashMap<>();
		}
		
		public boolean[] get(SpawnerType type) {
			return map.getOrDefault(type, is).clone();
		}
		
		public void load() {
			map.clear();
			is[0] = CF.s.file.getBoolean(path + ".DEFAULT.range");
			is[1] = CF.s.file.getBoolean(path + ".DEFAULT.delay");
			is[2] = CF.s.file.getBoolean(path + ".DEFAULT.amount");
			Stream.of(SpawnerType.values())
			.forEach(type -> {
				boolean a0 = b(path + "." + type.name() + ".range", 0);
				boolean a1 = b(path + "." + type.name() + ".delay", 1);
				boolean a2 = b(path + "." + type.name() + ".amount", 2);
				if(a0 == is[0] && a1 == is[1] && a2 == is[2]) return;
				boolean[] as = {a0, a1, a2};
				map.put(type, as);
			});
		}
		
		private boolean b(String path, int i) {
			if(CF.s.exists(path) == false) return is[i];
			return CF.s.file.getBoolean(path);
		}
		
	}
	
	public static class SingleIntegerMap {
		
		protected final String path;
		protected int i;
		protected final Map<SpawnerType, Integer> map;
		
		public SingleIntegerMap(String path) {
			this.path = path;
			this.map = new HashMap<>();
		}
		
		public int get(SpawnerType type) {
			return map.getOrDefault(type, i);
		}
		
		public void load() {
			map.clear();
			i = CF.s.file.getInt(path + ".DEFAULT");
			Stream.of(SpawnerType.values())
			.forEach(type -> {
				int a = CF.s.file.getInt(path + "." + type.name());
				if(a == 0 || a == i) return;
				map.put(type, a);
			});
		}
		
		public void check(IntPredicate predicate, int def, String warning) {
			if(predicate.test(i) == true) return;
			Text.failure(warning);
			i = def;
		}
		
	}
	
	public static class SinglePriceMap extends SingleIntegerMap {
		
		private final String tp;
		private boolean u;
		
		public SinglePriceMap(String path) {
			super(path + ".prices");
			tp = path;
		}
		
		public boolean using() {
			return u;
		}
		
		public void load() {
			map.clear();
			if((u = CF.s.file.getBoolean(tp + ".use-price")) == false) return;
			super.load();
		}
		
	}
	
	public static record ValueChanger(double change, ChangerType type) {
		
		public double change(double value) {
			return type.change(value, change);
		}
		
		public static ValueChanger of(String path) {
			String s = CF.s.file.getString(path);
			if(s == null || s.length() < 2) return new ValueChanger(0, ChangerType.NONE);
			ChangerType type = ChangerType.of(s.charAt(0));
			double value;
			try {
				value = Double.parseDouble(s.substring(1));
			} catch (Exception e) {
				value = 0;
			}
			return new ValueChanger(value, type);
		}
		
	}
	
	public static enum ChangerType {

		NONE {
			@Override
			public double change(double value, double other) {
				return value;
			}
		},
		ADD {
			@Override
			public double change(double value, double other) {
				return value + other;
			}
		},
		SUBTRACT {
			@Override
			public double change(double value, double other) {
				return value - other;
			}
		},
		MULTIPLY {
			@Override
			public double change(double value, double other) {
				return value * other;
			}
		},
		DIVIDE {
			@Override
			public double change(double value, double other) {
				return value / other;
			}
		};
		
		public abstract double change(double value, double other);
		
		public static ChangerType of(char c) {
			return switch (c) {
			case '+' -> ADD;
			case '-' -> SUBTRACT;
			case '*' -> MULTIPLY;
			case '/' -> DIVIDE;
			default -> NONE;
			};
		}
		
	}

}
