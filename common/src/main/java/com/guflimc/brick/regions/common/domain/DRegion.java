package com.guflimc.brick.regions.common.domain;

import com.guflimc.brick.regions.api.domain.region.ModifiableRegion;
import com.guflimc.brick.regions.api.domain.region.RegionKey;
import io.ebean.annotation.Index;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.util.UUID;

@Entity
@Index(columnNames = {"world_id", "name"}, unique = true)
public class DRegion extends DLocality implements ModifiableRegion {

    @Column(name = "region_key")
    private RegionKey key; // TODO converter

    @Column(name = "region_archived")
    private boolean archived;

    public DRegion() {
    }

    public DRegion(UUID worldId, RegionKey key) {
        super(worldId);
        this.key = key;
    }

    @Override
    public RegionKey key() {
        return key;
    }

    @Override
    public void setArchived(boolean archived) {
        this.archived = archived;
    }

    @Override
    public boolean archived() {
        return archived;
    }
}
