package com.github.thedeathlycow.frostiful.mixins.compat.healthoverlay.absent;

import com.github.thedeathlycow.frostiful.client.FrozenHeartsOverlay;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(InGameHud.class)
abstract class ColdHeartOverlay {

    private final int[] heartYPositions = new int[FrozenHeartsOverlay.MAX_COLD_HEARTS];
    private final int[] heartXPositions = new int[FrozenHeartsOverlay.MAX_COLD_HEARTS];

    @Inject(
            method = "renderHealthBar",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/hud/InGameHud;drawHeart(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/gui/hud/InGameHud$HeartType;IIIZZ)V",
                    ordinal = 0,
                    shift = At.Shift.AFTER
            ),
            locals = LocalCapture.CAPTURE_FAILEXCEPTION
    )
    private void captureHeartPositions(
            MatrixStack matrices,
            PlayerEntity player,
            int x, int y,
            int lines,
            int regeneratingHeartIndex,
            float maxHealth, int lastHealth, int health,
            int absorption,
            boolean blinking,
            CallbackInfo ci,
            InGameHud.HeartType heartType,
            int i, int j, int k, int l,
            int m,
            int n, int o,
            int p, int q
    ) {
        if (m < FrozenHeartsOverlay.MAX_COLD_HEARTS) {
            heartYPositions[m] = q;
            heartXPositions[m] = p;
        }
    }

    @Inject(
            method = "renderHealthBar",
            at = @At(
                    value = "TAIL"
            )
    )
    private void drawColdHeartOverlayBar(
            MatrixStack matrices,
            PlayerEntity player,
            int x, int y,
            int lines,
            int regeneratingHeartIndex,
            float maxHealth, int lastHealth, int health,
            int absorption,
            boolean blinking,
            CallbackInfo ci
    ) {
        FrozenHeartsOverlay.drawHeartOverlayBar(matrices, player, heartXPositions, heartYPositions);
    }

}
