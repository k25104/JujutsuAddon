package com.arf8vhg7.jja.feature.jja.technique.shared.activation;

import com.arf8vhg7.jja.feature.player.progression.grade.SorcererGradeTier;
import com.arf8vhg7.jja.feature.player.progression.witness.NearbyPlayerWitnessService;
import com.arf8vhg7.jja.util.JjaAdvancementHelper;
import javax.annotation.Nullable;
import net.mcreator.jujutsucraft.init.JujutsucraftModMobEffects;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

public final class CutTheWorldWitnessService {
    public static final ResourceLocation CUT_THE_WORLD_ID = ResourceLocation.fromNamespaceAndPath(
        "jujutsucraft",
        "skill_dismantle_cut_the_world"
    );
    static final ResourceLocation SKILL_SUKUNA_ID = ResourceLocation.fromNamespaceAndPath("jujutsucraft", "skill_sukuna");

    private CutTheWorldWitnessService() {
    }

    public static void witness(@Nullable Entity source) {
        if (source == null) {
            return;
        }

        NearbyPlayerWitnessService.forEachNearbyServerPlayer(source, player -> {
            if (canUnlock(player)) {
                JjaAdvancementHelper.award(player, CUT_THE_WORLD_ID);
            }
        });
    }

    static boolean canUnlock(boolean hasSpecialFive, boolean hasSkillSukuna, boolean hasSukunaEffect) {
        return (hasSpecialFive && hasSkillSukuna) || hasSukunaEffect;
    }

    private static boolean canUnlock(ServerPlayer player) {
        return canUnlock(
            JjaAdvancementHelper.has(player, SorcererGradeTier.SPECIAL_5.advancementId()),
            JjaAdvancementHelper.has(player, SKILL_SUKUNA_ID),
            player.hasEffect(JujutsucraftModMobEffects.SUKUNA_EFFECT.get())
        );
    }
}
