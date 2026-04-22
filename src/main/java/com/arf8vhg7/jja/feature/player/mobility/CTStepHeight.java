package com.arf8vhg7.jja.feature.player.mobility;

import com.arf8vhg7.jja.compat.jujutsucraft.JjaJujutsucraftCompat;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public final class CTStepHeight {
    private CTStepHeight() {
    }

    public static void apply(Entity entity) {
        if (!(entity instanceof Player player)) {
            return;
        }
        var playerVars = JjaJujutsucraftCompat.jjaGetPlayerVariables(player);
        if (playerVars == null) {
            return;
        }
        if (playerVars.PlayerCurseTechnique != 0.0 && player.tickCount % 10 == 0) {
            if (player.isSprinting()) {
                player.setMaxUpStep(1.1F);
            } else {
                player.setMaxUpStep(0.6F);
            }
        }
    }
}
