package com.guflimc.brick.regions.api.domain;

import com.guflimc.brick.orm.api.attributes.AttributeKey;

import java.util.Optional;
import java.util.function.Function;

public interface PersistentLocality extends Locality {

    void setPriority(int priority);

    // attributes

    <T> void setAttribute(LocalityAttributeKey<T> key, T value);

    <T> void removeAttribute(LocalityAttributeKey<T> key);

    <T> Optional<T> attribute(LocalityAttributeKey<T> key);

    class LocalityAttributeKey<T> extends AttributeKey<T> {

        public LocalityAttributeKey(String name, Class<T> type, Function<T, String> serializer, Function<String, T> deserializer) {
            super(name, type, serializer, deserializer);
        }

    }
}
