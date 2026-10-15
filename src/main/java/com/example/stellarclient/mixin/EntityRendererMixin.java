package com.example.stellarclient.mixin;

import com.example.stellarclient.StellarConfig;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Same username toggle for any player renderer that uses the base-class check. */
@Mixin(EntityRenderer.class)
public abstract class EntityRendererMixin {
    @Inject(method = "hasLabel", at = @At("HEAD"), cancellable = true, require = 0)
    private void stellar$hideNames(Entity entity, double squaredDistanceToCamera,
                                   CallbackInfoReturnable<Boolean> cir) {
        if (StellarConfig.get().hideUsernames && entity instanceof PlayerEntity) {
            cir.setReturnValue(false);
        }
    }
}
