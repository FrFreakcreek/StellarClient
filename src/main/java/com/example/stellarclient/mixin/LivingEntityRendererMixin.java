package com.example.stellarclient.mixin;

import com.example.stellarclient.StellarConfig;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Username toggle: when enabled, players get no nametag label. */
@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin {
    @Inject(method = "hasLabel", at = @At("HEAD"), cancellable = true, require = 0)
    private void stellar$hideNames(LivingEntity entity, double squaredDistanceToCamera,
                                   CallbackInfoReturnable<Boolean> cir) {
        if (StellarConfig.get().hideUsernames && entity instanceof PlayerEntity) {
            cir.setReturnValue(false);
        }
    }
}
