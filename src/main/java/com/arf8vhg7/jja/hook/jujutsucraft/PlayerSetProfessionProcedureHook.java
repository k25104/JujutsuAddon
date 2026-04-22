package com.arf8vhg7.jja.hook.jujutsucraft;

import com.arf8vhg7.jja.feature.jja.technique.shared.selection.ForceRandomProfessionFlagService;
import com.arf8vhg7.jja.feature.jja.technique.shared.selection.RandomCtSelectionService;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

public final class PlayerSetProfessionProcedureHook {
    private PlayerSetProfessionProcedureHook() {
    }

    public static void applySelectableRandomProfession(Entity entity) {
        try {
            if (RandomCtSelectionService.isSelectableRandomSelectionPending(entity) && entity instanceof ServerPlayer player) {
                ForceRandomProfessionFlagService.handle(player);
            }
        } finally {
            RandomCtSelectionService.finishSelectableRandomSelection(entity);
        }
    }
}
