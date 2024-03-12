package mc.rellox.spawnermeta.utility;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import mc.rellox.spawnermeta.SpawnerMeta;
import mc.rellox.spawnermeta.api.spawner.ICache;
import mc.rellox.spawnermeta.api.spawner.IGenerator;
import mc.rellox.spawnermeta.api.spawner.IVirtual;
import mc.rellox.spawnermeta.configuration.Language;
import mc.rellox.spawnermeta.configuration.Settings;
import mc.rellox.spawnermeta.spawner.ActiveVirtual;
import mc.rellox.spawnermeta.spawner.type.SpawnerType;
import mc.rellox.spawnermeta.text.Text;
import mc.rellox.spawnermeta.text.content.Content;
import mc.rellox.spawnermeta.text.order.IOrder;
import mc.rellox.spawnermeta.utility.reflect.Reflect.RF;
import mc.rellox.spawnermeta.view.layout.LayoutRegistry;

public final class DataManager {
	
	private static NamespacedKey key_empty;

	private static NamespacedKey key_upgrades;
	private static NamespacedKey key_charges;

	private static NamespacedKey key_new;
	private static NamespacedKey key_default;
	private static NamespacedKey key_type;
	private static NamespacedKey key_stack;
	private static NamespacedKey key_levels;
	private static NamespacedKey key_spawnable;
	private static NamespacedKey key_enabled;
	
	private static NamespacedKey key_owner;
	
	private static NamespacedKey key_spawned;
	
	private static Block last;
	private static CreatureSpawner spawner;
	
	public static void initialize() {
		key_empty = new NamespacedKey(SpawnerMeta.instance(), "empty");
		key_upgrades = new NamespacedKey(SpawnerMeta.instance(), "upgrades");
		key_charges = new NamespacedKey(SpawnerMeta.instance(), "charges");
		key_spawnable = new NamespacedKey(SpawnerMeta.instance(), "maximum");
		key_new = new NamespacedKey(SpawnerMeta.instance(), "new");
		key_default = new NamespacedKey(SpawnerMeta.instance(), "default");
		key_type = new NamespacedKey(SpawnerMeta.instance(), "type");
		key_stack = new NamespacedKey(SpawnerMeta.instance(), "stack");
		key_levels = new NamespacedKey(SpawnerMeta.instance(), "levels");
		key_spawnable = new NamespacedKey(SpawnerMeta.instance(), "maximum");
		key_enabled = new NamespacedKey(SpawnerMeta.instance(), "enabled");
		key_owner = new NamespacedKey(SpawnerMeta.instance(), "owner");
		key_spawned = new NamespacedKey(SpawnerMeta.instance(), "spawned");
	}
	
	public static int[] i() {
		return new int[] {1, 1, 1};
	}

	public static ItemStack getSpawner(IVirtual spawner, int a) {
		return getSpawners(spawner.getType(), spawner.getUpgradeLevels(), spawner.getCharges(), spawner.getSpawnable(),
				a, spawner.isEmpty(), true).get(0);
	}

	public static ItemStack getSpawner(SpawnerType type, int amount) {
		return getSpawners(type, i(), 0, Settings.settings.spawnable_amount.get(type), amount, false, true).get(0);
	}
	
	public static List<ItemStack> getSpawners(Block block, boolean ignore) {
		return getSpawners(getType(block), getUpgradeLevels(block), getCharges(block),
				getSpawnable(block), getStack(block), isEmpty(block), ignore);
	}
	
	public static List<ItemStack> getSpawners(IGenerator generator, boolean ignore) {
		ICache cache = generator.cache();
		return getSpawners(cache.type(), generator.spawner().getUpgradeLevels(),
				cache.charges(), cache.spawnable(), cache.stack(),
				cache.empty(), ignore);
	}

	public static List<ItemStack> getSpawners(SpawnerType type, int amount, boolean empty, boolean ignore) {
		return getSpawners(type, i(), 0, Settings.settings.spawnable_amount.get(type), amount, empty, ignore);
	}

	/**
	 * @param type - spawner type
	 * @param levels - range, delay and amount
	 * @param charges - charges
	 * @param spawnable - spawnable amount
	 * @param amount - amount
	 * @param empty - is empty
	 * @param ignore - ignore spawnable amount splitting
	 * @return List of items, can be empty
	 */
	
