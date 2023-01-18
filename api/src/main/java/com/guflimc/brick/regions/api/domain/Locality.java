package com.guflimc.brick.regions.api.domain;

import com.guflimc.brick.math.common.geometry.pos3.Point3;

import java.util.UUID;

public interface Locality {

    UUID id();

    UUID worldId();

    int priority();

    boolean contains(Point3 point);

}
