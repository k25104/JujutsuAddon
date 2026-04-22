package com.arf8vhg7.jja.feature.jja.resource.ce;

import com.arf8vhg7.jja.feature.player.state.PlayerStateAccess;
import com.arf8vhg7.jja.feature.player.state.model.PlayerAddonStatsState;
import net.mcreator.jujutsucraft.network.JujutsucraftModVariables;
import net.minecraft.world.entity.Entity;

public final class CePowerPreservation {
    private CePowerPreservation() {
    }

    public static double normalizeCursePowerChange(
        Entity entity,
        JujutsucraftModVariables.PlayerVariables playerVars,
        double rawCursePowerChange,
        double healCursePower
    ) {
        if (playerVars == null) {
            return rawCursePowerChange + Math.round(healCursePower);
        }

        PlayerAddonStatsState addonStats = PlayerStateAccess.addonStats(entity);
        if (addonStats == null) {
            playerVars.PlayerCursePowerChange = 0.0;
            return rawCursePowerChange + Math.round(healCursePower);
        }

        double preservation = addonStats.getCursePowerPreservation();
        preservation += rawCursePowerChange;
        preservation += healCursePower / 10.0;

        double delta = 0.0;
        if (preservation >= 1.0) {
            delta = Math.floor(preservation);
            preservation -= delta;
        } else if (preservation <= -1.0) {
            delta = Math.ceil(preservation);
            preservation -= delta;
        }

        addonStats.setCursePowerPreservation(preservation);
        playerVars.PlayerCursePowerChange = 0.0;
        return delta;
    }
}