	public static List<ItemStack> getSpawners(SpawnerType type, int[] levels, int charges, int spawnable, int amount,
			boolean empty, boolean ignore) {
		List<ItemStack> list = new ArrayList<>();
		if(type == null) type = SpawnerType.PIG;
		if(empty == true && Settings.settings.empty_store_inside == false) type = SpawnerType.EMPTY;
		if(Settings.settings.spawnable_enabled == true) {
			if(spawnable <= 0 || amount <= 0) return list;
			if(amount > 1) {
				if(ignore == true) {
					list.addAll(getSpawner(type, levels, charges, spawnable, amount, empty));
					return list;
				}
				if(amount > spawnable) amount = spawnable;
				int e = spawnable % amount;
				if(e == 0) list.addAll(getSpawner(type, levels, charges, spawnable / amount, amount, empty));
				else {
					int d = spawnable / amount;
					list.addAll(getSpawner(type, levels, charges, d, amount - 1, empty));
					list.addAll(getSpawner(type, levels, charges, e + d, 1, empty));
				}
			} else list.addAll(getSpawner(type, levels, charges, spawnable, amount, empty));
		} else list.addAll(getSpawner(type, levels, charges, spawnable, amount, empty));
		return list;
	}
	
	public static List<ItemStack> getSpawner(SpawnerType type, int[] levels, int charges,
			int spawnable, int amount, boolean empty) {
		ItemStack item = new ItemStack(Material.SPAWNER, 1);
		ItemMeta meta = item.getItemMeta();
		List<Content> name;
		if(empty == true) {
			if(Settings.settings.empty_store_inside == true && type != SpawnerType.EMPTY)
				name = Language.list("Spawner-item.empty-stored.name", "type", type);
			else name = Language.list("Spawner-item.empty.name");
		} else name = Language.list("Spawner-item.regular.name", "type", type);

		if(name.size() > 0) meta.setDisplayName(name.remove(0).text());
		
		IOrder order = LayoutRegistry.order_spawner.oderer();
		
		order.named(name);
		order.submit("HEADER", () -> Language.list("Spawner-item.header"));
		order.submit("RANGE", () -> Language.list("Spawner-item.upgrade.range", "level", Utils.roman(levels[0])));
		order.submit("DELAY", () -> Language.list("Spawner-item.upgrade.delay", "level", Utils.roman(levels[1])));
		order.submit("AMOUNT", () -> Language.list("Spawner-item.upgrade.amount", "level", Utils.roman(levels[2])));
		if(Settings.settings.charges_enabled == true) {
			boolean inf = charges >= 1_000_000_000;
			order.submit("CHARGES", () -> Language.list("Spawner-item.charges",
					"charges", inf ? Text.infinity : charges));
		}
		if(Settings.settings.spawnable_enabled == true) {
			order.submit("SPAWNABLE", () -> Language.list("Spawner-item.spawnable", "spawnable", spawnable));
		}

		meta.setLore(order.build());
		
		meta.addItemFlags(ItemFlag.values());
		Utils.hideCustomFlags(meta);
		item.setItemMeta(meta);
		modify(item, type, levels, charges, spawnable, empty);
		List<ItemStack> list = new ArrayList<>(amount + 63 >> 6);
		while(amount > 0) {
			ItemStack clone = item.clone();
			clone.setAmount(amount >= 64 ? 64 : amount);
			list.add(clone);
			amount -= 64;
		}
		return list;
	}
	
