package com.guflimc.brick.regions.common;

import com.guflimc.brick.regions.api.domain.LocalityAttributeKey;
import com.guflimc.brick.regions.api.domain.LocalityProtectionRule;
import com.guflimc.brick.regions.api.domain.Region;
import com.guflimc.brick.regions.api.domain.modifiable.ModifiableLocality;

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

    public void onRuleAdd(LocalityProtectionRule rule) {}

    public void onRuleRemove(LocalityProtectionRule rule) {}

}
