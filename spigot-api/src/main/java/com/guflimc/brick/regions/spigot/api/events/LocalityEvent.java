package com.guflimc.brick.regions.spigot.api.events;

import com.guflimc.brick.regions.api.domain.Locality;
import org.bukkit.event.Event;

public abstract class LocalityEvent extends Event {

    private final Locality locality;

    public LocalityEvent(Locality locality) {
        this.locality = locality;
    }

    public LocalityEvent(Locality locality, boolean async) {
        super(async);
        this.locality = locality;
    }

    public Locality locality() {
        return locality;
    }

}
