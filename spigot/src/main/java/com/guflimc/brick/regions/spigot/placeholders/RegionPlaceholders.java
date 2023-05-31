package com.guflimc.brick.regions.spigot.placeholders;

import com.guflimc.brick.placeholders.api.module.BasePlaceholderModule;
import com.guflimc.brick.placeholders.api.resolver.PlaceholderResolver;
import com.guflimc.brick.placeholders.spigot.api.SpigotPlaceholderAPI;
import com.guflimc.brick.regions.api.domain.region.Region;
import com.guflimc.brick.regions.spigot.api.SpigotRegionAPI;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.util.Comparator;
import java.util.function.Function;

public class RegionPlaceholders {

    private static <T> PlaceholderResolver<Player, T> region(Function<Region, T> map) {
        return PlaceholderResolver.requireEntity((placeholder, context) ->
                SpigotRegionAPI.get().regionsAt(context.entity()).stream()
                        .max(Comparator.comparing(Region::priority))
                        .map(map).orElse(null));
    }

    public static void init() {
        BasePlaceholderModule<Player> module = new BasePlaceholderModule<>("region");
        module.register("name", region(region -> Component.text(region.key().name())));
        SpigotPlaceholderAPI.get().register(module);
    }

}
