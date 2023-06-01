package com.guflimc.brick.regions.common;

import com.guflimc.brick.regions.api.domain.Region;
import com.guflimc.brick.regions.api.domain.attribute.RegionAttributeKey;
import com.guflimc.brick.regions.api.domain.attribute.RegionRule;

public abstract class EventManager {

    public static EventManager INSTANCE = new EventManager() {
    }; // default to empty implementation

    //

    public void onCreate(Region region) {
    }

    public void onDelete(Region region) {
    }

    public void onRegister(Region region) {
    }

    public void onUnregister(Region region) {
    }

    public void onPropertyChange(Region region) {
    }

    public <T> void onAttributeChange(Region region, RegionAttributeKey<T> key, T previousValue, T value) {
    }

    public <T> void onAttributeRemove(Region region, RegionAttributeKey<T> key, T previousValue) {
    }

    public void onRuleAdd(Region region, RegionRule rule) {
    }

    public void onRuleRemove(Region region, RegionRule rule) {
    }

    public void onSave(Region region) {
    }

}
