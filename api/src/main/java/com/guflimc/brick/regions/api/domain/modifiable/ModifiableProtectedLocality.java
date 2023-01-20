package com.guflimc.brick.regions.api.domain.modifiable;

import com.guflimc.brick.regions.api.domain.LocalityProtectionRule;
import com.guflimc.brick.regions.api.domain.ProtectedLocality;
import com.guflimc.brick.regions.api.rules.RuleStatus;
import com.guflimc.brick.regions.api.rules.RuleTarget;
import com.guflimc.brick.regions.api.rules.RuleType;

public interface ModifiableProtectedLocality extends ModifiableLocality, ProtectedLocality {

    LocalityProtectionRule addProtectionRule(int priority, RuleStatus status, RuleTarget target, RuleType... ruleTypes);

    default LocalityProtectionRule addProtectionRule(RuleStatus status, RuleTarget target, RuleType... ruleTypes) {
        return addProtectionRule(0, status, target, ruleTypes);
    }

    void removeRule(LocalityProtectionRule rule);

    void removeRules();

}
