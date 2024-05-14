package mc.rellox.spawnermeta.utility.reflect;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import mc.rellox.spawnermeta.configuration.Settings;
import mc.rellox.spawnermeta.utility.reflect.type.Accessor;
import mc.rellox.spawnermeta.utility.reflect.type.Construct;
import mc.rellox.spawnermeta.utility.reflect.type.Invoker;

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
		 * @param e - exception
		 */
		
		public static void debug(Exception e) {
			if(Settings.settings.debug == true) e.printStackTrace();
		}
		
		/**
		 * Prints stack trace if {@code debug} and {@code warn} is set to {@code true}.
		 * 
		 * @param e - exception
		 */
		
		public static void debug(Exception e, boolean warn) {
			if(Settings.settings.debug == true && warn == true) e.printStackTrace();
		}
		
		public static Class<?> craft(String s) {
			if(SERVER_VERSION.equalsIgnoreCase("craftbukkit") == true)
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
		 * @param <E> - enum type
		 * @param c - enum class
		 * @param name - name of the enum constant
		 * @return enum value from the given name or {@code null} if no enum value was found
		 */
		
		public static <E extends Enum<E>> E enumerate(Class<E> c, String name) {
			return enumerate(c, name, null);
		}
		
		/**
		 * Returns an enum value from the given class and name.
		 * 
		 * @param <E> - enum type
		 * @param c - enum class
		 * @param name - name of the enum constant
		 * @param d - default value
		 * @return enum value from the given name or {@code null} if no enum value was found
		 */
		
		public static <E extends Enum<E>> E enumerate(Class<E> c, String name, E d) {
			try {
				return Enum.valueOf(c, name);
			} catch (Exception e) {}
			return d;
		}
		
		/**
		 * Returns an enum value list from the given class and name array.
		 * 
		 * @param <E> - enum type
		 * @param c - enum class
		 * @param ss - array of enum constant names
		 * @return valid enum value list from the given name list
		 */
		
		public static <E extends Enum<E>> E enumerate(Class<E> c, String... ss) {
			var a = enumerates(c, List.of(ss));
			return a.isEmpty() == true ? null : a.get(0);
		}
		
		/**
		 * Returns a value list from the given class and name array.
		 * 
		 * @param <E> - type
		 * @param mapper - mapper
		 * @param ss - array of enum constant names
		 * @return valid enum value list from the given name list
		 */
		
		public static <E> E enumerate(Function<String, E> mapper, String... ss) {
			var a = enumerates(mapper, List.of(ss));
			return a.isEmpty() == true ? null : a.get(0);
		}
		
		/**
		 * Returns an enum value list from the given class and name array.
		 * 
		 * @param <E> - enum type
		 * @param c - enum class
		 * @param ss - array of enum constant names
		 * @return valid enum value list from the given name list
		 */
		
		public static <E extends Enum<E>> List<E> enumerates(Class<E> c, String... ss) {
			return enumerates(c, List.of(ss));
		}
		
		/**
		 * Returns an enum value list from the given class and name list.
		 * 
		 * @param <E> - enum type
		 * @param c - enum class
		 * @param list - list of enum constant names
		 * @return valid enum value list from the given name list
		 */
		
		public static <E extends Enum<E>> List<E> enumerates(Class<E> c, List<String> list) {
			return enumerates(name -> enumerate(c, name), list);
		}
		
		/**
		 * Returns an constant value list from the given mapper and name list.
		 * 
		 * @param <E> - constant type
		 * @param f - mapper
		 * @param list - list of contant names
		 * @return valid constant value list from the given names
		 */
		
		public static <E> List<E> enumerates(Function<String, E> f, List<String> list) {
			try {
				return list.stream()
						.map(s -> {
							try {
								return f.apply(s);
							} catch (Exception e) {}
							return null;
						})
						.filter(Objects::nonNull)
						.collect(Collectors.toList());
			} catch (Exception e) {}
			return new ArrayList<>();
		}
		
		/**
		 * Returns a class from the given class path.
		 * 
		 * @param name - class path
		 * @return class from the given path, or {@code null} if no class was found
		 */
		
		public static Class<?> get(String name) {
			try {
				return Class.forName(name);
			} catch (Exception e) {
				RF.debug(e);
			}
			return null;
		}
		
		/**
		 * Returns a subclass from the given class and name.
		 * 
		 * @param c - superclass
		 * @param n - subclass name
		 * @return subclass from under the given class from the given name
		 */
		
		public static Class<?> subclass(Class<?> c, String n) {
			try {
				String path = c.getCanonicalName() + "." + n;
				for(Class<?> d : c.getClasses()) {
					if(d.getCanonicalName().equals(path) == true) {
						return d;
					}
				}
				for(Class<?> d : c.getDeclaredClasses()) {
					if(d.getCanonicalName().equals(path) == true) {
						return d;
					}
				}
			} catch (Exception e) {
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
			return order(get(clazz), returns, true, params);
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
			return fetch( object, name, true);
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
