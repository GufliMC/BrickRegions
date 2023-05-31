package com.guflimc.brick.regions.common;

import com.guflimc.brick.regions.api.domain.locality.Locality;
import com.guflimc.brick.regions.api.domain.locality.LocalityAttributeKey;
import com.guflimc.brick.regions.api.domain.locality.LocalityRule;
import com.guflimc.brick.regions.api.domain.locality.ModifiableLocality;
import com.guflimc.brick.regions.api.domain.region.Region;

public abstract class EventManager {

    public static EventManager INSTANCE = new EventManager() {}; // default to empty implementation

    //

    public void onCreate(Region region) {}

    public void onDelete(Region region) {}

    public void onRegister(Region region) {}

    public void onUnregister(Region region) {}

    public void onPropertyChange(ModifiableLocality locality) {}

    public <T> void onAttributeChange(ModifiableLocality locality, LocalityAttributeKey<T> key, T previousValue, T value) {}

    public <T> void onAttributeRemove(ModifiableLocality locality, LocalityAttributeKey<T> key, T previousValue) {}

    public void onRuleAdd(Locality locality, LocalityRule rule) {}

    public void onRuleRemove(Locality locality, LocalityRule rule) {}

    public void onSave(Locality locality) {}

}
