package com.guflimc.brick.regions.spigot.squaremap;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.jpenilla.squaremap.api.Squaremap;
import xyz.jpenilla.squaremap.api.SquaremapProvider;

public class BrickRegionsSquaremap extends JavaPlugin implements Listener {

    private SquaremapRenderer renderer;

    @Override
    public void onEnable() {
        Squaremap squaremap = SquaremapProvider.get();
        renderer = new SquaremapRenderer(squaremap);

        // register event listener
        getServer().getPluginManager().registerEvents(new EventListener(this, renderer), this);

        // render already loaded worlds
        Bukkit.getWorlds().forEach(renderer::render);

        getLogger().info("Enabled " + nameAndVersion() + ".");
    }

    @Override
    public void onDisable() {
        if ( renderer != null ) {
            Bukkit.getWorlds().forEach(world -> renderer.clear(world));
        }
        getLogger().info("Disabled " + nameAndVersion() + ".");
    }

    private String nameAndVersion() {
        return getDescription().getName() + " v" + getDescription().getVersion();
    }

}
