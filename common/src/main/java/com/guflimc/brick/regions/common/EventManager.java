package com.guflimc.brick.regions.common;

import com.guflimc.brick.regions.api.domain.Region;
import com.guflimc.brick.regions.api.domain.attribute.RegionAttributeKey;
import com.guflimc.brick.regions.api.rules.Rule;
import org.jetbrains.annotations.NotNull;

public abstract class EventManager {

    public static EventManager INSTANCE = new EventManager() {
    }; // default to empty implementation

    //

    public void onCreate(@NotNull Region region) {
    }

    public void onDelete(@NotNull Region region) {
    }

    public void onRegister(@NotNull Region region) {
    }

    public void onUnregister(@NotNull Region region) {
    }

    public void onPropertyChange(@NotNull Region region) {
    }

    public <T> void onAttributeChange(@NotNull Region.Attributeable region, @NotNull RegionAttributeKey<T> key, T previousValue, T value) {
    }

    public <T> void onAttributeRemove(@NotNull Region.Attributeable region, @NotNull RegionAttributeKey<T> key, T previousValue) {
    }

    public void onRuleAdd(@NotNull Region.Ruleable region, @NotNull Rule rule) {
    }

    public void onRuleRemove(@NotNull Region.Ruleable region, @NotNull Rule rule) {
    }

}
