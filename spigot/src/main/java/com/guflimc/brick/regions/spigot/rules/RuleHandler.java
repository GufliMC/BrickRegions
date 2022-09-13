package com.guflimc.brick.regions.spigot.rules;

import com.guflimc.brick.regions.api.rules.RuleType;
import com.guflimc.brick.regions.spigot.api.SpigotRegionAPI;
import com.guflimc.brick.regions.spigot.api.events.PlayerRegionsBuildEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class RuleHandler implements Listener {

    @EventHandler
    public void onBuild(PlayerRegionsBuildEvent event) {
        if (!SpigotRegionAPI.get().isAllowed(event.player(), RuleType.BUILD, event.regions())) {
            event.setCancelled(true);
        }
    }



}
