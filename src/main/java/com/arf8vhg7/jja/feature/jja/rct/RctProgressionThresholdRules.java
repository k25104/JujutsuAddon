package com.arf8vhg7.jja.feature.jja.rct;

import com.arf8vhg7.jja.compat.jujutsucraft.JjaJujutsucraftCompat;
import net.mcreator.jujutsucraft.init.JujutsucraftModMobEffects;
import net.mcreator.jujutsucraft.network.JujutsucraftModVariables;
import net.minecraft.server.level.ServerPlayer;

public final class RctProgressionThresholdRules {

    private static final int HIGURUMA_TECHNIQUE_ID = 27;

    private RctProgressionThresholdRules() {
    }

    public static double resolveThreshold(ServerPlayer player, int jujutsuUpgradeDifficulty) {
        if (player == null) {
            return resolveThreshold(jujutsuUpgradeDifficulty, false, false);
        }

        JujutsucraftModVariables.PlayerVariables variables = JjaJujutsucraftCompat.jjaGetPlayerVariables(player);
        boolean hasHiguruma = hasHigurumaTechnique(variables);
        boolean hasSixEyes = player.hasEffect(JujutsucraftModMobEffects.SIX_EYES.get());
        return resolveThreshold(jujutsuUpgradeDifficulty, hasHiguruma, hasSixEyes);
    }

    static double resolveThreshold(int jujutsuUpgradeDifficulty, boolean hasHiguruma, boolean hasSixEyes) {
        double threshold = 100.0D * (1.0D + jujutsuUpgradeDifficulty / 10.0D);
        if (hasHiguruma) {
            threshold /= 10.0D;
        }
        if (hasSixEyes) {
            threshold /= 10.0D;
        }
        return threshold;
    }

    static boolean hasHigurumaTechnique(JujutsucraftModVariables.PlayerVariables variables) {
        if (variables == null) {
            return false;
        }
        return hasHigurumaTechnique(variables.PlayerCurseTechnique, variables.PlayerCurseTechnique2);
    }

    static boolean hasHigurumaTechnique(double primaryTechniqueValue, double secondaryTechniqueValue) {
        int primaryTechnique = (int) Math.round(primaryTechniqueValue);
        int secondaryTechnique = (int) Math.round(secondaryTechniqueValue);
        return primaryTechnique == HIGURUMA_TECHNIQUE_ID || secondaryTechnique == HIGURUMA_TECHNIQUE_ID;
    }
}
