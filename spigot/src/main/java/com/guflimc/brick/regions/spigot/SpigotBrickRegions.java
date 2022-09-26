package com.guflimc.brick.regions.spigot;

import cloud.commandframework.annotations.AnnotationParser;
import cloud.commandframework.bukkit.BukkitCommandManager;
import cloud.commandframework.execution.CommandExecutionCoordinator;
import cloud.commandframework.meta.SimpleCommandMeta;
import com.google.gson.Gson;
import com.guflimc.brick.gui.spigot.SpigotBrickGUI;
import com.guflimc.brick.i18n.spigot.api.SpigotI18nAPI;
import com.guflimc.brick.i18n.spigot.api.namespace.SpigotNamespace;
import com.guflimc.brick.regions.api.domain.Region;
import com.guflimc.brick.regions.common.BrickRegionsConfig;
import com.guflimc.brick.regions.common.BrickRegionsDatabaseContext;
import com.guflimc.brick.regions.common.commands.RegionCommands;
import com.guflimc.brick.regions.common.commands.arguments.RegionArgument;
import com.guflimc.brick.regions.spigot.api.SpigotRegionAPI;
import com.guflimc.brick.regions.spigot.commands.SpigotRegionCommands;
import com.guflimc.brick.regions.spigot.commands.SpigotSelectionCommands;
import com.guflimc.brick.regions.spigot.listeners.BuildListener;
import com.guflimc.brick.regions.spigot.listeners.EntityPlaceListener;
import com.guflimc.brick.regions.spigot.listeners.MoveListener;
import com.guflimc.brick.regions.spigot.rules.RuleHandler;
import com.guflimc.brick.regions.spigot.selection.SelectionRenderer;
import com.guflimc.brick.regions.spigot.selection.listeners.SelectionListener;
import io.leangen.geantyref.TypeToken;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.units.qual.N;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Locale;
import java.util.function.Function;

public class SpigotBrickRegions extends JavaPlugin {

    private static final Logger logger = LoggerFactory.getLogger(SpigotBrickRegions.class);

    public final Gson gson = new Gson();
    public BrickRegionsConfig config;

    public final NamespacedKey SELECTION_WAND_KEY = new NamespacedKey(this, "selection_wand");
    public final NamespacedKey SELECTION_TYPE = new NamespacedKey(this, "selection_type");

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

        // ACTIONS
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new MoveListener(this), this);
        pm.registerEvents(new BuildListener(), this);
        pm.registerEvents(new EntityPlaceListener(), this);

        // RULES
        pm.registerEvents(new RuleHandler(), this);

        // SELECTION
        new SelectionRenderer(this);
        pm.registerEvents(new SelectionListener(this), this);

        // GUI
        SpigotBrickGUI.register(this);

        // COMMANDS
        try {
            BukkitCommandManager<CommandSender> commandManager = new BukkitCommandManager<>(
                    this,
                    CommandExecutionCoordinator.simpleCoordinator(),
                    Function.identity(),
                    Function.identity()
            );

            commandManager.parserRegistry().registerParserSupplier(TypeToken.get(Region.class),
                    ps -> new RegionArgument.RegionParser<>());

            AnnotationParser<CommandSender> annotationParser = new AnnotationParser<>(
                    commandManager,
                    CommandSender.class,
                    parameters -> SimpleCommandMeta.empty()
            );

            annotationParser.parse(new RegionCommands());
            annotationParser.parse(new SpigotRegionCommands());
            annotationParser.parse(new SpigotSelectionCommands(this));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

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
