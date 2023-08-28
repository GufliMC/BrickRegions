//package com.guflimc.brick.regions.api.domain.attribute;
//
//import com.guflimc.brick.regions.api.domain.Region;
//import com.guflimc.brick.regions.api.rules.attributes.RuleStatus;
//import com.guflimc.brick.regions.api.rules.attributes.RuleCondition;
//import com.guflimc.brick.regions.api.rules.action.RuleActionBase;
//
//import java.util.*;
//
//public interface RegionRule {
//
//    Region region();
//
//    int priority();
//
//    RuleStatus status();
//
//    RuleCondition selector();
//
//    RuleActionBase type();
//
//    //
//
//    /**
//     * Returns the best matching rule from the given regions that applies to the given subject and is of the given type.
//     */
//    static <S> Optional<RegionRule> match(S subject, RuleActionBase type, Collection<Region> regions) {
//        return match(regions.stream()
//                .filter(Region.Ruleable.class::isInstance)
//                .map(Region.Ruleable.class::cast)
//                .flatMap(rg -> rg.rules().stream()) // flat map to all rules
//                .filter(rule -> rule.type() == type || rule.type() == RuleActionBase.ALL) // filter by correct type
//                .filter(rule -> rule.selector().test(subject, rule.region())) // filter by predicate
//                .toList());
//    }
//
//    /**
//     * Returns the best matching rule based on multiple priorities:
//     * region > rule > target > type (ALL or specific) > status
//     */
//    static <S> Optional<RegionRule> match(Collection<RegionRule> rules) {
//        return rules.stream().max(Comparator
//                .comparingInt((RegionRule rr) -> rr.region().priority()) // region
//                .thenComparingInt(RegionRule::priority) // rule
//                .thenComparingInt(rr -> rr.selector().priority()) // target
//                .thenComparingInt(rr -> rr.type() == RuleActionBase.ALL ? 0 : 1) // type (specific > ALL)
//                .thenComparingInt(rr -> rr.status().ordinal()) // status (ALLOW > DENY)
//        );
//    }
//
//    /**
//     * Returns if the given action is ALLOWED or DENIED for the given subject in the given regions.
//     */
//    static <S> RuleStatus status(S subject, RuleActionBase type, Collection<Region> regions) {
//        return status(match(subject, type, regions).orElse(null));
//    }
//
//    /**
//     * Returns the status of the given optional rule. Will fall back to ALLOWED by default.
//     */
//    static RuleStatus status(RegionRule rule) {
//        return rule == null ? RuleStatus.ALLOW : rule.status();
//    }
//
//}
