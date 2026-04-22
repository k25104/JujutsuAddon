package com.arf8vhg7.jja.feature.jja.domain.sd;

import com.arf8vhg7.jja.feature.player.progression.witness.NearbyPlayerWitnessService;
import com.arf8vhg7.jja.util.JjaAdvancementHelper;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

public final class SimpleDomainWitnessService {
    public static final ResourceLocation MASTERY_SIMPLE_DOMAIN_ID = ResourceLocation.fromNamespaceAndPath("jujutsucraft", "mastery_simple_domain");
    static final ResourceLocation SORCERER_GRADE_SPECIAL_ID = ResourceLocation.fromNamespaceAndPath("jujutsucraft", "sorcerer_grade_special");

    private SimpleDomainWitnessService() {
    }

    public static void witness(@Nullable Entity source) {
        if (source == null) {
            return;
        }

        NearbyPlayerWitnessService.forEachNearbyServerPlayer(source, player -> {
            if (canUnlock(player)) {
                JjaAdvancementHelper.award(player, MASTERY_SIMPLE_DOMAIN_ID);
            }
        });
    }

    static boolean canUnlock(boolean hasSpecialGrade) {
        return hasSpecialGrade;
    }

    private static boolean canUnlock(ServerPlayer player) {
        return canUnlock(JjaAdvancementHelper.has(player, SORCERER_GRADE_SPECIAL_ID));
    }
}
