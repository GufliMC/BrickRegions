package com.guflimc.brick.regions.api.rules;

import com.guflimc.brick.regions.api.domain.Region;
import com.guflimc.brick.regions.api.rules.attributes.RuleAction;
import com.guflimc.brick.regions.api.rules.attributes.RuleCondition;
import com.guflimc.brick.regions.api.rules.attributes.RuleStatus;

public interface Rule {

    Region region();

    RuleStatus status();

    RuleCondition condition();

    RuleAction action();

    int priority();

}
