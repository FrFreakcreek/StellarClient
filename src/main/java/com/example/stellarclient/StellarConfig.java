package com.example.stellarclient;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/** JSON config at config/stellarclient.json. Edit the file, then restart the game. */
public class StellarConfig {
    public boolean hudEnabled = true;     // master switch (also toggled in-game with H)
    public boolean showFps = true;
    public boolean showCps = true;
    public boolean showCoords = true;
    public boolean showPing = true;
    public boolean showKeystrokes = true;
    public int zoomFov = 30;              // FOV while holding the zoom key (30 = max zoom)
    public int hudX = 6;                  // left padding in pixels
    public int hudY = 6;                  // top padding in pixels
    public boolean background = true;     // dark box behind the text

    // --- Loot beams (particle columns above dropped items) ---
    public boolean lootBeams = true;
    public String lootBeamMinRarity = "COMMON"; // COMMON, UNCOMMON, RARE or EPIC
    public double lootBeamRange = 32.0;   // blocks
    public double lootBeamHeight = 6.0;   // column height in blocks
    public int lootBeamMaxItems = 30;     // cap, so piles of items cannot tank FPS

    // --- Username toggle ---
    public boolean hideUsernames = false; // hide nametags above players (toggle with N)

    // --- FPS boost preset (toggle with B). Never raises a value, only lowers it. ---
    public int boostViewDistance = 8;          // chunks
    public int boostSimulationDistance = 6;    // chunks
    public double boostEntityDistance = 0.75;  // 0.5 - 5.0 (vanilla default 1.0)

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path PATH =
            FabricLoader.getInstance().getConfigDir().resolve("stellarclient.json");
    private static StellarConfig instance = new StellarConfig();

    public static StellarConfig get() { return instance; }

    public static void load() {
        try {
            if (Files.exists(PATH)) {
                StellarConfig loaded = GSON.fromJson(Files.readString(PATH), StellarConfig.class);
                if (loaded != null) instance = loaded;
            }
            Files.writeString(PATH, GSON.toJson(instance)); // write defaults / normalize
        } catch (IOException | RuntimeException e) {
            StellarClient.LOGGER.error("[StellarClient] Config error, using defaults", e);
        }
    }
}
