package com.guflimc.brick.regions.api.selection;


import com.guflimc.brick.math.common.geometry.pos2.Vector2;
import com.guflimc.brick.math.common.geometry.pos3.Point3;
import com.guflimc.brick.math.common.geometry.shape2d.Polygon;
import com.guflimc.brick.math.common.geometry.shape3d.PolyPrism;
import com.guflimc.brick.math.common.geometry.shape3d.Shape3;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PolySelection extends AbstractSelection {

    private final List<Vector2> vertices = new ArrayList<>();
    private double minY = Double.POSITIVE_INFINITY;
    private double maxY = Double.NEGATIVE_INFINITY;

    public PolySelection(UUID worldId) {
        super(worldId);
    }

    public int size() {
        return vertices.size();
    }

    public void add(Point3 point) {
        double oldMinY = minY;
        double oldMaxY = maxY;
        Runnable oldUndo = undo;
        undo = () -> {
            this.minY = oldMinY;
            this.maxY = oldMaxY;
            vertices.remove(vertices.size() - 1);
            this.undo = oldUndo;
        };

        vertices.add(new Vector2(point.x(), point.z()));

        if (point.y() < minY) {
            minY = point.y();
        }
        if (point.y() > maxY) {
            maxY = point.y();
        }
    }

    public List<Vector2> points() {
        return vertices;
    }

    @Override
    public boolean isValid() {
        return vertices.size() >= 3;
    }

    @Override
    public double minY() {
        return minY;
    }

    @Override
    public double maxY() {
        return maxY;
    }

    @Override
    public void expandY() {
        double oldMinY = minY;
        double oldMaxY = maxY;
        Runnable oldUndo = undo;
        undo = () -> {
            this.minY = oldMinY;
            this.maxY = oldMaxY;
            this.undo = oldUndo;
        };

        minY = -64;
        maxY = 319;
    }

    @Override
    public Shape3 shape() {
        if ( !isValid() ) {
            throw new IllegalStateException("PolySelection is not valid yet.");
        }
        return new PolyPrism(minY, maxY, new Polygon(vertices));
    }

}
