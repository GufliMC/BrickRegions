package com.guflimc.brick.regions.spigot.listeners;

import com.guflimc.brick.regions.api.domain.locality.LocalityAttributeKey;
import com.guflimc.brick.regions.api.domain.region.Region;
import com.guflimc.brick.regions.spigot.SpigotBrickRegions;
import com.guflimc.brick.regions.spigot.api.events.PlayerRegionsMoveDisplayEvent;
import com.guflimc.brick.regions.spigot.api.events.PlayerRegionsMoveEvent;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.title.TitlePart;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
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

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onMove(PlayerRegionsMoveEvent event) {
        handle(event.player(),
                event.uniqueFrom().stream().map(Region.class::cast).toList(),
                event.uniqueFrom().stream().map(Region.class::cast).toList());
    }

//    @EventHandler
//    public void onJoin(PlayerJoinEvent event) {
//        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
//            Collection<Region> to = SpigotRegionAPI.get().regionsAt(event.getPlayer());
//            handle(event.getPlayer(), List.of(), to);
//        });
//    }

    //

    private <T> T get(Collection<Region> ca, LocalityAttributeKey<T> la, Collection<Region> cb, LocalityAttributeKey<T> lb) {
        Region ra = ca.stream()
                .filter(rg -> rg.attribute(la).isPresent())
                .max(Comparator.comparingInt(Region::priority))
                .orElse(null);

        T result = null;
        int priority = Integer.MIN_VALUE;

        if (ra != null) {
            result = ra.attribute(la).orElse(null);
            priority = ra.priority();
        }

        Region rb = cb.stream()
                .filter(rg -> rg.attribute(lb).isPresent())
                .max(Comparator.comparingInt(Region::priority))
                .orElse(null);

        if (rb != null && rb.priority() > priority) {
            result = rb.attribute(lb).orElse(null);
        }

        return result;
    }

    private void handle(Player player, Collection<Region> from, Collection<Region> to) {
        if (from.isEmpty() && to.isEmpty()) {
            return;
        }

        Component title = get(to, LocalityAttributeKey.ENTRANCE_TITLE, from, LocalityAttributeKey.EXIT_TITLE);
        Component subtitle = get(to, LocalityAttributeKey.ENTRANCE_SUBTITLE, from, LocalityAttributeKey.EXIT_SUBTITLE);
        Component actionbar = get(to, LocalityAttributeKey.ENTRANCE_ACTIONBAR, from, LocalityAttributeKey.EXIT_ACTIONBAR);

        PlayerRegionsMoveDisplayEvent e = new PlayerRegionsMoveDisplayEvent(player, from, to, title, subtitle, actionbar);
        Bukkit.getServer().getPluginManager().callEvent(e);

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

        if (e.actionbar() != null) {
            audience.sendActionBar(e.actionbar());
        }

        audience.sendTitlePart(TitlePart.TIMES, DEFAULT_TIMES);
    }

}
