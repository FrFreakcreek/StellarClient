package com.example.stellarclient;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.SimpleOption;

/** Hold the zoom key to narrow your FOV; releasing restores the FOV you had before. */
public final class ZoomHandler {
    private static boolean zooming = false;
    private static int savedFov = 70;

    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(ZoomHandler::tick);
        // Safety: never leave the zoomed FOV saved in options if the game closes mid-zoom.
        ClientLifecycleEvents.CLIENT_STOPPING.register(client -> restore(client));
    }

    private static void tick(MinecraftClient client) {
        if (client.player == null) { restore(client); return; }
        SimpleOption<Integer> fov = client.options.getFov();
        boolean held = Keys.ZOOM.isPressed() && client.currentScreen == null;

        if (held) {
            if (!zooming) { savedFov = fov.getValue(); zooming = true; }
            fov.setValue(StellarConfig.get().zoomFov);
        } else {
            restore(client);
        }
    }

    private static void restore(MinecraftClient client) {
        if (!zooming) return;
        client.options.getFov().setValue(savedFov);
        zooming = false;
    }

    private ZoomHandler() {}
}
