package com.arf8vhg7.jja.feature.jja.domain.de;

import com.arf8vhg7.jja.compat.jujutsucraft.JjaJujutsucraftCompat;
import com.arf8vhg7.jja.util.JjaAdvancementHelper;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.mcreator.jujutsucraft.init.JujutsucraftModGameRules;
import net.mcreator.jujutsucraft.network.JujutsucraftModVariables;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameRules;

public final class DomainExpansionProgression {
    public static final ResourceLocation MASTERY_ID = ResourceLocation.fromNamespaceAndPath(
        "jujutsucraft",
        "mastery_domain_expansion"
    );
    private static final double SPECIAL_CT_ID = 27.0D;
    private static final GameRules.Key<GameRules.IntegerValue> JUJUTSU_UPGRADE_DIFFICULTY_RULE = Objects.requireNonNull(
        JujutsucraftModGameRules.JUJUTSUUPGRADEDIFFICULTY
    );

    private DomainExpansionProgression() {
    }

    public static void awardFromTechniqueUsage(@Nullable ServerPlayer player) {
        if (player == null) {
            return;
        }

        @Nullable JujutsucraftModVariables.PlayerVariables variables = JjaJujutsucraftCompat.jjaGetPlayerVariables(player);
        if (variables == null) {
            return;
        }

        double difficultyScale = 1.0D + player.level().getGameRules().getInt(difficultyRule()) / 10.0D;
        boolean specialThresholds = Double.compare(variables.PlayerCurseTechnique, SPECIAL_CT_ID) == 0
            || Double.compare(variables.PlayerCurseTechnique2, SPECIAL_CT_ID) == 0;
        if (variables.PlayerTechniqueUsedNumber > (specialThresholds ? 100.0D : 12000.0D) * difficultyScale) {
            JjaAdvancementHelper.award(player, MASTERY_ID);
        }
    }

    private static @Nonnull GameRules.Key<GameRules.IntegerValue> difficultyRule() {
        return Objects.requireNonNull(JUJUTSU_UPGRADE_DIFFICULTY_RULE);
    }
}
