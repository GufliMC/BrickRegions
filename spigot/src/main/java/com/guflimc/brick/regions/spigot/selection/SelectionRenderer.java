package com.guflimc.brick.regions.spigot.selection;

import com.guflimc.brick.math.common.geometry.pos2.Vector2;
import com.guflimc.brick.math.common.geometry.shape2d.Shape2;
import com.guflimc.brick.regions.api.selection.Selection;
import com.guflimc.brick.regions.spigot.SpigotBrickRegions;
import com.guflimc.brick.regions.spigot.api.SpigotRegionAPI;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;

import java.util.Optional;

public class SelectionRenderer {

    private final SpigotBrickRegions plugin;

    public SelectionRenderer(SpigotBrickRegions plugin) {
        this.plugin = plugin;
        plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            plugin.getServer().getOnlinePlayers().forEach(this::render);
        }, 10L, 10L);
    }

    private void render(Player player) {
        Optional<Selection> sel = SpigotRegionAPI.get().selection(player);
        if (sel.isEmpty()) {
            return;
        }

        ItemStack item = player.getInventory().getItemInMainHand();
        if (item.getType() == Material.AIR) {
            return;
        }

        if (!item.getItemMeta().getPersistentDataContainer().has(plugin.SELECTION_WAND_KEY, PersistentDataType.BYTE)) {
            return;
        }

        renderSelection(player, sel.get());
    }

    private void renderSelection(Player player, Selection sel) {
        if (!sel.isValid()) {
            return;
        }

        double minY = sel.minY();
        double maxY = sel.maxY();

        if (minY < player.getLocation().getY() - 15) {
            minY = player.getLocation().getY() - 15;
        }
        if (maxY > player.getLocation().getY() + 15) {
            maxY = player.getLocation().getY() + 15;
        }

        Shape2 contour = sel.shape().contour();

        // borders
        Vector2 from = contour.vertices().get(0);
        for (int i = 1; i < contour.vertices().size(); i++) {
            Vector2 to = contour.vertices().get(i);
            renderWall(player, minY, maxY, from, to);
            from = to;
        }

        // from last point back to beginning
        renderWall(player, minY, maxY, from, contour.vertices().get(0));

        // floor & ceiling
    }

    private void renderWall(Player player, double minY, double maxY, Vector2 from, Vector2 to) {
        Vector2 vector = to.subtract(from);
        double length = vector.length();
        double hStep, vStep;
        int hSteps;

        // horizontal lines
        hStep = .5;
        hSteps = (int) Math.floor(length / hStep);

        double height = maxY - minY;
        vStep = height / Math.floor(height / 2d);

        Vector2 vec = vector.scale(1 / length).scale(hStep);
        Vector2 point = from;
        for (int j = 0; j < hSteps; j++) {
            point = point.add(vec);

            for (double y = minY; y <= maxY; y += vStep) {
                sendParticle(player, new Location(player.getWorld(), point.x() + .5, y + .5, point.y() + .5));
            }
        }

        // vertical lines
        hStep = length / Math.floor(length / 2);
        hSteps = (int) Math.floor(length / hStep);

        vStep = .5;

        vec = vector.scale(1 / length).scale(hStep);
        point = from;
        for (int j = 0; j < hSteps; j++) {
            point = point.add(vec);

            for (double y = minY; y <= maxY; y += vStep) {
                sendParticle(player, new Location(player.getWorld(), point.x() + .5, y + .5, point.y() + .5));
            }
        }
    }

    private void sendParticle(Player player, Location location) {
        Vector diff = location.clone().subtract(player.getLocation()).toVector();
        double angle = player.getEyeLocation().getDirection().angle(diff);
        if (angle < 1.2) {
            player.spawnParticle(Particle.FLAME,
                    location, 1, 0, 0, 0, 0);
        }
    }
}