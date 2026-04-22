package com.arf8vhg7.jja.feature.jja.domain.da;

import com.arf8vhg7.jja.compat.jujutsucraft.JjaJujutsucraftCompat;
import com.arf8vhg7.jja.feature.jja.domain.AntiDomainProgressionConfig;
import com.arf8vhg7.jja.feature.player.progression.witness.NearbyPlayerWitnessService;
import com.arf8vhg7.jja.util.JjaAdvancementHelper;
import javax.annotation.Nullable;
import net.mcreator.jujutsucraft.init.JujutsucraftModMobEffects;
import net.mcreator.jujutsucraft.network.JujutsucraftModVariables;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public final class DomainAmplificationWitnessService {
    public static final ResourceLocation MASTERY_DOMAIN_AMPLIFICATION_ID = ResourceLocation.fromNamespaceAndPath(
        "jujutsucraft",
        "mastery_domain_amplification"
    );
    static final ResourceLocation MASTERY_DOMAIN_EXPANSION_ID = ResourceLocation.fromNamespaceAndPath(
        "jujutsucraft",
        "mastery_domain_expansion"
    );
    static final ResourceLocation SORCERER_GRADE_SPECIAL_ID = ResourceLocation.fromNamespaceAndPath("jujutsucraft", "sorcerer_grade_special");
    private static final int HIGURUMA_TECHNIQUE_ID = 27;

    private DomainAmplificationWitnessService() {
    }

    public static void witness(@Nullable Entity source) {
        if (!(source instanceof LivingEntity livingEntity) || !livingEntity.hasEffect(JujutsucraftModMobEffects.DOMAIN_AMPLIFICATION.get())) {
            return;
        }

        NearbyPlayerWitnessService.forEachNearbyServerPlayer(source, player -> {
            if (canUnlock(player)) {
                JjaAdvancementHelper.award(player, MASTERY_DOMAIN_AMPLIFICATION_ID);
            }
        });
    }

    public static boolean shouldSuppressLegacyGrant() {
        return AntiDomainProgressionConfig.isDaItemOnly();
    }

    static boolean canUnlock(boolean hasDomainExpansionMastery, boolean hasSpecialGrade, boolean currentHiguruma) {
        return hasDomainExpansionMastery && (hasSpecialGrade || currentHiguruma);
    }

    static boolean isCurrentHiguruma(@Nullable JujutsucraftModVariables.PlayerVariables variables) {
        return variables != null && isCurrentHiguruma(variables.PlayerCurseTechnique, variables.PlayerCurseTechnique2);
    }

    static boolean isCurrentHiguruma(double primaryTechnique, double secondaryTechnique) {
        return Math.round(primaryTechnique) == HIGURUMA_TECHNIQUE_ID || Math.round(secondaryTechnique) == HIGURUMA_TECHNIQUE_ID;
    }

    private static boolean canUnlock(ServerPlayer player) {
        return canUnlock(
            JjaAdvancementHelper.has(player, MASTERY_DOMAIN_EXPANSION_ID),
            JjaAdvancementHelper.has(player, SORCERER_GRADE_SPECIAL_ID),
            isCurrentHiguruma(JjaJujutsucraftCompat.jjaGetPlayerVariables(player))
        );
    }
}
