package com.arf8vhg7.jja.feature.player.mobility.fly;

import net.mcreator.jujutsucraft.init.JujutsucraftModMobEffects;
import net.mcreator.jujutsucraft.init.JujutsucraftModParticleTypes;
import net.mcreator.jujutsucraft.procedures.EntityVectorProcedure;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;

public final class FlySpaceControlService {
    private static final String KEY_SPACE_HELD = "PRESS_SPACE";
    private static final String KEY_MOVE_FORWARD = "PRESS_W";
    private static final String KEY_MOVE_BACK = "PRESS_S";
    private static final String KEY_SPACE_HOLD_TICKS = "jjaFlySpaceHoldTicks";
    private static final String KEY_SPACE_HOLD_STARTED_IN_AIR = "jjaFlySpaceHoldStartedInAir";
    private static final String KEY_LAST_SPACE_HOLD_TICKS = "jjaFlySpaceLastHoldTicks";
    private static final String KEY_CONTROLLED_FLIGHT_ACTIVE = "jjaFlyControlledFlightActive";
    private static final int FLY_START_HOLD_TICKS = 4;
    private static final double DOUBLE_JUMP_SPEED = 1.0D;
    private static final ResourceLocation GLASS_FALL_SOUND_ID = ResourceLocation.fromNamespaceAndPath("minecraft", "block.glass.fall");

    private FlySpaceControlService() {
    }

    public static void tick(ServerPlayer player) {
        if (player == null) {
            return;
        }
        if (player.isCreative() || player.isSpectator()) {
            clearRuntimeState(player);
            return;
        }

        boolean spaceHeld = isSpaceHeld(player);
        if (spaceHeld) {
            setSpaceHoldTicks(player, getSpaceHoldTicks(player) + 1);
        } else if (getSpaceHoldTicks(player) != 0) {
            setSpaceHoldTicks(player, 0);
        }

        boolean hasFlyEffect = player.hasEffect(JujutsucraftModMobEffects.FLY_EFFECT.get());
        if ((!spaceHeld || !hasFlyEffect) && isControlledFlightActive(player)) {
            stopControlledFlight(player);
            return;
        }

        if (hasFlyEffect && !isControlledFlightActive(player) && getSpaceHoldTicks(player) >= FLY_START_HOLD_TICKS) {
            startControlledFlight(player);
        }

        if (shouldCompensateFlyEffectDuration(player)) {
            compensateFlyEffectDuration(player);
        }
    }

    public static void onSpacePressed(Player player) {
        if (player == null) {
            return;
        }
        setSpaceHoldStartedInAir(player, !player.onGround());
    }

