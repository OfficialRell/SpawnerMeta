package mc.rellox.spawnermeta.configuration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.IntPredicate;
import java.util.function.IntSupplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import mc.rellox.spawnermeta.api.configuration.IFileValues;
import mc.rellox.spawnermeta.api.events.SpawnerExplodeEvent.ExplosionType;
import mc.rellox.spawnermeta.api.spawner.IGenerator;
import mc.rellox.spawnermeta.api.spawner.ISpawner;
import mc.rellox.spawnermeta.api.spawner.location.ISelector.Selection;
import mc.rellox.spawnermeta.configuration.Configuration.CF;
import mc.rellox.spawnermeta.prices.Group;
import mc.rellox.spawnermeta.prices.IncreaseType;
import mc.rellox.spawnermeta.prices.Price;
import mc.rellox.spawnermeta.prices.PriceManager;
import mc.rellox.spawnermeta.spawner.type.SpawnerType;
import mc.rellox.spawnermeta.spawner.type.UpgradeType;
import mc.rellox.spawnermeta.text.Text;
import mc.rellox.spawnermeta.text.order.OrderList;
import mc.rellox.spawnermeta.utility.Utils;
import mc.rellox.spawnermeta.utility.reflect.Reflect.RF;

public final class Settings {
	
	public static final Settings settings = new Settings();
	
	public static void reload() {
		settings.reload0();
	}
	
	public boolean debug;

	public boolean spawning;
	
	public final TripleIntegerMap spawner_values;
	public final TripleIntegerMap spawner_value_increase;
	
	public int check_ticks;
	
	public int radius;
	public Selection selection;
	public boolean spawner_switching;
	
	public boolean empty_enabled;
	public boolean empty_destroy_eggs_removing;
	public boolean empty_destroy_eggs_breaking;
	public boolean empty_store_inside;
	public boolean empty_verify_removing;
	
	public boolean spawning_particles;
	public boolean disable_item_spawners;
	public final Set<SpawnerType> spawner_disabled;
	
	public boolean allow_renaming;
	
	public IntSupplier slime_size;
	public int slime_box;
	
	public boolean cancel_spawning_event;
	public boolean send_spawning_event;
	public boolean cancel_break_event;
	
	public boolean kill_entities_on_spawn;
	public boolean entities_drop_xp;
	
	public int nearby_limit;
	public boolean nearby_reduce;
	
	public int items_taking_ticks;
	public int items_remind_ticks;
	
	public boolean holograms_regular_enabled;
	public boolean holograms_regular_show_natural;
	public int holograms_regular_radius;
	public boolean holograms_warning_enabled;
	public int holograms_warning_radius;
	
	public boolean upgrade_interface_enabled;

	public final TripleBooleanMap upgrades_upgradeable;
	public final TripleIntegerMap upgrades_levels;
	public final TripleIntegerMap upgrades_prices;
	public final TripleIntegerMap upgrades_price_increase;
	public IncreaseType upgrade_increase_type;

	public final SingleIntegerMap charges_price;
	public boolean charges_enabled;
	public boolean charges_allow_stacking;
	public boolean charges_ignore_natural;
	public int charges_buy_first;
	public int charges_buy_second;
	
	public final SinglePriceMap changing_price;
	public boolean changing_enabled;
	public final Map<SpawnerType, Material> changing_materials;
	public final Set<SpawnerType> changing_deny_from;
	public final Set<SpawnerType> changing_deny_to;

	public final SinglePriceMap placing_price;
	public boolean placing_enabled;

	public final SinglePriceMap stacking_price;
	public boolean stacking_enabled;
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

	public final SinglePriceMap breaking_price;
	public boolean unbreakable;
	public boolean ignore_permission;
	public double breaking_dropping_chance;
	public ValueChanger breaking_chance_changer_owned;
	public ValueChanger breaking_chance_changer_not_owned;
	public ValueChanger breaking_chance_changer_natural;
	public boolean breaking_drop_on_ground;
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

