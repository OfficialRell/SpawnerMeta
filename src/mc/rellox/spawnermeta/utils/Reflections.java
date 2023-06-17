package mc.rellox.spawnermeta.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import mc.rellox.spawnermeta.configuration.Settings;
import mc.rellox.spawnermeta.version.Version;

public final class Reflections {

	public static final class RF {
		
		public static void debug(Exception x) {
			if(Settings.settings.debug == true) x.printStackTrace();
		}
		
		public static Class<?> craft(String s) {
			try {
				return Class.forName("org.bukkit.craftbukkit." + Version.server + "." + s);
			} catch (ClassNotFoundException e) {
				return null;
			}
		}
		
		public static Class<?> nms(String s) {
			try {
				return Class.forName("net.minecraft.server." + Version.server + "." + s);
			} catch (ClassNotFoundException e) {
				return null;
			}
		}
		
		public static Class<?> get(String s) {
			try {
				return Class.forName(s);
			} catch (ClassNotFoundException e) {
				return null;
			}
		}

		public static <E extends Enum<E>> E enumerate(Class<E> c, String name) {
			return enumerate(c, name, null);
		}

		public static <E extends Enum<E>> E enumerate(Class<E> c, String name, E e) {
			try {
				return Enum.valueOf(c, name);
			} catch (Exception x) {}
			return e;
		}

		public static <E extends Enum<E>> E enumerates(Class<E> c, String... names) {
			return Stream.of(names)
					.map(name -> enumerate(c, name))
					.filter(e -> e != null)
					.findFirst()
					.orElse(null);
		}

		public static <E extends Enum<E>> List<E> enumerate(Class<E> c, List<String> names) {
			return names.stream()
					.map(name -> enumerate(c, name))
					.filter(e -> e != null)
					.collect(Collectors.toList());
		}

		public static Invoker<?> order(Object o, String m, Class<?>... cs) {
			try {
				Method method = o.getClass().getMethod(m, cs);
				return os -> {
					try {
						return method.invoke(o, os);
					} catch (Exception e) {
						debug(e);
					}
					return null;
				};
			} catch (Exception e) {
				debug(e);
			}
			return null_invoker;
		}

		public static Invoker<?> order(Class<?> c, String m, Class<?>... cs) {
			try {
				Method method = c.getMethod(m, cs);
				return os -> {
					try {
						return method.invoke(null, os);
					} catch (Exception e) {
						debug(e);
					}
					return null;
				};
			} catch (Exception e) {
				debug(e);
			}
			return null_invoker;
		}
		
		public static Object direct(Object o, String m) {
			return order(o, m).invoke();
		}
		
		public static Object direct(Class<?> c, String m) {
			return order(c, m).invoke();
		}
		
		public static <R> R direct(Object o, String m, Class<R> r) {
			return order(o, m).as(r).invoke();
		}
		
		public static <R> R direct(Class<?> c, String m, Class<R> r) {
			return order(c, m).as(r).invoke();
		}

		@SuppressWarnings("unchecked")
		public static <R> Construct<R> build(Class<R> c, Class<?>... cs) {
			try {
				Constructor<?> constructor = c.getConstructor(cs);
				return os -> {
					try {
						return (R) constructor.newInstance(os);
					} catch (Exception e) {
						debug(e);
					}
					return null;
				};
			} catch (Exception e) {
				debug(e);
			}
			return (Construct<R>) null_constructor;
		}

		public static Accessor<?> access(Object o, String f) {
			try {
				Field field = field(o.getClass(), f);
				field.setAccessible(true);
				return of(field, o);
			} catch (Exception e) {
				debug(e);
			}
			return null_accessor;
		}

		public static Accessor<?> accessI(Object o, String f) {
			try {
				Field field = field(o.getClass(), f);
				field.setAccessible(true);
				return ofI(field, o);
			} catch (Exception e) {}
			return null_accessor;
		}
		
		public static Object fetch(Object o, String f) {
			return access(o, f).field();
		}
		
		public static Object fetch(Class<?> c, String f) {
			return access(c, f).field();
		}

		private static Field field(Class<?> c, String n) throws Exception {
			try {
				return c.getField(n);
			} catch (Exception e) {
				return c.getDeclaredField(n);
			}
		}

		private static final Invoker<?> null_invoker = o -> null;
		private static final Construct<?> null_constructor = o -> null;
		private static final Accessor<?> null_accessor = () -> null;

		@FunctionalInterface
		public static interface Invoker<R> {

			R invoke(Object... os);

			default R invoke(R d, Object... os) {
				R r = invoke(os);
				return r == null ? d : r;
			}
			
			@SuppressWarnings("unchecked")
			default <O> Invoker<O> as(Class<O> c) {
				return (Invoker<O>) this;
			}

		}

		@FunctionalInterface
		public static interface Construct<R> {

			R instance(Object... os);
			
			@SuppressWarnings("unchecked")
			default <O> Construct<O> as(Class<O> c) {
				return (Construct<O>) this;
			}

		}

		@FunctionalInterface
		public static interface Accessor<R> {

			R field();

			default R field(R d) {
				R r = field();
				return r == null ? d : r;
			}

			default void set(R r) {}

			@SuppressWarnings("unchecked")
			default void force(Object o) {
				set((R) o);
			}
			
			@SuppressWarnings("unchecked")
			default <O> Accessor<O> as(Class<O> c) {
				return (Accessor<O>) this;
			}

		}

		private static <R> Accessor<R> of(Field field, Object o) {
			return new Accessor<R>() {
				@SuppressWarnings("unchecked")
				@Override
				public R field() {
					try {
						field.setAccessible(true);
						return (R) field.get(o);
					} catch (Exception e) {
						debug(e);
					}
					return null;
				}
				@Override
				public void set(R r) {
					try {
						field.setAccessible(true);
						field.set(o, r);
					} catch (Exception e) {
						debug(e);
					}
				}
			};
		}

		private static <R> Accessor<R> ofI(Field field, Object o) {
			return new Accessor<R>() {
				@SuppressWarnings("unchecked")
				@Override
				public R field() {
					try {
						field.setAccessible(true);
						return (R) field.get(o);
					} catch (Exception e) {}
					return null;
				}
				@Override
				public void set(R r) {
					try {
						field.setAccessible(true);
						field.set(o, r);
					} catch (Exception e) {}
				}
			};
		}

	}

}
