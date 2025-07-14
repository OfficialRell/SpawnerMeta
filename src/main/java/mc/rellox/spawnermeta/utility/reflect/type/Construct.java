package mc.rellox.spawnermeta.utility.reflect.type;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.stream.Collectors;

import mc.rellox.spawnermeta.utility.reflect.Reflect.RF;

public interface Construct<R> {
	
	static Construct<?> empty = _empty();
	
	private static Construct<?> _empty(){
		return new Construct<>() {
			@Override
			public Object instance(Object... os) {
				return null;
			}
			@Override
			public Class<? extends Object> type() {
				return Void.class;
			}
			@Override
			public Class<?>[] parameters() {
				return null;
			}
		};
	}
	
	@SuppressWarnings("unchecked")
	static <R> Construct<R> empty() {
		return (Construct<R>) empty;
	}
	
	R instance(Object... os);
	
	Class<? extends R> type();
	
	Class<?>[] parameters();
	
	@SuppressWarnings("unchecked")
	default <O> Construct<O> as(Class<O> c) {
		return (Construct<O>) this;
	}
	
	static <R> Construct<R> of(Class<R> clazz, boolean warn, Class<?>... params) {
		try {
			Constructor<R> constructor = constructor(clazz, warn, params);
			if(constructor == null) return empty();
			constructor.setAccessible(true);
			return new Construct<>() {
				@Override
				public R instance(Object... os) {
					try {
						return constructor.newInstance(os);
					} catch (Exception e) {
						RF.debug(e, warn);
					}
					return null;
				}
				@Override
				public Class<? extends R> type() {
					return clazz;
				}
				@Override
				public Class<?>[] parameters() {
					return params;
				}
			};
		} catch (Exception e) {
			RF.debug(e, warn);
		}
		return empty();
	}
	
	private static <R> Constructor<R> constructor(Class<R> clazz, boolean warn, Class<?>... params) {
		try {
			return clazz.getConstructor(params);
		} catch (Exception e) {}
		try {
			return clazz.getDeclaredConstructor(params);
		} catch (Exception e) {}
		RF.debug(new NoSuchMethodException("No such constructor for class ("
				+ clazz.getName() + ") with parameters: " + error(params)), warn);
		return null;
	}
	
	private static String error(Class<?>... params) {
		return params == null || params.length == 0 ? "()"
						: Arrays.stream(params)
						.map(c -> c == null ? "null" : c.getName())
						.collect(Collectors.joining(",", "(", ")"));
	}

}
