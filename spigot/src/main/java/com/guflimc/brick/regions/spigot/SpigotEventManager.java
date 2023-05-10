package com.guflimc.brick.regions.spigot;

import com.guflimc.brick.regions.api.domain.Locality;
import com.guflimc.brick.regions.api.domain.LocalityAttributeKey;
import com.guflimc.brick.regions.api.domain.LocalityProtectionRule;
import com.guflimc.brick.regions.api.domain.Region;
import com.guflimc.brick.regions.api.domain.modifiable.ModifiableLocality;
import com.guflimc.brick.regions.api.domain.tile.TileRegion;
import com.guflimc.brick.regions.common.EventManager;
import com.guflimc.brick.regions.spigot.api.events.*;
import org.bukkit.Bukkit;

public class SpigotEventManager extends EventManager {

    @Override
    public void onCreate(Region region) {
        Bukkit.getServer().getPluginManager().callEvent(new RegionCreateEvent(region, !Bukkit.isPrimaryThread()));
    }

    @Override
    public void onDelete(Region region) {
        Bukkit.getServer().getPluginManager().callEvent(new RegionDeleteEvent(region, !Bukkit.isPrimaryThread()));
    }

    @Override
    public void onRegister(Region region) {
        Bukkit.getServer().getPluginManager().callEvent(new RegionRegisterEvent(region, !Bukkit.isPrimaryThread()));
    }

    @Override
    public void onUnregister(Region region) {
        Bukkit.getServer().getPluginManager().callEvent(new RegionUnregisterEvent(region, !Bukkit.isPrimaryThread()));
    }

    @Override
    public void onPropertyChange(ModifiableLocality locality) {
        Bukkit.getServer().getPluginManager().callEvent(new LocalityPropertyChangeEvent(locality, !Bukkit.isPrimaryThread()));
    }

    @Override
    public <T> void onAttributeChange(ModifiableLocality locality, LocalityAttributeKey<T> key, T previousValue, T value) {
        Bukkit.getServer().getPluginManager().callEvent(new LocalityAttributeChangeEvent<>(locality, !Bukkit.isPrimaryThread(), key, previousValue, value));
    }

    @Override
    public <T> void onAttributeRemove(ModifiableLocality locality, LocalityAttributeKey<T> key, T previousValue) {
        Bukkit.getServer().getPluginManager().callEvent(new LocalityAttributeRemoveEvent<>(locality, !Bukkit.isPrimaryThread(), key, previousValue));
    }

    @Override
    public void onRuleAdd(LocalityProtectionRule rule) {
        Bukkit.getServer().getPluginManager().callEvent(new LocalityRuleAddEvent(rule.locality(), !Bukkit.isPrimaryThread(), rule));
    }

    @Override
    public void onRuleRemove(LocalityProtectionRule rule) {
        Bukkit.getServer().getPluginManager().callEvent(new LocalityRuleRemoveEvent(rule.locality(), !Bukkit.isPrimaryThread(), rule));
    }

    @Override
    public void onSave(Locality locality) {
        Bukkit.getServer().getPluginManager().callEvent(new LocalitySaveEvent(locality, !Bukkit.isPrimaryThread()));
    }
}
