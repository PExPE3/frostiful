package com.github.thedeathlycow.frostiful.entity;

import com.github.thedeathlycow.frostiful.attributes.FEntityAttributes;
import com.github.thedeathlycow.frostiful.block.FBlocks;
import com.github.thedeathlycow.frostiful.block.FrozenTorchBlock;
import com.github.thedeathlycow.frostiful.config.FrostifulConfig;
import com.github.thedeathlycow.frostiful.enchantment.EnervationEnchantment;
import com.github.thedeathlycow.frostiful.init.Frostiful;
import com.github.thedeathlycow.frostiful.item.FItems;
import com.github.thedeathlycow.frostiful.item.FrostWandItem;
import com.github.thedeathlycow.frostiful.sound.FSoundEvents;
import com.github.thedeathlycow.frostiful.tag.blocks.FBlockTags;
import com.github.thedeathlycow.frostiful.util.survival.FrostHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.*;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.*;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.raid.RaiderEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.Properties;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.Hand;
import net.minecraft.util.math.*;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.*;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.ThreadLocalRandom;

/**
 * By remapping {@link SpellcastingIllagerEntity.Spell}s, the Frostologer has the following spells:
 * <p>
 * SUMMON_VEX = SUMMON_MINIONS
 * DISAPPEAR = DESTROY_HEAT_SOURCES
 */
public class FrostologerEntity extends SpellcastingIllagerEntity implements RangedAttackMob {

    private static final TrackedData<Boolean> IS_USING_FROST_WAND = DataTracker.registerData(
            FrostologerEntity.class, TrackedDataHandlerRegistry.BOOLEAN
    );

    private static final float POWER_PARTICLES_FREEZING_SCALE_START = 0.95f;
    private static final int NUM_POWER_PARTICLES = 3;


    public float prevStrideDistance;
    public float strideDistance;
    public double prevCapeX;
    public double prevCapeY;
    public double prevCapeZ;
    public double capeX;
    public double capeY;
    public double capeZ;

    protected FrostologerEntity(EntityType<? extends FrostologerEntity> entityType, World world) {
        super(entityType, world);
        this.experiencePoints = 20;
    }

