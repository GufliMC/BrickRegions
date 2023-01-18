package com.guflimc.brick.regions.api.selection;

import com.guflimc.brick.math.common.geometry.shape3d.Shape3;

import java.util.UUID;

public interface Selection {

    UUID worldId();

    double minY();

    double maxY();

    boolean isValid();

    void expandY();

    void undo();

    Shape3 shape();

}
