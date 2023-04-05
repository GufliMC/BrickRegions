package com.guflimc.brick.regions.api.domain;

import com.guflimc.brick.orm.api.attributes.AttributeKey;

import java.awt.*;
import java.util.function.Function;

public class LocalityAttributeKey<T> extends AttributeKey<T> {

    // Modify the settings when this locality is rendered on a map.

    public static final LocalityAttributeKey<Boolean> MAP_HIDDEN = new LocalityAttributeKey<>("map_hidden", Boolean.class,
            Object::toString, Boolean::parseBoolean);

    public static final LocalityAttributeKey<Color> MAP_FILL_COLOR = new LocalityAttributeKey<>("map_fill_color", Color.class,
             c -> c.getRGB() + "", Color::decode);

    public static final LocalityAttributeKey<Double> MAP_FILL_OPACITY = new LocalityAttributeKey<>("map_fill_opacity", Double.class,
            Object::toString, Double::parseDouble);

    public static final LocalityAttributeKey<Color> MAP_STROKE_COLOR = new LocalityAttributeKey<>("map_stroke_color", Color.class,
            c -> c.getRGB() + "", Color::decode);

    public static final LocalityAttributeKey<Double> MAP_STROKE_OPACITY = new LocalityAttributeKey<>("map_stroke_opacity", Double.class,
            Object::toString, Double::parseDouble);

    public static final LocalityAttributeKey<Integer> MAP_STROKE_WEIGHT = new LocalityAttributeKey<>("map_stroke_weight", Integer.class,
            Object::toString, Integer::parseInt);

    public static final LocalityAttributeKey<String> MAP_HOVER_TEXT = new LocalityAttributeKey<>("map_hover_text", String.class,
            Function.identity(), Function.identity());

    public static final LocalityAttributeKey<String> MAP_CLICK_TEXT = new LocalityAttributeKey<>("map_click_text", String.class,
            Function.identity(), Function.identity());


    public LocalityAttributeKey(String name, Class<T> type, Function<T, String> serializer, Function<String, T> deserializer) {
        super(name, type, serializer, deserializer);
    }

}