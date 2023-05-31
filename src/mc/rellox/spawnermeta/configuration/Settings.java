package mc.rellox.spawnermeta.configuration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import mc.rellox.spawnermeta.api.events.SpawnerExplodeEvent.ExplosionType;
import mc.rellox.spawnermeta.configuration.Configuration.CF;
import mc.rellox.spawnermeta.prices.Group;
import mc.rellox.spawnermeta.prices.IncreaseType;
import mc.rellox.spawnermeta.prices.Price;
import mc.rellox.spawnermeta.prices.PriceManager;
import mc.rellox.spawnermeta.spawner.SpawnerSpawning;
import mc.rellox.spawnermeta.spawner.SpawnerSpawning.SpawningType;
import mc.rellox.spawnermeta.spawner.SpawnerType;
import mc.rellox.spawnermeta.spawner.UpgradeType;
import mc.rellox.spawnermeta.text.order.OrderList;
import mc.rellox.spawnermeta.utils.DataManager;

public final class Settings {
	
	public static final Settings settings = new Settings();
	
	public static void reload() {
		settings.reload0();
	}
	
	public boolean debug;
	
	public boolean disable_spawning;
	
	public final TripleIntegerMap spawner_values;
	public final TripleIntegerMap spawner_value_increase;
	
	public SpawnerSpawning spawner_spawning;
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
	
	public int slime_size;
	
	public boolean cancel_spawning_event;
	public boolean send_spawning_event;
	public boolean cancel_break_event;
	
	public boolean kill_entities_on_spawn;
	public boolean entities_drop_xp;
	
	public int nearby_entity_limit;
	
	public int items_taking_ticks;
	public int items_remind_ticks;
	
	public boolean holograms_enabled;
	public boolean holograms_show_natural;
	
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

	public final SinglePriceMap placing_price;
	public boolean placing_enabled;

	public final SinglePriceMap stacking_price;
	public boolean stacking_enabled;
	public int stacking_spawner_limit;
	public boolean stacking_ignore_limit;
	public boolean stacking_nearby_enabled;
	public int stacking_nearby_radius;
	public boolean stacking_nearby_particles;

	public final SinglePriceMap breaking_price;
	public boolean breaking_enabled;
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

	public boolean owned_ignore_limit;
	public int owned_spawner_limit;
	public boolean owned_can_break;
	public boolean owned_can_stack;
	public boolean owned_can_change;
	public boolean owned_can_open;
	public boolean owned_can_upgrade;
	public boolean natural_can_break;
	public boolean natural_can_stack;
	public boolean natural_can_change;
	public boolean natural_can_open;
	public boolean natural_can_upgrade;
	
	public int stacking_ticks;

	public boolean spawnable_enabled;
	public final SingleIntegerMap spawnable_amount;
	
	public final Map<ExplosionType, boolean[]> explosion_types;
	
	public boolean spawner_view_enabled;
	public final List<SpawnerType> spawner_view_entities;
	
	public String command_view;
	public String command_shop;
	public String command_drops;
	
	public int spawner_version;
	
