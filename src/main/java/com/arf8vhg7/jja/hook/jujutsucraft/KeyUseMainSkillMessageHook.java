package com.arf8vhg7.jja.hook.jujutsucraft;

import com.arf8vhg7.jja.feature.jja.technique.shared.slot.RegisteredCurseTechniqueSlots;
import com.arf8vhg7.jja.feature.player.state.PlayerStateAccess;
import com.arf8vhg7.jja.feature.player.state.model.PlayerSkillState;
import net.minecraft.world.entity.player.Player;

public final class KeyUseMainSkillMessageHook {
    private KeyUseMainSkillMessageHook() {
    }

    public static boolean handlePress(Player player, int pressedSlot) {
        PlayerSkillState skillState = PlayerStateAccess.skill(player);
        if (skillState == null) {
            return true;
        }
        skillState.setPressedSlot(pressedSlot);
        if (skillState.isPressSkillRegistrationToggle() && pressedSlot != 0) {
            RegisteredCurseTechniqueSlots.save(player, pressedSlot);
            skillState.setPressedSlot(0);
            return false;
        }
        return true;
    }
}