    public static DefaultAttributeContainer.Builder createFrostologerAttributes() {
        return HostileEntity.createHostileAttributes()
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.5)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 12.0)
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 75.0)
                .add(FEntityAttributes.MAX_FROST, 45.0)
                .add(FEntityAttributes.FROST_RESISTANCE, -5.0);
    }

    public static boolean isHeatSource(BlockState state) {
        int minLightForWarmth = Frostiful.getConfig().freezingConfig.getMinLightForWarmth();
        return state.getLuminance() >= minLightForWarmth;
    }

    public boolean isInHeatedArea() {
        int minLightForWarmth = Frostiful.getConfig().freezingConfig.getMinLightForWarmth();
        return this.world.getLightLevel(LightType.BLOCK, this.getBlockPos()) > minLightForWarmth;
    }

    /**
     * 'Destroys' the heat source at the given `blockPos` by transforming `state` into another block according to the following rules:
     * <ul>
     * <li> protected block -> do nothing </li>
     * <li> full cubes -> ice </li>
     * <li> lava (level 8) -> obsidian </li>
     * <li> torches -> frozen torch </li>
     * <li> waterlogged blocks -> ice </li>
     * <li> everything else -> air </li>
     * </ul>
     *
     * @param world    The server world
     * @param state    The state to transform
     * @param blockPos The position of `state` in `world`.
     */
    public void destroyHeatSource(ServerWorld world, BlockState state, BlockPos blockPos) {

        BlockState frozenState;
        Block heatedBlock = state.getBlock();
        FluidState fluidState = state.getFluidState();

        if (state.isIn(FBlockTags.FROSTOLOGER_CANNOT_FREEZE)) {
            frozenState = state;
        } else if (state.isFullCube(world, blockPos)) {
            frozenState = Blocks.ICE.getDefaultState();
        } else if (fluidState.isOf(Fluids.LAVA) && fluidState.getLevel() == 8) {
            frozenState = Blocks.OBSIDIAN.getDefaultState();
        } else if (heatedBlock instanceof TorchBlock) {
            BlockState torch = FrozenTorchBlock.freezeTorch(state);
            frozenState = torch != null ? torch : Blocks.AIR.getDefaultState();
        } else if (state.contains(Properties.WATERLOGGED) && state.get(Properties.WATERLOGGED)) {
            frozenState = Blocks.ICE.getDefaultState();
        } else {
            frozenState = Blocks.AIR.getDefaultState();
        }

        world.setBlockState(blockPos, frozenState);

        world.playSound(
                null,
                blockPos,
                SoundEvents.BLOCK_FIRE_EXTINGUISH,
                SoundCategory.HOSTILE,
                0.5f, 1.0f + ((this.random.nextFloat() % 0.2f) - 0.1f)
        );

        Vec3d centeredPos = Vec3d.ofCenter(blockPos);

        world.spawnParticles(
                ParticleTypes.SMOKE,
                centeredPos.x, centeredPos.y, centeredPos.z,
                12,
                0.1, 1, 0.1,
                0.1
        );
    }

    @Override
    public boolean isInvulnerableTo(DamageSource damageSource) {
        return damageSource == DamageSource.FREEZE || super.isInvulnerableTo(damageSource);
    }

    protected void initGoals() {
        super.initGoals();
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new SpellcastingIllagerEntity.LookAtTargetGoal());

        this.goalSelector.add(2, new FrostWandCastGoal(this, 1.0, 40, 10f));

        this.goalSelector.add(2, new FleeEntityGoal<>(this, PlayerEntity.class, 8.0F, 1.2, 1.5));
        this.goalSelector.add(2, new FleeEntityGoal<>(this, IronGolemEntity.class, 8.0F, 1.2, 1.5));

        this.goalSelector.add(4, new FrostWandAttackGoal());
        this.goalSelector.add(4, new SummonMinionsGoal());
        this.goalSelector.add(4, new IcicleAttackGoal(UniformIntProvider.create(7, 12)));

        this.goalSelector.add(6, new DestroyHeatSourcesGoal(15));

        this.goalSelector.add(8, new WanderAroundGoal(this, 0.6));
        this.goalSelector.add(9, new LookAtEntityGoal(this, PlayerEntity.class, 3.0F, 1.0F));
        this.goalSelector.add(10, new LookAtEntityGoal(this, MobEntity.class, 8.0F));

        this.targetSelector.add(
                1,
                new RevengeGoal(this, RaiderEntity.class)
                        .setGroupRevenge()
        );
        this.targetSelector.add(
                2,
                new ActiveTargetGoal<>(this, PlayerEntity.class, true)
                        .setMaxTimeWithoutVisibility(300)
        );
        this.targetSelector.add(
                3,
                new ActiveTargetGoal<>(this, MerchantEntity.class, false)
                        .setMaxTimeWithoutVisibility(300)
        );
        this.targetSelector.add(
                3,
                new ActiveTargetGoal<>(this, IronGolemEntity.class, false)
        );
    }

    @Override
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable NbtCompound entityNbt) {
        this.initEquipment(world.getRandom(), difficulty);
        return super.initialize(world, difficulty, spawnReason, entityData, entityNbt);
    }

    @Override
    protected void initEquipment(Random random, LocalDifficulty difficulty) {
        this.setStackInHand(Hand.MAIN_HAND, new ItemStack(FItems.FROST_WAND));
        this.equipStack(EquipmentSlot.CHEST, new ItemStack(FItems.FROSTOLOGY_CLOAK));
        this.enchantMainHandItem(random, difficulty.getClampedLocalDifficulty());

        // equipment drops handled with loot table
        this.setEquipmentDropChance(EquipmentSlot.MAINHAND, 0.0f);
        this.setEquipmentDropChance(EquipmentSlot.CHEST, 0.0f);
    }

    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(IS_USING_FROST_WAND, false);
    }

    @Override
    public void tick() {
        super.tick();

        this.updateCapeAngles();

        if (this.world.isClient && this.getFreezingScale() >= POWER_PARTICLES_FREEZING_SCALE_START) {
            this.spawnPowerParticles();
        }

        FrostifulConfig config = Frostiful.getConfig();
        if (this.getFreezingScale() < config.combatConfig.getFrostologerMaxPassiveFreezing()) {
            FrostHelper.addLivingFrost(this, config.combatConfig.getFrostologerPassiveFreezingPerTick());
        }
    }

    @Override
    public void tickRiding() {
        super.tickRiding();
        this.prevStrideDistance = this.strideDistance;
        this.strideDistance = 0.0F;
    }

    @Override
    public void tickMovement() {
        super.tickMovement();

        this.prevStrideDistance = this.strideDistance;

        float walkSpeed;
        if (this.onGround && !this.isDead() && !this.isSwimming()) {
            walkSpeed = Math.min(0.1F, (float)this.getVelocity().horizontalLength());
        } else {
            walkSpeed = 0.0F;
        }
        this.strideDistance += (walkSpeed - this.strideDistance) * 0.4f;

        if (this.world.isClient) {
            return;
        }

        // do not place snow/destroy heat sources unless mobGriefing is on
        if (!this.world.getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING)) {
            return;
        }


        ServerWorld serverWorld = (ServerWorld) this.world; // covered by isClient check above

        BlockPos frostologerPos = this.getBlockPos();
        BlockState snow = Blocks.SNOW.getDefaultState();

        for (BlockPos blockPos : new BlockPos[]{frostologerPos, frostologerPos.down()}) {

            BlockState blockState = this.world.getBlockState(blockPos);
            if (isHeatSource(blockState)) {
                this.destroyHeatSource(serverWorld, blockState, blockPos);
            }

            if (blockState.isAir() && snow.canPlaceAt(this.world, blockPos)) {
                this.world.setBlockState(blockPos, snow);
                this.world.emitGameEvent(
                        GameEvent.BLOCK_PLACE,
                        blockPos,
                        GameEvent.Emitter.of(this, blockState)
                );
            }
        }
    }

    @Environment(EnvType.CLIENT)
    private void spawnPowerParticles() {

        ThreadLocalRandom random = ThreadLocalRandom.current();

        Box box = this.getBoundingBox();

        for (int i = 0; i < NUM_POWER_PARTICLES; i++) {
            // pick random pos in bounding box
            double x = box.getMin(Direction.Axis.X) + random.nextDouble(box.getXLength());
            double y = box.getMin(Direction.Axis.Y) + random.nextDouble(box.getYLength());
            double z = box.getMin(Direction.Axis.Z) + random.nextDouble(box.getZLength());

            world.addParticle(
                    ParticleTypes.SNOWFLAKE,
                    x, y, z,
                    0, 0.06f, 0
            );
        }
    }

    @Override
    public IllagerEntity.State getState() {
        if (this.isSpellcasting()) {
            return State.SPELLCASTING;
        } else {
            return this.isCelebrating() ? State.CELEBRATING : State.NEUTRAL;
        }
    }

    @Override
    public void attack(LivingEntity target, float pullProgress) {
        if (this.activeItemStack.isOf(FItems.FROST_WAND)) {
            this.getLookControl().lookAt(target);
            FrostWandItem.fireFrostSpell(this.activeItemStack.copy(), this.world, this);
        }
    }

    public boolean hasTarget() {
        LivingEntity target = this.getTarget();
        return target != null && target.isAlive();
    }

    public boolean isTargetRooted() {
        LivingEntity target = this.getTarget();
        return target != null
                && ((RootedEntity) target).frostiful$isRooted();
    }

    public boolean isUsingFrostWand() {
        return this.dataTracker.get(IS_USING_FROST_WAND);
    }

    private void startUsingFrostWand() {
        this.playSound(
                FSoundEvents.ITEM_FROST_WAND_PREPARE_CAST,
                1.0f, 1.0f
        );
        this.dataTracker.set(IS_USING_FROST_WAND, true);
    }

    private void stopUsingFrostWand() {
        this.dataTracker.set(IS_USING_FROST_WAND, false);
    }

    @Override
    public void addBonusForWave(int wave, boolean unused) {

    }

    private void updateCapeAngles() {
        this.prevCapeX = this.capeX;
        this.prevCapeY = this.capeY;
        this.prevCapeZ = this.capeZ;
        double dx = this.getX() - this.capeX;
        double dy = this.getY() - this.capeY;
        double dz = this.getZ() - this.capeZ;
        double threshold = 10.0;
        if (dx > threshold) {
            this.capeX = this.getX();
            this.prevCapeX = this.capeX;
        }

        if (dz > threshold) {
            this.capeZ = this.getZ();
            this.prevCapeZ = this.capeZ;
        }

        if (dy > threshold) {
            this.capeY = this.getY();
            this.prevCapeY = this.capeY;
        }

        if (dx < -threshold) {
            this.capeX = this.getX();
            this.prevCapeX = this.capeX;
        }

        if (dz < -threshold) {
            this.capeZ = this.getZ();
            this.prevCapeZ = this.capeZ;
        }

        if (dy < -threshold) {
            this.capeY = this.getY();
            this.prevCapeY = this.capeY;
        }

        this.capeX += dx / 4;
        this.capeZ += dz / 4;
        this.capeY += dy / 4;
    }

    @Override
    public boolean isTeammate(@Nullable Entity other) {
        if (other == null) {
            return false;
        } else if (other == this) {
            return true;
        } else if (super.isTeammate(other)) {
            return true;
        } else if (other.getType() == FEntityTypes.BITER) {
            return this.isTeammate(((BiterEntity) other).getOwner());
        } else if (other instanceof LivingEntity && ((LivingEntity) other).getGroup() == EntityGroup.ILLAGER) {
            return this.getScoreboardTeam() == null && other.getScoreboardTeam() == null;
        } else {
            return false;
        }
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return FSoundEvents.ENTITY_FROSTOLOGER_AMBIENT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return FSoundEvents.ENTITY_FROSTOLOGER_DEATH;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return FSoundEvents.ENTITY_FROSTOLOGER_HURT;
    }

    @Override
    public SoundEvent getCelebratingSound() {
        return FSoundEvents.ENTITY_CHILLAGER_CELEBRATE;
    }

    @Override
    protected SoundEvent getCastSpellSound() {
        return FSoundEvents.ENTITY_FROSTOLOGER_CAST_SPELL;
    }

    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.dataTracker.set(IS_USING_FROST_WAND, nbt.getBoolean("IsUsingFrostWand"));
    }

    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putBoolean("IsUsingFrostWand", this.dataTracker.get(IS_USING_FROST_WAND));
    }

    protected class FrostWandAttackGoal extends AttackGoal {
        public FrostWandAttackGoal() {
            super(FrostologerEntity.this);
        }

        @Override
        public boolean canStart() {
            return FrostologerEntity.this.isTargetRooted()
                    && super.canStart();
        }

    }

    protected class FrostWandCastGoal extends ProjectileAttackGoal {

        public FrostWandCastGoal(RangedAttackMob mob, double mobSpeed, int intervalTicks, float maxShootRange) {
            super(mob, mobSpeed, intervalTicks, maxShootRange);
        }

        public boolean canStart() {
            return super.canStart()
                    && FrostologerEntity.this.hasTarget()
                    && !FrostologerEntity.this.isTargetRooted()
                    && FrostologerEntity.this.getMainHandStack().isOf(FItems.FROST_WAND);
        }

        public void start() {
            super.start();
            FrostologerEntity.this.setAttacking(true);
            FrostologerEntity.this.setCurrentHand(Hand.MAIN_HAND);
            FrostologerEntity.this.startUsingFrostWand();
        }

        public void stop() {
            super.stop();
            FrostologerEntity.this.setAttacking(false);
            FrostologerEntity.this.clearActiveItem();
            FrostologerEntity.this.stopUsingFrostWand();
        }
    }

    protected class DestroyHeatSourcesGoal extends SpellcastingIllagerEntity.CastSpellGoal {

        private final int range;

        protected DestroyHeatSourcesGoal(int range) {
            super();
            this.range = range;
        }

        public void start() {
            super.start();
            if (FrostologerEntity.this.isOnFire()) {
                FrostologerEntity.this.extinguish();
                FrostologerEntity.this.playExtinguishSound();
            }
        }

        public boolean canStart() {
            // no super call as that requires a target to be selected
            if (FrostologerEntity.this.isSpellcasting()) {
                return false;
            } else if (FrostologerEntity.this.age < this.startTime) {
                return false;
            } else {
                return FrostologerEntity.this.hasTarget() || FrostologerEntity.this.isInHeatedArea();
            }
        }

        @Override
        public boolean shouldContinue() {
            return this.spellCooldown > 0;
        }

        public void tick() {

            Box box = FrostologerEntity.this.getBoundingBox().expand(this.range);

            @Nullable
            ServerWorld serverWorld = null;
            if (!world.isClient) {
                serverWorld = (ServerWorld) world;
            }


            int heatDrain = Frostiful.getConfig().combatConfig.getFrostologerHeatDrainPerTick();
            for (LivingEntity victim : world.getEntitiesByClass(LivingEntity.class, box, (entity) -> true)) {
                FrostHelper.addLivingFrost(victim, heatDrain);

                if (serverWorld != null) {
                    EnervationEnchantment.addHeatDrainParticles(
                            serverWorld, FrostologerEntity.this, victim, 5, 0.08
                    );
                }
            }

            super.tick();
        }

        @Override
        protected void castSpell() {

            if (!FrostologerEntity.this.world.getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING)) {
                return;
            }

            BlockPos origin = FrostologerEntity.this.getBlockPos();
            Vec3i distance = new Vec3i(this.range, this.range, this.range);
            for (BlockPos pos : BlockPos.iterate(origin.subtract(distance), origin.add(distance))) {
                BlockState state = FrostologerEntity.this.world.getBlockState(pos);
                if (FrostologerEntity.isHeatSource(state) && world instanceof ServerWorld serverWorld) {
                    FrostologerEntity.this.destroyHeatSource(serverWorld, state, pos);
                }
            }
        }

        @Override
        protected int getInitialCooldown() {
            return 60;
        }

        @Override
        protected int getSpellTicks() {
            return 60;
        }

        @Override
        protected int startTimeDelay() {
            return 140;
        }

        @Nullable
        @Override
        protected SoundEvent getSoundPrepare() {
            return FSoundEvents.ENTITY_FROSTOLOGER_PREPARE_CAST_BLIZZARD;
        }

        @Override
        protected Spell getSpell() {
            return Spell.DISAPPEAR;
        }

    }

    protected class SummonMinionsGoal extends SpellcastingIllagerEntity.CastSpellGoal {
        private final TargetPredicate closeMinionPredicate = TargetPredicate.createNonAttackable()
                .setBaseMaxDistance(16.0)
                .ignoreVisibility()
                .ignoreDistanceScalingFactor();

        public void start() {
            super.start();
            if (FrostologerEntity.this.isOnFire()) {
                FrostologerEntity.this.extinguish();
                FrostologerEntity.this.playExtinguishSound();
            }
        }

        public boolean canStart() {
            if (FrostologerEntity.this.random.nextInt(2) == 0) {
                return false;
            } else if (!super.canStart()) {
                return false;
            } else if (!FrostologerEntity.this.isTargetRooted()) {
                return false;
            } else {
                int numNearbyMinions = FrostologerEntity.this.world.getTargets(
                        BiterEntity.class,
                        this.closeMinionPredicate,
                        FrostologerEntity.this,
                        FrostologerEntity.this.getBoundingBox().expand(16.0)
                ).size();

                return FrostologerEntity.this.random.nextInt(8) + 1 > numNearbyMinions;
            }
        }

        @Override
        protected void castSpell() {
            ServerWorld serverWorld = (ServerWorld) FrostologerEntity.this.world;

            for (int i = 0; i < 3; ++i) {
                BlockPos blockPos = FrostologerEntity.this.getBlockPos()
                        .add(
                                -2 + FrostologerEntity.this.random.nextInt(5),
                                1,
                                -2 + FrostologerEntity.this.random.nextInt(5)
                        );

                // use vex entity as placeholder for custom minions
                BiterEntity minionEntity = FEntityTypes.BITER.create(FrostologerEntity.this.world);

                if (minionEntity == null) {
                    return;
                }

                minionEntity.refreshPositionAndAngles(blockPos, 0.0F, 0.0F);

                minionEntity.initialize(
                        serverWorld,
                        FrostologerEntity.this.world.getLocalDifficulty(blockPos),
                        SpawnReason.MOB_SUMMONED,
                        null, null
                );
                minionEntity.setOwner(FrostologerEntity.this);

                serverWorld.spawnEntityAndPassengers(minionEntity);
            }
        }

        @Override
        protected int getSpellTicks() {
            return 100;
        }

        @Override
        protected int startTimeDelay() {
            return 20;
        }

        @Nullable
        @Override
        protected SoundEvent getSoundPrepare() {
            return SoundEvents.ENTITY_EVOKER_PREPARE_SUMMON;
        }

        @Override
        protected Spell getSpell() {
            return Spell.SUMMON_VEX;
        }
    }

    protected class IcicleAttackGoal extends SpellcastingIllagerEntity.CastSpellGoal {

        private final IntProvider numIciclesProvider;

        public IcicleAttackGoal(IntProvider numIciclesProvider) {
            this.numIciclesProvider = numIciclesProvider;
        }

        public void start() {
            super.start();
            if (FrostologerEntity.this.isOnFire()) {
                FrostologerEntity.this.extinguish();
                FrostologerEntity.this.playExtinguishSound();
            }
        }

        public boolean canStart() {
            if (FrostologerEntity.this.random.nextInt(2) == 0) {
                return false;
            } else if (!super.canStart()) {
                return false;
            } else {
                return FrostologerEntity.this.isTargetRooted();
            }
        }

        @Override
        protected void castSpell() {
            ServerWorld serverWorld = (ServerWorld) FrostologerEntity.this.world;

            int numIcicles = this.numIciclesProvider.get(random);
            for (int i = 0; i < numIcicles; ++i) {
                BlockPos blockPos = FrostologerEntity.this.getBlockPos()
                        .add(
                                -2 + FrostologerEntity.this.random.nextInt(5),
                                2,
                                -2 + FrostologerEntity.this.random.nextInt(5)
                        );

                ThrownIcicleEntity icicle = FEntityTypes.THROWN_ICICLE.create(FrostologerEntity.this.world);

                if (icicle == null) {
                    return;
                }

                icicle.refreshPositionAndAngles(blockPos, 0.0F, 0.0F);
                icicle.setOwner(FrostologerEntity.this);

                icicle.setVelocity(
                        FrostologerEntity.this,
                        FrostologerEntity.this.getPitch() + FrostologerEntity.this.random.nextFloat(),
                        FrostologerEntity.this.getHeadYaw() + FrostologerEntity.this.random.nextFloat(),
                        0.0f, 3.0f, 1.0f
                );

                serverWorld.spawnEntityAndPassengers(icicle);
            }
        }

        @Override
        protected int getSpellTicks() {
            return 20;
        }

        @Override
        protected int startTimeDelay() {
            return 20;
        }

        @Nullable
        @Override
        protected SoundEvent getSoundPrepare() {
            return SoundEvents.ENTITY_EVOKER_PREPARE_SUMMON;
        }

        @Override
        protected Spell getSpell() {
            return Spell.SUMMON_VEX;
        }
    }
}
