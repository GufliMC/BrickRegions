package com.guflimc.brick.regions.spigot;

import com.google.gson.Gson;
import com.guflimc.brick.gui.spigot.SpigotBrickGUI;
import com.guflimc.brick.i18n.spigot.api.SpigotI18nAPI;
import com.guflimc.brick.i18n.spigot.api.namespace.SpigotNamespace;
import com.guflimc.brick.regions.api.RegionAPI;
import com.guflimc.brick.regions.api.domain.Tile;
import com.guflimc.brick.regions.api.selection.Selection;
import com.guflimc.brick.regions.common.BrickRegionsConfig;
import com.guflimc.brick.regions.common.BrickRegionsDatabaseContext;
import com.guflimc.brick.regions.common.commands.RegionArguments;
import com.guflimc.brick.regions.spigot.commands.RegionAttributeCommands;
import com.guflimc.brick.regions.common.commands.RegionCommands;
import com.guflimc.brick.regions.spigot.api.SpigotRegionAPI;
import com.guflimc.brick.regions.spigot.benchmark.Benchmark;
import com.guflimc.brick.regions.spigot.commands.SpigotRegionCommands;
import com.guflimc.brick.regions.spigot.commands.SpigotSelectionCommands;
import com.guflimc.brick.regions.spigot.listeners.*;
import com.guflimc.brick.regions.spigot.placeholders.RegionPlaceholders;
import com.guflimc.brick.regions.spigot.rules.RuleHandler;
import com.guflimc.brick.regions.spigot.selection.SelectionRenderer;
import com.guflimc.brick.regions.spigot.selection.listeners.SelectionListener;
import com.guflimc.colonel.minecraft.paper.PaperColonel;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Locale;
import java.util.UUID;

public class SpigotBrickRegions extends JavaPlugin {

    private static final Logger logger = LoggerFactory.getLogger(SpigotBrickRegions.class);

    public final Gson gson = new Gson();

    public BrickRegionsConfig config;
    public BukkitAudiences adventure;

    public final NamespacedKey SELECTION_WAND_KEY = new NamespacedKey(this, "selection_wand");
    public final NamespacedKey SELECTION_TYPE = new NamespacedKey(this, "selection_type");

    public final Benchmark benchmark = new Benchmark(this);

    private BrickRegionsDatabaseContext databaseContext;

    //

    @Override
    public void onEnable() {
        saveResource("config.json", false);
        try (
                InputStream is = new FileInputStream(new File(getDataFolder(), "config.json"));
                InputStreamReader isr = new InputStreamReader(is)
        ) {
            config = gson.fromJson(isr, BrickRegionsConfig.class);
        } catch (IOException e) {
            logger.error("Cannot load configuration.", e);
            return;
        }

        // DATABASE
        databaseContext = new BrickRegionsDatabaseContext(config.database);

        // REGION MANAGER
        SpigotBrickRegionManager manager = new SpigotBrickRegionManager(databaseContext);
        SpigotRegionAPI.setRegionManager(manager);

        // TRANSLATIONS
        SpigotNamespace namespace = new SpigotNamespace(this, Locale.ENGLISH);
        namespace.loadValues(this, "languages");
        SpigotI18nAPI.get().register(namespace);

        // ADVENTURE
        adventure = BukkitAudiences.create(this);

        // ACTIONS
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new PlayerMoveListener(this), this);
        pm.registerEvents(new BlockBuildListener(), this);
        pm.registerEvents(new EntityBuildListener(), this);
        pm.registerEvents(new CollectItemsListener(), this);
        pm.registerEvents(new EntityBuildListener(), this);
        pm.registerEvents(new EntityDamageListener(), this);
        pm.registerEvents(new EntityInteractListener(this), this);
        pm.registerEvents(new ContainerListener(), this);
        pm.registerEvents(new BlockInteractListener(), this);
        pm.registerEvents(new DropItemsListener(), this);
        pm.registerEvents(new PlayerMoveTitlesListener(this), this);

        // WORLD LOADING
        pm.registerEvents(new WorldListener(manager), this);

        // RULES
        pm.registerEvents(new RuleHandler(), this);

        // SELECTION
        new SelectionRenderer(this);
        pm.registerEvents(new SelectionListener(this), this);

        // GUI
        SpigotBrickGUI.register(this);

        // PLACEHOLDERS
        if (pm.isPluginEnabled("BrickPlaceholders")) {
            RegionPlaceholders.init();
        }

        // COMMANDS
        PaperColonel colonel = new PaperColonel(this);

        // SOURCE MAPPER
        colonel.registry().registerSourceMapper(UUID.class, "worldId", s -> ((Player) s).getWorld().getUID());
        colonel.registry().registerSourceMapper(Tile.class, s -> SpigotRegionAPI.get().tileAt((Player) s).orElseThrow());
        colonel.registry().registerSourceMapper(Selection.class, s -> SpigotRegionAPI.get().selection((Player) s).orElseThrow());

        // CUSTOM ARGUMENTS
        colonel.registerAll(new RegionArguments());

        // ACTUAL COMMANDS
        colonel.registerAll(new RegionCommands());
        colonel.registerAll(new SpigotRegionCommands(this));
        colonel.registerAll(new SpigotSelectionCommands(this));

        RegionAttributeCommands.register(colonel);

        getLogger().info("Enabled " + nameAndVersion() + ".");
    }

    @Override
    public void onDisable() {
        if (databaseContext != null) {
            databaseContext.shutdown();
        }

        getLogger().info("Disabled " + nameAndVersion() + ".");
    }

    private String nameAndVersion() {
        return getDescription().getName() + " v" + getDescription().getVersion();
    }

}
