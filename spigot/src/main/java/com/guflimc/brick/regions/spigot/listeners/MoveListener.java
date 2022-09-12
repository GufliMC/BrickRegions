package com.guflimc.brick.regions.spigot.listeners;

import com.guflimc.brick.maths.spigot.api.SpigotMaths;
import com.guflimc.brick.regions.api.RegionAPI;
import com.guflimc.brick.regions.api.domain.Region;
import com.guflimc.brick.regions.spigot.events.PlayerRegionEnterEvent;
import com.guflimc.brick.regions.spigot.events.PlayerRegionLeaveEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class MoveListener implements Listener {

    private final Map<Player, Location> lastLocation = new ConcurrentHashMap<>();

    public MoveListener(JavaPlugin plugin) {
        plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin,
                () -> lastLocation.keySet().forEach(this::update),
                1L, 1L);
    }

    private void update(Player player) {
        Location from = lastLocation.get(player);
        Location to = player.getLocation();

        if ( from.getBlockX() == to.getBlockX()
                && from.getBlockY() == to.getBlockY()
                && from.getBlockZ() == to.getBlockZ() ) {
            return;
        }

        lastLocation.put(player, to);
        handleMove(player, from, to);
    }

    private void handleMove(Player player, Location from, Location to) {
        Collection<Region> fromRegions = RegionAPI.get().regionsAt(SpigotMaths.toPosition(from));
        Collection<Region> toRegions = RegionAPI.get().regionsAt(SpigotMaths.toPosition(to));

        Set<Region> common = new HashSet<>(fromRegions);
        common.retainAll(toRegions);

        Set<Region> uniqueFromRegions = new HashSet<>(fromRegions);
        uniqueFromRegions.removeAll(common);

        Set<Region> uniqueToRegions = new HashSet<>(toRegions);
        uniqueToRegions.removeAll(common);

        if ( uniqueFromRegions.isEmpty() && uniqueToRegions.isEmpty() ) {
            return;
        }

        // call region leave events
        for ( Region rg : uniqueFromRegions ) {
            PlayerRegionLeaveEvent event = new PlayerRegionLeaveEvent(player, rg);
            Bukkit.getPluginManager().callEvent(event);
        }

        // call regions enter events
        for (Region rg : uniqueToRegions ) {
            PlayerRegionEnterEvent event = new PlayerRegionEnterEvent(player, rg);
            Bukkit.getPluginManager().callEvent(event);
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        lastLocation.put(event.getPlayer(), event.getPlayer().getLocation());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        lastLocation.remove(event.getPlayer());
    }

}
