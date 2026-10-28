package service;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import model.annotation.Id;

// Generic repository: stores any type T, using the field marked @Id as the key
public class Repository<T> {
    private final Map<String, T> items = new HashMap<>();

    public T save(T entity) {
        items.put(idOf(entity), entity);
        return entity;
    }

    @SafeVarargs
    public final void saveAll(T... entities) {
        for (T entity : entities) {
            save(entity);
        }
    }

    public T findById(String id) {
        return items.get(id);
    }

    public ArrayList<T> findAll() {
        return new ArrayList<>(items.values());
    }

    public boolean delete(String id) {
        return items.remove(id) != null;
    }

    private String idOf(T entity) {
        for (Class<?> cls = entity.getClass(); cls != null; cls = cls.getSuperclass()) {
            for (Field field : cls.getDeclaredFields()) {
                if (field.isAnnotationPresent(Id.class)) {
                    try {
                        field.setAccessible(true);
                        return String.valueOf(field.get(entity));
                    } catch (IllegalAccessException e) {
                        throw new IllegalStateException("cannot read @Id field", e);
                    }
                }
            }
        }
        throw new IllegalArgumentException("no @Id field found on " + entity.getClass().getSimpleName());
    }
}
