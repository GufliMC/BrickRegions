package com.guflimc.brick.regions.common.domain;

import io.ebean.annotation.ConstraintMode;
import io.ebean.annotation.DbForeignKey;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(
        name = "region_attributes",
        uniqueConstraints = @UniqueConstraint(columnNames = {"region_id", "name"})
)
public class DRegionAttribute {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, name = "attrvalue")
    private String value;

    @ManyToOne(optional = false)
    @DbForeignKey(onDelete = ConstraintMode.CASCADE)
    @JoinColumn(name = "region_id")
    private DRegion region;

    public DRegionAttribute() {
    }

    public DRegionAttribute(DRegion region, String name, String value) {
        this.region = region;
        this.name = name;
        this.value = value;
    }

    public DRegion region() {
        return region;
    }

    public String name() {
        return name;
    }

    public String value() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}
