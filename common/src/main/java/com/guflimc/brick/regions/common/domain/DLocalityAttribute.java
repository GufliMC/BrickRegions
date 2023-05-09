package com.guflimc.brick.regions.common.domain;

import io.ebean.annotation.ConstraintMode;
import io.ebean.annotation.DbForeignKey;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(
        name = "locality_attributes",
        uniqueConstraints = @UniqueConstraint(columnNames = {"locality_id", "name"})
)
public class DLocalityAttribute {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, name = "attrvalue")
    private String value;

    @ManyToOne(optional = false)
    @DbForeignKey(onDelete = ConstraintMode.CASCADE)
    private DModifiableLocality locality;

    public DLocalityAttribute() {
    }

    public DLocalityAttribute(DModifiableLocality locality, String name, String value) {
        this.locality = locality;
        this.name = name;
        this.value = value;
    }

    public DModifiableLocality locality() {
        return locality;
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