	public static List<ItemStack> getSpawner(SpawnerType type, String values, int amount, boolean empty) {
		if(values == null || values.isEmpty() == true) return List.of();
		String[] vs = values.split("[,;:]");
		if(vs.length < 5) {
			var os = vs;
			vs = new String[] {"-", "-", "-", "-", "-"};
			for(int i = 0; i < os.length; i++) vs[i] = os[i];
		}
		int[] is = new int[5];
		int i = 0;
		for(; i < 5; i++) {
			if(i > 2 && vs[i].equals("inf") == true
					|| vs[i].equals("infinite") == true) is[i] = 1_500_000_000;
			else if(vs[i].equals("-") == true) is[i] = 0;
			else if(Utils.isInteger(vs[i]) == true) is[i] = Integer.parseInt(vs[i]);
			else return List.of();
		}
		int[] ls = Settings.settings.upgrades_levels.get(type);
		for(i = 0; i < 3; i++) {
			if(is[i] < 1) is[i] = 1;
			if(is[i] > ls[i]) is[i] = ls[i];
		}
		if(is[3] < 0) is[3] = 0;
		if(is[4] <= 0) is[4] = Settings.settings.spawnable_amount.get(type);
		return getSpawner(type, is, is[3], is[4], amount, empty);
	}

	public static void setNewSpawner(Player player, Block block, boolean empty) {
		if(getNew(block) == 1) {
			int[] r = getUpgradeAttributes(block);
			setDelayConstant(block, r[1]);
			return;
		}
		CreatureSpawner cs = cast(block);
		if(cs == null) return;
		EntityType entity = cs.getSpawnedType();
		SpawnerType type;
		if(empty == true) type = SpawnerType.EMPTY;
		else {
			if(entity == null) {
				if(Settings.settings.empty_enabled == true) {
					type = SpawnerType.EMPTY;
					empty = true;
				} else type = SpawnerType.PIG;
			} else type = SpawnerType.of(entity);
			if(type == null) type = SpawnerType.PIG;
		}
		setNewSpawner(player, block, type, i(), 0, Settings.settings.spawnable_amount.get(type), empty);
	}

	public static void setNewSpawner(Player player, Block block, SpawnerType type, int[] levels,
			int charges, int spawnable, boolean empty) {
		setOwner(block, player);
		setNew(block);
		setStack(block, 1);
		int[] r = attributes(type, levels);
		setUpgradeLevels(block, levels);
		setDelayConstant(block, r[1]);
		setSpawnable(block, spawnable);
		setDefault(block);
		setOneCount(block);
		setType(block, type);
		setEnabled(block, true);
		if(empty == true) setEmpty(block);
		setCharges(block, charges);
	}
	
	public static void updateValues(Block block) {
		SpawnerType type = getType(block);
		int[] l = getUpgradeLevels(block), r = attributes(type, l);
		setUpgradeLevels(block, l);
		setDelay(block, r[1]);
		setDefault(block);
		setOneCount(block);
		setDelayConstant(block, r[1]);
	}
	
	public static void reset(Block block) {
		CreatureSpawner cs = cast(block);
		if(cs == null) return;
		cs.setMaxSpawnDelay(800);
		cs.setMaxSpawnDelay(200);
		cs.setRequiredPlayerRange(16);
		cs.setSpawnCount(4);
		cs.update();
	}
	
	public static boolean cancelledByOwner(Block block, Player player) {
		UUID owner = getOwner(block);
		return owner != null && player.getUniqueId().equals(owner) == false;
	}
	
	private static int[] attributes(SpawnerType type, int[] l) {
		int[] r = Settings.settings.spawner_values.get(type), n = Settings.settings.spawner_value_increase.get(type);
		for(int k = 0; k < 3; k++) for(int i = 1; i < l[k]; i++) r[k] += n[k];
		return r;
	}

	protected static void updateType(Block block) {
		updateType(block, null);
	}

	protected static void updateType(Block block, SpawnerType type) {
		CreatureSpawner cs = cast(block);
		if(cs == null) return;
		cs.setSpawnedType((type == null ? getType(block) : type).entity());
		cs.update();
	}

	public static void modify(ItemStack item, SpawnerType type, int[] levels,
			int charges, int spawnable, boolean empty) {
		ItemMeta meta = item.getItemMeta();
		PersistentDataContainer p = meta.getPersistentDataContainer();
		p.set(key_type, PersistentDataType.STRING, type.name());
		p.set(key_upgrades, PersistentDataType.INTEGER_ARRAY, Arrays.copyOf(levels, 3));
		p.set(key_charges, PersistentDataType.INTEGER, charges);
		p.set(key_spawnable, PersistentDataType.INTEGER, spawnable);
		if(empty == true) p.set(key_empty, PersistentDataType.INTEGER, 1);
		item.setItemMeta(meta);
	}

