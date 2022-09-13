package com.guflimc.brick.regions.api.rules;

import com.guflimc.brick.regions.api.domain.Region;

public interface RuleTarget<S> {

    String name();

    boolean test(S subject, Region region);

}
