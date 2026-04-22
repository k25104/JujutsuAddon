package com.arf8vhg7.jja.feature.jja.technique.shared.activation;

import com.arf8vhg7.jja.util.JjaItemUseHelper;
import net.minecraft.world.entity.player.Player;

public final class JjaTechniqueUseDenialHelper {
    private JjaTechniqueUseDenialHelper() {
    }

    public static boolean deny(Player player, boolean resetPressZ, boolean actionBar) {
        if (player == null) {
            return false;
        }
        if (resetPressZ) {
            player.getPersistentData().putBoolean("PRESS_Z", false);
        }
        JjaItemUseHelper.displayDontUse(player, actionBar);
        return false;
    }
}
