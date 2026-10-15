package com.example.stellarclient;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;

import java.util.ArrayDeque;

/**
 * Clicks-per-second counter. Polls the attack/use keys once per tick and counts
 * released -> pressed transitions (so it reads up to ~20 CPS; plenty for a HUD).
 */
public final class CpsTracker {
    private static final ArrayDeque<Long> LEFT = new ArrayDeque<>();
    private static final ArrayDeque<Long> RIGHT = new ArrayDeque<>();
    private static boolean leftWas, rightWas;

    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(CpsTracker::tick);
    }

    private static void tick(MinecraftClient client) {
        if (client.player == null || client.currentScreen != null) {
            leftWas = rightWas = false;
            return;
        }
        long now = System.currentTimeMillis();
        boolean left = client.options.attackKey.isPressed();
        boolean right = client.options.useKey.isPressed();
        if (left && !leftWas) LEFT.addLast(now);
        if (right && !rightWas) RIGHT.addLast(now);
        leftWas = left;
        rightWas = right;
    }

    public static int left() { return count(LEFT); }
    public static int right() { return count(RIGHT); }

    /** Drops clicks older than 1 second and returns how many remain. */
    private static int count(ArrayDeque<Long> clicks) {
        long cutoff = System.currentTimeMillis() - 1000;
        while (!clicks.isEmpty() && clicks.peekFirst() < cutoff) clicks.removeFirst();
        return clicks.size();
    }

    private CpsTracker() {}
}
