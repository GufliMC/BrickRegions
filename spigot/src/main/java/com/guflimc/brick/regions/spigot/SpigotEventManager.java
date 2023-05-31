package com.guflimc.brick.regions.spigot;

import com.guflimc.brick.regions.api.domain.locality.Locality;
import com.guflimc.brick.regions.api.domain.locality.LocalityAttributeKey;
import com.guflimc.brick.regions.api.domain.locality.LocalityRule;
import com.guflimc.brick.regions.api.domain.locality.ModifiableLocality;
import com.guflimc.brick.regions.api.domain.region.Region;
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
        if (locality instanceof Region rg) {
            Bukkit.getServer().getPluginManager().callEvent(new RegionPropertyChangeEvent(rg, !Bukkit.isPrimaryThread()));
        }
    }

    @Override
    public <T> void onAttributeChange(ModifiableLocality locality, LocalityAttributeKey<T> key, T previousValue, T value) {
        if (locality instanceof Region rg) {
            Bukkit.getServer().getPluginManager().callEvent(new RegionAttributeChangeEvent<>(rg, !Bukkit.isPrimaryThread(), key, previousValue, value));
        }
    }

    @Override
    public <T> void onAttributeRemove(ModifiableLocality locality, LocalityAttributeKey<T> key, T previousValue) {
        if (locality instanceof Region rg) {
            Bukkit.getServer().getPluginManager().callEvent(new RegionAttributeRemoveEvent<>(rg, !Bukkit.isPrimaryThread(), key, previousValue));
        }
    }

    @Override
    public void onRuleAdd(Locality locality, LocalityRule rule) {
        if (locality instanceof Region rg) {
            Bukkit.getServer().getPluginManager().callEvent(new RegionRuleAddEvent(rg, !Bukkit.isPrimaryThread(), rule));
        }
    }

    @Override
    public void onRuleRemove(Locality locality, LocalityRule rule) {
        if (locality instanceof Region rg) {
            Bukkit.getServer().getPluginManager().callEvent(new RegionRuleRemoveEvent(rg, !Bukkit.isPrimaryThread(), rule));
        }
    }

    @Override
    public void onSave(Locality locality) {
        if (locality instanceof Region rg) {
            Bukkit.getServer().getPluginManager().callEvent(new RegionSaveEvent(rg, !Bukkit.isPrimaryThread()));
        }
    }
}
