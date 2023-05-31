package com.guflimc.brick.regions.api.domain.locality;

import com.guflimc.brick.regions.api.rules.RuleStatus;
import com.guflimc.brick.regions.api.rules.RuleTarget;
import com.guflimc.brick.regions.api.rules.RuleType;

public interface ModifiableLocality extends Locality {

    // PROPERTIES

    void setPriority(int priority);

    // ATTRIBUTES

    <T> void setAttribute(LocalityAttributeKey<T> key, T value);

    <T> void removeAttribute(LocalityAttributeKey<T> key);


    // RULES

    LocalityRule addRule(int priority, RuleStatus status, RuleTarget target, RuleType... ruleTypes);

    default LocalityRule addRule(RuleStatus status, RuleTarget target, RuleType... ruleTypes) {
        return addRule(0, status, target, ruleTypes);
    }

    void removeRule(LocalityRule rule);

    void removeRules();

}
