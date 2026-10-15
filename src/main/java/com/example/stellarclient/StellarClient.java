package com.example.stellarclient;

import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Entrypoint: wires up config, keybinds, the HUD, CPS tracking and zoom. */
public class StellarClient implements ClientModInitializer {
    public static final String MOD_ID = "stellarclient";
    public static final Logger LOGGER = LoggerFactory.getLogger("StellarClient");

    @Override
    public void onInitializeClient() {
        LOGGER.info("[StellarClient] Loading...");
        StellarConfig.load();
        Keys.register();
        CpsTracker.register();
        ZoomHandler.register();
        HudRenderer.register();
        LootBeams.register();
        FpsBoost.register();
        LOGGER.info("[StellarClient] Loaded OK.");
    }
}
