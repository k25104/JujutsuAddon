package com.arf8vhg7.jja.feature.jja.technique.shared.selection;

import net.mcreator.jujutsucraft.JujutsucraftMod;
import net.mcreator.jujutsucraft.procedures.SelectRandomProcedure;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

public final class ForceRandomCtChangerService {
    static final int DEFERRED_SELECTION_DELAY_TICKS = 1;

    private ForceRandomCtChangerService() {
    }

    public static boolean handle(Entity entity) {
        if (!(entity instanceof ServerPlayer player)) {
            return false;
        }

        JujutsucraftMod.queueServerWork(DEFERRED_SELECTION_DELAY_TICKS, () -> {
            if (!shouldCompleteDeferredSelection(player.isAlive(), player.isRemoved())) {
                return;
            }
            SelectRandomProcedure.execute(player.level(), player.getX(), player.getY(), player.getZ(), player);
            ForceRandomCtChangerCeColorService.apply(player);
        });
        return true;
    }

    static boolean shouldCompleteDeferredSelection(boolean isAlive, boolean isRemoved) {
        return isAlive && !isRemoved;
    }
}
