package com.guflimc.brick.regions.api.domain;

import com.guflimc.brick.regions.api.attributes.AttributeKey;
import com.guflimc.brick.regions.api.rules.RuleStatus;
import com.guflimc.brick.regions.api.rules.RuleTarget;
import com.guflimc.brick.regions.api.rules.RuleType;
import net.kyori.adventure.text.Component;

import java.util.List;
import java.util.Optional;

public interface PersistentRegion extends Region {

    void setPriority(int priority);

    void setDisplayName(Component displayName);

    // attributes

    <T> void setAttribute(AttributeKey<T> key, T value);

    <T> void removeAttribute(AttributeKey<T> key);

    <T> Optional<T> attribute(AttributeKey<T> key);

    // protection rules

    List<RegionProtectionRule> rules();

    RegionProtectionRule addProtectionRule(int priority, RuleStatus status, RuleTarget<?> target, RuleType... ruleTypes);

    default RegionProtectionRule addProtectionRule(RuleStatus status, RuleTarget<?> target, RuleType... ruleTypes) {
        return addProtectionRule(0, status, target, ruleTypes);
    }

    void removeRule(RegionProtectionRule rule);

    void clearRules();

}
