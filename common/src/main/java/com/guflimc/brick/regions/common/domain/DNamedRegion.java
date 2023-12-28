package com.guflimc.brick.regions.common.domain;

import com.guflimc.brick.regions.api.domain.Region;
import io.ebean.annotation.Index;
import org.jetbrains.annotations.NotNull;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import java.util.UUID;

@Entity
@Index(columnNames = {"region_world_id", "region_name"}, unique = true)
public class DNamedRegion extends DRegion implements Region.Named {

    @Column(name = "region_name")
    private String name;

    public DNamedRegion() {
    }

    public DNamedRegion(@NotNull UUID worldId, @NotNull String name) {
        super(worldId);
        this.name = name;
    }

    //

    @Override
    public String name() {
        return name;
    }

}