	public OrderList order_spawner, order_stats, order_upgrade, order_disabled;
	
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
		this.spawner_disabled = new HashSet<>(4);
		this.chance_permissions = new HashMap<>(4);
		this.explosion_types = new HashMap<>(4);
		this.spawner_view_entities = new ArrayList<>(8);
	}
	
	protected void reload0() {
		debug = CF.s.getBoolean("Debug-errors");

		spawner_values.load();
		spawner_value_increase.load();
		SpawningType spawning = SpawningType.of(CF.s.getString("Spawners.spawning-type"));
		int r = CF.s.getInteger("Spawners.spawning-radius");
		spawner_spawning = spawning.spread(r < 1 ? 1 : r > 8 ? 8 : r);
		spawner_switching = CF.s.getBoolean("Spawners.switching");
		
		empty_enabled = CF.s.getBoolean("Spawners.empty.enabled");
		empty_destroy_eggs_removing = CF.s.getBoolean("Spawners.empty.destroy-eggs.when-removing");
		empty_destroy_eggs_breaking = CF.s.getBoolean("Spawners.empty.destroy-eggs.when-breaking");
		empty_store_inside = CF.s.getBoolean("Spawners.empty.store-eggs-inside");
		empty_verify_removing = CF.s.getBoolean("Spawners.empty.egg-removing-verify");
		
		spawning_particles = CF.s.getBoolean("Spawners.spawning-particles");
		disable_item_spawners = CF.s.getBoolean("Spawners.disable-item-spawners");
		spawner_disabled.clear();
		spawner_disabled.addAll(CF.s.getStringList("Spawners.disabled-spawners").stream()
				.map(SpawnerType::of).filter(s -> s != null).collect(Collectors.toList()));
		
		slime_size = CF.s.getInteger("Spawners.default-slime-size");
		
		cancel_spawning_event = CF.s.getBoolean("Events.cancel-spawning-event");
		send_spawning_event = CF.s.getBoolean("Events.send-spawner-event");
		cancel_break_event = CF.s.getBoolean("Events.cancel-break-event");
		
		kill_entities_on_spawn = CF.s.getBoolean("Spawners.kill-entities-on-spawn");
		entities_drop_xp = CF.s.getBoolean("Spawners.drop-xp-when-instant-kill");
		
		allow_renaming = CF.s.getBoolean("Spawners.allow-renaming");
		
		nearby_entity_limit = CF.s.getInteger("Spawners.nearby-entity-limit");

		items_taking_ticks = CF.s.getInteger("Items.taking-ticks");
		items_remind_ticks = CF.s.getInteger("Items.taking-remind-ticks");
		
		holograms_enabled = CF.s.getBoolean("Modifiers.holograms.enabled");
		holograms_show_natural = CF.s.getBoolean("Modifiers.holograms.show-natural");
		
		upgrade_interface_enabled = CF.s.getBoolean("Modifiers.upgrade-interface.enabled");
		upgrades_upgradeable.load();
		upgrades_levels.load();
		upgrades_prices.load();
		upgrades_price_increase.load();
		upgrade_increase_type = IncreaseType.of(CF.s.getString("Modifiers.upgrades.price-increase-type"));

		charges_enabled = CF.s.getBoolean("Modifiers.charges.enabled");
		charges_allow_stacking = CF.s.getBoolean("Modifiers.charges.allow-stacking");
		charges_ignore_natural = CF.s.getBoolean("Modifiers.charges.ignore-natural");
		charges_price.load();
		charges_buy_first = CF.s.getInteger("Modifiers.charges.buy-amount.first");
		charges_buy_second = CF.s.getInteger("Modifiers.charges.buy-amount.second");
		
		changing_enabled = CF.s.getBoolean("Modifiers.changing.enabled");
		changing_price.load();
		
		placing_enabled = CF.s.getBoolean("Modifiers.placing.enabled");
		placing_price.load();
		
		stacking_enabled = CF.s.getBoolean("Modifiers.stacking.enabled");
		stacking_price.load();
		stacking_spawner_limit = CF.s.getInteger("Modifiers.stacking.spawner-limit");
		stacking_ignore_limit = CF.s.getBoolean("Modifiers.stacking.ignore-limit");
		stacking_nearby_enabled = CF.s.getBoolean("Modifiers.stacking.when-nearby.enabled");
		stacking_nearby_radius = CF.s.getInteger("Modifiers.stacking.when-nearby.radius");
		if(stacking_nearby_radius < 1) stacking_nearby_radius = 1;
		if(stacking_nearby_radius > 16) stacking_nearby_radius = 16;
		stacking_nearby_particles = CF.s.getBoolean("Modifiers.stacking.when-nearby.particles");
		
		stacking_ticks = CF.s.getInteger("Modifiers.stacking.ticks-per");
		
		breaking_enabled = CF.s.getBoolean("Modifiers.breaking.enabled");
		breaking_price.load();
		breaking_dropping_chance = CF.s.getDouble("Modifiers.breaking.dropping-chance");
		breaking_chance_changer_owned = ValueChanger.of("Modifiers.breaking.chance-changing.owned");
		breaking_chance_changer_not_owned = ValueChanger.of("Modifiers.breaking.chance-changing.not-owned");
		breaking_chance_changer_natural = ValueChanger.of("Modifiers.breaking.chance-changing.natural");
		breaking_drop_on_ground = CF.s.getBoolean("Modifiers.breaking.drop-on-ground");
		breaking_silk_enabled = CF.s.getBoolean("Modifiers.breaking.silk-requirement.enabled");
		breaking_silk_level = CF.s.getInteger("Modifiers.breaking.silk-requirement.level");
		breaking_silk_break_owned = CF.s.getBoolean("Modifiers.breaking.silk-requirement.break-owned");
		breaking_silk_break_natural = CF.s.getBoolean("Modifiers.breaking.silk-requirement.break-natural");
		breaking_silk_destroy = CF.s.getBoolean("Modifiers.breaking.silk-requirement.destroy-on-fail");
		breaking_durability_enabled = CF.s.getBoolean("Modifiers.breaking.enable-durability");
		breaking_durability_to_remove = CF.s.getInteger("Modifiers.breaking.durability-to-remove");
		breaking_xp_on_failure = CF.s.getInteger("Modifiers.breaking.xp-on-failure");
		Set<String> keys = CF.s.getKeys("Modifiers.breaking.permissions");
		chance_permissions.clear();
		if(keys.isEmpty() == false) {
			for(String key : keys) {
				String p = "spawnermeta.breaking.permission." + key;
				double c = CF.s.getDouble("Modifiers.breaking.permissions." + key);
				chance_permissions.put(p, c);
			}
		}
		breaking_show_owner = CF.s.getBoolean("Modifiers.breaking.show-owner");
		
		entity_target = CF.s.getBoolean("Modifiers.entity-target");
		entity_movement = CF.s.getBoolean("Modifiers.entity-movement");
		check_spawner_nerf = CF.s.getBoolean("Modifiers.check-spawner-nerf");
		spawn_babies = CF.s.getBoolean("Modifiers.spawn-babies");
		spawn_with_equipment = CF.s.getBoolean("Modifiers.spawn-with-equipment");
		modify_stacked_entities = CF.s.getBoolean("Modifiers.modify-stacked-entities");
		safety_limit = CF.s.getInteger("Modifiers.safety-limit");
		
		chunk_enabled = CF.s.getBoolean("Modifiers.chunk-limits.enabled");
		chunk_limit = CF.s.getInteger("Modifiers.chunk-limits.spawner-limit");
		chunk_entity_limit = CF.s.getInteger("Modifiers.chunk-limits.entities-in-chuck");
		
		item_show_header = CF.s.getBoolean("Modifiers.spawner-item.show-header");
		item_show_range = CF.s.getBoolean("Modifiers.spawner-item.show-range");
		item_show_delay = CF.s.getBoolean("Modifiers.spawner-item.show-delay");
		item_show_amount = CF.s.getBoolean("Modifiers.spawner-item.show-amount");
		
		owned_ignore_limit = CF.s.getBoolean("Modifiers.players.owned.ignore-limit");
		owned_spawner_limit = CF.s.getInteger("Modifiers.players.owned.spawner-limit");
		owned_can_break = CF.s.getBoolean("Modifiers.players.owned.can-break");
		owned_can_change = CF.s.getBoolean("Modifiers.players.owned.can-stack");
		owned_can_open = CF.s.getBoolean("Modifiers.players.owned.can-open");
		owned_can_stack = CF.s.getBoolean("Modifiers.players.owned.can-stack");
		owned_can_upgrade = CF.s.getBoolean("Modifiers.players.owned.can-upgrade");
		natural_can_break = CF.s.getBoolean("Modifiers.players.natural.can-break");
		natural_can_change = CF.s.getBoolean("Modifiers.players.natural.can-stack");
		natural_can_open = CF.s.getBoolean("Modifiers.players.natural.can-open");
		natural_can_stack = CF.s.getBoolean("Modifiers.players.natural.can-stack");
		natural_can_upgrade = CF.s.getBoolean("Modifiers.players.natural.can-upgrade");

		spawnable_enabled = CF.s.getBoolean("Modifiers.spawnable.enabled");
		spawnable_amount.load();
		
		explosion_types.clear();
		Arrays.asList(ExplosionType.values()).forEach(x -> {
			boolean[] bs = {
					CF.s.getBoolean("Miscellaneous.explosions." + x.name() + ".break-spawners"),
					CF.s.getBoolean("Miscellaneous.explosions." + x.name() + ".drop-spawners"),
					CF.s.getBoolean("Miscellaneous.explosions." + x.name() + ".break-natural-spawners"),
					CF.s.getBoolean("Miscellaneous.explosions." + x.name() + ".drop-natural-spawners")
			};
			explosion_types.put(x, bs);
		});

		spawner_view_enabled = CF.s.getBoolean("Spawner-view.enabled");
		Set<SpawnerType> list = CF.s.getStringList("Spawner-view.ignore-entities").stream()
				.map(SpawnerType::of)
				.filter(s -> s != null)
				.collect(Collectors.toSet());
		spawner_view_entities.clear();
		spawner_view_entities.addAll(Stream.of(SpawnerType.values())
				.filter(s -> s.exists() == true && list.contains(s) == false)
				.collect(Collectors.toList()));
		spawner_view_entities.remove(SpawnerType.EMPTY);
		
		command_view = CF.s.getString("Commands.spawner-view");
		command_shop = CF.s.getString("Commands.spawner-shop");
		command_drops = CF.s.getString("Commands.spawner-drops");
		
		spawner_version = CF.s.getInteger("Spawner-version");
		
		order_spawner = new OrderList(CF.s.getStringList("Items.layout.spawner-item"));
		order_stats = new OrderList(CF.s.getStringList("Items.layout.upgrades.stat-item"));
		order_upgrade = new OrderList(CF.s.getStringList("Items.layout.upgrades.upgrade-item"));
		order_disabled = new OrderList(CF.s.getStringList("Items.layout.upgrades.disabled-upgrade-item"));
		
		PriceManager.reload();
	}
	
	public void update_spawners() {
		spawner_version++;
		CF.s.set("Spawner-version", spawner_version);
	}
	
	public boolean disabled(SpawnerType type) {
		return spawner_disabled.contains(type) == true;
	}
	
	public double breaking_chance(Player player) {
		double chance = breaking_dropping_chance;
		if(chance_permissions.isEmpty() == true) return chance;
		for(Entry<String, Double> e : chance_permissions.entrySet()) {
			if(player.hasPermission(e.getKey()) == true) {
				double other = e.getValue();
				if(chance < other) chance = other;
			}
		}
		return chance;
	}
	
	public boolean has_silk(Player player) {
		ItemStack item = player.getInventory().getItemInMainHand();
		if(item == null) return false;
		ItemMeta meta = item.getItemMeta();
		if(meta == null) return false;
		return meta.getEnchantLevel(Enchantment.SILK_TOUCH) >= breaking_silk_level;
	}
	
	public ItemStack view_item(SpawnerType type) {
		boolean[] upgrades = upgrades_upgradeable.get(type);
		int[] prices = upgrades_prices.get(type), increases = upgrades_price_increase.get(type), levels = upgrades_levels.get(type);
		ItemStack item = new ItemStack(Material.SPAWNER);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(Language.get("Inventory.spawner-view.items.name", "type", type).text());
		List<String> lore = new ArrayList<>();
		lore.add("");
		for(int i = 0; i < 3; i++) {
			if(upgrades[i] == false) continue;
			UpgradeType u = UpgradeType.values()[i];
			Price t = Price.of(Group.upgrades, prices[i]);
			Price n = Price.of(Group.upgrades, increases[i]);
			lore.add(Language.get("Inventory.spawner-view.items.header." + u.lower()).text());
			lore.add(Language.get("Inventory.spawner-view.items.price", "price", t).text());
			lore.add(Language.get("Inventory.spawner-view.items.price-increase",
					"increase", upgrade_increase_type.format(n)).text());
			lore.add(Language.get("Inventory.spawner-view.items.maximum-level",
					"level", levels[i]).text());
		}
		if(spawnable_enabled == true) {
			lore.add("");
			lore.add(Language.get("Inventory.spawner-view.items.spawnable",
					"spawnable", spawnable_amount.get(type)).text());
		}
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}
	
	public int charges_price(SpawnerType type, Block block) {
		return charges_price.get(type) * DataManager.getStack(block) * (DataManager.getSpawnerLevel(block) + 1);
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
			is[0] = CF.s.getInteger(path + ".DEFAULT.range");
			is[1] = CF.s.getInteger(path + ".DEFAULT.delay");
			is[2] = CF.s.getInteger(path + ".DEFAULT.amount");
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
			if(CF.s.is(path) == false) return is[i];
			int a = CF.s.getInteger(path);
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
			is[0] = CF.s.getBoolean(path + ".DEFAULT.range");
			is[1] = CF.s.getBoolean(path + ".DEFAULT.delay");
			is[2] = CF.s.getBoolean(path + ".DEFAULT.amount");
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
			if(CF.s.is(path) == false) return is[i];
			return CF.s.getBoolean(path);
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
			i = CF.s.getInteger(path + ".DEFAULT");
			Stream.of(SpawnerType.values())
			.forEach(type -> {
				int a = CF.s.getInteger(path + "." + type.name());
				if(a == 0 || a == i) return;
				map.put(type, a);
			});
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
			if((u = CF.s.getBoolean(tp + ".use-price")) == false) return;
			super.load();
		}
		
	}
	
	public static record ValueChanger(double change, ChangerType type) {
		
		public double change(double value) {
			return type.change(value, change);
		}
		
		public static ValueChanger of(String path) {
			String s = CF.s.getString(path);
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
