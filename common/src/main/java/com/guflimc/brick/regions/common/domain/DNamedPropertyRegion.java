package com.guflimc.brick.regions.common.domain;

import com.guflimc.brick.regions.api.domain.Region;
import com.guflimc.brick.regions.common.EventManager;
import io.ebean.annotation.DbDefault;
import org.jetbrains.annotations.NotNull;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import java.util.UUID;

@Entity
public class DNamedPropertyRegion extends DNamedRegion implements Region.PropertyModifiable, Region.ActiveModifiable {

    @Column(name = "region_priority", nullable = false)
    @DbDefault("0")
    protected int priority = 1;

    @Column(name = "region_active", nullable = false)
    @DbDefault("true")
    protected boolean active = true;

    public DNamedPropertyRegion() {
    }

    public DNamedPropertyRegion(@NotNull UUID worldId, @NotNull String name) {
        super(worldId, name);
    }

    @Override
    public int priority() {
        return priority;
    }

    @Override
    public void setPriority(int priority) {
        this.priority = priority;
        EventManager.INSTANCE.onPropertyChange(this);
    }

    @Override
    public boolean active() {
        return active;
    }

    @Override
    public void setActive(boolean active) {
        if ( active == this.active ) return;
        this.active = active;

        if ( active ) {
            EventManager.INSTANCE.onActivate(this);
        } else {
            EventManager.INSTANCE.onDeactivate(this);
        }
    }
}
