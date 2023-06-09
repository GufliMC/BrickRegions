package com.guflimc.brick.regions.common.domain;

import com.guflimc.brick.math.common.geometry.pos3.Point3;
import com.guflimc.brick.regions.api.domain.Region;
import com.guflimc.brick.regions.api.domain.attribute.RegionAttributeKey;
import com.guflimc.brick.regions.api.domain.attribute.RegionRule;
import com.guflimc.brick.regions.api.rules.RuleStatus;
import com.guflimc.brick.regions.api.rules.RuleTarget;
import com.guflimc.brick.regions.api.rules.RuleType;
import com.guflimc.brick.regions.common.EventManager;
import io.ebean.annotation.Index;
import org.jetbrains.annotations.NotNull;

import jakarta.persistence.*;
import java.util.*;

@Entity
@Inheritance
@Table(name = "regions")
public class DRegion implements Region, Region.AttributeModifiable, Region.RuleModifiable {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "region_world_id", nullable = false)
    private UUID worldId;

    @OneToMany(targetEntity = DRegionAttribute.class, mappedBy = "region",
            cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<DRegionAttribute> attributes = new ArrayList<>();

    @OneToMany(targetEntity = DRegionRule.class, mappedBy = "region",
            cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<DRegionRule> rules = new ArrayList<>();

    public DRegion() {
    }

    public DRegion(@NotNull UUID worldId) {
        this.worldId = worldId;
    }

    //

    public UUID id() {
        return id;
    }

    @Override
    public UUID worldId() {
        return worldId;
    }

    @Override
    public int priority() {
        return 0;
    }

    @Override
    public boolean contains(@NotNull Point3 point) {
        return false;
    }

    //

    // attributes

    @Override
    public <U> void removeAttribute(@NotNull RegionAttributeKey<U> key) {
        attributes.stream()
                .filter(attribute -> attribute.name().equals(key.name()))
                .toList()
                .forEach(attribute -> {
                    attributes.remove(attribute);
                    EventManager.INSTANCE.onAttributeRemove(this, key, key.deserialize(attribute.value()));
                });
    }

    @Override
    public <U> void setAttribute(@NotNull RegionAttributeKey<U> key, @NotNull U value) {
        if (value == null) {
            removeAttribute(key);
            return;
        }

        DRegionAttribute attribute = attributes.stream()
                .filter(attr -> attr.name().equals(key.name()))
                .findFirst().orElse(null);

        if (attribute == null) {
            attributes.add(new DRegionAttribute(this, key.name(), key.serialize(value)));
            EventManager.INSTANCE.onAttributeChange(this, key, null, value);
            return;
        }

        attribute.setValue(key.serialize(value));
        EventManager.INSTANCE.onAttributeChange(this, key, key.deserialize(attribute.value()), value);
    }

    @Override
    public <U> Optional<U> attribute(RegionAttributeKey<U> key) {
        return attributes.stream()
                .filter(attr -> attr.name().equals(key.name()))
                .findFirst()
                .flatMap(ra -> Optional.ofNullable(key.deserialize(ra.value())));
    }

    // rules

    @Override
    public List<RegionRule> rules() {
        return Collections.unmodifiableList(rules);
    }

    public RegionRule addRule(int priority, @NotNull RuleStatus status, @NotNull RuleTarget target, RuleType @NotNull ... ruleTypes) {
        DRegionRule rule = new DRegionRule(this, priority, status, target, ruleTypes);
        rules.add(rule);

        EventManager.INSTANCE.onRuleAdd(this, rule);
        return rule;
    }

    public RegionRule addRule(@NotNull RuleStatus status, @NotNull RuleTarget target, RuleType @NotNull ... ruleTypes) {
        return addRule(0, status, target, ruleTypes);
    }

    @Override
    public void removeRule(@NotNull RegionRule rule) {
        if (rule instanceof DRegionRule && rules.contains(rule)) {
            rules.remove(rule);
            EventManager.INSTANCE.onRuleRemove(this, rule);
        }
    }

    @Override
    public void removeRules() {
        rules.forEach(rule -> EventManager.INSTANCE.onRuleRemove(this, rule));
        rules.clear();
    }
}
