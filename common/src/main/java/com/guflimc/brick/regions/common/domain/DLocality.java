package com.guflimc.brick.regions.common.domain;

import com.guflimc.brick.math.common.geometry.pos3.Point3;
import com.guflimc.brick.regions.api.domain.LocalityAttributeKey;
import com.guflimc.brick.regions.api.domain.LocalityProtectionRule;
import com.guflimc.brick.regions.api.domain.modifiable.ModifiableAttributedLocality;
import com.guflimc.brick.regions.api.domain.modifiable.ModifiableProtectedLocality;
import com.guflimc.brick.regions.api.rules.RuleStatus;
import com.guflimc.brick.regions.api.rules.RuleTarget;
import com.guflimc.brick.regions.api.rules.RuleType;
import com.guflimc.brick.regions.common.EventManager;
import io.ebean.annotation.DbDefault;

import javax.persistence.*;
import java.util.*;

@Entity
@Inheritance
@Table(name = "localities")
public abstract class DLocality implements ModifiableProtectedLocality, ModifiableAttributedLocality {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private UUID worldId;

    @Column(nullable = false)
    @DbDefault("1")
    private int priority = 1;

    @OneToMany(targetEntity = DLocalityAttribute.class, mappedBy = "locality",
            cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<DLocalityAttribute> attributes = new ArrayList<>();

    @OneToMany(targetEntity = DLocalityProtectionRule.class, mappedBy = "locality",
            cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<DLocalityProtectionRule> rules = new ArrayList<>();

    public DLocality() {
    }

    public DLocality(UUID worldId) {
        this.worldId = worldId;
    }

    //

    @Override
    public UUID id() {
        return id;
    }

    @Override
    public UUID worldId() {
        return worldId;
    }

    @Override
    public int priority() {
        return priority;
    }

    @Override
    public void setPriority(int priority) {
        this.priority = priority;
        EventManager.INSTANCE.onPropertyChange(this);
    }

    @Override
    public boolean contains(Point3 point) {
        return false;
    }

    //

    // attributes

    @Override
    public <U> void removeAttribute(LocalityAttributeKey<U> key) {
        attributes.stream()
                .filter(attribute -> attribute.name().equals(key.name()))
                .toList()
                .forEach(attribute -> {
                    attributes.remove(attribute);
                    EventManager.INSTANCE.onAttributeRemove(this, key, key.deserialize(attribute.value()));
                });
    }

    @Override
    public <U> void setAttribute(LocalityAttributeKey<U> key, U value) {
        if (value == null) {
            removeAttribute(key);
            return;
        }

        DLocalityAttribute attribute = attributes.stream()
                .filter(attr -> attr.name().equals(key.name()))
                .findFirst().orElse(null);

        if (attribute == null) {
            attributes.add(new DLocalityAttribute(this, key.name(), key.serialize(value)));
            EventManager.INSTANCE.onAttributeChange(this, key, null, value);
            return;
        }

        attribute.setValue(key.serialize(value));
        EventManager.INSTANCE.onAttributeChange(this, key, key.deserialize(attribute.value()), value);
    }

    @Override
    public <U> Optional<U> attribute(LocalityAttributeKey<U> key) {
        return attributes.stream()
                .filter(attr -> attr.name().equals(key.name()))
                .findFirst()
                .flatMap(ra -> Optional.ofNullable(key.deserialize(ra.value())));
    }

    // rules

    @Override
    public List<LocalityProtectionRule> rules() {
        return Collections.unmodifiableList(rules);
    }

    public LocalityProtectionRule addProtectionRule(int priority, RuleStatus status, RuleTarget target, RuleType... ruleTypes) {
        DLocalityProtectionRule rule = new DLocalityProtectionRule(this, priority, status, target, ruleTypes);
        rules.add(rule);

        EventManager.INSTANCE.onRuleAdd(rule);
        return rule;
    }

    public LocalityProtectionRule addProtectionRule(RuleStatus status, RuleTarget target, RuleType... ruleTypes) {
        return addProtectionRule(0, status, target, ruleTypes);
    }

    @Override
    public void removeRule(LocalityProtectionRule rule) {
        if (rule instanceof DLocalityProtectionRule && rules.contains(rule)) {
            rules.remove(rule);
            EventManager.INSTANCE.onRuleRemove(rule);
        }
    }

    @Override
    public void removeRules() {
        rules.forEach(EventManager.INSTANCE::onRuleRemove);
        rules.clear();
    }

}