	public final Set<SpawnerType> silent_entities;
	public boolean entity_target;
	public boolean entity_movement;
	public boolean check_spawner_nerf;
	public boolean spawn_babies;
	public boolean spawn_with_equipment;
	public boolean modify_stacked_entities;
	public int safety_limit;
	
	public boolean chunk_enabled;
	public int chunk_limit;
	public int chunk_entity_limit;
	
	public boolean item_show_header;
	public boolean item_show_range;
	public boolean item_show_delay;
	public boolean item_show_amount;

	public boolean owned_if_online;
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
	
	public int stacking_ticks;

	public boolean spawnable_enabled;
	public final SingleIntegerMap spawnable_amount;
	
	public final Map<ExplosionType, boolean[]> explosion_types;
	
	public boolean spawner_view_enabled;
	public final List<SpawnerType> spawner_view_entities;
	
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
	
	public OrderList order_spawner, order_stats, order_upgrade, order_disabled;
	
	public boolean use_delimiter;
	public char delimiter;
	public boolean use_abbreviations;
	public final List<String> abbreviations;
	
	private Settings() {
		this.spawner_values = new TripleIntegerMap("Spawners.values");
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
		this.stacking_permissions = new HashMap<>(4);
		this.chance_permissions = new HashMap<>(4);
		this.ownership_permissions = new HashMap<>(4);
		this.explosion_types = new EnumMap<>(ExplosionType.class);
		this.changing_materials = new HashMap<>(4);
		this.changing_deny_from = EnumSet.noneOf(SpawnerType.class);
		this.changing_deny_to = EnumSet.noneOf(SpawnerType.class);
		this.silent_entities = EnumSet.noneOf(SpawnerType.class);
		this.spawner_view_entities = new ArrayList<>(8);
		this.abbreviations = new ArrayList<>();
		this.aliases_view = new ArrayList<>();
		this.aliases_shop = new ArrayList<>();
		this.aliases_drops = new ArrayList<>();
		this.aliases_locations = new ArrayList<>();
		this.aliases_trust = new ArrayList<>();
	}
	
