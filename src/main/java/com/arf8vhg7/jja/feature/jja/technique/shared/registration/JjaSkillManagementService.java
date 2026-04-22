package com.arf8vhg7.jja.feature.jja.technique.shared.registration;

import com.arf8vhg7.jja.compat.jujutsucraft.JjaJujutsucraftCompat;
import com.arf8vhg7.jja.feature.player.state.PlayerStateAccess;
import com.arf8vhg7.jja.feature.player.state.JjaPlayerStateSync;
import com.arf8vhg7.jja.feature.player.state.model.PlayerSkillState;
import java.util.Collection;
import net.mcreator.jujutsucraft.network.JujutsucraftModVariables;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

public final class JjaSkillManagementService {
    private JjaSkillManagementService() {
    }

    public static boolean isHiddenCandidate(Entity entity, int curseTechniqueId, String canonicalName) {
        if (entity == null || curseTechniqueId == 0 || canonicalName == null || canonicalName.isEmpty()) {
            return false;
        }
        JujutsucraftModVariables.PlayerVariables playerVars = JjaJujutsucraftCompat.jjaGetPlayerVariables(entity);
        if (playerVars != null && playerVars.use_mainSkill) {
            return false;
        }
        PlayerSkillState skillState = PlayerStateAccess.skill(entity);
        if (skillState == null) {
            return false;
        }
        boolean manuallyHidden = skillState.isHiddenCurseTechniqueName(curseTechniqueId, canonicalName);
        boolean hiddenBySlotRegistration = !skillState.isPressSkillRegistrationToggle()
            && skillState.hasRegisteredCurseTechniqueName(curseTechniqueId, canonicalName);
        return manuallyHidden || hiddenBySlotRegistration;
    }

    public static ToggleResult toggleHiddenSkills(ServerPlayer player, Collection<String> canonicalNames) {
        if (player == null || canonicalNames == null || canonicalNames.isEmpty()) {
            return ToggleResult.noop();
        }
        PlayerSkillState skillState = PlayerStateAccess.skill(player);
        int curseTechniqueId = JjaJujutsucraftCompat.jjaGetActiveCurseTechniqueId(player);
        if (skillState == null || curseTechniqueId == 0) {
            return ToggleResult.noop();
        }
        boolean hide = canonicalNames.stream().anyMatch(name -> !skillState.isHiddenCurseTechniqueName(curseTechniqueId, name));
        boolean changed = false;
        for (String canonicalName : canonicalNames) {
            if (canonicalName == null || canonicalName.isEmpty()) {
                continue;
            }
            boolean currentHidden = skillState.isHiddenCurseTechniqueName(curseTechniqueId, canonicalName);
            if (currentHidden == hide) {
                continue;
            }
            skillState.setHiddenCurseTechniqueName(curseTechniqueId, canonicalName, hide);
            changed = true;
        }
        if (changed) {
            JjaPlayerStateSync.sync(player);
        }
        return new ToggleResult(hide, changed);
    }

    public record ToggleResult(boolean hidden, boolean changed) {
        public static ToggleResult noop() {
            return new ToggleResult(false, false);
        }
    }
}
