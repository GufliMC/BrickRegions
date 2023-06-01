package com.guflimc.brick.regions.api.domain;

import com.guflimc.brick.math.common.geometry.pos3.Location;
import com.guflimc.brick.math.common.geometry.pos3.Point3;
import com.guflimc.brick.math.common.geometry.pos3.Vector3;
import com.guflimc.brick.math.common.geometry.shape3d.Shape3;
import com.guflimc.brick.regions.api.domain.attribute.RegionAttributeKey;
import com.guflimc.brick.regions.api.domain.attribute.RegionRule;
import com.guflimc.brick.regions.api.rules.RuleStatus;
import com.guflimc.brick.regions.api.rules.RuleTarget;
import com.guflimc.brick.regions.api.rules.RuleType;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public interface Region {

    UUID worldId();

    int priority();

    boolean contains(@NotNull Point3 point);

    //

    static interface Keyed extends Region {

        UUID id();

        String name();

    }

    static interface Attributeable extends Region {

        <T> Optional<T> attribute(RegionAttributeKey<T> key);

    }

    static interface AttributeModifiable extends Attributeable {

        <T> void setAttribute(@NotNull RegionAttributeKey<T> key, @NotNull T value);

        <T> void removeAttribute(@NotNull RegionAttributeKey<T> key);

    }

    static interface Ruleable extends Region {

        List<RegionRule> rules();

    }

    static interface RuleModifiable extends Ruleable {

        RegionRule addRule(int priority, @NotNull RuleStatus status, @NotNull RuleTarget target, @NotNull RuleType... ruleTypes);

        default RegionRule addRule(@NotNull RuleStatus status, @NotNull RuleTarget target, @NotNull RuleType... ruleTypes) {
            return addRule(0, status, target, ruleTypes);
        }

        void removeRule(@NotNull RegionRule rule);

        void removeRules();

    }

    static interface Activateable extends Region {

        boolean active();

    }

    static interface ActiveModifiable extends Region, Activateable {

        void setActive(boolean active);

    }

    static interface PropertyModifiable extends Region {

        void setPriority(int priority);

    }

    static interface Shaped extends Region {

        Shape3 shape();

        @Override
        default boolean contains(@NotNull Point3 point) {
            point = new Vector3(point.blockX(), point.blockY(), point.blockZ());
            return shape().contains(point);
        }

    }

    static interface World extends Region {

        @Override
        default boolean contains(@NotNull Point3 point) {
            return !(point instanceof Location loc) || Objects.equals(loc.worldId(), worldId());
        }

    }
}
