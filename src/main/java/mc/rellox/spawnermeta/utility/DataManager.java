package mc.rellox.spawnermeta.utility;

import io.github.rvskele.paperlib.PaperLib;
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
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

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
    private static CreatureSpawner cached;

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
        key_enabled = new NamespacedKey(SpawnerMeta.instance(), "enabled");
        key_owner = new NamespacedKey(SpawnerMeta.instance(), "owner");
        key_spawned = new NamespacedKey(SpawnerMeta.instance(), "spawned");
    }

    public static int[] i() {
        return new int[] { 1, 1, 1 };
    }

    public static NamespacedKey typeKey() {
        return key_type;
    }

    public static NamespacedKey ownerKey() {
        return key_owner;
    }

    public static ItemStack getSpawner(IVirtual spawner, int a) {
        return getSpawners(spawner.getType(), spawner.getUpgradeLevels(), spawner.getCharges(), spawner.getSpawnable(),
                a, spawner.isEmpty(), true).getFirst();
    }

    public static ItemStack getSpawner(SpawnerType type, int amount) {
        return getSpawners(type, i(), 0, Settings.settings.spawnable_amount.get(type),
                amount, false, true).getFirst();
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
     * @param type      spawner type
     * @param levels    range, delay and amount
     * @param charges   charges
     * @param spawnable spawnable amount
     * @param amount    amount
     * @param empty     is empty
     * @param ignore    ignore spawnable amount splitting
     * @return List of items, can be empty
     */

    public static List<ItemStack> getSpawners(SpawnerType type, int[] levels, int charges, int spawnable, int amount,
                                              boolean empty, boolean ignore) {
        List<ItemStack> list = new ArrayList<>();
        if(type == null) type = SpawnerType.PIG;
        if(empty && !Settings.settings.empty_store_inside) type = SpawnerType.EMPTY;
        if(Settings.settings.spawnable_enabled) {
            if(spawnable <= 0 || amount <= 0) return list;
            if(amount > 1) {
                if(ignore) {
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
        if(empty) {
            if(Settings.settings.empty_store_inside && type != SpawnerType.EMPTY)
                name = Language.list("Spawner-item.empty-stored.name", "type", type);
            else name = Language.list("Spawner-item.empty.name");
        } else name = Language.list("Spawner-item.regular.name", "type", type);

        if(!name.isEmpty()) meta.setDisplayName(name.removeFirst().text());

        IOrder order = LayoutRegistry.order_spawner.oderer();

        order.named(name);
        order.submit("HEADER", () -> Language.list("Spawner-item.header"));
        order.submit("RANGE", () -> Language.list("Spawner-item.upgrade.range", "level", Utility.roman(levels[0])));
        order.submit("DELAY", () -> Language.list("Spawner-item.upgrade.delay", "level", Utility.roman(levels[1])));
        order.submit("AMOUNT", () -> Language.list("Spawner-item.upgrade.amount", "level", Utility.roman(levels[2])));
        if(Settings.settings.charges_enabled) {
            boolean inf = charges >= 1_000_000_000;
            order.submit("CHARGES", () -> Language.list("Spawner-item.charges",
                    "charges", inf ? Text.infinity : charges));
        }
        if(Settings.settings.spawnable_enabled) {
            order.submit("SPAWNABLE", () -> Language.list("Spawner-item.spawnable", "spawnable", spawnable));
        }

        order.submit("INFO", () -> Language.list("Spawner-item.info"));

        meta.setLore(order.build());

        meta.addItemFlags(Utility.ITEM_FLAGS);

        item.setItemMeta(meta);
        modify(item, type, levels, charges, spawnable, empty);
        List<ItemStack> list = new ArrayList<>(amount + 63 >> 6);
        while(amount > 0) {
            ItemStack clone = item.clone();
            clone.setAmount(Math.min(amount, 64));
            list.add(clone);
            amount -= 64;
        }
        return list;
    }

    public static List<ItemStack> getSpawner(SpawnerType type, String values, int amount, boolean empty) {
        if(values == null || values.isEmpty()) return List.of();
        String[] vs = values.split("[,;:]");
        if(vs.length < 5) {
            var os = vs;
            vs = new String[] { "-", "-", "-", "-", "-" };
            System.arraycopy(os, 0, vs, 0, os.length);
        }
        int[] is = new int[5];
        int i = 0;
        for(; i < 5; i++) {
            if(i > 2 && vs[i].equals("inf")
                    || vs[i].equals("infinite")) is[i] = 1_500_000_000;
            else if(vs[i].equals("-")) is[i] = 0;
            else if(Utility.isInteger(vs[i])) is[i] = Integer.parseInt(vs[i]);
            else return List.of();
        }
        int[] levels = Settings.settings.upgrades_levels.get(type);
        for(i = 0; i < 3; i++) {
            if(is[i] < 1) is[i] = 1;
            if(is[i] > levels[i]) is[i] = levels[i];
        }
        if(is[3] < 0) is[3] = 0;
        if(is[4] <= 0) is[4] = Settings.settings.spawnable_amount.get(type);
        return getSpawner(type, is, is[3], is[4], amount, empty);
    }

    public static void setNewSpawner(Player player, Block block, boolean empty) {
        if(getNew(block) == 1) {
            int[] attributes = getUpgradeAttributes(block);
            setDelayConstant(block, attributes[1]);
            return;
        }
        CreatureSpawner spawner = cast(block);
        if(spawner == null) return;
        EntityType entity = spawner.getSpawnedType();
        SpawnerType type;
        if(empty) type = SpawnerType.EMPTY;
        else {
            if(entity == null) {
                if(Settings.settings.empty_enabled) {
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
        int[] attributes = attributes(block, type, levels);
        setUpgradeLevels(block, levels);
        setDelayConstant(block, attributes[1]);
        setSpawnable(block, spawnable);
        setDefault(block);
        setOneCount(block);
        setType(block, type);
        setEnabled(block, true);
        if(empty) setEmpty(block);
        setCharges(block, charges);
    }

    public static void updateValues(Block block) {
        SpawnerType type = getType(block);
        int[] levels = getUpgradeLevels(block), attributes = attributes(block, type, levels);
        setUpgradeLevels(block, levels);
        setDelay(block, attributes[1]);
        setDefault(block);
        setOneCount(block);
        setDelayConstant(block, attributes[1]);
    }

    public static void reset(Block block) {
        CreatureSpawner spawner = cast(block);
        if(spawner == null) return;
        int max = spawner.getMaxSpawnDelay();
        if(max >= 200) {
            spawner.setMinSpawnDelay(200);
            spawner.setMaxSpawnDelay(800);
        } else {
            spawner.setMaxSpawnDelay(800);
            spawner.setMinSpawnDelay(200);
        }
        spawner.setRequiredPlayerRange(16);
        spawner.setSpawnCount(4);
        if(spawner.getSpawnedType() == SpawnerType.EMPTY.entity())
            spawner.setSpawnedType(null);
        spawner.update();
    }

    public static void validateEmpty(Block block) {
        if(!Settings.settings.reset_spawner_values) return;
        CreatureSpawner spawner = cast(block);
        if(spawner == null) return;
        EntityType type = spawner.getSpawnedType();
        if(type != null) return;
        boolean empty = isEmpty(block);
        if(!empty) return;
        spawner.setSpawnedType(SpawnerType.EMPTY.entity());
        spawner.update();
    }

    public static boolean cancelledByOwner(Block block, Player player) {
        UUID owner = getOwner(block);
        return owner != null && !player.getUniqueId().equals(owner);
    }

    private static int[] attributes(Block block, SpawnerType type, int[] levels) {
        int hash = block.hashCode();

        Settings.IRange[] ranges = Settings.settings.spawner_values.get(type);
        int[] values = { ranges[0].roll(hash), ranges[1].roll(hash), ranges[2].roll(hash) };
        int[] increases = Settings.settings.spawner_value_increase.get(type);

        for(int i = 0; i < 3; i++)
            for(int j = 1; j < levels[i]; j++)
                values[i] += increases[i];
        return values;
    }

    public static void modify(ItemStack item, SpawnerType type, int[] levels,
                              int charges, int spawnable, boolean empty) {
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer container = meta.getPersistentDataContainer();
        container.set(key_type, PersistentDataType.STRING, type.name());
        container.set(key_upgrades, PersistentDataType.INTEGER_ARRAY, Arrays.copyOf(levels, 3));
        container.set(key_charges, PersistentDataType.INTEGER, charges);
        container.set(key_spawnable, PersistentDataType.INTEGER, spawnable);
        if(empty) container.set(key_empty, PersistentDataType.INTEGER, 1);
        item.setItemMeta(meta);
    }

    public static IVirtual getSpawnerItem(ItemStack item) {
        return getSpawnerItem(item, false);
    }

    public static IVirtual getSpawnerItem(ItemStack item, boolean nullable) {
        if(item == null || item.getType() != Material.SPAWNER) return null;
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer container = meta.getPersistentDataContainer();
        SpawnerType type;
        if(container.has(key_type, PersistentDataType.STRING)) {
            type = SpawnerType.of(container.get(key_type, PersistentDataType.STRING));
        } else if(meta instanceof BlockStateMeta bsm) {
            if(bsm.getBlockState() instanceof CreatureSpawner cs) {
                type = SpawnerType.ofAll(cs.getSpawnedType());
            } else type = nullable ? null : SpawnerType.PIG;
        } else type = nullable ? null : SpawnerType.PIG;
        boolean empty = container.getOrDefault(key_empty, PersistentDataType.INTEGER, 0) >= 1;
        if(type == null) {
            if(Settings.settings.empty_enabled) {
                type = SpawnerType.EMPTY;
                empty = true;
            } else if(!nullable) type = SpawnerType.PIG;
            else return null;
        }
        int[] levels = container.getOrDefault(key_upgrades, PersistentDataType.INTEGER_ARRAY, i());
        if(levels.length > 3) levels = Arrays.copyOf(levels, 3);
        int charges = container.getOrDefault(key_charges, PersistentDataType.INTEGER, 0);
        int spawnable = container.getOrDefault(key_spawnable, PersistentDataType.INTEGER,
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
        CreatureSpawner spawner = cast(block);
        if(spawner == null) return;
        spawner.getPersistentDataContainer().set(key_new, PersistentDataType.INTEGER, 1);
        spawner.update();
    }

    public static int getNew(Block block) {
        CreatureSpawner spawner = cast(block);
        if(spawner == null) return 0;
        return spawner.getPersistentDataContainer()
                .getOrDefault(key_new, PersistentDataType.INTEGER, 0);
    }

    public static void setEnabled(Block block, boolean b) {
        CreatureSpawner spawner = cast(block);
        if(spawner == null) return;
        spawner.getPersistentDataContainer().set(key_enabled, PersistentDataType.INTEGER, b ? 1 : 0);
        spawner.update();
    }

    public static boolean isEnabled(Block block) {
        CreatureSpawner spawner = cast(block);
        if(spawner == null) return false;
        return spawner.getPersistentDataContainer()
                .getOrDefault(key_enabled, PersistentDataType.INTEGER, 1) >= 1;
    }

    public static void setEmpty(Block block) {
        CreatureSpawner spawner = cast(block);
        if(spawner == null) return;
        spawner.getPersistentDataContainer().set(key_empty, PersistentDataType.INTEGER, 1);
        spawner.update();
    }

    public static boolean isEmpty(Block block) {
        CreatureSpawner spawner = cast(block);
        if(spawner == null) return false;
        return spawner.getPersistentDataContainer()
                .getOrDefault(key_empty, PersistentDataType.INTEGER, 0) >= 1;
    }

    public static void setSpawned(Entity entity) {
        entity.getPersistentDataContainer().set(key_spawned, PersistentDataType.BYTE, (byte) 1);
    }

    public static boolean isSpawned(Entity entity) {
        return entity.getPersistentDataContainer()
                .getOrDefault(key_spawned, PersistentDataType.BYTE, (byte) 0) > 0;
    }

    private static void setOneCount(Block block) {
        CreatureSpawner spawner = cast(block);
        if(spawner == null) return;
        spawner.setSpawnCount(1);
        spawner.update();
    }

    public static boolean isItemSpawner(Block block) {
        CreatureSpawner spawner = cast(block);
        if(spawner == null) return false;
        return Utility.isItem(spawner.getSpawnedType());
    }

    public static void setType(Block block, SpawnerType type) {
        CreatureSpawner spawner = cast(block);
        if(spawner == null) return;
        spawner.getPersistentDataContainer().set(key_type, PersistentDataType.STRING, type.name());
        spawner.setSpawnedType(type.entity());
        spawner.update();
    }

    public static SpawnerType getType(Block block) {
        CreatureSpawner spawner = cast(block);
        if(spawner == null) return SpawnerType.PIG;
        String type_string = spawner.getPersistentDataContainer().get(key_type, PersistentDataType.STRING);
        SpawnerType type = type_string == null
                ? SpawnerType.of(spawner.getSpawnedType())
                : SpawnerType.of(type_string);
        return type == null ? SpawnerType.PIG : type;
    }

    public static boolean isEmptyType(Block block) {
        CreatureSpawner spawner = cast(block);
        if(spawner == null) return true;
        String type = spawner.getPersistentDataContainer().get(key_type, PersistentDataType.STRING);
        if(type == null || type.equalsIgnoreCase("EMPTY")) return true;
        return spawner.getSpawnedType() == EntityType.AREA_EFFECT_CLOUD;
    }

    public static void setStack(Block block, int s) {
        CreatureSpawner spawner = cast(block);
        if(spawner == null) return;
        spawner.getPersistentDataContainer().set(key_stack, PersistentDataType.INTEGER, s);
        spawner.update();
    }

    public static int getStack(Block block) {
        CreatureSpawner spawner = cast(block);
        if(spawner == null) return 1;
        return spawner.getPersistentDataContainer()
                .getOrDefault(key_stack, PersistentDataType.INTEGER, 1);
    }

    public static void setOwner(Block block, Player player) {
        CreatureSpawner spawner = cast(block);
        if(spawner == null || player == null) return;
        spawner.getPersistentDataContainer()
                .set(key_owner, PersistentDataType.STRING, player.getUniqueId().toString());
        spawner.update();
    }

    public static void setOwner(Block block, UUID id) {
        CreatureSpawner spawner = cast(block);
        if(spawner == null) return;
        spawner.getPersistentDataContainer().set(key_owner, PersistentDataType.STRING, id.toString());
        spawner.update();
    }

    public static boolean isOwned(Block block) {
        CreatureSpawner spawner = cast(block);
        if(spawner == null) return false;
        String id = spawner.getPersistentDataContainer().get(key_owner, PersistentDataType.STRING);
        return id != null;
    }

    public static UUID getOwner(Block block) {
        CreatureSpawner spawner = cast(block);
        if(spawner == null) return null;
        String id = spawner.getPersistentDataContainer().get(key_owner, PersistentDataType.STRING);
        if(id == null) return null;
        try {
            return UUID.fromString(id);
        } catch(Exception ignored) {}
        return null;
    }

    public static int getSpawnable(Block block) {
        CreatureSpawner spawner = cast(block);
        if(spawner == null) return -1;
        PersistentDataContainer container = spawner.getPersistentDataContainer();
        int spawnable = container.getOrDefault(key_spawnable, PersistentDataType.INTEGER, -1);
        if(spawnable < 0) {
            spawnable = Settings.settings.spawnable_amount.get(getType(block));
            container.set(key_spawnable, PersistentDataType.INTEGER, spawnable);
        }
        return spawnable;
    }

    public static void setSpawnable(Block block, int spawnable) {
        CreatureSpawner spawner = cast(block);
        if(spawner == null) return;
        spawner.getPersistentDataContainer().set(key_spawnable, PersistentDataType.INTEGER, spawnable);
        spawner.update();
    }

    public static int[] getUpgradeAttributes(Block block) {
        return attributes(block, getType(block), getUpgradeLevels(block));
    }

    public static boolean isRotating(Block block) {
        CreatureSpawner spawner = cast(block);
        return spawner != null && spawner.getRequiredPlayerRange() > 0;
    }

    public static void setRotating(Block block, boolean rotating) {
        CreatureSpawner spawner = cast(block);
        if(spawner == null) return;
        if(rotating) {
            int[] attributes = getUpgradeAttributes(block);
            spawner.setRequiredPlayerRange(attributes[0]);
        } else spawner.setRequiredPlayerRange(0);
        spawner.update();
    }

    public static void setUpgradeLevels(Block block, int[] levels) {
        CreatureSpawner spawner = cast(block);
        if(spawner == null) return;
        if(levels.length > 3) levels = Arrays.copyOf(levels, 3);
        spawner.getPersistentDataContainer().set(key_levels, PersistentDataType.INTEGER_ARRAY, levels);
        spawner.update();
    }

    public static int[] getUpgradeLevels(Block block) {
        CreatureSpawner spawner = cast(block);
        if(spawner == null) return i();
        int[] levels = spawner.getPersistentDataContainer()
                .getOrDefault(key_levels, PersistentDataType.INTEGER_ARRAY, i());
        return levels.length > 3 ? Arrays.copyOf(levels, 3) : levels;
    }

    public static int getSpawnerLevel(Block block) {
        int result = 0, i = 0;
        int[] levels = getUpgradeLevels(block);
        do {
            result += levels[i++];
        } while(i < 3);
        return result - 3;
    }

    public static void resetDelay(Block block) {
        setDelay(block, getDelay(block));
    }

    public static void setDelayConstant(Block block, int interval) {
        CreatureSpawner spawner = cast(block);
        if(spawner == null) return;

        int min = spawner.getMinSpawnDelay();
        int max = spawner.getMaxSpawnDelay();
        if(min == interval && max == interval) return;

        if(interval < 1) interval = 1;
        if((++interval > min && interval < max) || interval < min) {
            spawner.setMinSpawnDelay(interval);
            spawner.setMaxSpawnDelay(interval);
        } else {
            spawner.setMaxSpawnDelay(interval);
            spawner.setMinSpawnDelay(interval);
        }

        spawner.setDelay(interval);
        spawner.update();
    }

    public static int getDelay(Block block) {
        CreatureSpawner spawner = cast(block);
        return spawner == null ? 0 : spawner.getMaxSpawnDelay();
    }

    public static void setDelay(Block block, int s) {
        CreatureSpawner spawner = cast(block);
        if(spawner == null) return;
        spawner.setDelay(s);
        spawner.update();
    }

    public static int getDefault(Block block) {
        CreatureSpawner spawner = cast(block);
        if(spawner == null) return 0;
        return spawner.getPersistentDataContainer()
                .getOrDefault(key_default, PersistentDataType.INTEGER, 0);
    }

    public static void setDefault(Block block) {
        CreatureSpawner spawner = cast(block);
        if(spawner == null) return;
        spawner.getPersistentDataContainer().set(key_default, PersistentDataType.INTEGER, 1);
        spawner.update();
    }

    public static void setCharges(Block block, int c) {
        if(c < 0) c = 0;
        CreatureSpawner spawner = cast(block);
        if(spawner == null) return;
        PersistentDataContainer container = spawner.getPersistentDataContainer();
        container.set(key_charges, PersistentDataType.INTEGER, c);
        spawner.update();
    }

    public static int getCharges(Block block) {
        CreatureSpawner spawner = cast(block);
        return spawner == null ? 0 : spawner.getPersistentDataContainer()
                .getOrDefault(key_charges, PersistentDataType.INTEGER, 0);
    }

    public static EntityType getEntity(Block block) {
        var spawner = cast(block);
        if(spawner == null) return null;
        return spawner.getSpawnedType();
    }

    public static EntityType getEntity(ItemStack item) {
        if(item == null) return null;
        ItemMeta meta = item.getItemMeta();
        if(meta == null) return null;
        if(meta instanceof BlockStateMeta state
                && state.getBlockState() instanceof CreatureSpawner spawner)
            return spawner.getSpawnedType();
        return null;
    }

    private static CreatureSpawner cast(Block block) {
        try {
            if(block.equals(last)) return cached;
            if(PaperLib.getBlockState(block, false).getState() instanceof CreatureSpawner spawner) {
                last = block;
                return cached = spawner;
            }
        } catch(Exception e) {
            RF.debug(e);
        }
        return null;
    }

}
