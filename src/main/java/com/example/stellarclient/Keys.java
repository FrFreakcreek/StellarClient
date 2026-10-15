package com.example.stellarclient;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

/** Registers keybinds (rebindable in Options > Controls) and handles the toggles. */
public final class Keys {
    public static KeyBinding TOGGLE_HUD;
    public static KeyBinding ZOOM;
    public static KeyBinding FPS_BOOST;
    public static KeyBinding LOOT_BEAMS;
    public static KeyBinding USERNAMES;

    private static KeyBinding make(String name, int key, KeyBinding.Category category) {
        return KeyBindingHelper.registerKeyBinding(
                new KeyBinding("key.stellarclient." + name, InputUtil.Type.KEYSYM, key, category));
    }

    public static void register() {
        // Since 1.21.9 categories are objects, and each may only be registered once.
        KeyBinding.Category category =
                KeyBinding.Category.create(Identifier.of(StellarClient.MOD_ID, "main"));

        TOGGLE_HUD = make("toggle_hud", GLFW.GLFW_KEY_H, category);
        ZOOM = make("zoom", GLFW.GLFW_KEY_C, category);
        FPS_BOOST = make("fps_boost", GLFW.GLFW_KEY_B, category);
        LOOT_BEAMS = make("loot_beams", GLFW.GLFW_KEY_L, category);
        USERNAMES = make("usernames", GLFW.GLFW_KEY_N, category);

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            StellarConfig cfg = StellarConfig.get();
            while (TOGGLE_HUD.wasPressed()) {
                cfg.hudEnabled = !cfg.hudEnabled;
                say(client, cfg.hudEnabled, "stellarclient.hud");
            }
            while (LOOT_BEAMS.wasPressed()) {
                cfg.lootBeams = !cfg.lootBeams;
                say(client, cfg.lootBeams, "stellarclient.beams");
            }
            while (USERNAMES.wasPressed()) {
                cfg.hideUsernames = !cfg.hideUsernames;
                // Message says whether names are SHOWN.
                say(client, !cfg.hideUsernames, "stellarclient.names");
            }
            while (FPS_BOOST.wasPressed()) {
                say(client, FpsBoost.toggle(client), "stellarclient.boost");
            }
        });
    }

    /** Action bar message: "<prefix>_on" or "<prefix>_off". */
    private static void say(MinecraftClient client, boolean on, String prefix) {
        if (client.player != null) {
            client.player.sendMessage(Text.translatable(prefix + (on ? "_on" : "_off")), true);
        }
    }

    private Keys() {}
}
