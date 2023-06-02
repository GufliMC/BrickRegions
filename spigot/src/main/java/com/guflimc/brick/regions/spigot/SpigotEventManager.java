package com.guflimc.brick.regions.spigot;

import com.guflimc.brick.regions.api.domain.Region;
import com.guflimc.brick.regions.api.domain.attribute.RegionAttributeKey;
import com.guflimc.brick.regions.api.domain.attribute.RegionRule;
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
    public void onPropertyChange(Region region) {
        Bukkit.getServer().getPluginManager().callEvent(new RegionPropertyChangeEvent(region, !Bukkit.isPrimaryThread()));
    }

    @Override
    public <T> void onAttributeChange(Region.Attributeable region, RegionAttributeKey<T> key, T previousValue, T value) {
        Bukkit.getServer().getPluginManager().callEvent(new RegionAttributeChangeEvent<>(region, !Bukkit.isPrimaryThread(), key, previousValue, value));
    }

    @Override
    public <T> void onAttributeRemove(Region.Attributeable region, RegionAttributeKey<T> key, T previousValue) {
        Bukkit.getServer().getPluginManager().callEvent(new RegionAttributeRemoveEvent<>(region, !Bukkit.isPrimaryThread(), key, previousValue));
    }

    @Override
    public void onRuleAdd(Region.Ruleable region, RegionRule rule) {
        Bukkit.getServer().getPluginManager().callEvent(new RegionRuleAddEvent(region, !Bukkit.isPrimaryThread(), rule));
    }

    @Override
    public void onRuleRemove(Region.Ruleable region, RegionRule rule) {
        Bukkit.getServer().getPluginManager().callEvent(new RegionRuleRemoveEvent(region, !Bukkit.isPrimaryThread(), rule));
    }

    @Override
    public void onActivate(Region.Activateable region) {
        Bukkit.getServer().getPluginManager().callEvent(new RegionActivateEvent(region, !Bukkit.isPrimaryThread()));
    }

    @Override
    public void onDeactivate(Region.Activateable region) {
        Bukkit.getServer().getPluginManager().callEvent(new RegionDeactivateEvent(region, !Bukkit.isPrimaryThread()));
    }
}
