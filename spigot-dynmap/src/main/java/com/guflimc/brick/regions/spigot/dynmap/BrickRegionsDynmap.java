package com.guflimc.brick.regions.spigot.dynmap;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.dynmap.DynmapAPI;

public class BrickRegionsDynmap extends JavaPlugin implements Listener {

    private DynmapRenderer renderer;

    @Override
    public void onEnable() {
        DynmapAPI dynmap = (DynmapAPI) getServer().getPluginManager().getPlugin("dynmap");
        renderer = new DynmapRenderer(dynmap);

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
