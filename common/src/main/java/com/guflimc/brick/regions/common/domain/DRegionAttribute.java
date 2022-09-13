package com.guflimc.brick.regions.common.domain;

import com.guflimc.brick.regions.api.domain.Region;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

@Entity
@Table(
        name = "region_attributes",
        uniqueConstraints = @UniqueConstraint(columnNames = {"region_id", "name"})
)
public class DRegionAttribute {

    @Id
    @GeneratedValue
    @JdbcTypeCode(SqlTypes.CHAR)
    private UUID id;

    @ManyToOne(optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private DRegion region;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String value;

    public DRegionAttribute() {
    }

    public DRegionAttribute(DRegion region, String name, String value) {
        this.region = region;
        this.name = name;
        this.value = value;
    }

    public Region region() {
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
