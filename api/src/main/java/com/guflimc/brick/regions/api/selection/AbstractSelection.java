package com.guflimc.brick.regions.api.selection;

import java.util.UUID;

public abstract class AbstractSelection implements Selection {

    private final UUID worldId;
    protected Runnable undo;

    protected AbstractSelection(UUID worldId) {
        this.worldId = worldId;
    }

    @Override
    public void undo() {
        if (undo != null) {
            undo.run();
        }
    }

    @Override
    public UUID worldId() {
        return worldId;
    }
}
