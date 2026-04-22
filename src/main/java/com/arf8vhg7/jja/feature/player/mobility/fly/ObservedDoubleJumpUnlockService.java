package com.arf8vhg7.jja.feature.player.mobility.fly;

import com.arf8vhg7.jja.compat.jujutsucraft.JjaJujutsucraftCompat;
import com.arf8vhg7.jja.feature.player.progression.grade.SorcererGradeAdvancementHelper;
import com.arf8vhg7.jja.feature.player.progression.grade.SorcererGradeTier;
import com.arf8vhg7.jja.feature.player.progression.witness.NearbyPlayerWitnessService;
import com.arf8vhg7.jja.feature.player.state.PlayerStateAccess;
import com.arf8vhg7.jja.feature.player.state.model.PlayerAddonStatsState;
import javax.annotation.Nullable;
import net.mcreator.jujutsucraft.init.JujutsucraftModMobEffects;
import net.mcreator.jujutsucraft.network.JujutsucraftModVariables;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;

public final class ObservedDoubleJumpUnlockService {
    private static final int HIGURUMA_TECHNIQUE_ID = 27;
    private static final int HIGURUMA_DOUBLE_JUMP_AMPLIFIER = 4;
    private static final int EFFECT_DURATION = -1;

    private ObservedDoubleJumpUnlockService() {
    }

    public static void observeDoubleJump(@Nullable Entity source) {
        observe(source);
    }

    public static void observeHigurumaBarrierStart(@Nullable Entity source) {
        if (!(source instanceof ServerPlayer player) || !isCurrentHiguruma(JjaJujutsucraftCompat.jjaGetPlayerVariables(player))) {
            return;
        }
        observe(source);
    }

    public static boolean debugObserve(@Nullable ServerPlayer player) {
        if (player == null || !canUnlock(player)) {
            return false;
        }
        unlock(PlayerStateAccess.addonStats(player));
        applyUnlockedEffect(player);
        return true;
    }

    public static boolean hasUnlock(@Nullable ServerPlayer player) {
        PlayerAddonStatsState addonStats = PlayerStateAccess.addonStats(player);
        return addonStats != null && addonStats.isObservedDoubleJumpUnlock();
    }

    public static void applyUnlockedEffect(@Nullable Entity entity) {
        if (!(entity instanceof ServerPlayer player) || !player.onGround()) {
            return;
        }

        Integer desiredAmplifier = resolveDesiredAmplifier(player);
        if (desiredAmplifier == null) {
            return;
        }

        MobEffectInstance currentEffect = player.getEffect(JujutsucraftModMobEffects.DOUBLE_JUMP_EFFECT.get());
        int currentAmplifier = currentEffect == null ? -1 : currentEffect.getAmplifier();
        int currentDuration = currentEffect == null ? 0 : currentEffect.getDuration();
        if (!shouldRefreshEffect(currentAmplifier, currentDuration, desiredAmplifier)) {
            return;
        }

        player.addEffect(new MobEffectInstance(JujutsucraftModMobEffects.DOUBLE_JUMP_EFFECT.get(), EFFECT_DURATION, desiredAmplifier, false, false));
    }

    static boolean canUnlock(boolean currentHiguruma, boolean hasGrade1, boolean hasSpecial1) {
        return currentHiguruma ? hasGrade1 : hasSpecial1;
    }

    static boolean isCurrentHiguruma(@Nullable JujutsucraftModVariables.PlayerVariables variables) {
        if (variables == null) {
            return false;
        }
        return Math.round(variables.PlayerCurseTechnique) == HIGURUMA_TECHNIQUE_ID
            || Math.round(variables.PlayerCurseTechnique2) == HIGURUMA_TECHNIQUE_ID;
    }

    static int resolveSpecialTierAmplifier(@Nullable SorcererGradeTier tier) {
        if (tier == SorcererGradeTier.SPECIAL_1) {
            return 0;
        }
        if (tier == SorcererGradeTier.SPECIAL_2) {
            return 1;
        }
        if (tier == SorcererGradeTier.SPECIAL_3) {
            return 2;
        }
        if (tier == SorcererGradeTier.SPECIAL_4) {
            return 3;
        }
        if (tier == SorcererGradeTier.SPECIAL_5) {
            return 4;
        }
        return -1;
    }

    static boolean shouldRefreshEffect(int currentAmplifier, int currentDuration, int desiredAmplifier) {
        if (currentAmplifier < desiredAmplifier) {
            return true;
        }
        if (currentAmplifier > desiredAmplifier) {
            return false;
        }
        return currentDuration != EFFECT_DURATION;
    }

    static void unlock(@Nullable PlayerAddonStatsState addonStats) {
        if (addonStats != null) {
            addonStats.setObservedDoubleJumpUnlock(true);
        }
    }

    @Nullable
    static Integer resolveDesiredAmplifier(
        boolean unlocked,
        boolean currentHiguruma,
        boolean hasGrade1,
        @Nullable SorcererGradeTier highestTier
    ) {
        if (!unlocked) {
            return null;
        }
        if (currentHiguruma && hasGrade1) {
            return HIGURUMA_DOUBLE_JUMP_AMPLIFIER;
        }

        int amplifier = resolveSpecialTierAmplifier(highestTier);
        return amplifier >= 0 ? amplifier : null;
    }

    private static void observe(@Nullable Entity source) {
        if (source == null) {
            return;
        }

        NearbyPlayerWitnessService.forEachNearbyServerPlayer(source, player -> {
            if (canUnlock(player)) {
                unlock(PlayerStateAccess.addonStats(player));
            }
        });
    }

    private static boolean canUnlock(ServerPlayer player) {
        return canUnlock(
            isCurrentHiguruma(JjaJujutsucraftCompat.jjaGetPlayerVariables(player)),
            com.arf8vhg7.jja.util.JjaAdvancementHelper.has(player, SorcererGradeTier.GRADE_1.advancementId()),
            com.arf8vhg7.jja.util.JjaAdvancementHelper.has(player, SorcererGradeTier.SPECIAL_1.advancementId())
        );
    }

    @Nullable
    private static Integer resolveDesiredAmplifier(ServerPlayer player) {
        PlayerAddonStatsState addonStats = PlayerStateAccess.addonStats(player);
        return resolveDesiredAmplifier(
            addonStats != null && addonStats.isObservedDoubleJumpUnlock(),
            isCurrentHiguruma(JjaJujutsucraftCompat.jjaGetPlayerVariables(player)),
            com.arf8vhg7.jja.util.JjaAdvancementHelper.has(player, SorcererGradeTier.GRADE_1.advancementId()),
            SorcererGradeAdvancementHelper.findHighestTier(player)
        );
    }
}
