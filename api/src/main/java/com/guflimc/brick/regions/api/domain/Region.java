package com.guflimc.brick.regions.api.domain;

import java.util.UUID;

public interface Region {

    UUID id();

    UUID worldId();

    String name();

    int priority();

}
