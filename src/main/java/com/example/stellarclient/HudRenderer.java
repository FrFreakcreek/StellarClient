package com.example.stellarclient;

import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

/** Draws the stats panel and the WASD keystrokes every frame. */
public final class HudRenderer {
    // Colors are ARGB: since 1.21.6 an alpha of 0 means invisible, so always start with FF.
    private static final int WHITE = 0xFFFFFFFF;
    private static final int PANEL = 0x90000000;
    private static final int KEY_OFF = 0x70000000;
    private static final int KEY_ON = 0xB0FFFFFF;
    private static final int KEY_SIZE = 20;
    private static final int GAP = 2;

    public static void register() {
        HudElementRegistry.attachElementBefore(
                VanillaHudElements.CHAT,
                Identifier.of(StellarClient.MOD_ID, "hud"),
                HudRenderer::render);
    }

    private static void render(DrawContext ctx, RenderTickCounter tickCounter) {
        MinecraftClient mc = MinecraftClient.getInstance();
        StellarConfig cfg = StellarConfig.get();
        if (!cfg.hudEnabled || mc.player == null || mc.options.hudHidden) return;

        TextRenderer font = mc.textRenderer;

        // ---- Stats panel ----
        List<String> lines = new ArrayList<>();
        if (cfg.showFps) lines.add("FPS: " + mc.getCurrentFps());
        if (cfg.showCps) lines.add("CPS: " + CpsTracker.left() + " | " + CpsTracker.right());
        if (cfg.showCoords) {
            lines.add(String.format("XYZ: %d, %d, %d",
                    (int) Math.floor(mc.player.getX()),
                    (int) Math.floor(mc.player.getY()),
                    (int) Math.floor(mc.player.getZ())));
        }
        if (cfg.showPing && mc.getNetworkHandler() != null) {
            PlayerListEntry me = mc.getNetworkHandler().getPlayerListEntry(mc.player.getUuid());
            if (me != null) lines.add("Ping: " + me.getLatency() + " ms");
        }

        int x = cfg.hudX;
        int y = cfg.hudY;
        if (!lines.isEmpty()) {
            int width = 0;
            for (String s : lines) width = Math.max(width, font.getWidth(s));
            int height = lines.size() * 10;
            if (cfg.background) ctx.fill(x - 3, y - 3, x + width + 3, y + height + 1, PANEL);
            int ty = y;
            for (String s : lines) {
                ctx.drawText(font, s, x, ty, WHITE, true);
                ty += 10;
            }
            y += height + 8;
        }

        // ---- Keystrokes (W on top, A S D underneath) ----
        if (cfg.showKeystrokes) {
            var o = mc.options;
            int step = KEY_SIZE + GAP;
            drawKey(ctx, font, "W", x + step, y, o.forwardKey.isPressed());
            drawKey(ctx, font, "A", x, y + step, o.leftKey.isPressed());
            drawKey(ctx, font, "S", x + step, y + step, o.backKey.isPressed());
            drawKey(ctx, font, "D", x + step * 2, y + step, o.rightKey.isPressed());
        }
    }

    private static void drawKey(DrawContext ctx, TextRenderer font, String label,
                                int x, int y, boolean pressed) {
        ctx.fill(x, y, x + KEY_SIZE, y + KEY_SIZE, pressed ? KEY_ON : KEY_OFF);
        // Pressed keys get dark text so they stay readable on the light box.
        int color = pressed ? 0xFF000000 : WHITE;
        ctx.drawText(font, label,
                x + (KEY_SIZE - font.getWidth(label)) / 2, y + (KEY_SIZE - 8) / 2, color, false);
    }

    private HudRenderer() {}
}
