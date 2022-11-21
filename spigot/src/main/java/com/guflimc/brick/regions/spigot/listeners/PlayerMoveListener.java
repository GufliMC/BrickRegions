package com.guflimc.brick.regions.spigot.listeners;

import com.guflimc.brick.maths.spigot.api.SpigotMaths;
import com.guflimc.brick.regions.api.RegionAPI;
import com.guflimc.brick.regions.api.domain.Region;
import com.guflimc.brick.regions.spigot.api.SpigotRegionAPI;
import com.guflimc.brick.regions.spigot.api.events.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerMoveListener implements Listener {

    private final JavaPlugin plugin;
    private final Map<Player, Location> lastLocation = new ConcurrentHashMap<>();

    public PlayerMoveListener(JavaPlugin plugin) {
        this.plugin = plugin;

        for ( Player player : Bukkit.getOnlinePlayers() ) {
            lastLocation.put(player, player.getLocation());
            handleMoveAsync(player, Collections.emptyList(), SpigotRegionAPI.get().regionsAt(player));
        }

        plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin,
                () -> lastLocation.keySet().forEach(this::update),
                1L, 1L);
    }

    private void update(Player player) {
        Location from = lastLocation.get(player);
        Location to = player.getLocation();

        if (from.getBlockX() == to.getBlockX()
                && from.getBlockY() == to.getBlockY()
                && from.getBlockZ() == to.getBlockZ()) {
            return;
        }

        lastLocation.put(player, to);
        handleMove(player, from, to);
    }

    private void handleMove(Player player, Location from, Location to) {
        Collection<Region> fromRegions = RegionAPI.get().regionsAt(SpigotMaths.toBrickLocation(from));
        Collection<Region> toRegions = RegionAPI.get().regionsAt(SpigotMaths.toBrickLocation(to));

        handleMove(player, fromRegions, toRegions);
    }

    private void handleMoveAsync(Player player, Collection<Region> fromRegions, Collection<Region> toRegions) {
        Bukkit.getServer().getScheduler().runTaskAsynchronously(plugin, () ->
                handleMove(player, fromRegions, toRegions));
    }

    private void handleMove(Player player, Collection<Region> fromRegions, Collection<Region> toRegions) {
        Set<Region> common = new HashSet<>(fromRegions);
        common.retainAll(toRegions);

        Set<Region> uniqueFromRegions = new HashSet<>(fromRegions);
        uniqueFromRegions.removeAll(common);

        Set<Region> uniqueToRegions = new HashSet<>(toRegions);
        uniqueToRegions.removeAll(common);

        if (uniqueFromRegions.isEmpty() && uniqueToRegions.isEmpty()) {
            return;
        }

        PlayerRegionsMoveEvent event = new PlayerRegionsMoveEvent(player, fromRegions, toRegions, uniqueFromRegions, uniqueToRegions);
        Bukkit.getPluginManager().callEvent(event);

        // call region leave events
        for (Region rg : uniqueFromRegions) {
            PlayerRegionLeaveEvent ev = new PlayerRegionLeaveEvent(rg, player);
            Bukkit.getPluginManager().callEvent(ev);
        }

        // call regions enter events
        for (Region rg : uniqueToRegions) {
            PlayerRegionEnterEvent ev = new PlayerRegionEnterEvent(rg, player);
            Bukkit.getPluginManager().callEvent(ev);
        }
    }

    @EventHandler
    public void onCreate(RegionCreateEvent event) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            Collection<Region> regions = SpigotRegionAPI.get().regionsAt(player);
            if (!regions.contains(event.region())) {
                continue;
            }

            Collection<Region> from = new HashSet<>(regions);
            from.remove(event.region());

            handleMoveAsync(player, from, regions);
        }
    }

    @EventHandler
    public void onDelete(RegionDeleteEvent event) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            Collection<Region> regions = SpigotRegionAPI.get().regionsAt(player);
            if (!regions.contains(event.region())) {
                continue;
            }

            Collection<Region> to = new HashSet<>(regions);
            to.remove(event.region());

            handleMoveAsync(player, regions, to);
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        lastLocation.put(event.getPlayer(), event.getPlayer().getLocation());
        handleMoveAsync(event.getPlayer(), Collections.emptyList(), SpigotRegionAPI.get().regionsAt(event.getPlayer()));
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        lastLocation.remove(event.getPlayer());
        handleMoveAsync(event.getPlayer(), SpigotRegionAPI.get().regionsAt(event.getPlayer()), Collections.emptyList());
    }

}