	public static IVirtual getSpawnerItem(ItemStack item) {
		return getSpawnerItem(item, false);
	}

	public static IVirtual getSpawnerItem(ItemStack item, boolean nullable) {
		if(item == null || item.getType() != Material.SPAWNER) return null;
		ItemMeta meta = item.getItemMeta();
		PersistentDataContainer p = meta.getPersistentDataContainer();
		SpawnerType type;
		if(p.has(key_type, PersistentDataType.STRING) == true) {
			type = SpawnerType.of(p.get(key_type, PersistentDataType.STRING));
		} else if(meta instanceof BlockStateMeta bsm) {
			if(bsm.getBlockState() instanceof CreatureSpawner cs) {
				type = SpawnerType.ofAll(cs.getSpawnedType());
			} else type = nullable ? null : SpawnerType.PIG;
		} else type = nullable ? null : SpawnerType.PIG;
		boolean empty = p.getOrDefault(key_empty, PersistentDataType.INTEGER, 0) >= 1;
		if(type == null) {
			if(Settings.settings.empty_enabled == true) {
				type = SpawnerType.EMPTY;
				empty = true;
			} else if(nullable == false) type = SpawnerType.PIG;
			else return null;
		}
		int[] levels = p.getOrDefault(key_upgrades, PersistentDataType.INTEGER_ARRAY, i());
		if(levels.length > 3) levels = Arrays.copyOf(levels, 3);
		int charges = p.getOrDefault(key_charges, PersistentDataType.INTEGER, 0);
		int spawnable = p.getOrDefault(key_spawnable, PersistentDataType.INTEGER,
				Settings.settings.spawnable_enabled
				? Settings.settings.spawnable_amount.get(type) : 0);
		return new ActiveVirtual(type, levels, charges, spawnable, empty);
	}

	public static IVirtual getSpawnerItem(Block block) {
		if(block == null || block.getType() != Material.SPAWNER) return null;
		return new ActiveVirtual(getType(block), getUpgradeLevels(block),
				getCharges(block), getSpawnable(block), isEmpty(block));
	}

	public static void setNew(Block block) {
		CreatureSpawner cs = cast(block);
		if(cs == null) return;
		cs.getPersistentDataContainer().set(key_new, PersistentDataType.INTEGER, 1);
		cs.update();
	}

	public static int getNew(Block block) {
		CreatureSpawner cs = cast(block);
		if(cs == null) return 0;
		return cs.getPersistentDataContainer().getOrDefault(key_new, PersistentDataType.INTEGER, 0);
	}

	public static void setEnabled(Block block, boolean b) {
		CreatureSpawner cs = cast(block);
		if(cs == null) return;
		cs.getPersistentDataContainer().set(key_enabled, PersistentDataType.INTEGER, b ? 1 : 0);
		cs.update();
	}

	public static boolean isEnabled(Block block) {
		CreatureSpawner cs = cast(block);
		if(cs == null) return false;
		return cs.getPersistentDataContainer().getOrDefault(key_enabled, PersistentDataType.INTEGER, 1) >= 1;
	}

	public static void setEmpty(Block block) {
		CreatureSpawner cs = cast(block);
		if(cs == null) return;
		cs.getPersistentDataContainer().set(key_empty, PersistentDataType.INTEGER, 1);
		cs.update();
	}

	public static boolean isEmpty(Block block) {
		CreatureSpawner cs = cast(block);
		if(cs == null) return false;
		return cs.getPersistentDataContainer().getOrDefault(key_empty, PersistentDataType.INTEGER, 0) >= 1;
	}

	public static void setSpawned(Entity entity) {
		entity.getPersistentDataContainer().set(key_spawned, PersistentDataType.BYTE, (byte) 1);
	}

	public static boolean isSpawned(Entity entity) {
		return entity.getPersistentDataContainer().getOrDefault(key_spawned, PersistentDataType.BYTE, (byte) 0) > 0;
	}
	
	private static void setOneCount(Block block) {
		CreatureSpawner cs = cast(block);
		if(cs == null) return;
		cs.setSpawnCount(1);
		cs.update();
	}

