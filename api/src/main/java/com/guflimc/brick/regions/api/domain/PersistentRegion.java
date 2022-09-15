package com.guflimc.brick.regions.api.domain;

import com.guflimc.brick.regions.api.attributes.AttributeKey;
import com.guflimc.brick.regions.api.rules.RuleStatus;
import com.guflimc.brick.regions.api.rules.RuleTarget;
import com.guflimc.brick.regions.api.rules.RuleType;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public interface PersistentRegion extends Region {

    void setPriority(int priority);

    <T> void setAttribute(AttributeKey<T> key, T value);

    <T> Optional<T> attribute(AttributeKey<T> key);

//    RegionRule addRule(int priority, RuleStatus status, RuleTarget<?> target, RuleType... ruleTypes);
//
//    default RegionRule addRule(RuleStatus status, RuleTarget<?> target, RuleType... ruleTypes) {
//        return addRule(0, status, target, ruleTypes);
//    }

    void removeRule(RegionRule rule);

    Collection<RegionRule> rules();

}
