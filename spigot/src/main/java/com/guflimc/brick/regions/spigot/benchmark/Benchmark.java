package com.guflimc.brick.regions.spigot.benchmark;

import com.guflimc.brick.maths.api.geo.pos.Point;
import com.guflimc.brick.maths.spigot.api.SpigotMaths;
import com.guflimc.brick.regions.spigot.api.events.PlayerRegionsBlockBreakEvent;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Benchmark implements Listener {

    private final Map<Block, Long> timestamps = new HashMap<>();
    private final List<Long> samples = new ArrayList<>();
    private final JavaPlugin plugin;

    private boolean running = false;

    public Benchmark(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    //

    public void start() {
        if ( running ) return;
        running = true;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public void stop() {
        if ( !running ) return;
        running = false;

        HandlerList.unregisterAll(this);

        if ( samples.size() < 20 ) {
            plugin.getLogger().info("Benchmark: not enough samples, please break a lot of blocks while benchmarking.");
            return;
        }

        long average = samples.stream().mapToLong(Long::longValue).sum() / samples.size();
        double averageMs = average / 1000000.0;

        plugin.getLogger().info("--- Benchmarks ---");
        plugin.getLogger().info(String.format("Average: %.3f ms", averageMs));
        plugin.getLogger().info(String.format("Samples: %d", samples.size()));

        // cleanup
        timestamps.clear();
        samples.clear();
    }

    public boolean isRunning() {
        return running;
    }

    //

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        timestamps.put(event.getBlock(), System.nanoTime());
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onBreak(PlayerRegionsBlockBreakEvent event) {
        if ( timestamps.containsKey(event.block()) ) {
            samples.add(System.nanoTime() - timestamps.remove(event.block()));
        }
    }

    //


}
