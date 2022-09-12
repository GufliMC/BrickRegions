package com.guflimc.brick.regions.api.selection;

import com.guflimc.brick.maths.api.geo.area.Area;
import com.guflimc.brick.maths.api.geo.area.Contour;
import com.guflimc.brick.maths.api.geo.area.PolyArea;
import com.guflimc.brick.maths.api.geo.pos.Point;
import com.guflimc.brick.maths.api.geo.pos.Vector2;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PolySelection extends AbstractSelection {

    private final List<Vector2> points = new ArrayList<>();
    private double minY = Double.MAX_VALUE;
    private double maxY = Double.MIN_VALUE;

    protected PolySelection(UUID worldId) {
        super(worldId);
    }

    public int size() {
        return points.size();
    }

    public void add(Point point) {
        double oldMinY = minY;
        double oldMaxY = maxY;
        Runnable oldUndo = undo;
        undo = () -> {
            this.minY = oldMinY;
            this.maxY = oldMaxY;
            points.remove(points.size() - 1);
            this.undo = oldUndo;
        };

        points.add(new Vector2(point.x(), point.z()));

        if (point.y() < minY) {
            minY = point.y();
        }
        if (point.y() > maxY) {
            maxY = point.y();
        }
    }

    public List<Vector2> points() {
        return points;
    }

    @Override
    public boolean isValid() {
        return points.size() >= 3;
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

        minY = -70;
        maxY = 320;
    }

    @Override
    public Area area() {
        if ( !isValid() ) {
            throw new IllegalStateException("PolySelection is not valid yet.");
        }
        return new PolyArea(minY, maxY, points);
    }

}
