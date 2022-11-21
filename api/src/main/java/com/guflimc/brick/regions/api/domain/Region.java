package com.guflimc.brick.regions.api.domain;

import com.guflimc.brick.maths.api.geo.pos.Point;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.UUID;

public interface Region {

    UUID id();

    UUID worldId();

    String name();

    default Component displayName() {
        return Component.text(name(), NamedTextColor.WHITE);
    }

    int priority();

    boolean contains(Point point);

}
