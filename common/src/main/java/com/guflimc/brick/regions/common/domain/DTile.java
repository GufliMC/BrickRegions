package com.guflimc.brick.regions.common.domain;

import com.guflimc.brick.math.common.geometry.pos2.Point2;
import com.guflimc.brick.math.common.geometry.shape2d.Shape2;
import com.guflimc.brick.math.database.GsonTools;
import com.guflimc.brick.math.database.Point2Converter;
import com.guflimc.brick.math.database.Shape2Converter;
import com.guflimc.brick.regions.api.domain.tile.Tile;
import io.ebean.annotation.ConstraintMode;
import io.ebean.annotation.DbForeignKey;

import javax.persistence.*;
import java.util.UUID;

@Entity
public class DTile implements Tile {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(optional = false)
    @DbForeignKey(onDelete = ConstraintMode.CASCADE)
    private DTileGroup group;

    @Convert(converter = Point2Converter.class)
    private Point2 position;

    @Convert(converter = Shape2Converter.class)
    private Shape2 shape;

    public DTile() {
    }

    public DTile(DTileGroup group, Point2 position, Shape2 shape) {
        this.group = group;
        this.position = position;
        this.shape = shape;
    }

    public Point2 position() {
        return position;
    }

    public Shape2 shape() {
        return shape;
    }

    @Override
    public String toString() {
        return "DTile{position:" + position + "}";
    }
}
