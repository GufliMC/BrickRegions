package com.guflimc.brick.regions.api.domain.region;

import org.jetbrains.annotations.NotNull;

public record RegionKey(String namespace, String name) {

    public static String BRICK_REGIONS_NAMESPACE = "brick-regions";

    public RegionKey(@NotNull String namespace, @NotNull String name) {
        if ( !namespace.equals(namespace.toLowerCase()) ) {
            throw new IllegalArgumentException("namespace must be lowercase");
        }
        if ( !name.equals(name.toLowerCase()) ) {
            throw new IllegalArgumentException("name must be lowercase");
        }

        this.namespace = namespace;
        this.name = name;
    }

    public RegionKey(@NotNull String name) {
        this(BRICK_REGIONS_NAMESPACE, name);
    }

}
