package com.guflimc.brick.regions.spigot.placeholders;

import com.guflimc.brick.placeholders.spigot.api.SpigotPlaceholderAPI;
import com.guflimc.brick.regions.api.domain.Region;
import com.guflimc.brick.regions.spigot.api.SpigotRegionAPI;
import net.kyori.adventure.text.Component;

import java.util.Comparator;

public class RegionPlaceholders {

    public static void init() {
        SpigotPlaceholderAPI.get().registerReplacer("region_name", (player) ->
                SpigotRegionAPI.get().regionsAt(player).stream()
                        .max(Comparator.comparing(Region::priority))
                        .map(rg -> Component.text(rg.name())).orElse(null)
        );

        SpigotPlaceholderAPI.get().registerReplacer("region_display_name", (player) ->
                SpigotRegionAPI.get().regionsAt(player).stream()
                        .max(Comparator.comparing(Region::priority))
                        .map(Region::displayName).orElse(null)
        );
    }

}
