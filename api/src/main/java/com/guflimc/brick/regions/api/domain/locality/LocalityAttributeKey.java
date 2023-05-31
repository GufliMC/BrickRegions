package com.guflimc.brick.regions.api.domain.locality;

import com.guflimc.adventure.MixedLegacyComponentSerializer;
import com.guflimc.brick.orm.api.attributes.AttributeKey;
import net.kyori.adventure.text.Component;

import java.awt.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class LocalityAttributeKey<T> extends AttributeKey<T> {

    private final static Map<String, LocalityAttributeKey<?>> KEYS = new ConcurrentHashMap<>();

    // Modify the settings when this locality is rendered on a map.

    public static final LocalityAttributeKey<Boolean> MAP_HIDDEN = new LocalityAttributeKey<>("map_hidden", Boolean.class,
            Object::toString, Boolean::parseBoolean);

    public static final LocalityAttributeKey<Color> MAP_FILL_COLOR = new LocalityAttributeKey<>("map_fill_color", Color.class,
             c -> String.valueOf(c.getRGB()), Color::decode);

    public static final LocalityAttributeKey<Double> MAP_FILL_OPACITY = new LocalityAttributeKey<>("map_fill_opacity", Double.class,
            Object::toString, Double::parseDouble);

    public static final LocalityAttributeKey<Color> MAP_STROKE_COLOR = new LocalityAttributeKey<>("map_stroke_color", Color.class,
            c -> String.valueOf(c.getRGB()), Color::decode);

    public static final LocalityAttributeKey<Double> MAP_STROKE_OPACITY = new LocalityAttributeKey<>("map_stroke_opacity", Double.class,
            Object::toString, Double::parseDouble);

    public static final LocalityAttributeKey<Integer> MAP_STROKE_WEIGHT = new LocalityAttributeKey<>("map_stroke_weight", Integer.class,
            Object::toString, Integer::parseInt);

    public static final LocalityAttributeKey<String> MAP_HOVER_TEXT = new LocalityAttributeKey<>("map_hover_text", String.class,
            Function.identity(), Function.identity());

    public static final LocalityAttributeKey<String> MAP_CLICK_TEXT = new LocalityAttributeKey<>("map_click_text", String.class,
            Function.identity(), Function.identity());

    //

    public static final LocalityAttributeKey<Component> ENTRANCE_TITLE = new LocalityAttributeKey<>("entrance_title", Component.class,
            MixedLegacyComponentSerializer::serialize, MixedLegacyComponentSerializer::deserialize);

    public static final LocalityAttributeKey<Component> ENTRANCE_SUBTITLE = new LocalityAttributeKey<>("entrance_subtitle", Component.class,
            MixedLegacyComponentSerializer::serialize, MixedLegacyComponentSerializer::deserialize);

    public static final LocalityAttributeKey<Component> ENTRANCE_ACTIONBAR = new LocalityAttributeKey<>("entrance_actionbar", Component.class,
            MixedLegacyComponentSerializer::serialize, MixedLegacyComponentSerializer::deserialize);

    public static final LocalityAttributeKey<Component> EXIT_TITLE = new LocalityAttributeKey<>("exit_title", Component.class,
            MixedLegacyComponentSerializer::serialize, MixedLegacyComponentSerializer::deserialize);

    public static final LocalityAttributeKey<Component> EXIT_SUBTITLE = new LocalityAttributeKey<>("exit_subtitle", Component.class,
            MixedLegacyComponentSerializer::serialize, MixedLegacyComponentSerializer::deserialize);

    public static final LocalityAttributeKey<Component> EXIT_ACTIONBAR = new LocalityAttributeKey<>("exit_actionbar", Component.class,
            MixedLegacyComponentSerializer::serialize, MixedLegacyComponentSerializer::deserialize);

    //

    private final boolean hidden;

    public LocalityAttributeKey(String name, Class<T> type, boolean hidden, Function<T, String> serializer, Function<String, T> deserializer) {
        super(name, type, serializer, deserializer);
        this.hidden = hidden;

        if ( KEYS.containsKey(name) ) {
            throw new IllegalArgumentException("An attribute key with that name already exists.");
        }
        KEYS.put(name, this);
    }

    public LocalityAttributeKey(String name, Class<T> type, Function<T, String> serializer, Function<String, T> deserializer) {
        this(name, type, false, serializer, deserializer);
    }

    //

    public boolean hidden() {
        return hidden;
    }

    //

    public static LocalityAttributeKey<?>[] values() {
        return KEYS.values().toArray(LocalityAttributeKey<?>[]::new);
    }

    public static LocalityAttributeKey<?> valueOf(String name) {
        return KEYS.get(name);
    }

}