package com.guflimc.brick.regions.api.domain;

import com.guflimc.brick.math.common.geometry.pos3.Location;
import com.guflimc.brick.math.common.geometry.pos3.Point3;
import com.guflimc.brick.math.common.geometry.pos3.Vector3;
import com.guflimc.brick.math.common.geometry.shape3d.Shape3;
import com.guflimc.brick.regions.api.domain.attribute.RegionAttributeKey;
import com.guflimc.brick.regions.api.rules.Rule;
import com.guflimc.brick.regions.api.rules.attributes.RuleAction;
import com.guflimc.brick.regions.api.rules.attributes.RuleCondition;
import com.guflimc.brick.regions.api.rules.attributes.RuleStatus;
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

        List<Rule> rules();

    }

    static interface RuleModifiable extends Ruleable {

        Rule addRule(@NotNull RuleStatus status, @NotNull RuleCondition condition, @NotNull RuleAction action);

        Rule addRule(@NotNull RuleStatus status, @NotNull RuleCondition condition, @NotNull RuleAction action, int priority);

        void removeRule(@NotNull Rule rule);

        void removeRules();

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
