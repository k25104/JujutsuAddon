package com.arf8vhg7.jja.feature.jja.domain.de;

import com.arf8vhg7.jja.feature.player.progression.witness.NearbyPlayerWitnessService;
import com.arf8vhg7.jja.feature.player.state.PlayerStateAccess;
import com.arf8vhg7.jja.feature.player.state.model.PlayerAddonStatsState;
import com.arf8vhg7.jja.util.JjaAdvancementHelper;
import javax.annotation.Nullable;
import net.mcreator.jujutsucraft.init.JujutsucraftModMobEffects;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public final class OpenBarrierMasteryReservationService {
    public static final ResourceLocation MASTERY_OPEN_BARRIER_ID = ResourceLocation.fromNamespaceAndPath(
        "jujutsucraft",
        "mastery_open_barrier_type_domain"
    );
    private static final double RANDOM_GRANT_THRESHOLD = 0.01D;
    private static final int OPEN_BARRIER_AMPLIFIER_THRESHOLD = 1;
    private static final int DE_USED_IMMEDIATE_THRESHOLD = 100;

    private OpenBarrierMasteryReservationService() {
    }

    public static void observeNearbyOpenBarrier(@Nullable Entity source) {
        if (!(source instanceof LivingEntity livingEntity) || !hasOpenBarrierAmplifier(livingEntity.getEffect(JujutsucraftModMobEffects.DOMAIN_EXPANSION.get()))) {
            return;
        }

        NearbyPlayerWitnessService.forEachNearbyServerPlayer(source, player -> {
            if (hasPending(player)) {
                award(player);
            }
        });
    }

    public static void clearPendingIfAwarded(@Nullable ServerPlayer player, @Nullable ResourceLocation advancementId) {
        if (player != null && MASTERY_OPEN_BARRIER_ID.equals(advancementId)) {
            clearPending(player);
        }
    }

    public static void maybeAwardFromDeUsed(@Nullable Entity entity) {
        if (!(entity instanceof ServerPlayer player) || !shouldImmediatelyAwardByDeUsed(player)) {
            return;
        }
        award(player);
    }

    public static double resolveReservedRandomRoll(double roll, @Nullable Entity entity) {
        if (!(entity instanceof ServerPlayer player) || !shouldReserveNonItemGrantRoll(roll)) {
            return roll;
        }
        reservePending(player);
        return 1.0D;
    }

    public static boolean hasPending(@Nullable ServerPlayer player) {
        PlayerAddonStatsState addonStats = PlayerStateAccess.addonStats(player);
        return addonStats != null && addonStats.isPendingOpenBarrierMastery();
    }

    public static boolean hasAwarded(@Nullable ServerPlayer player) {
        return player != null && JjaAdvancementHelper.has(player, MASTERY_OPEN_BARRIER_ID);
    }

    public static boolean debugReservePending(@Nullable ServerPlayer player) {
        if (player == null || hasAwarded(player)) {
            clearPending(player);
            return false;
        }
        reservePending(player);
        return hasPending(player);
    }

    static boolean shouldReserveNonItemGrantRoll(double roll) {
        return roll < RANDOM_GRANT_THRESHOLD;
    }

    static boolean hasOpenBarrierAmplifier(@Nullable MobEffectInstance effectInstance) {
        return effectInstance != null && effectInstance.getAmplifier() >= OPEN_BARRIER_AMPLIFIER_THRESHOLD;
    }

    static boolean shouldImmediatelyAwardByDeUsed(int deUsed) {
        return deUsed > DE_USED_IMMEDIATE_THRESHOLD;
    }

    static boolean shouldImmediatelyAwardByDeUsed(@Nullable ServerPlayer player) {
        PlayerAddonStatsState addonStats = PlayerStateAccess.addonStats(player);
        return addonStats != null && shouldImmediatelyAwardByDeUsed(addonStats.getDeUsed());
    }

    static void reservePending(@Nullable PlayerAddonStatsState addonStats) {
        if (addonStats != null) {
            addonStats.setPendingOpenBarrierMastery(true);
        }
    }

    static void clearPending(@Nullable PlayerAddonStatsState addonStats) {
        if (addonStats != null) {
            addonStats.setPendingOpenBarrierMastery(false);
        }
    }

    private static void reservePending(@Nullable ServerPlayer player) {
        if (player == null || JjaAdvancementHelper.has(player, MASTERY_OPEN_BARRIER_ID)) {
            clearPending(player);
            return;
        }
        reservePending(PlayerStateAccess.addonStats(player));
    }

    private static void clearPending(@Nullable ServerPlayer player) {
        clearPending(PlayerStateAccess.addonStats(player));
    }

    private static void award(ServerPlayer player) {
        if (JjaAdvancementHelper.award(player, MASTERY_OPEN_BARRIER_ID) || JjaAdvancementHelper.has(player, MASTERY_OPEN_BARRIER_ID)) {
            clearPending(player);
        }
    }
}
