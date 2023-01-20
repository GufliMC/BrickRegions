package com.guflimc.brick.regions.common.domain;

import com.guflimc.brick.orm.jpa.converters.ComponentConverter;
import com.guflimc.brick.regions.api.domain.modifiable.ModifiableRegion;
import com.guflimc.brick.regions.common.EventManager;
import io.ebean.annotation.DbDefault;
import io.ebean.annotation.Index;
import net.kyori.adventure.text.Component;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import java.util.UUID;

@Entity
@Index(columnNames = {"world_id", "name"}, unique = true)
public class DRegion extends DLocality implements ModifiableRegion {

    @Column(name = "region_name")
    private String name;

    @Convert(converter = ComponentConverter.class)
    @Column(name = "region_displayname", length = 2048)
    @DbDefault("null")
    private Component displayName;

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

    @Override
    public Component displayName() {
        return displayName == null ? ModifiableRegion.super.displayName() : displayName;
    }

    @Override
    public void setDisplayName(Component displayName) {
        this.displayName = displayName;
        EventManager.INSTANCE.onPropertyChange(this);
    }

}
