package com.guflimc.brick.regions.spigot;

import com.google.gson.Gson;
import com.guflimc.brick.i18n.spigot.api.SpigotI18nAPI;
import com.guflimc.brick.i18n.spigot.api.namespace.SpigotNamespace;
import com.guflimc.brick.regions.api.RegionAPI;
import com.guflimc.brick.regions.common.BrickRegionsConfig;
import com.guflimc.brick.regions.common.BrickRegionsDatabaseContext;
import com.guflimc.brick.regions.spigot.api.SpigotRegionAPI;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Locale;

public class SpigotBrickRegions extends JavaPlugin {

    private static final Logger logger = LoggerFactory.getLogger(SpigotBrickRegions.class);

    public final Gson gson = new Gson();

    public BrickRegionsConfig config;

    //

    @Override
    public void onEnable() {
        try (
                InputStream is = getResource("config.json");
                InputStreamReader isr = new InputStreamReader(is)
        ) {
            config = gson.fromJson(isr, BrickRegionsConfig.class);
        } catch (IOException e) {
            logger.error("Cannot load configuration.", e);
            return;
        }

        // DATABASE
        BrickRegionsDatabaseContext databaseContext = new BrickRegionsDatabaseContext(config.database);

        // REGION MANAGER
        SpigotBrickRegionManager manager = new SpigotBrickRegionManager(databaseContext);
        SpigotRegionAPI.setRegionManager(manager);

        // TRANSLATIONS
        SpigotNamespace namespace = new SpigotNamespace(this, Locale.ENGLISH);
        namespace.loadValues(this, "languages");
        SpigotI18nAPI.get().register(namespace);

        // LISTENERS
        // TODO

        // COMMANDS


        getLogger().info("Enabled " + nameAndVersion() + ".");
    }

    @Override
    public void onDisable() {
        getLogger().info("Disabled " + nameAndVersion() + ".");
    }

    private String nameAndVersion() {
        return getDescription().getName() + " v" + getDescription().getVersion();
    }

}
