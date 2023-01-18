package com.guflimc.brick.regions.spigot.listeners;

import com.guflimc.brick.math.spigot.SpigotMath;
import com.guflimc.brick.regions.api.RegionAPI;
import com.guflimc.brick.regions.api.domain.Locality;
import com.guflimc.brick.regions.api.domain.Region;
import com.guflimc.brick.regions.spigot.api.events.PlayerLocalitiesEntityInteractEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class EntityInteractListener implements Listener {


    private record Interact(Player player, Entity entity, boolean cancelled) { }
    private final Set<Interact> cache = new HashSet<>();

    public EntityInteractListener(JavaPlugin plugin) {
        // we will clear the cache every tick, since double fired events will fire both at the same tick, this is perfect.
        plugin.getServer().getScheduler().runTaskTimer(plugin, cache::clear, 0, 1);
    }

    private void entityInteract(Player player, Entity entity, Cancellable e) {
        Collection<Locality> regions = RegionAPI.get().localitiesAt(SpigotMath.toBrickLocation(entity.getLocation()));
        if (regions.isEmpty()) {
            return;
        }

        PlayerLocalitiesEntityInteractEvent event = new PlayerLocalitiesEntityInteractEvent(player, regions, entity);
        Bukkit.getPluginManager().callEvent(event);
        e.setCancelled(event.isCancelled());
    }

    /**
     * Info: this event might fire twice in some situations. Mainly when both the main hand and the off-hand
     * can do some action.
     */
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onInteractEntity(PlayerInteractEntityEvent e) {
        Entity ent = e.getRightClicked();
        if (ent instanceof Player) {
            return;
        }

        /*
         * We don't care which hand is used when the event is fired, and we only want to call our custom region event once.
         * We will therefore cache the result of our custom event after the first fire and reuse this on the second fire.
         */

        Interact cached = cache.stream().filter(c -> c.player == e.getPlayer() && c.entity == ent)
                .findFirst().orElse(null);

        if (cached != null) {
            e.setCancelled(cached.cancelled);
            return;
        }

        entityInteract(e.getPlayer(), ent, new Cancellable() {
            @Override
            public boolean isCancelled() {
                return false;
            }

            @Override
            public void setCancelled(boolean cancel) {
                Interact interact = new Interact(e.getPlayer(), ent, cancel);
                cache.add(interact);
                e.setCancelled(cancel);
            }
        });
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onArmorStand(PlayerArmorStandManipulateEvent e) {
        entityInteract(e.getPlayer(), e.getRightClicked(), e);
    }

}
