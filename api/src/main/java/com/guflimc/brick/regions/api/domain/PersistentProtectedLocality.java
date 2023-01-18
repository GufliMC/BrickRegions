package com.guflimc.brick.regions.api.domain;

import com.guflimc.brick.regions.api.rules.RuleStatus;
import com.guflimc.brick.regions.api.rules.RuleTarget;
import com.guflimc.brick.regions.api.rules.RuleType;

public interface PersistentProtectedLocality extends PersistentLocality, ProtectedLocality {

    LocalityProtectionRule addProtectionRule(int priority, RuleStatus status, RuleTarget<?> target, RuleType... ruleTypes);

    default LocalityProtectionRule addProtectionRule(RuleStatus status, RuleTarget<?> target, RuleType... ruleTypes) {
        return addProtectionRule(0, status, target, ruleTypes);
    }

    void removeRule(LocalityProtectionRule rule);

    void clearRules();

}