	public static boolean isItemSpawner(Block block) {
		CreatureSpawner cs = cast(block);
		if(cs == null) return false;
		return cs.getSpawnedType() == EntityType.DROPPED_ITEM;
	}

	public static void setType(Block block, SpawnerType type) {
		CreatureSpawner cs = cast(block);
		if(cs == null) return;
		cs.getPersistentDataContainer().set(key_type, PersistentDataType.STRING, type.name());
		cs.setSpawnedType(type.entity());
		cs.update();
	}

	public static SpawnerType getType(Block block) {
		CreatureSpawner cs = cast(block);
		if(cs == null) return SpawnerType.PIG;
		String st = cs.getPersistentDataContainer().get(key_type, PersistentDataType.STRING);
		SpawnerType type = st == null ? SpawnerType.of(cs.getSpawnedType()) : SpawnerType.of(st);
		return type == null ? SpawnerType.PIG : type;
	}

	public static boolean isEmptyType(Block block) {
		CreatureSpawner cs = cast(block);
		if(cs == null) return true;
		String st = cs.getPersistentDataContainer().get(key_type, PersistentDataType.STRING);
		if(st == null || st.equalsIgnoreCase("EMPTY") == true) return true;
		return cs.getSpawnedType() == EntityType.AREA_EFFECT_CLOUD;
	}

	public static void setStack(Block block, int s) {
		CreatureSpawner cs = cast(block);
		if(cs == null) return;
		cs.getPersistentDataContainer().set(key_stack, PersistentDataType.INTEGER, s);
		cs.update();
	}

	public static int getStack(Block block) {
		CreatureSpawner cs = cast(block);
		if(cs == null) return 1;
		return cs.getPersistentDataContainer().getOrDefault(key_stack, PersistentDataType.INTEGER, 1);
	}

	public static void setOwner(Block block, Player player) {
		CreatureSpawner cs = cast(block);
		if(cs == null || player == null) return;
		cs.getPersistentDataContainer().set(key_owner, PersistentDataType.STRING, player.getUniqueId().toString());
		cs.update();
	}

	public static void setOwner(Block block, UUID id) {
		CreatureSpawner cs = cast(block);
		if(cs == null) return;
		cs.getPersistentDataContainer().set(key_owner, PersistentDataType.STRING, id.toString());
		cs.update();
	}

	public static boolean isOwned(Block block) {
		CreatureSpawner cs = cast(block);
		if(cs == null) return false;
		String id = cs.getPersistentDataContainer().get(key_owner, PersistentDataType.STRING);
		return id != null;
	}

	public static UUID getOwner(Block block) {
		CreatureSpawner cs = cast(block);
		if(cs == null) return null;
		String id = cs.getPersistentDataContainer().get(key_owner, PersistentDataType.STRING);
		if(id == null) return null;
		try {
			UUID uuid = UUID.fromString(id);
			return uuid;
		} catch (Exception e) {}
		return null;
	}

	public static int getSpawnable(Block block) {
		CreatureSpawner cs = cast(block);
		if(cs == null) return -1;
		PersistentDataContainer p = cs.getPersistentDataContainer();
		int s = p.getOrDefault(key_spawnable, PersistentDataType.INTEGER, -1);
		if(s < 0) {
			s = Settings.settings.spawnable_amount.get(getType(block));
			p.set(key_spawnable, PersistentDataType.INTEGER, s);
		}
		return s;
	}

	public static void setSpawnable(Block block, int s) {
		CreatureSpawner cs = cast(block);
		if(cs == null) return;
		cs.getPersistentDataContainer().set(key_spawnable, PersistentDataType.INTEGER, s);
		cs.update();
	}

	public static int[] getUpgradeAttributes(Block block) {
		SpawnerType type = getType(block);
		var vs = Settings.settings.spawner_values.get(type);
		var is = Settings.settings.spawner_value_increase.get(type);
		var ls = getUpgradeLevels(block);
		for(int i = 0; i < 3; i++)
			for(int j = 1; j < ls[i]; j++) vs[i] += is[i];
		return vs;
	}
	
	public static boolean isRotating(Block block) {
		CreatureSpawner cs = cast(block);
		return cs == null ? false : cs.getRequiredPlayerRange() > 0;
	}
	
