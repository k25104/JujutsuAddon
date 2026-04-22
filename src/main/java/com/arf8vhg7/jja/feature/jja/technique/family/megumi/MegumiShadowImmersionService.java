package com.arf8vhg7.jja.feature.jja.technique.family.megumi;

import com.arf8vhg7.jja.feature.player.mobility.fly.FlySpaceControlService;
import java.util.Objects;
import javax.annotation.Nullable;
import net.mcreator.jujutsucraft.init.JujutsucraftModMobEffects;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public final class MegumiShadowImmersionService {
    private static final String KEY_FLIGHT_ACTIVE = "jjaMegumiShadowFlightActive";
    private static final String KEY_PREVIOUS_MAYFLY = "jjaMegumiShadowPreviousMayfly";
    private static final String KEY_PREVIOUS_FLYING = "jjaMegumiShadowPreviousFlying";

    private MegumiShadowImmersionService() {
    }

    public static void tick(ServerPlayer player) {
        if (player == null) {
            return;
        }

        boolean wasActive = isShadowImmersionActive(player);
        boolean feetInOwnerShadow = isOwnerShadowBlock(player.level(), player.blockPosition(), player);
        boolean headInOwnerShadow = isOwnerShadowBlock(player.level(), BlockPos.containing(player.getEyePosition()), player);
        boolean bodyInOwnerShadow = MegumiShadowService.isEntityInsideOwnedShadow(player.level(), player, player.getUUID());
        boolean standingOnOwnerShadowFloor = isOwnerShadowBlock(player.level(), player.blockPosition().below(), player);
        boolean shadowTechniqueHeld = MegumiShadowService.isShadowTechniqueHeld(player);
        boolean shouldBeActive = MegumiShadowImmersionRules.shouldRemainActive(
            wasActive,
            feetInOwnerShadow,
            headInOwnerShadow,
            bodyInOwnerShadow,
            standingOnOwnerShadowFloor
        );

        if (shouldBeActive != wasActive) {
            if (shouldBeActive) {
                enterShadowImmersion(player);
            } else {
                exitShadowImmersion(player);
            }
        }

        if (shouldBeActive && shadowTechniqueHeld && MegumiShadowImmersionRules.shouldRefreshShadowHoldEffects(player.level().getGameTime())) {
            refreshShadowHoldEffects(player);
        }

        if (shouldBeActive) {
            if (player.level() instanceof ServerLevel level) {
                MegumiShadowService.expandAroundOwner(level, player);
            }
            maintainFlightPrivilege(player);
        }
    }

    public static boolean isShadowImmersionActive(@Nullable Entity entity) {
        return entity != null && entity.getPersistentData().getBoolean(KEY_FLIGHT_ACTIVE);
    }

    public static boolean isShadowInvulnerable(@Nullable Entity entity) {
        return entity instanceof LivingEntity livingEntity
            && MegumiShadowImmersionRules.isShadowInvulnerabilityActive(
                isShadowImmersionActive(livingEntity),
                livingEntity.hasEffect(Objects.requireNonNull(JujutsucraftModMobEffects.NEUTRALIZATION.get()))
            );
    }

    public static void clearRuntimeState(@Nullable Entity entity) {
        if (entity == null) {
            return;
        }

        boolean wasActive = isShadowImmersionActive(entity);
        boolean previousMayfly = entity.getPersistentData().getBoolean(KEY_PREVIOUS_MAYFLY);
        boolean previousFlying = entity.getPersistentData().getBoolean(KEY_PREVIOUS_FLYING);
        entity.getPersistentData().putBoolean(KEY_FLIGHT_ACTIVE, false);
        entity.getPersistentData().remove(KEY_PREVIOUS_MAYFLY);
        entity.getPersistentData().remove(KEY_PREVIOUS_FLYING);

        if (entity instanceof ServerPlayer player) {
            if (wasActive && !player.isCreative() && !player.isSpectator() && !FlySpaceControlService.isControlledFlightActive(player)) {
                player.getAbilities().mayfly = previousMayfly;
                player.getAbilities().flying = previousFlying;
                player.onUpdateAbilities();
                player.fallDistance = 0.0F;
            }
        }
    }

    private static void enterShadowImmersion(ServerPlayer player) {
        player.getPersistentData().putBoolean(KEY_FLIGHT_ACTIVE, true);
        if (player.isCreative() || player.isSpectator()) {
            return;
        }

        boolean hadControlledFlight = FlySpaceControlService.isControlledFlightActive(player);
        player.getPersistentData().putBoolean(KEY_PREVIOUS_MAYFLY, player.getAbilities().mayfly && !hadControlledFlight);
        player.getPersistentData().putBoolean(KEY_PREVIOUS_FLYING, player.getAbilities().flying && !hadControlledFlight);

        player.getAbilities().mayfly = true;
        player.getAbilities().flying = true;
        player.onUpdateAbilities();
        player.fallDistance = 0.0F;
    }

    private static void exitShadowImmersion(ServerPlayer player) {
        boolean previousMayfly = player.getPersistentData().getBoolean(KEY_PREVIOUS_MAYFLY);
        boolean previousFlying = player.getPersistentData().getBoolean(KEY_PREVIOUS_FLYING);
        player.getPersistentData().putBoolean(KEY_FLIGHT_ACTIVE, false);
        player.getPersistentData().remove(KEY_PREVIOUS_MAYFLY);
        player.getPersistentData().remove(KEY_PREVIOUS_FLYING);

        if (player.isCreative() || player.isSpectator() || FlySpaceControlService.isControlledFlightActive(player)) {
            return;
        }

        player.getAbilities().mayfly = previousMayfly;
        player.getAbilities().flying = previousFlying;
        player.onUpdateAbilities();
        player.fallDistance = 0.0F;
    }

    private static boolean isOwnerShadowBlock(Level level, BlockPos pos, ServerPlayer player) {
        if (!level.getBlockState(pos).is(MegumiShadowBlocks.SHADOW_BLOCK.get())) {
            return false;
        }

        BlockEntity blockEntity = level.getBlockEntity(pos);
        return blockEntity instanceof ShadowBlockEntity shadowBlockEntity && shadowBlockEntity.isOwnedBy(player.getUUID());
    }

    private static void refreshShadowHoldEffects(ServerPlayer player) {
        refreshShadowHoldEffect(
            player,
            Objects.requireNonNull(JujutsucraftModMobEffects.COOLDOWN_TIME.get()),
            MegumiShadowRules.SHADOW_HOLD_EFFECT_DURATION_TICKS,
            MegumiShadowRules.SHADOW_HOLD_COOLDOWN_AMPLIFIER
        );
        refreshShadowHoldEffect(
            player,
            Objects.requireNonNull(JujutsucraftModMobEffects.COOLDOWN_TIME_COMBAT.get()),
            MegumiShadowRules.SHADOW_HOLD_EFFECT_DURATION_TICKS,
            MegumiShadowRules.SHADOW_HOLD_COOLDOWN_AMPLIFIER
        );
        refreshShadowHoldEffect(
            player,
            Objects.requireNonNull(JujutsucraftModMobEffects.UNSTABLE.get()),
            MegumiShadowRules.SHADOW_HOLD_EFFECT_DURATION_TICKS,
            MegumiShadowRules.SHADOW_HOLD_UNSTABLE_AMPLIFIER
        );
    }

    private static void refreshShadowHoldEffect(ServerPlayer player, MobEffect effect, int duration, int amplifier) {
        MobEffectInstance current = player.getEffect(effect);
        if (current != null && current.getDuration() > duration && current.getAmplifier() >= amplifier) {
            return;
        }

        int nextDuration = current != null && current.getAmplifier() < amplifier
            ? Math.max(current.getDuration(), duration)
            : duration;
        int nextAmplifier = current == null ? amplifier : Math.max(current.getAmplifier(), amplifier);
        player.addEffect(
            new MobEffectInstance(
                effect,
                nextDuration,
                nextAmplifier,
                false,
                false,
                false
            )
        );
    }

    private static void maintainFlightPrivilege(ServerPlayer player) {
        if (player.isCreative() || player.isSpectator()) {
            return;
        }

        if (!player.getAbilities().mayfly || !player.getAbilities().flying) {
            player.getAbilities().mayfly = true;
            player.getAbilities().flying = true;
            player.onUpdateAbilities();
        }
        player.fallDistance = 0.0F;
    }
}
