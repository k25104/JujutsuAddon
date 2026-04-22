package com.arf8vhg7.jja.hook.jujutsucraft;

import com.arf8vhg7.jja.compat.jujutsucraft.JjaJujutsucraftCompat;
import net.mcreator.jujutsucraft.network.JujutsucraftModVariables;
import net.minecraft.world.item.context.UseOnContext;

public final class NyoiStaffItemHook {
    private static final double KASHIMO_TECHNIQUE_ID = 7.0D;

    private NyoiStaffItemHook() {
    }

    public static boolean shouldPlace(UseOnContext context) {
        if (context == null || context.getPlayer() == null) {
            return false;
        }
        JujutsucraftModVariables.PlayerVariables playerVariables = JjaJujutsucraftCompat.jjaGetPlayerVariables(context.getPlayer());
        return isKashimoTechnique(
            playerVariables != null ? playerVariables.PlayerCurseTechnique : 0.0D,
            playerVariables != null ? playerVariables.PlayerCurseTechnique2 : 0.0D
        );
    }

    static boolean isKashimoTechnique(double playerCurseTechnique, double playerCurseTechnique2) {
        return playerCurseTechnique == KASHIMO_TECHNIQUE_ID || playerCurseTechnique2 == KASHIMO_TECHNIQUE_ID;
    }
}
