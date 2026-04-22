package com.arf8vhg7.jja.hook.jujutsucraft;

import com.arf8vhg7.jja.feature.jja.technique.family.gojo.GojoProgressionService;
import com.arf8vhg7.jja.feature.jja.technique.family.megumi.MegumiShadowService;
import com.arf8vhg7.jja.feature.player.state.PlayerStateAccess;
import com.arf8vhg7.jja.feature.player.state.model.PlayerSkillState;
import net.minecraft.world.entity.Entity;

public final class KeyStartTechniqueOnKeyReleasedProcedureHook {
    private KeyStartTechniqueOnKeyReleasedProcedureHook() {
    }

    public static void clearPressedSlot(Entity entity) {
        if (entity == null) {
            return;
        }
        GojoProgressionService.onTeleportKeyReleased(entity);
        MegumiShadowService.releaseShadowTechnique(entity);
        PlayerSkillState skillState = PlayerStateAccess.skill(entity);
        if (skillState != null) {
            skillState.setPressedSlot(0);
        }
    }
}
