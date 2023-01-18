package com.guflimc.brick.regions.spigot.listeners;

import com.guflimc.brick.regions.api.domain.Region;
import com.guflimc.brick.regions.spigot.SpigotBrickRegions;
import com.guflimc.brick.regions.spigot.api.events.PlayerRegionsMoveEvent;
import com.guflimc.brick.regions.spigot.api.events.PlayerRegionsTitlesEvent;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.title.TitlePart;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Comparator;

public class PlayerMoveTitlesListener implements Listener {

    private final static Title.Times DEFAULT_TIMES = Title.Times.times(
            Duration.of(250, ChronoUnit.MILLIS),
            Duration.of(1500, ChronoUnit.MILLIS),
            Duration.of(250, ChronoUnit.MILLIS)
    );

    private final SpigotBrickRegions plugin;

    public PlayerMoveTitlesListener(SpigotBrickRegions plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onMove(PlayerRegionsMoveEvent event) {
        handle(event.player(),
                event.from().stream().filter(Region.class::isInstance).map(Region.class::cast).toList(),
                event.to().stream().filter(Region.class::isInstance).map(Region.class::cast).toList());
    }

//    @EventHandler
//    public void onJoin(PlayerJoinEvent event) {
//        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
//            Collection<Region> to = SpigotRegionAPI.get().regionsAt(event.getPlayer());
//            handle(event.getPlayer(), List.of(), to);
//        });
//    }

    //

    private void handle(Player player, Collection<Region> from, Collection<Region> to) {

        Component titleFrom = from.stream()
                .max(Comparator.comparingInt(Region::priority))
                .map(Region::displayName).orElse(null);

        Component titleTo = to.stream()
                .max(Comparator.comparingInt(Region::priority))
                .map(Region::displayName).orElse(null);

        if (titleTo != null && titleTo.equals(titleFrom)) {
            titleTo = null;
        }

        PlayerRegionsTitlesEvent e = new PlayerRegionsTitlesEvent(player, from, to, titleTo, null);
        Bukkit.getServer().getPluginManager().callEvent(e);

        if (e.title() == null && e.subtitle() == null) {
            return;
        }

        Audience audience = plugin.adventure.player(player);
        if (e.title() != null) {
            audience.sendTitlePart(TitlePart.TITLE, e.title());
        } else {
            audience.sendTitlePart(TitlePart.TITLE, Component.empty());
        }
        if (e.subtitle() != null) {
            audience.sendTitlePart(TitlePart.SUBTITLE, e.subtitle());
        } else {
            audience.sendTitlePart(TitlePart.SUBTITLE, Component.empty());
        }
        audience.sendTitlePart(TitlePart.TIMES, DEFAULT_TIMES);
    }

}
