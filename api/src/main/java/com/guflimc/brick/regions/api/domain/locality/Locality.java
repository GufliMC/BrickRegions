package com.guflimc.brick.regions.api.domain.locality;

import com.guflimc.brick.math.common.geometry.pos3.Point3;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface Locality {

    UUID id();

    UUID worldId();

    int priority();

    // SPATIAL

    boolean contains(@NotNull Point3 point);

    // ATTRIBUTES

    <T> Optional<T> attribute(LocalityAttributeKey<T> key);

    // RULES

    List<LocalityRule> rules();

}
