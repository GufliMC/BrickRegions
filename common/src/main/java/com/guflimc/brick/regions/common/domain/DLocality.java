package com.guflimc.brick.regions.common.domain;

import com.guflimc.brick.math.common.geometry.pos3.Point3;
import com.guflimc.brick.regions.api.domain.Locality;
import com.guflimc.brick.regions.common.EventManager;

import javax.persistence.*;
import java.util.*;

@Entity
@Inheritance
@Table(name = "localities")
public abstract class DLocality implements Locality {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "locality_world_id", nullable = false)
    private UUID worldId;

    public DLocality() {
    }

    public DLocality(UUID worldId) {
        this.worldId = worldId;
    }

    //

    @Override
    public UUID id() {
        return id;
    }

    @Override
    public UUID worldId() {
        return worldId;
    }

    @Override
    public boolean contains(Point3 point) {
        return false;
    }

}
