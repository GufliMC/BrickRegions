package com.guflimc.brick.regions.api.selection;

import com.guflimc.brick.maths.api.geo.area.Area;
import com.guflimc.brick.maths.api.geo.area.Contour;
import com.guflimc.brick.maths.api.geo.area.CuboidArea;
import com.guflimc.brick.maths.api.geo.pos.Point;
import com.guflimc.brick.maths.api.geo.pos.Vector;

import java.util.UUID;

public class CubeSelection extends AbstractSelection {

    // is not the same as min/max: pos2 can have a lower x, y and/or z value than pos1
    private Vector pos1;
    private Vector pos2;

    protected CubeSelection(UUID worldId) {
        super(worldId);
    }

    public Vector pos1() {
        return pos1;
    }

    public Vector pos2() {
        return pos2;
    }

    public void setPos1(Point pos1) {
        Vector oldPos = this.pos1;
        Runnable oldUndo = undo;
        this.undo = () -> {
            this.pos1 = oldPos;
            this.undo = oldUndo;
        };

        this.pos1 = Vector.from(pos1);
    }

    public void setPos2(Point pos2) {
        Vector oldPos = this.pos2;
        Runnable oldUndo = undo;
        this.undo = () -> {
            this.pos2 = oldPos;
            this.undo = oldUndo;
        };

        this.pos2 = Vector.from(pos2);
    }

    @Override
    public boolean isValid() {
        return pos1 != null && pos2 != null;
    }

    @Override
    public double minY() {
        return Math.min(pos1.y(), pos2.y());
    }

    @Override
    public double maxY() {
        return Math.max(pos1.y(), pos2.y());
    }

    @Override
    public void expandY() {
        Vector oldPos1 = this.pos1;
        Vector oldPos2 = this.pos2;
        Runnable oldUndo = undo;
        this.undo = () -> {
            this.pos1 = oldPos1;
            this.pos2 = oldPos2;
            this.undo = oldUndo;
        };

        pos1 = new Vector(pos1.x(), -70, pos1.z());
        pos2 = new Vector(pos2.x(), 320, pos2.z());
    }

    @Override
    public Area area() {
        return CuboidArea.of(pos1, pos2);
    }
}
