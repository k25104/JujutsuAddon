package com.arf8vhg7.jja.feature.jja.technique.shared.selection;

import com.arf8vhg7.jja.compat.jujutsucraft.JjaJujutsucraftCompat;
import com.arf8vhg7.jja.feature.jja.resource.ce.CEColorService;
import com.arf8vhg7.jja.feature.player.state.JjaPlayerStateSync;
import com.arf8vhg7.jja.feature.player.state.PlayerStateAccess;
import com.arf8vhg7.jja.feature.player.state.model.PlayerAddonStatsState;
import net.mcreator.jujutsucraft.network.JujutsucraftModVariables;
import net.minecraft.server.level.ServerPlayer;

final class ForceRandomCtChangerCeColorService {
    private ForceRandomCtChangerCeColorService() {
    }

    static void apply(ServerPlayer player) {
        PlayerAddonStatsState addonStats = PlayerStateAccess.addonStats(player);
        if (addonStats == null) {
            return;
        }

        if (!shouldAssignRandomColor(player)) {
            addonStats.clearCeColorOverride();
            JjaPlayerStateSync.sync(player);
            return;
        }

        addonStats.setCeColorOverride(CEColorService.randomColorId());
        JjaPlayerStateSync.sync(player);
    }

    private static boolean shouldAssignRandomColor(ServerPlayer player) {
        JujutsucraftModVariables.PlayerVariables playerVariables = JjaJujutsucraftCompat.jjaGetPlayerVariables(player);
        if (playerVariables == null) {
            return false;
        }
        return CEColorService.allowsOverride(player);
    }
}
