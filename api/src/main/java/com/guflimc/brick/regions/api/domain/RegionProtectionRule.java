package com.guflimc.brick.regions.api.domain;

import com.guflimc.brick.regions.api.rules.RuleStatus;
import com.guflimc.brick.regions.api.rules.RuleTarget;
import com.guflimc.brick.regions.api.rules.RuleType;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Stream;

public interface RegionProtectionRule {

    Region region();

    int priority();

    RuleStatus status();

    RuleTarget<?> target();

    RuleType[] types();

    //

    /**
     * Returns the best matching rule from the given regions that applies to the given subject and is of the given type.
     */
    static <S> Optional<RegionProtectionRule> match(S subject, RuleType type, Collection<Region> regions) {
        return match(persistentRegions(regions)
                .flatMap(rg -> rg.rules().stream()) // flat map to all rules
                .filter(rule -> Arrays.stream(rule.types()).anyMatch(t -> t == type)) // filter by correct type
                .filter(rule -> rule.target().testAny(subject, rule.region()))// filter by predicate
                .toList());
    }

    /**
     * Returns the best matching rule based on multiple priorities:
     * region > rule > target > status
     */
    static <S> Optional<RegionProtectionRule> match(Collection<RegionProtectionRule> rules) {
        return rules.stream().max(Comparator
                .comparingInt((RegionProtectionRule rr) -> rr.region().priority()) // region
                .thenComparingInt(RegionProtectionRule::priority) // rule
                .thenComparingInt(rr -> rr.target().priority()) // target
                .thenComparingInt(rr -> rr.status().ordinal()) // status (ALLOW > DENY)
        );
    }

    /**
     * Returns if the given action is ALLOWED or DENIED for the given subject in the given regions.
     */
    static <S> RuleStatus status(S subject, RuleType type, Collection<Region> regions) {
        return status(match(subject, type, regions));
    }

    /**
     * Returns the status of the given optional rule. Will fallback to ALLOWED by default.
     */
    static RuleStatus status(Optional<RegionProtectionRule> rule) {
        return rule
                .map(RegionProtectionRule::status)
                .orElse(RuleStatus.ALLOW);
    }

    private static Stream<PersistentRegion> persistentRegions(Collection<Region> regions) {
        return regions.stream()
                .filter(rg -> rg instanceof PersistentRegion)
                .map(rg -> (PersistentRegion) rg);
    }

}
