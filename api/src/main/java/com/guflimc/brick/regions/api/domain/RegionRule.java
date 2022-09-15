package com.guflimc.brick.regions.api.domain;

import com.guflimc.brick.regions.api.rules.RuleStatus;
import com.guflimc.brick.regions.api.rules.RuleTarget;
import com.guflimc.brick.regions.api.rules.RuleType;

public interface RegionRule {

    Region region();

    int priority();

    RuleStatus status();

    RuleTarget<?> target();

    RuleType[] types();

}
