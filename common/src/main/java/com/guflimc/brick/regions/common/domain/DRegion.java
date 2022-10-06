package com.guflimc.brick.regions.common.domain;

import com.guflimc.brick.regions.api.attributes.AttributeKey;
import com.guflimc.brick.regions.api.domain.PersistentRegion;
import com.guflimc.brick.regions.api.domain.RegionProtectionRule;
import com.guflimc.brick.regions.api.rules.RuleStatus;
import com.guflimc.brick.regions.api.rules.RuleTarget;
import com.guflimc.brick.regions.api.rules.RuleType;
import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.*;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorValue("REGION")
@DiscriminatorColumn(name = "type")
@Table(name = "regions")
public class DRegion implements PersistentRegion {

    @Id
    @GeneratedValue
    @JdbcTypeCode(SqlTypes.CHAR)
    private UUID id;

    @JdbcTypeCode(SqlTypes.CHAR)
    private UUID worldId;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    @ColumnDefault("1")
    private int priority = 1;

    @OneToMany(targetEntity = DRegionAttribute.class, mappedBy = "region",
            cascade = { CascadeType.PERSIST, CascadeType.REMOVE, CascadeType.MERGE }, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<DRegionAttribute> attributes = new ArrayList<>();

    @OneToMany(targetEntity = DRegionProtectionRule.class, mappedBy = "region",
            cascade = { CascadeType.PERSIST, CascadeType.REMOVE, CascadeType.MERGE }, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<DRegionProtectionRule> rules = new ArrayList<>();

    public DRegion() {
    }

    public DRegion(UUID worldId, String name) {
        this.worldId = worldId;
        this.name = name;
    }

    @Override
    public UUID id() {
        return id;
    }

    public UUID worldId() {
        return worldId;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public int priority() {
        return priority;
    }

    @Override
    public void setPriority(int priority) {
        this.priority = priority;
    }

    // attributes

    @Override
    public <T> void removeAttribute(AttributeKey<T> key) {
        attributes.removeIf(attr -> attr.name().equals(key.name()));
    }

    @Override
    public <T> void setAttribute(AttributeKey<T> key, T value) {
        if (value == null) {
            removeAttribute(key);
            return;
        }

        attributes.stream().filter(attr -> attr.name().equals(key.name())).findFirst()
                .ifPresentOrElse(
                        attr -> attr.setValue(key.serialize(value)),
                        () -> attributes.add(new DRegionAttribute(this, key.name(), key.serialize(value)))
                );
    }

    @Override
    public <T> Optional<T> attribute(AttributeKey<T> key) {
        return attributes.stream().filter(attr -> attr.name().equals(key.name())).findFirst()
                .map(ra -> key.deserialize(ra.value()));
    }

    // protection rules

    @Override
    public List<RegionProtectionRule> rules() {
        return Collections.unmodifiableList(rules);
    }

    public RegionProtectionRule addProtectionRule(int priority, RuleStatus status, RuleTarget<?> target, RuleType... ruleTypes) {
        DRegionProtectionRule rule = new DRegionProtectionRule(this, priority, status, target, ruleTypes);
        rules.add(rule);
        return rule;
    }

    public RegionProtectionRule addProtectionRule(RuleStatus status, RuleTarget<?> target, RuleType... ruleTypes) {
        return addProtectionRule(0, status, target, ruleTypes);
    }

    @Override
    public void removeRule(RegionProtectionRule rule) {
        rules.remove(rule);
    }

    @Override
    public void clearRules() {
        rules.clear();
    }

}
