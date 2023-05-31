package com.guflimc.brick.regions.api.domain.locality;

import com.guflimc.brick.regions.api.rules.RuleStatus;
import com.guflimc.brick.regions.api.rules.RuleTarget;
import com.guflimc.brick.regions.api.rules.RuleType;

import java.util.*;

public interface LocalityRule {

    Locality locality();

    int priority();

    RuleStatus status();

    RuleTarget target();

    RuleType[] types();

    //

    /**
     * Returns the best matching rule from the given regions that applies to the given subject and is of the given type.
     */
    static <T extends Locality, S> Optional<LocalityRule> match(S subject, RuleType type, Collection<T> regions) {
        return match(regions.stream()
                .flatMap(rg -> rg.rules().stream()) // flat map to all rules
                .filter(rule -> Arrays.stream(rule.types()).anyMatch(t -> t == type || t == RuleType.ALL)) // filter by correct type
                .filter(rule -> rule.target().test(subject, rule.locality())) // filter by predicate
                .toList());
    }

    /**
     * Returns the best matching rule based on multiple priorities:
     * region > rule > target > type (ALL or specific) > status
     */
    static <S> Optional<LocalityRule> match(Collection<LocalityRule> rules) {
        return rules.stream().max(Comparator
                .comparingInt((LocalityRule rr) -> rr.locality().priority()) // region
                .thenComparingInt(LocalityRule::priority) // rule
                .thenComparingInt(rr -> rr.target().priority()) // target
                .thenComparingInt(rr -> List.of(rr.types()).contains(RuleType.ALL) ? 0 : 1) // type (specific > ALL)
                .thenComparingInt(rr -> rr.status().ordinal()) // status (ALLOW > DENY)
        );
    }

    /**
     * Returns if the given action is ALLOWED or DENIED for the given subject in the given regions.
     */
    static <S> RuleStatus status(S subject, RuleType type, Collection<Locality> regions) {
        return status(match(subject, type, regions).orElse(null));
    }

    /**
     * Returns the status of the given optional rule. Will fall back to ALLOWED by default.
     */
    static RuleStatus status(LocalityRule rule) {
        return rule == null ? RuleStatus.ALLOW : rule.status();
    }

}
