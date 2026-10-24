package mc.rellox.spawnermeta.utility.reflect.type;

import mc.rellox.spawnermeta.utility.reflect.Reflect.RF;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public interface Accessor<R> {

    Accessor<?> empty = _empty();

    private static Accessor<?> _empty() {
        return new Accessor<>() {
            @Override
            public Object get() {
                return null;
            }

            @Override
            public Object objected(Object object) {
                return null;
            }

            @Override
            public void set(Object value) {}

            @Override
            public String name() {
                return "null";
            }

            @Override
            public Class<?> type() {
                return null;
            }
        };
    }

    @SuppressWarnings("unchecked")
    static <R> Accessor<R> empty() {
        return (Accessor<R>) empty;
    }

    R get();

    R objected(Object object);

    void set(R value);

    String name();

    Class<? extends R> type();

    default boolean warning() {
        return false;
    }

    default R get(R fallback) {
        R value = get();
        return value == null ? fallback : value;
    }

    @SuppressWarnings("unchecked")
    default void force(Object object) {
        set((R) object);
    }

    default Accessor<?> next(String next) {
        return next(next, null);
    }

    default <O> Accessor<O> next(String next, Class<O> returns) {
        R value = get();
        return value == null ? empty()
                : of(value.getClass(), value, next, returns, warning());
    }

    @SuppressWarnings("unchecked")
    default <O> Accessor<O> as(Class<O> clazz) {
        return (Accessor<O>) this;
    }

    static <R> Accessor<R> of(Class<?> clazz, Object object, String name, Class<R> returns, boolean warn) {
        if(clazz == null) return empty();
        try {
            Field field = field(clazz, name, warn);
            if(field == null) return empty();
            field.setAccessible(true);
            return of(field, object, returns, warn);
        } catch(Exception e) {
            RF.debug(e, warn);
        }
        return empty();
    }

    static <R> List<Accessor<R>> of(Class<?> clazz, Object object, Class<R> returns, int limit, boolean warn) {
        if(clazz == null) return List.of(empty());
        try {
            Set<Field> set = fields(clazz);
            if(set.isEmpty()) {
                RF.debug(new NoSuchFieldException("No fields with return type of "
                        + returns.getName()), warn);
                return List.of(empty());
            }
            return set.stream()
                    .limit(limit)
                    .peek(field -> field.setAccessible(true))
                    .map(field -> of(field, object, returns, warn))
                    .toList();
        } catch(Exception e) {
            RF.debug(e, warn);
        }
        return List.of(empty());
    }

    private static <R> Accessor<R> of(Field field, Object object, Class<R> returns, boolean warn) {
        return new Accessor<>() {
            @SuppressWarnings("unchecked")
            @Override
            public R get() {
                try {
                    return (R) field.get(object);
                } catch(Exception e) {
                    RF.debug(e, warn);
                }
                return null;
            }

            @SuppressWarnings("unchecked")
            @Override
            public R objected(Object object) {
                try {
                    return (R) field.get(object);
                } catch(Exception e) {
                    RF.debug(e, warn);
                }
                return null;
            }

            @Override
            public void set(R value) {
                try {
                    field.set(object, value);
                } catch(Exception e) {
                    RF.debug(e, warn);
                }
            }

            @Override
            public String name() {
                return field.getName();
            }

            @SuppressWarnings("unchecked")
            @Override
            public Class<? extends R> type() {
                return returns == null
                        ? (Class<? extends R>) field.getType()
                        : returns;
            }
        };
    }

    private static Field field(Class<?> clazz, String name, boolean warn) {
        Field f = field0(clazz, name);
        if(f != null) return f;
        RF.debug(new NoSuchFieldException("No such field with name: " + name), warn);
        return null;
    }

    private static Field field0(Class<?> clazz, String name) {
        if(clazz.equals(Object.class)
                || clazz.getName().equals("java.lang.Object"))
            return null;
        try {
            return clazz.getDeclaredField(name);
        } catch(Exception ignored) {}
        try {
            return clazz.getField(name);
        } catch(Exception ignored) {}
        return field0(clazz.getSuperclass(), name);
    }

    private static Set<Field> fields(Class<?> c) {
        Set<Field> set = new HashSet<>();
        fields0(set, c);
        return set;
    }

    private static void fields0(Set<Field> set, Class<?> c) {
        if(c.equals(Object.class)
                || c.getName().equals("java.lang.Object"))
            return;
        set.addAll(Arrays.asList(c.getFields()));
        set.addAll(Arrays.asList(c.getDeclaredFields()));
        fields0(set, c.getSuperclass());
    }

}
