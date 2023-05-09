package com.guflimc.brick.regions.common.domain;

import com.guflimc.brick.regions.api.domain.modifiable.ModifiableRegion;
import io.ebean.annotation.Index;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.util.UUID;

@Entity
@Index(columnNames = {"world_id", "name"}, unique = true)
public class DRegion extends DModifiableLocality implements ModifiableRegion {

    @Column(name = "region_name")
    private String name;

    public DRegion() {
    }

    public DRegion(UUID worldId, String name) {
        super(worldId);
        this.name = name;
    }

    @Override
    public String name() {
        return name;
    }

}
