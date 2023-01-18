package com.guflimc.brick.regions.api.selection;

import com.guflimc.brick.math.common.geometry.pos3.Point3;
import com.guflimc.brick.math.common.geometry.pos3.Vector3;
import com.guflimc.brick.math.common.geometry.shape3d.RectPrism;
import com.guflimc.brick.math.common.geometry.shape3d.Shape3;

import java.util.UUID;

public class CubeSelection extends AbstractSelection {

    // is not the same as min/max: pos2 can have a lower x, y and/or z value than pos1
    private Vector3 pos1;
    private Vector3 pos2;

    public CubeSelection(UUID worldId) {
        super(worldId);
    }

    public Vector3 pos1() {
        return pos1;
    }

    public Vector3 pos2() {
        return pos2;
    }

    public void setPos1(Point3 pos1) {
        Vector3 oldPos = this.pos1;
        Runnable oldUndo = undo;
        this.undo = () -> {
            this.pos1 = oldPos;
            this.undo = oldUndo;
        };

        this.pos1 = Vector3.of(pos1);
    }

    public void setPos2(Point3 pos2) {
        Vector3 oldPos = this.pos2;
        Runnable oldUndo = undo;
        this.undo = () -> {
            this.pos2 = oldPos;
            this.undo = oldUndo;
        };

        this.pos2 = Vector3.of(pos2);
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
        Vector3 oldPos1 = this.pos1;
        Vector3 oldPos2 = this.pos2;
        Runnable oldUndo = undo;
        this.undo = () -> {
            this.pos1 = oldPos1;
            this.pos2 = oldPos2;
            this.undo = oldUndo;
        };

        pos1 = new Vector3(pos1.x(), -64, pos1.z());
        pos2 = new Vector3(pos2.x(), 319, pos2.z());
    }

    @Override
    public Shape3 shape() {
        return RectPrism.of(pos1, pos2);
    }
}
