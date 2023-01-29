package com.github.thedeathlycow.frostiful.mixins.block;

import com.github.thedeathlycow.frostiful.block.FrozenTorchBlock;
import com.github.thedeathlycow.frostiful.entity.FEntityTypes;
import com.github.thedeathlycow.frostiful.init.Frostiful;
import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractBlock.class)
public abstract class WindBlowoutMixin {

    @Inject(
            method = "onEntityCollision",
            at = @At("HEAD")
    )
    private void onCollideWithFreezingTorch(BlockState state, World world, BlockPos pos, Entity entity, CallbackInfo ci) {
        if (!world.isClient && entity.getType() == FEntityTypes.FREEZING_WIND) {

            if (!Frostiful.getConfig().freezingConfig.isWindDestroysTorches()) {
                return;
            }

            @Nullable
            BlockState blownOutState;

            if (state.isIn(BlockTags.FIRE)) {
                blownOutState = Blocks.AIR.getDefaultState();
            } else if (state.isIn(BlockTags.CANDLES)) {
                blownOutState = state.with(CandleBlock.LIT, false);
            } else if (state.isIn(BlockTags.CANDLE_CAKES)) {
                blownOutState = state.with(CandleCakeBlock.LIT, false);
            } else {
                blownOutState = FrozenTorchBlock.freezeTorch(state);
            }

            if (blownOutState != null) {
                world.setBlockState(pos, blownOutState);
            }
        }
    }

}