	protected void reload0() {
		IFileValues file = CF.s;
		
		debug = file.getBoolean("Debug-errors");

		spawning = true;
		
		check_ticks = CF.s.getInteger("Spawners.checking-ticks", 20, 1000);
		
		spawner_values.load();
		spawner_value_increase.load();
		selection = RF.enumerate(Selection.class, file.getString("Spawners.spawning-type"),
				Selection.SINGLE);
		radius = CF.s.getInteger("Spawners.spawning-radius", 1, 8);
		spawner_switching = file.getBoolean("Spawners.switching");
		
		empty_enabled = file.getBoolean("Spawners.empty.enabled");
		empty_destroy_eggs_removing = file.getBoolean("Spawners.empty.destroy-eggs.when-removing");
		empty_destroy_eggs_breaking = file.getBoolean("Spawners.empty.destroy-eggs.when-breaking");
		empty_store_inside = file.getBoolean("Spawners.empty.store-eggs-inside");
		empty_verify_removing = file.getBoolean("Spawners.empty.egg-removing-verify");
		
		spawning_particles = file.getBoolean("Spawners.spawning-particles");
		disable_item_spawners = file.getBoolean("Spawners.disable-item-spawners");
		spawner_disabled.clear();
		spawner_disabled.addAll(file.getStrings("Spawners.disabled-spawners").stream()
				.map(SpawnerType::of).filter(s -> s != null).collect(Collectors.toList()));
		
		final int z = file.getInteger("Spawners.default-slime-size", 0, 8);
		final int[] ss = {1, 2, 4};
		slime_size = z <= 0 ? () -> ss[Utils.random(3)] : () -> z;
		slime_box = z <= 0 ? 3 : (int) (0.51 * (z + 1) + 1);
		
		cancel_spawning_event = file.getBoolean("Events.cancel-spawning-event");
		send_spawning_event = file.getBoolean("Events.send-spawner-event");
		cancel_break_event = file.getBoolean("Events.cancel-break-event");
		
		kill_entities_on_spawn = file.getBoolean("Spawners.kill-entities-on-spawn");
		entities_drop_xp = file.getBoolean("Spawners.drop-xp-when-instant-kill");
		
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
		
		upgrade_interface_enabled = file.getBoolean("Modifiers.upgrade-interface.enabled");
		upgrades_upgradeable.load();
		upgrades_levels.load();
		upgrades_prices.load();
		upgrades_price_increase.load();
		upgrade_increase_type = IncreaseType.of(file.getString("Modifiers.upgrades.price-increase-type"));

		charges_enabled = file.getBoolean("Modifiers.charges.enabled");
		charges_allow_stacking = file.getBoolean("Modifiers.charges.allow-stacking");
		charges_ignore_natural = file.getBoolean("Modifiers.charges.ignore-natural");
		charges_price.load();
		charges_buy_first = file.getInteger("Modifiers.charges.buy-amount.first", 1, Integer.MAX_VALUE);
		charges_buy_second = file.getInteger("Modifiers.charges.buy-amount.second", 1, Integer.MAX_VALUE);
		
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
				file.getString("Modifiers.changing.deny.from")));
		changing_deny_to.clear();
		changing_deny_to.addAll(RF.enumerates(SpawnerType.class,
				file.getString("Modifiers.changing.deny.to")));
		
		placing_enabled = file.getBoolean("Modifiers.placing.enabled");
		placing_price.load();
		
		stacking_enabled = file.getBoolean("Modifiers.stacking.enabled");
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
		breaking_silk_enabled = file.getBoolean("Modifiers.breaking.silk-requirement.enabled");
		breaking_silk_level = file.getInteger("Modifiers.breaking.silk-requirement.level");
		breaking_silk_break_owned = file.getBoolean("Modifiers.breaking.silk-requirement.break-owned");
		breaking_silk_break_natural = file.getBoolean("Modifiers.breaking.silk-requirement.break-natural");
		breaking_silk_destroy = file.getBoolean("Modifiers.breaking.silk-requirement.destroy-on-fail");
		breaking_durability_enabled = file.getBoolean("Modifiers.breaking.enable-durability");
		breaking_durability_to_remove = file.getInteger("Modifiers.breaking.durability-to-remove");
		breaking_xp_on_failure = file.getInteger("Modifiers.breaking.xp-on-failure");
		chance_permissions.clear();
		CF.s.keys("Modifiers.breaking.permissions")
		.forEach(key -> {
			if(key.equals("example") == true) return;
			String perm = "spawnermeta.breaking.permission." + key;
			double chance = file.getDouble("Modifiers.breaking.chance-permissions." + key);
			chance_permissions.put(perm, chance);
		});
		breaking_show_owner = file.getBoolean("Modifiers.breaking.show-owner");

		silent_entities.clear();
		silent_entities.addAll(RF.enumerates(SpawnerType.class,
				file.getString("Modifiers.silent-entities")));
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
				.filter(s -> s != null)
				.collect(Collectors.toSet());
		spawner_view_entities.clear();
		spawner_view_entities.addAll(Stream.of(SpawnerType.values())
				.filter(s -> s.exists() == true && list.contains(s) == false)
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
		
		order_spawner = new OrderList(file.getStrings("Items.layout.spawner-item"));
		order_stats = new OrderList(file.getStrings("Items.layout.upgrades.stat-item"));
		order_upgrade = new OrderList(file.getStrings("Items.layout.upgrades.upgrade-item"));
		order_disabled = new OrderList(file.getStrings("Items.layout.upgrades.disabled-upgrade-item"));
		
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
	
	public String price(int f) {
		if(use_abbreviations == true && abbreviations.isEmpty() == false) {
			if(f < 1_000) return f + "";
			double i = 1;
			String s = abbreviations.get(0);
			for(String a : abbreviations) {
				i *= 1_000;
				if(f >= i) {
					s = a;
				} else break;
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
		Utils.hideCustomFlags(meta);
		item.setItemMeta(meta);
		return item;
	}
	
	public int charges_price(SpawnerType type, ISpawner spawner) {
		return charges_price.get(type) *spawner.getStack() * (spawner.getSpawnerLevel() + 1);
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
