package com.guflimc.brick.regions.api.domain;

import com.guflimc.brick.maths.api.geo.pos.Point;

import java.util.UUID;

public interface Region {

    UUID id();

    UUID worldId();

    String name();

    int priority();

    boolean contains(Point point);

}
