package mc.rellox.spawnermeta.utility.reflect.type;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import mc.rellox.spawnermeta.utility.reflect.Reflect.RF;

public interface Invoker<T> {
	
	static Invoker<?> empty = _empty();
	
	private static Invoker<?> _empty() {
		return new Invoker<>() {
			@Override
			public Object invoke(Object... os) {
				return null;
			}
			@Override
			public String name() {
				return "null";
			}
			@Override
			public Class<?>[] parameters() {
				return null;
			}
		};
	}
	
	@SuppressWarnings("unchecked")
	static <R> Invoker<R> empty() {
		return (Invoker<R>) empty;
	}
	
	T invoke(Object... os);
	
	String name();
	
	Class<?>[] parameters();

	default T invoke(T def, Object... os) {
		T r = invoke(os);
		return r == null ? def : r;
	}
	
	@SuppressWarnings("unchecked")
	default <E> Invoker<E> as(Class<E> c) {
		return (Invoker<E>) this;
	}
	
	static Invoker<?> of(Object object, String name, boolean warn, Class<?>... params) {
		return of(object.getClass(), object, name, warn, params);
	}
	
	static Invoker<?> of(Class<?> c, Object object, String name, boolean warn, Class<?>... params) {
		try {
			Method method = method(c, name, warn, params);
			return of(method, object, warn, params);
		} catch (Exception e) {
			RF.debug(e, warn);
		}
		return empty;
	}
	
	public static <R> Invoker<R> of(Class<?> clazz, Object object, Class<R> returns, boolean warn, Class<?>... params) {
		if(clazz == null) return empty();
		try {
			Set<Method> set = methods(clazz);
			Stream<Method> stream = set.stream();
			if(returns != null) {
				stream.filter(m -> m.getReturnType().equals(returns));
			}
			stream = stream.filter(m -> {
				Class<?>[] ps = m.getParameterTypes();
				if(ps == null || ps.length <= 0) return params == null;
				for(int i = 0; i < ps.length && i < params.length; i++)
					if(ps[i].equals(params[i]) == false) return false;
				return true;
			});
			Method method = stream
					.findFirst()
					.orElse(null);
			return of(method, object, warn, params);
		} catch (Exception e) {
			RF.debug(e, warn);
		}
		return empty();
	}
	
	private static <R> Invoker<R> of(Method method, Object object, boolean warn, Class<?>...params) {
		if(method == null) return empty();
		return new Invoker<>() {
			@SuppressWarnings("unchecked")
			@Override
			public R invoke(Object... os) {
				try {
					method.setAccessible(true);
					return (R) method.invoke(object, os);
				} catch (Exception e) {
					RF.debug(e, warn);
				}
				return null;
			}
			@Override
			public String name() {
				return method.getName();
			}
			@Override
			public Class<?>[] parameters() {
				return params;
			}
		};
	}
	
	private static Method method(Class<?> c, String name, boolean warn, Class<?>...params) {
		try {
			return c.getDeclaredMethod(name, params);
		} catch (Exception e) {}
		try {
			return c.getMethod(name, params);
		} catch (Exception e) {}
		RF.debug(new NoSuchMethodException("No method with name: " + name), warn);
		return null;
	}
	
	private static Set<Method> methods(Class<?> c) {
		Set<Method> set = new HashSet<>();
		Stream.of(c.getMethods()).forEach(set::add);
		Stream.of(c.getDeclaredMethods()).forEach(set::add);
		return set;
	}

}
