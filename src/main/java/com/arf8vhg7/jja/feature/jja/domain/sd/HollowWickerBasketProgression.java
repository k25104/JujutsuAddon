package com.arf8vhg7.jja.feature.jja.domain.sd;

import com.arf8vhg7.jja.feature.jja.domain.AntiDomainProgressionConfig;
import com.arf8vhg7.jja.compat.jujutsucraft.JjaJujutsucraftCompat;
import com.arf8vhg7.jja.util.JjaAdvancementHelper;
import java.util.Objects;
import javax.annotation.Nullable;
import net.mcreator.jujutsucraft.init.JujutsucraftModGameRules;
import net.mcreator.jujutsucraft.network.JujutsucraftModVariables;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameRules;

public final class HollowWickerBasketProgression {
    public static final ResourceLocation MASTERY_ID = ResourceLocation.fromNamespaceAndPath("jja", "mastery_hollow_wicker_basket");
    public static final ResourceLocation UPSTREAM_SIMPLE_DOMAIN_MASTERY_ID = ResourceLocation.fromNamespaceAndPath(
        "jujutsucraft",
        "mastery_simple_domain"
    );
    private static final double SPECIAL_CT_ID = 27.0D;
    private static final GameRules.Key<GameRules.IntegerValue> JUJUTSU_UPGRADE_DIFFICULTY_RULE = Objects.requireNonNull(
        JujutsucraftModGameRules.JUJUTSUUPGRADEDIFFICULTY
    );

    private HollowWickerBasketProgression() {
    }

    static boolean shouldAwardSimpleDomainTechniqueProgress(boolean sdItemOnly) {
        return !sdItemOnly;
    }

    public static void awardFromTechniqueUsage(@Nullable ServerPlayer player) {
        if (player == null) {
            return;
        }

        @Nullable JujutsucraftModVariables.PlayerVariables variables = JjaJujutsucraftCompat.jjaGetPlayerVariables(player);
        if (variables == null) {
            return;
        }

        double difficultyScale = 1.0D + player.level().getGameRules().getInt(JUJUTSU_UPGRADE_DIFFICULTY_RULE) / 10.0D;
        boolean specialThresholds = Double.compare(variables.PlayerCurseTechnique, SPECIAL_CT_ID) == 0
            || Double.compare(variables.PlayerCurseTechnique2, SPECIAL_CT_ID) == 0;
        if (variables.PlayerTechniqueUsedNumber > (specialThresholds ? 2000.0D : 4000.0D) * difficultyScale) {
            if (shouldAwardSimpleDomainTechniqueProgress(AntiDomainProgressionConfig.isSdItemOnly())) {
                JjaAdvancementHelper.award(player, UPSTREAM_SIMPLE_DOMAIN_MASTERY_ID);
            } else {
                JjaAdvancementHelper.award(player, MASTERY_ID);
            }
        }
    }
}