	public static void setRotating(Block block, boolean b) {
		CreatureSpawner cs = cast(block);
		if(cs == null) return;
		if(b == true) {
			int[] is = getUpgradeAttributes(block);
			cs.setRequiredPlayerRange(is[0]);
		} else cs.setRequiredPlayerRange(0);
		cs.update();
	}

	public static void setUpgradeLevels(Block block, int[] s) {
		CreatureSpawner cs = cast(block);
		if(cs == null) return;
		if(s.length > 3) s = Arrays.copyOf(s, 3);
		cs.getPersistentDataContainer().set(key_levels, PersistentDataType.INTEGER_ARRAY, s);
		cs.update();
	}

	public static int[] getUpgradeLevels(Block block) {
		CreatureSpawner cs = cast(block);
		if(cs == null) return i();
		int[] is = cs.getPersistentDataContainer().getOrDefault(key_levels, PersistentDataType.INTEGER_ARRAY, i());
		return is.length > 3 ? Arrays.copyOf(is, 3) : is;
	}

	public static int getSpawnerLevel(Block block) {
		int r = 0, i = 0;
		int[] s = getUpgradeLevels(block);
		do {
			r += s[i++];
		} while(i < 3);
		return r - 3;
	}

	public static void resetDelay(Block block) {
		setDelay(block, getDelay(block));
	}

	public static void setDelayConstant(Block block, int s) {
		CreatureSpawner cs = cast(block);
		if(cs == null) return;
		int min = cs.getMinSpawnDelay();
		int max = cs.getMaxSpawnDelay();
		if(min == s && max == s) return;
		if(s < 1) s = 1;
		if((++s > min && s < max) || s < min) {
			cs.setMinSpawnDelay(s);
			cs.setMaxSpawnDelay(s);
		} else {
			cs.setMaxSpawnDelay(s);
			cs.setMinSpawnDelay(s);
		}
		cs.setDelay(s);
		cs.update();
	}

	public static int getDelay(Block block) {
		CreatureSpawner cs = cast(block);
		return cs == null ? 0 : cs.getMaxSpawnDelay();
	}

	public static void setDelay(Block block, int s) {
		CreatureSpawner cs = cast(block);
		if(cs == null) return;
		cs.setDelay(s);
		cs.update();
	}

	public static int getDefault(Block block) {
		CreatureSpawner cs = cast(block);
		if(cs == null) return 0;
		return cs.getPersistentDataContainer().getOrDefault(key_default, PersistentDataType.INTEGER, 0);
	}

	public static void setDefault(Block block) {
		CreatureSpawner cs = cast(block);
		if(cs == null) return;
		cs.getPersistentDataContainer().set(key_default, PersistentDataType.INTEGER, 1);
		cs.update();
	}
	
	public static void setCharges(Block block, int c) {
		if(c < 0) c = 0;
		CreatureSpawner cs = cast(block);
		if(cs == null) return;
		PersistentDataContainer p = cs.getPersistentDataContainer();
		p.set(key_charges, PersistentDataType.INTEGER, c);
		cs.update();
	}

	public static int getCharges(Block block) {
		CreatureSpawner cs = cast(block);
		return cs == null ? 0 : cs.getPersistentDataContainer()
				.getOrDefault(key_charges, PersistentDataType.INTEGER, 0);
	}
	
	public static EntityType getEntity(Block block) {
		var cs = cast(block);
		if(cs == null) return null;
		return cs.getSpawnedType();
	}
	
	public static EntityType getEntity(ItemStack item) {
		if(item == null) return null;
		ItemMeta meta = item.getItemMeta();
		if(meta == null) return null;
		if(meta instanceof BlockStateMeta bs
				&& bs.getBlockState() instanceof CreatureSpawner cs)
			return cs.getSpawnedType();
		return null;
	}
	
	private static CreatureSpawner cast(Block block) {
		try {
			if(block.equals(last) == true) return spawner;
			if(block.getState() instanceof CreatureSpawner cs) {
				last = block;
				return spawner = cs;
			}
		} catch (Exception e) {
			RF.debug(e);
		}
		return null;
	}


}
