package com.example.stellarclient;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.Rarity;

/**
 * Loot "beams": a shimmering particle column above each nearby dropped item, colored by
 * the item's rarity. Particles are client-side only and respect your Particles setting.
 */
public final class LootBeams {

    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(LootBeams::tick);
    }

    private static void tick(MinecraftClient client) {
        StellarConfig cfg = StellarConfig.get();
        if (!cfg.lootBeams || client.world == null || client.player == null) return;

        Rarity min = parseRarity(cfg.lootBeamMinRarity);
        double rangeSq = cfg.lootBeamRange * cfg.lootBeamRange;
        var random = client.world.getRandom();
        int beams = 0;

        for (Entity e : client.world.getEntities()) {
            if (!(e instanceof ItemEntity item)) continue;
            if (item.squaredDistanceTo(client.player) > rangeSq) continue;

            Rarity rarity = item.getStack().getRarity();
            if (rarity.ordinal() < min.ordinal()) continue;
            if (++beams > cfg.lootBeamMaxItems) break;

            ParticleEffect effect = effectFor(rarity);
            for (int i = 0; i < 2; i++) { // two sparks per tick at random heights = a column
                double y = item.getY() + 0.3 + random.nextDouble() * cfg.lootBeamHeight;
                client.world.addParticleClient(effect, item.getX(), y, item.getZ(), 0.0, 0.02, 0.0);
            }
        }
    }

    private static ParticleEffect effectFor(Rarity rarity) {
        return switch (rarity) {
            case EPIC -> ParticleTypes.WITCH;                 // purple
            case RARE -> ParticleTypes.SOUL_FIRE_FLAME;       // blue
            case UNCOMMON -> ParticleTypes.HAPPY_VILLAGER;    // green
            default -> ParticleTypes.END_ROD;                 // white
        };
    }

    private static Rarity parseRarity(String name) {
        try { return Rarity.valueOf(name.trim().toUpperCase()); }
        catch (RuntimeException e) { return Rarity.COMMON; }
    }

    private LootBeams() {}
}
