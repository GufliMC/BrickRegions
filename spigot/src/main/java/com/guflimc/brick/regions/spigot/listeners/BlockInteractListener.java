package com.guflimc.brick.regions.spigot.listeners;

import com.guflimc.brick.math.spigot.SpigotMath;
import com.guflimc.brick.regions.api.RegionAPI;
import com.guflimc.brick.regions.api.domain.Region;
import com.guflimc.brick.regions.spigot.api.events.PlayerRegionsBlockInteractEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

public class BlockInteractListener implements Listener {

    private static final List<Predicate<String>> blocks = Arrays.asList(
            (s) -> s.contains("DOOR"),
            (s) -> s.contains("GATE"),
            (s) -> s.endsWith("BED"),
            (s) -> s.contains("BUTTON"),
            (s) -> s.contains("CAULDRON"),
            (s) -> Arrays.asList(
                    "BELL",
                    "COMPARATOR",
                    "LECTERN",
                    "NOTE_BLOCK",
                    "REPEATER",
                    "LEVER",
                    "LOOM",
                    "LODESTONE",
                    "CAMERA",
                    "CARTOGRAPHY_TABLE",
                    "COMPOSTER",
                    "SMOKER",
                    "SMITHING_TABLE",
                    "STONECUTTER",
                    "TNT",
                    "GRINDSTONE",
                    "PUMPKIN",
                    "COMMAND_BLOCK",
                    "CRAFTING_TABLE",
                    "ANVIL",
                    "JUKEBOX", // not a container
                    "SWEET_BERRY_BUSH"
            ).contains(s)
    );

    private void blockInteract(Player player, Block block, Cancellable e) {
        Collection<Region> regions = RegionAPI.get().regionsAt(SpigotMath.toBrickLocation(block.getLocation()));
        if (regions.isEmpty()) {
            return;
        }

        PlayerRegionsBlockInteractEvent event = new PlayerRegionsBlockInteractEvent(player, regions, block);
        Bukkit.getPluginManager().callEvent(event);
        e.setCancelled(event.isCancelled());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onInteractBlock(PlayerInteractEvent e) {
        Material type = e.getClickedBlock().getType();
        String typename = type.name();

        // pressure plate, farmland, tripwire
        if (e.getAction() == Action.PHYSICAL) {
            blockInteract(e.getPlayer(), e.getClickedBlock(), e);
            return;
        }

        // clicks
        if (e.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        if (blocks.stream().noneMatch((pre) -> pre.test(typename))) {
            return;
        }

        blockInteract(e.getPlayer(), e.getClickedBlock(), e);
    }

}
