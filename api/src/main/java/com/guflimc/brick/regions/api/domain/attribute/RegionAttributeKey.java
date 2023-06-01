package com.guflimc.brick.regions.api.domain.attribute;

import com.guflimc.adventure.MixedLegacyComponentSerializer;
import com.guflimc.brick.orm.api.attributes.AttributeKey;
import net.kyori.adventure.text.Component;

import java.awt.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class RegionAttributeKey<T> extends AttributeKey<T> {

    private final static Map<String, RegionAttributeKey<?>> KEYS = new ConcurrentHashMap<>();

    // Modify the settings when this locality is rendered on a map.

    public static final RegionAttributeKey<Boolean> MAP_HIDDEN = new RegionAttributeKey<>("map_hidden", Boolean.class,
            Object::toString, Boolean::parseBoolean);

    public static final RegionAttributeKey<Color> MAP_FILL_COLOR = new RegionAttributeKey<>("map_fill_color", Color.class,
             c -> String.valueOf(c.getRGB()), Color::decode);

    public static final RegionAttributeKey<Double> MAP_FILL_OPACITY = new RegionAttributeKey<>("map_fill_opacity", Double.class,
            Object::toString, Double::parseDouble);

    public static final RegionAttributeKey<Color> MAP_STROKE_COLOR = new RegionAttributeKey<>("map_stroke_color", Color.class,
            c -> String.valueOf(c.getRGB()), Color::decode);

    public static final RegionAttributeKey<Double> MAP_STROKE_OPACITY = new RegionAttributeKey<>("map_stroke_opacity", Double.class,
            Object::toString, Double::parseDouble);

    public static final RegionAttributeKey<Integer> MAP_STROKE_WEIGHT = new RegionAttributeKey<>("map_stroke_weight", Integer.class,
            Object::toString, Integer::parseInt);

    public static final RegionAttributeKey<String> MAP_HOVER_TEXT = new RegionAttributeKey<>("map_hover_text", String.class,
            Function.identity(), Function.identity());

    public static final RegionAttributeKey<String> MAP_CLICK_TEXT = new RegionAttributeKey<>("map_click_text", String.class,
            Function.identity(), Function.identity());

    //

    public static final RegionAttributeKey<Component> ENTRANCE_TITLE = new RegionAttributeKey<>("entrance_title", Component.class,
            MixedLegacyComponentSerializer::serialize, MixedLegacyComponentSerializer::deserialize);

    public static final RegionAttributeKey<Component> ENTRANCE_SUBTITLE = new RegionAttributeKey<>("entrance_subtitle", Component.class,
            MixedLegacyComponentSerializer::serialize, MixedLegacyComponentSerializer::deserialize);

    public static final RegionAttributeKey<Component> ENTRANCE_ACTIONBAR = new RegionAttributeKey<>("entrance_actionbar", Component.class,
            MixedLegacyComponentSerializer::serialize, MixedLegacyComponentSerializer::deserialize);

    public static final RegionAttributeKey<Component> EXIT_TITLE = new RegionAttributeKey<>("exit_title", Component.class,
            MixedLegacyComponentSerializer::serialize, MixedLegacyComponentSerializer::deserialize);

    public static final RegionAttributeKey<Component> EXIT_SUBTITLE = new RegionAttributeKey<>("exit_subtitle", Component.class,
            MixedLegacyComponentSerializer::serialize, MixedLegacyComponentSerializer::deserialize);

    public static final RegionAttributeKey<Component> EXIT_ACTIONBAR = new RegionAttributeKey<>("exit_actionbar", Component.class,
            MixedLegacyComponentSerializer::serialize, MixedLegacyComponentSerializer::deserialize);

    //

    private final boolean hidden;

    public RegionAttributeKey(String name, Class<T> type, boolean hidden, Function<T, String> serializer, Function<String, T> deserializer) {
        super(name, type, serializer, deserializer);
        this.hidden = hidden;

        if ( KEYS.containsKey(name) ) {
            throw new IllegalArgumentException("An attribute key with that name already exists.");
        }
        KEYS.put(name, this);
    }

    public RegionAttributeKey(String name, Class<T> type, Function<T, String> serializer, Function<String, T> deserializer) {
        this(name, type, false, serializer, deserializer);
    }

    //

    public boolean hidden() {
        return hidden;
    }

    //

    public static RegionAttributeKey<?>[] values() {
        return KEYS.values().toArray(RegionAttributeKey<?>[]::new);
    }

    public static RegionAttributeKey<?> valueOf(String name) {
        return KEYS.get(name);
    }

}