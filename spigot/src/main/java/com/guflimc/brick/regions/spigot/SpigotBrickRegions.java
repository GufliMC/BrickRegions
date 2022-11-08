package com.guflimc.brick.regions.spigot;

import cloud.commandframework.annotations.AnnotationParser;
import cloud.commandframework.bukkit.BukkitCommandManager;
import cloud.commandframework.bukkit.parsers.WorldArgument;
import cloud.commandframework.context.CommandContext;
import cloud.commandframework.execution.CommandExecutionCoordinator;
import cloud.commandframework.meta.SimpleCommandMeta;
import com.google.gson.Gson;
import com.guflimc.brick.RuleTypes.common.commands.arguments.RuleTypeArgument;
import com.guflimc.brick.gui.spigot.SpigotBrickGUI;
import com.guflimc.brick.i18n.spigot.api.SpigotI18nAPI;
import com.guflimc.brick.i18n.spigot.api.namespace.SpigotNamespace;
import com.guflimc.brick.regions.api.domain.PersistentRegion;
import com.guflimc.brick.regions.api.rules.RuleStatus;
import com.guflimc.brick.regions.api.rules.RuleTarget;
import com.guflimc.brick.regions.api.rules.RuleType;
import com.guflimc.brick.regions.common.BrickRegionsConfig;
import com.guflimc.brick.regions.common.BrickRegionsDatabaseContext;
import com.guflimc.brick.regions.common.commands.RegionCommands;
import com.guflimc.brick.regions.common.commands.arguments.PersistentRegionArgument;
import com.guflimc.brick.regions.common.commands.arguments.RuleStatusArgument;
import com.guflimc.brick.regions.common.commands.arguments.RuleTargetArgument;
import com.guflimc.brick.regions.spigot.api.SpigotRegionAPI;
import com.guflimc.brick.regions.spigot.api.rules.SpigotRuleTarget;
import com.guflimc.brick.regions.spigot.commands.SpigotRegionCommands;
import com.guflimc.brick.regions.spigot.commands.SpigotSelectionCommands;
import com.guflimc.brick.regions.spigot.listeners.*;
import com.guflimc.brick.regions.spigot.rules.RuleHandler;
import com.guflimc.brick.regions.spigot.selection.SelectionRenderer;
import com.guflimc.brick.regions.spigot.selection.listeners.SelectionListener;
import io.leangen.geantyref.TypeToken;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
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
    public void onLoad() {
        SpigotRuleTarget.load();
    }

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
        pm.registerEvents(new BlockBuildListener(), this);
        pm.registerEvents(new EntityBuildListener(), this);
        pm.registerEvents(new CollectItemsListener(), this);
        pm.registerEvents(new EntityBuildListener(), this);
        pm.registerEvents(new EntityDamageListener(), this);
        pm.registerEvents(new EntityInteractListener(this), this);
        pm.registerEvents(new ContainerListener(), this);
        pm.registerEvents(new BlockInteractListener(), this);
        pm.registerEvents(new WorldListener(manager), this);

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

            commandManager.parserRegistry().registerParserSupplier(TypeToken.get(PersistentRegion.class),
                    ps -> new PersistentRegionArgument.PersistentRegionParser<>());

            commandManager.parserRegistry().registerParserSupplier(TypeToken.get(RuleType.class),
                    ps -> new RuleTypeArgument.RuleTypeParser<>());

            commandManager.parserRegistry().registerParserSupplier(TypeToken.get(RuleTarget.class),
                    ps -> new RuleTargetArgument.RuleTargetParser<>());

            commandManager.parserRegistry().registerParserSupplier(TypeToken.get(RuleStatus.class),
                    ps -> new RuleStatusArgument.RuleStatusParser<>());

//            commandManager.parserRegistry().registerParserSupplier(TypeToken.get(World.class),
//                    ps -> new WorldArgument.WorldParser<>());

            commandManager.registerCommandPreProcessor(pctx -> {
                CommandContext<CommandSender> ctx = pctx.getCommandContext();
                if ( ctx.getSender() instanceof Player p ) {
                    ctx.set("worldId", p.getWorld().getUID());
                }
            });

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
