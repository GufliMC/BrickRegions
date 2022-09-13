package com.guflimc.brick.regions.spigot.api.events;

import com.guflimc.brick.regions.api.domain.Region;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public abstract class RegionEvent extends Event {

    private final Region region;

    public RegionEvent(Region region) {
        this.region = region;
    }

    public RegionEvent(Region region, boolean async) {
        super(async);
        this.region = region;
    }

    public Region region() {
        return region;
    }

}