    public static void onSpaceReleased(Player player) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return;
        }
        int holdTicks = getSpaceHoldTicks(serverPlayer);
        boolean startedInAir = isSpaceHoldStartedInAir(serverPlayer);
        setLastSpaceHoldTicks(serverPlayer, holdTicks);
        setSpaceHoldTicks(serverPlayer, 0);
        setSpaceHoldStartedInAir(serverPlayer, false);

        boolean wasControlledFlightActive = isControlledFlightActive(serverPlayer);
        if (wasControlledFlightActive) {
            stopControlledFlight(serverPlayer);
            return;
        }
        if (serverPlayer.isCreative() || serverPlayer.isSpectator()) {
            return;
        }
        if (holdTicks >= FLY_START_HOLD_TICKS) {
            return;
        }
        if (!startedInAir) {
            return;
        }
        if (!canTriggerDoubleJump(serverPlayer)) {
            return;
        }

        performDoubleJump(serverPlayer);
    }

    public static void clearRuntimeState(Entity entity) {
        if (entity == null) {
            return;
        }
        if (entity instanceof ServerPlayer serverPlayer && isControlledFlightActive(serverPlayer)) {
            stopControlledFlight(serverPlayer);
        } else {
            entity.getPersistentData().putBoolean(KEY_CONTROLLED_FLIGHT_ACTIVE, false);
        }
        setSpaceHoldTicks(entity, 0);
        setSpaceHoldStartedInAir(entity, false);
        setLastSpaceHoldTicks(entity, 0);
    }

    public static boolean isControlledFlightActive(Entity entity) {
        return entity != null && entity.getPersistentData().getBoolean(KEY_CONTROLLED_FLIGHT_ACTIVE);
    }

    private static boolean canTriggerDoubleJump(ServerPlayer player) {
        return !player.onGround() && player.hasEffect(JujutsucraftModMobEffects.DOUBLE_JUMP_EFFECT.get());
    }

    private static boolean shouldCompensateFlyEffectDuration(ServerPlayer player) {
        if (!player.hasEffect(JujutsucraftModMobEffects.FLY_EFFECT.get())) {
            return false;
        }
        if (player.getAbilities().flying) {
            return false;
        }
        if (player.onGround() || player.isInWater()) {
            return false;
        }
        return !shouldRecoverFlyEffectUpstream(player);
    }

    private static boolean shouldRecoverFlyEffectUpstream(ServerPlayer player) {
        return (player.onGround() || player.isInWater()) && getCooldownBackStepAmplifier(player) < 5;
    }

    private static int getCooldownBackStepAmplifier(ServerPlayer player) {
        MobEffectInstance cooldownBackStep = player.getEffect(JujutsucraftModMobEffects.COOLDOWN_TIME_BACK_STEP.get());
        return cooldownBackStep == null ? -1 : cooldownBackStep.getAmplifier();
    }

    private static void compensateFlyEffectDuration(ServerPlayer player) {
        MobEffectInstance flyEffect = player.getEffect(JujutsucraftModMobEffects.FLY_EFFECT.get());
        if (flyEffect == null) {
            return;
        }
        player.addEffect(
            new MobEffectInstance(
                flyEffect.getEffect(),
                flyEffect.getDuration() + 1,
                flyEffect.getAmplifier(),
                flyEffect.isAmbient(),
                flyEffect.isVisible(),
                flyEffect.showIcon()
            )
        );
    }

    private static void performDoubleJump(ServerPlayer player) {
        MobEffectInstance doubleJump = player.getEffect(JujutsucraftModMobEffects.DOUBLE_JUMP_EFFECT.get());
        if (doubleJump == null) {
            return;
        }

        int amplifier = doubleJump.getAmplifier();
        int duration = doubleJump.getDuration();
        player.removeEffect(JujutsucraftModMobEffects.DOUBLE_JUMP_EFFECT.get());
        if (amplifier >= 1) {
            player.addEffect(new MobEffectInstance(JujutsucraftModMobEffects.DOUBLE_JUMP_EFFECT.get(), duration, amplifier - 1, false, false));
        }

        Vec3 lookAngle = player.getLookAngle();
        boolean movingForward = player.getPersistentData().getBoolean(KEY_MOVE_FORWARD);
        boolean movingBack = player.getPersistentData().getBoolean(KEY_MOVE_BACK);
        Vec3 jumpVelocity = resolveDoubleJumpVelocity(lookAngle, movingForward, movingBack);
        EntityVectorProcedure.execute(player, jumpVelocity.x, jumpVelocity.y, jumpVelocity.z);
        player.fallDistance = 0.0F;
        player.hasImpulse = true;

        SoundEvent soundEvent = ForgeRegistries.SOUND_EVENTS.getValue(GLASS_FALL_SOUND_ID);
        if (soundEvent != null) {
            for (int i = 0; i < 3; i++) {
                player.level().playSound(null, BlockPos.containing(player.getX(), player.getY(), player.getZ()), soundEvent, SoundSource.NEUTRAL, 1.0F, 1.0F);
            }
        }
        if (player.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles((SimpleParticleType) JujutsucraftModParticleTypes.PARTICLE_SHOCK_HIT.get(), player.getX(), player.getY(), player.getZ(), 0, 0.0, 0.0, 0.0, 0.0);
        }

        ObservedDoubleJumpUnlockService.observeDoubleJump(player);
    }

    static Vec3 resolveDoubleJumpVelocity(Vec3 lookAngle, boolean movingForward, boolean movingBack) {
        if (lookAngle == null) {
            return new Vec3(0.0D, DOUBLE_JUMP_SPEED, 0.0D);
        }
        if (!movingForward && !movingBack) {
            return new Vec3(0.0D, DOUBLE_JUMP_SPEED, 0.0D);
        }
        int direction = movingBack ? -1 : 1;
        return new Vec3(
            lookAngle.x * DOUBLE_JUMP_SPEED * direction,
            lookAngle.y * DOUBLE_JUMP_SPEED,
            lookAngle.z * DOUBLE_JUMP_SPEED * direction
        );
    }

    private static void startControlledFlight(ServerPlayer player) {
        player.getAbilities().mayfly = true;
        player.getAbilities().flying = true;
        player.onUpdateAbilities();
        player.fallDistance = 0.0F;
        player.getPersistentData().putBoolean(KEY_CONTROLLED_FLIGHT_ACTIVE, true);
    }

    private static void stopControlledFlight(ServerPlayer player) {
        player.getPersistentData().putBoolean(KEY_CONTROLLED_FLIGHT_ACTIVE, false);
        if (player.isCreative() || player.isSpectator()) {
            return;
        }
        player.getAbilities().flying = false;
        player.getAbilities().mayfly = false;
        player.onUpdateAbilities();
        player.fallDistance = 0.0F;
    }

    private static boolean isSpaceHeld(Entity entity) {
        return entity != null && entity.getPersistentData().getBoolean(KEY_SPACE_HELD);
    }

    private static int getSpaceHoldTicks(Entity entity) {
        return entity == null ? 0 : entity.getPersistentData().getInt(KEY_SPACE_HOLD_TICKS);
    }

    private static void setSpaceHoldTicks(Entity entity, int ticks) {
        if (entity != null) {
            entity.getPersistentData().putInt(KEY_SPACE_HOLD_TICKS, Math.max(0, ticks));
        }
    }

    private static boolean isSpaceHoldStartedInAir(Entity entity) {
        return entity != null && entity.getPersistentData().getBoolean(KEY_SPACE_HOLD_STARTED_IN_AIR);
    }

    private static void setSpaceHoldStartedInAir(Entity entity, boolean startedInAir) {
        if (entity != null) {
            entity.getPersistentData().putBoolean(KEY_SPACE_HOLD_STARTED_IN_AIR, startedInAir);
        }
    }

    private static void setLastSpaceHoldTicks(Entity entity, int ticks) {
        if (entity != null) {
            entity.getPersistentData().putInt(KEY_LAST_SPACE_HOLD_TICKS, Math.max(0, ticks));
        }
    }
}
