package com.guflimc.brick.regions.api.domain;

import com.guflimc.brick.orm.api.attributes.AttributeKey;
import com.guflimc.brick.regions.api.rules.RuleStatus;
import com.guflimc.brick.regions.api.rules.RuleTarget;
import com.guflimc.brick.regions.api.rules.RuleType;
import net.kyori.adventure.text.Component;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public interface PersistentRegion extends Region {

    void setPriority(int priority);

    void setDisplayName(Component displayName);

    // attributes

    <T> void setAttribute(RegionAttributeKey<T> key, T value);

    <T> void removeAttribute(RegionAttributeKey<T> key);

    <T> Optional<T> attribute(RegionAttributeKey<T> key);

    // protection rules

    List<RegionProtectionRule> rules();

    RegionProtectionRule addProtectionRule(int priority, RuleStatus status, RuleTarget<?> target, RuleType... ruleTypes);

    default RegionProtectionRule addProtectionRule(RuleStatus status, RuleTarget<?> target, RuleType... ruleTypes) {
        return addProtectionRule(0, status, target, ruleTypes);
    }

    void removeRule(RegionProtectionRule rule);

    void clearRules();

    //

    class RegionAttributeKey<T> extends AttributeKey<T> {

        public RegionAttributeKey(String name, Class<T> type, Function<T, String> serializer, Function<String, T> deserializer) {
            super(name, type, serializer, deserializer);
        }

    }

}
