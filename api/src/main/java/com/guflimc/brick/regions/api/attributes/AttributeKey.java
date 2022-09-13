package com.guflimc.brick.regions.api.attributes;

import java.util.function.Function;

public class AttributeKey<T> {

    private final String name;
    private final Class<T> type;

    private final Function<T, String> serializer;
    private final Function<String, T> deserializer;

    public AttributeKey(String name, Class<T> type, Function<T, String> serializer, Function<String, T> deserializer) {
        this.name = name;
        this.type = type;
        this.serializer = serializer;
        this.deserializer = deserializer;
    }

    public String name() {
        return name;
    }

    public Class<T> type() {
        return type;
    }

    public String serialize(T value) {
        return serializer.apply(value);
    }

    public T deserialize(String value) {
        return deserializer.apply(value);
    }

}
