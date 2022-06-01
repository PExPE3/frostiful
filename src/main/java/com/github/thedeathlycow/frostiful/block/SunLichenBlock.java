package com.github.thedeathlycow.frostiful.block;

import com.github.thedeathlycow.frostiful.block.state.property.FrostifulProperties;
import com.github.thedeathlycow.frostiful.config.group.FreezingConfigGroup;
import com.github.thedeathlycow.frostiful.sound.FrostifulSoundEvents;
import com.github.thedeathlycow.frostiful.util.survival.FrostHelper;
import com.google.common.collect.ImmutableBiMap;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.GlowLichenBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.LightType;
import net.minecraft.world.World;

import java.util.Random;

@SuppressWarnings("deprecation")
public class SunLichenBlock extends GlowLichenBlock implements Heatable {

    @Deprecated
    public static final IntProperty HEAT_LEVEL = FrostifulProperties.LEVEL_0_3;

    public static final int MAX_HEAT_LEVEL = 3;
    private static final float BASE_GROW_CHANCE = 0.017f;
    private static final float RANDOM_DISCHARGE_CHANCE = 0.13f;

    private static final ImmutableBiMap<Block, Block> HEAT_LEVEL_INCREASES = new ImmutableBiMap.Builder<Block, Block>()
            .build();

    private final int heatLevel;

    public SunLichenBlock(final int heatLevel, Settings settings) {
        super(settings);
        this.heatLevel = heatLevel;
    }

    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        if (entity instanceof LivingEntity livingEntity) {
            int heatLevel = getHeatLevel(state);
            if (heatLevel > 0 && this.canBurn(livingEntity)) {
                int heat = FreezingConfigGroup.SUN_LICHEN_HEAT_PER_LEVEL.getValue() * heatLevel;
                FrostHelper.removeLivingFrost(livingEntity, heat);
                entity.damage(DamageSource.HOT_FLOOR, 1);
                this.createFireParticles(world, pos);
                //!world.setBlockState(pos, state.with(HEAT_LEVEL, 0));
                this.playSound(world, pos);
            }
        }

        super.onEntityCollision(state, world, pos, entity);
    }

    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        int skyLight = world.getLightLevel(LightType.SKY, pos);
        int heatLevel = this.getHeatLevel();
        if ((skyLight > 0 && world.isDay()) && world.getRandom().nextFloat() < this.getChargeChance(skyLight)) {
            if (heatLevel < MAX_HEAT_LEVEL) {
                //!world.setBlockState(pos, state.with(HEAT_LEVEL, heatLevel + 1));
            }
        } else if ((skyLight == 0) && world.getRandom().nextFloat() < RANDOM_DISCHARGE_CHANCE) {
            //!world.setBlockState(pos, state.with(HEAT_LEVEL, Math.max(0, heatLevel - 1)));
        }
    }

    @Override
    public ImmutableBiMap<Block, Block> getHeatLevelIncreases() {
        return HEAT_LEVEL_INCREASES;
    }

    public int getHeatLevel() {
        return this.heatLevel;
    }

    private float getChargeChance(int skyLight) {
        return BASE_GROW_CHANCE * skyLight;
    }

    private boolean canBurn(LivingEntity entity) {
        if (entity.isSpectator() || (entity instanceof PlayerEntity player && player.isCreative())) {
            return false;
        }
        return !entity.isFireImmune();
    }

    private void playSound(World world, BlockPos pos) {
        if (world.isClient()) {
            return;
        }
        Random random = world.getRandom();
        float pitch = 0.8F + (random.nextFloat() - random.nextFloat()) * 0.4F;
        world.playSound(null, pos, FrostifulSoundEvents.FIRE_LICHEN_DISCHARGE, SoundCategory.BLOCKS, 0.7F, pitch);
    }

    private void createFireParticles(World world, BlockPos pos) {
        final double maxHorizontalOffset = 0.5;
        for (int i = 0; i < 10; i++) {
            double x = pos.getX() + 0.5;
            double y = pos.getY() + world.getRandom().nextDouble(0.33);
            double z = pos.getZ() + 0.5;

            x += world.getRandom().nextDouble(-maxHorizontalOffset, maxHorizontalOffset);
            z += world.getRandom().nextDouble(-maxHorizontalOffset, maxHorizontalOffset);
            world.addParticle(ParticleTypes.FLAME, x, y, z, 0.0D, 0.1D, 0.0D);
        }
    }

    private static int getHeatLevel(BlockState state) {
        return state.isOf(FrostifulBlocks.SUN_LICHEN) ? ((SunLichenBlock) state.getBlock()).getHeatLevel() : 0;
    }
}
