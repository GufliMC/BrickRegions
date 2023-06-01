package com.guflimc.brick.regions.common.domain;

import com.guflimc.brick.regions.api.domain.Region;
import io.ebean.annotation.Index;
import org.jetbrains.annotations.NotNull;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.util.UUID;

@Entity
@Index(columnNames = {"region_world_id", "region_name"}, unique = true)
public class DKeyedRegion extends DRegion implements Region.Keyed {

    @Column(name = "region_name")
    private String name;

    public DKeyedRegion() {
    }

    public DKeyedRegion(@NotNull UUID worldId, @NotNull String name) {
        super(worldId);
        this.name = name;
    }

    //

    @Override
    public String name() {
        return name;
    }

}
