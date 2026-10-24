package mc.rellox.spawnermeta.utility.reflect;

import mc.rellox.spawnermeta.configuration.Settings;
import mc.rellox.spawnermeta.utility.reflect.type.Accessor;
import mc.rellox.spawnermeta.utility.reflect.type.Construct;
import mc.rellox.spawnermeta.utility.reflect.type.Invoker;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class Reflect {

    private Reflect() {}

    public static final String SERVER_VERSION;

    static {
        String name = Bukkit.getServer().getClass().getPackage().getName();
        SERVER_VERSION = name.substring(name.lastIndexOf('.') + 1);
    }

    public static final class RF {

        /**
         * Prints stack trace if {@code debug} is set to {@code true}.
         *
         * @param exception exception
         */

        public static void debug(Exception exception) {
            debug(exception, true);
        }

        /**
         * Prints stack trace if {@code debug} and {@code warn} is set to {@code true}.
         *
         * @param exception exception
         * @param warn      warn flag
         */

        public static void debug(Exception exception, boolean warn) {
            if(Settings.settings.debug && warn) exception.printStackTrace();
        }

        public static Class<?> craft(String s) {
            if(SERVER_VERSION.equalsIgnoreCase("craftbukkit"))
                return RF.get("org.bukkit.craftbukkit." + s);
            return RF.get("org.bukkit.craftbukkit." + SERVER_VERSION + "." + s);
        }

        public static Class<?> nms(String s) {
            return RF.get("net.minecraft.server." + SERVER_VERSION + "." + s);
        }

        public static Class<?> craft_player() {
            return craft("entity.CraftPlayer");
        }

        public static Class<?> entity_player() {
            return nms("EntityPlayer");
        }

        public static Class<?> entity() {
            return nms("Entity");
        }

        public static Class<?> craft_living() {
            return craft("entity.CraftLivingEntity");
        }

        public static Object nmsItemNonNull(ItemStack item) {
            Object nmsi = nmsItem(item);
            return nmsi == null ? nmsItem(new ItemStack(Material.AIR)) : nmsi;
        }

        public static Object nmsItem(ItemStack item) {
            if(item == null) return null;
            return RF.order(ItemStack.class, "asNMSCopy", ItemStack.class)
                    .invoke(item);
        }

        /**
         * Returns an enum value from the given class and name.
         *
         * @param <E>   enum type
         * @param clazz enum class
         * @param name  name of the enum constant
         * @return Enum value from the given name or {@code null} if no enum value was found
         */

        public static <E extends Enum<E>> E enumerate(Class<E> clazz, String name) {
            return enumerate(clazz, name, null);
        }

        /**
         * Returns an enum value from the given class and name.
         *
         * @param <E>      enum type
         * @param clazz    enum class
         * @param name     name of the enum constant
         * @param fallback fallback value
         * @return Enum value from the given name or {@code null} if no enum value was found
         */

        public static <E extends Enum<E>> E enumerate(Class<E> clazz, String name, E fallback) {
            try {
                return Enum.valueOf(clazz, name);
            } catch(Exception ignored) {}
            return fallback;
        }

        /**
         * Returns the first valid enum value from the given class and name array.
         *
         * @param <E>   enum type
         * @param clazz enum class
         * @param array array of enum constant names
         * @return First valid enum value list from the given name list or {@code null}
         */

        public static <E extends Enum<E>> E enumerate(Class<E> clazz, String... array) {
            var values = enumerates(clazz, List.of(array));
            return values.isEmpty() ? null : values.get(0);
        }

        /**
         * Returns the first valid value from the given class and name array.
         *
         * @param <E>    type
         * @param mapper mapper
         * @param array  array of enum constant names
         * @return First valid enum value from the given name list or {@code null}
         */

        public static <E> E enumerate(Function<String, E> mapper, String... array) {
            var values = enumerates(mapper, List.of(array));
            return values.isEmpty() ? null : values.get(0);
        }

        /**
         * Returns an enum value list from the given class and name array.
         *
         * @param <E>   enum type
         * @param clazz enum class
         * @param array array of enum constant names
         * @return Valid enum value list from the given name list
         */

        public static <E extends Enum<E>> List<E> enumerates(Class<E> clazz, String... array) {
            return enumerates(clazz, List.of(array));
        }

        /**
         * Returns an enum value list from the given class and name list.
         *
         * @param <E>   enum type
         * @param clazz enum class
         * @param list  list of enum constant names
         * @return Valid enum value list from the given name list
         */

        public static <E extends Enum<E>> List<E> enumerates(Class<E> clazz, List<String> list) {
            return enumerates(name -> enumerate(clazz, name), list);
        }

        /**
         * Returns a value list from the given mapper and name list.
         *
         * @param <E>    value type
         * @param mapper mapper
         * @param list   list of content names
         * @return Valid value list from the given names
         */

        public static <E> List<E> enumerates(Function<String, E> mapper, List<String> list) {
            try {
                return list.stream()
                        .map(s -> {
                            try {
                                return mapper.apply(s);
                            } catch(Exception ignored) {}
                            return null;
                        })
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());
            } catch(Exception ignored) {}
            return new ArrayList<>();
        }

        /**
         * Returns a value from the given class and name.
         *
         * @param <E>      type
         * @param clazz    class
         * @param name     name of the constant
         * @param fallback fallback value
         * @return Value from the given name or {@code null} if no value was found
         */

        @SuppressWarnings("unchecked")
        public static <E> E fielded(Class<E> clazz, String name, E fallback) {
            try {
                return (E) RF.fetch(clazz, name, false);
            } catch(Exception ignored) {}
            return fallback;
        }

        /**
         * Returns a value from the given class and name.
         *
         * @param <E>   type
         * @param clazz class
         * @param name  name of the constant
         * @return Value from the given name or {@code null} if no value was found
         */

        public static <E> E fielded(Class<E> clazz, String name) {
            return fielded(clazz, name, null);
        }

        /**
         * @param <E>   type
         * @param clazz class
         * @param array array
         * @return Valid value list from the given name array
         */

        public static <E> E fielded(Class<E> clazz, String... array) {
            return Stream.of(array)
                    .map(s -> fielded(clazz, s))
                    .filter(Objects::nonNull)
                    .findFirst()
                    .orElse(null);
        }

        /**
         * Returns a class from the given class path.
         *
         * @param name class path
         * @return Class from the given path, or {@code null} if no class was found
         */

        public static Class<?> get(String name) {
            try {
                return Class.forName(name);
            } catch(Exception e) {
                RF.debug(e);
            }
            return null;
        }

        /**
         * Returns a subclass from the given class and name.
         *
         * @param clazz superclass
         * @param name  subclass name
         * @return subclass from under the given class from the given name
         */

        public static Class<?> subclass(Class<?> clazz, String name) {
            try {
                String path = clazz.getCanonicalName() + "." + name;
                for(Class<?> subclass : clazz.getClasses()) {
                    if(subclass.getCanonicalName().equals(path)) {
                        return subclass;
                    }
                }
                for(Class<?> subclass : clazz.getDeclaredClasses()) {
                    if(subclass.getCanonicalName().equals(path)) {
                        return subclass;
                    }
                }
            } catch(Exception e) {
                RF.debug(e);
            }
            return null;
        }

        public static Invoker<?> order(Class<?> clazz, Object object, String name, boolean warn, Class<?>... params) {
            return Invoker.of(clazz, object, name, warn, params);
        }

        public static Invoker<?> order(Object object, String name, boolean warn, Class<?>... params) {
            return order(object.getClass(), object, name, warn, params);
        }

        public static Invoker<?> order(Object object, String name, Class<?>... params) {
            return order(object, name, true, params);
        }

        public static Invoker<?> order(Class<?> clazz, String name, boolean warn, Class<?>... params) {
            return order(clazz, null, name, warn, params);
        }

        public static Invoker<?> order(Class<?> clazz, String name, Class<?>... params) {
            return order(clazz, name, true, params);
        }

        public static <R> R direct(Object object, String name, Class<R> type, boolean warn) {
            return order(object, name, warn).as(type).invoke();
        }

        public static <R> R direct(Object object, String name, Class<R> type) {
            return direct(object, name, type, true);
        }

        public static <R> R direct(Class<?> clazz, String name, Class<R> type, boolean warn) {
            return order(clazz, name, warn).as(type).invoke();
        }

        public static <R> R direct(Class<?> clazz, String name, Class<R> type) {
            return direct(clazz, name, type, true);
        }

        public static Object direct(Object object, String name, boolean warn) {
            return order(object, name, warn).invoke();
        }

        public static Object direct(Object object, String name) {
            return direct(object, name, true);
        }

        public static Object direct(Class<?> clazz, String name, boolean warn) {
            return order(clazz, name, warn).invoke();
        }

        public static Object direct(Class<?> clazz, String name) {
            return direct(clazz, name, true);
        }

        public static <R> Invoker<R> order(Class<?> clazz, Object object, Class<R> returns, boolean warn, Class<?>... params) {
            return Invoker.of(clazz, object, returns, warn, params);
        }

        public static <R> Invoker<R> order(Object object, Class<R> returns, boolean warn, Class<?>... params) {
            return order(object.getClass(), object, returns, warn, params);
        }

        public static <R> Invoker<R> order(Class<?> clazz, Class<R> returns, boolean warn, Class<?>... params) {
            return order(clazz, null, returns, warn, params);
        }

        public static <R> Invoker<R> order(Object object, Class<R> returns, Class<?>... params) {
            return order(object, returns, true, params);
        }

        public static <R> Invoker<R> order(Class<?> clazz, Class<R> returns, Class<?>... params) {
            return order(clazz, returns, true, params);
        }

        public static <R> Invoker<R> order(String clazz, Class<R> returns, boolean warn, Class<?>... params) {
            return order(get(clazz), returns, warn, params);
        }

        public static <R> Invoker<R> order(String clazz, Class<R> returns, Class<?>... params) {
            return order(clazz, returns, true, params);
        }

        public static <R> Accessor<R> access(Class<?> clazz, Object object, String name, Class<R> returns, boolean warn) {
            return Accessor.of(clazz, object, name, returns, warn);
        }

        public static <R> Accessor<R> access(Class<?> clazz, String name, Class<R> returns, boolean warn) {
            return access(clazz, null, name, returns, warn);
        }

        public static <R> Accessor<R> access(Object object, String name, Class<R> returns, boolean warn) {
            return access(object.getClass(), object, name, returns, warn);
        }

        public static <R> Accessor<R> access(Class<?> clazz, String name, Class<R> returns) {
            return access(clazz, name, returns, true);
        }

        public static <R> Accessor<R> access(Object object, String name, Class<R> returns) {
            return access(object, name, returns, true);
        }

        public static <R> Accessor<R> access(Class<?> clazz, String name, boolean warn) {
            return access(clazz, null, name, null, warn);
        }

        public static <R> Accessor<R> access(Object object, String name, boolean warn) {
            return access(object.getClass(), object, name, null, warn);
        }

        public static <R> Accessor<R> access(Class<?> clazz, String name) {
            return access(clazz, name, true);
        }

        public static <R> Accessor<R> access(Object object, String name) {
            return access(object, name, true);
        }

        public static <R> R fetch(Class<?> clazz, Object object, String name, Class<R> returns, boolean warn) {
            return access(clazz, object, name, returns, warn).get();
        }

        public static Object fetch(Class<?> clazz, Object object, String name, boolean warn) {
            return fetch(clazz, object, name, null, warn);
        }

        public static <R> R fetch(Class<?> clazz, String name, Class<R> returns, boolean warn) {
            return fetch(clazz, null, name, returns, warn);
        }

        public static <R> R fetch(Object object, String name, Class<R> returns, boolean warn) {
            return fetch(object.getClass(), object, name, returns, warn);
        }

        public static <R> R fetch(Class<?> clazz, String name, Class<R> returns) {
            return fetch(clazz, name, returns, true);
        }

        public static <R> R fetch(Object object, String name, Class<R> returns) {
            return fetch(object, name, returns, true);
        }

        public static Object fetch(Class<?> clazz, String name, boolean warn) {
            return fetch(clazz, null, name, warn);
        }

        public static Object fetch(Object object, String name, boolean warn) {
            return fetch(object, name, null, warn);
        }

        public static Object fetch(Class<?> clazz, String name) {
            return fetch(clazz, name, true);
        }

        public static Object fetch(Object object, String name) {
            return fetch(object, name, true);
        }

        public static <R> List<Accessor<R>> access(Class<?> clazz, Object object, Class<R> returns, int limit, boolean warn) {
            return Accessor.of(clazz, object, returns, limit, warn);
        }

        public static <R> List<Accessor<R>> access(Class<?> clazz, Class<R> returns, int limit, boolean warn) {
            return access(clazz, null, returns, limit, warn);
        }

        public static <R> List<Accessor<R>> access(Object object, Class<R> returns, int limit, boolean warn) {
            return access(object.getClass(), object, returns, limit, warn);
        }

        public static <R> List<Accessor<R>> access(Class<?> clazz, Class<R> returns, int limit) {
            return access(clazz, returns, limit, true);
        }

        public static <R> List<Accessor<R>> access(Object object, Class<R> returns, int limit) {
            return access(object, returns, limit, true);
        }

        public static <R> List<Accessor<R>> access(Class<?> clazz, Class<R> returns, boolean warn) {
            return access(clazz, returns, Integer.MAX_VALUE, warn);
        }

        public static <R> List<Accessor<R>> access(Object object, Class<R> returns, boolean warn) {
            return access(object, returns, Integer.MAX_VALUE, warn);
        }

        public static <R> List<Accessor<R>> access(Class<?> clazz, Class<R> returns) {
            return access(clazz, returns, Integer.MAX_VALUE, true);
        }

        public static <R> List<Accessor<R>> access(Object object, Class<R> returns) {
            return access(object, returns, Integer.MAX_VALUE, true);
        }

        public static <R> Construct<R> build(Class<R> clazz, boolean warn, Class<?>... params) {
            return Construct.of(clazz, warn, params);
        }

        public static <R> Construct<R> build(Class<R> clazz, Class<?>... params) {
            return build(clazz, true, params);
        }

        public static <R> R instance(Class<R> clazz, boolean warn) {
            return build(clazz, warn).instance();
        }

        public static <R> R instance(Class<R> clazz) {
            return instance(clazz, true);
        }
    }

}
