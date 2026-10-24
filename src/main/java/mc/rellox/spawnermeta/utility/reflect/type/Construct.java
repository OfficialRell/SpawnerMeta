package mc.rellox.spawnermeta.utility.reflect.type;

import mc.rellox.spawnermeta.utility.reflect.Reflect.RF;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.stream.Collectors;

public interface Construct<R> {

    Construct<?> empty = _empty();

    private static Construct<?> _empty() {
        return new Construct<>() {
            @Override
            public Object instance(Object... arguments) {
                return null;
            }

            @Override
            public Class<?> type() {
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

    R instance(Object... arguments);

    Class<? extends R> type();

    Class<?>[] parameters();

    @SuppressWarnings("unchecked")
    default <O> Construct<O> as(Class<O> clazz) {
        return (Construct<O>) this;
    }

    static <R> Construct<R> of(Class<R> clazz, boolean warn, Class<?>... params) {
        try {
            Constructor<R> constructor = constructor(clazz, warn, params);
            if(constructor == null) return empty();
            constructor.setAccessible(true);
            return new Construct<>() {
                @Override
                public R instance(Object... arguments) {
                    try {
                        return constructor.newInstance(arguments);
                    } catch(Exception e) {
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
        } catch(Exception e) {
            RF.debug(e, warn);
        }
        return empty();
    }

    private static <R> Constructor<R> constructor(Class<R> clazz, boolean warn, Class<?>... params) {
        try {
            return clazz.getConstructor(params);
        } catch(Exception ignored) {}
        try {
            return clazz.getDeclaredConstructor(params);
        } catch(Exception ignored) {}
        RF.debug(new NoSuchMethodException("No such constructor for class ("
                + clazz.getName() + ") with parameters: " + error(params)), warn);
        return null;
    }

    private static String error(Class<?>... params) {
        return params == null || params.length == 0
                ? "()"
                : Arrays.stream(params)
                .map(c -> c == null ? "null" : c.getName())
                .collect(Collectors.joining(",", "(", ")"));
    }

}
