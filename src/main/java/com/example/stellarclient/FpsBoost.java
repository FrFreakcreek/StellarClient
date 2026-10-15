package com.example.stellarclient;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.minecraft.client.MinecraftClient;

/**
 * One-key performance preset. Lowers render distance, simulation distance and entity
 * distance (never raises them), and puts your original values back when toggled off.
 * Note: this is a settings preset, not an engine rewrite. For deeper gains use Sodium.
 */
public final class FpsBoost {
    private static boolean active = false;
    private static int savedView, savedSim;
    private static double savedEntity;

    public static void register() {
        // Never leave the lowered values saved in options.txt when the game closes.
        ClientLifecycleEvents.CLIENT_STOPPING.register(FpsBoost::restore);
    }

    /** Flips the preset. Returns true if boost is now ON. */
    public static boolean toggle(MinecraftClient client) {
        if (active) restore(client); else apply(client);
        return active;
    }

    private static void apply(MinecraftClient client) {
        StellarConfig cfg = StellarConfig.get();
        savedView = client.options.getViewDistance().getValue();
        savedSim = client.options.getSimulationDistance().getValue();
        savedEntity = client.options.getEntityDistanceScaling().getValue();

        client.options.getViewDistance().setValue(Math.min(savedView, cfg.boostViewDistance));
        client.options.getSimulationDistance().setValue(Math.min(savedSim, cfg.boostSimulationDistance));
        client.options.getEntityDistanceScaling().setValue(Math.min(savedEntity, cfg.boostEntityDistance));
        active = true;
    }

    private static void restore(MinecraftClient client) {
        if (!active) return;
        client.options.getViewDistance().setValue(savedView);
        client.options.getSimulationDistance().setValue(savedSim);
        client.options.getEntityDistanceScaling().setValue(savedEntity);
        active = false;
    }

    private FpsBoost() {}
}
