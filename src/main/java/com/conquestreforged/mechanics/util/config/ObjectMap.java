package com.conquestreforged.mechanics.util.config;

import java.util.LinkedHashMap;
import java.util.Optional;

public class ObjectMap extends LinkedHashMap<String, Object> {

    public void set(String key, Object value) {
        put(key, value);
    }

    public <T> Optional<T> get(String key, Class<T> type) {
        Object value = get(key);
        if (!type.isInstance(value)) {
            return Optional.empty();
        }
        return Optional.of(type.cast(value));
    }

    public <T> Optional<T> get(Class<T> type, String... path) {
        ObjectMap map = this;
        for (int i = 0; i < path.length - 1; i++) {
            String key = path[i];
            Optional<ObjectMap> next = map.getMap(key);
            if (!next.isPresent()) {
                return Optional.empty();
            }
            map = next.get();
        }
        return map.get(path[path.length - 1], type);
    }

    public Optional<ObjectMap> getMap(String key) {
        return get(key, ObjectMap.class);
    }
}
