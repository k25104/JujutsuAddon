package com.arf8vhg7.jja.feature.jja.technique.shared.slot;

import com.arf8vhg7.jja.compat.jujutsucraft.JjaJujutsucraftCompat;
import com.arf8vhg7.jja.feature.jja.technique.shared.selection.JjaAddonTechniqueSelectionCatalog;
import com.arf8vhg7.jja.feature.jja.technique.shared.selection.TechniqueSelectionCandidate;
import com.arf8vhg7.jja.feature.player.state.JjaPlayerStateSync;
import com.arf8vhg7.jja.feature.player.state.PlayerStateAccess;
import com.arf8vhg7.jja.feature.player.state.model.PlayerSkillState;
import net.mcreator.jujutsucraft.network.JujutsucraftModVariables;
import net.minecraft.advancements.Advancement;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import java.util.Objects;

public final class RegisteredCurseTechniqueSlots {
    private static final int GETO_CT_ID = 18;
    private static final int MEGUMI_CT_ID = 6;
    private static final String SHIFT_NUE_TECHNIQUE_NAME = "entity.jujutsucraft.nue_totality";
    private static final String SHIFT_MAX_ELEPHANT_TECHNIQUE_NAME = "jujutsu.technique.choso3";
    private static final String SHIFT_MAHORAGA_TECHNIQUE_NAME = "item.jujutsucraft.mahoraga_wheel_helmet";
    private static final String NUE_TECHNIQUE_NAME = "entity.jujutsucraft.nue";
    private static final String MAX_ELEPHANT_TECHNIQUE_NAME = "entity.jujutsucraft.max_elephant";
    private static final String MAHORAGA_TECHNIQUE_NAME = "entity.jujutsucraft.eight_handled_sword_divergent_sila_divine_general_mahoraga";

    private RegisteredCurseTechniqueSlots() {
    }

    public static boolean save(Player player, int slot) {
        if (player == null || slot <= 0 || slot > 10) {
            return false;
        }
        JujutsucraftModVariables.PlayerVariables playerVars = JjaJujutsucraftCompat.jjaGetPlayerVariables(player);
        if (playerVars == null) {
            return false;
        }
        return save(player, slot, (int) Math.round(playerVars.PlayerSelectCurseTechnique), playerVars.PlayerSelectCurseTechniqueName);
    }

    public static boolean save(Player player, int slot, int registeredValue, String registeredTechniqueName) {
        if (player == null || slot <= 0 || slot > 10) {
            return false;
        }
        PlayerSkillState skillState = PlayerStateAccess.skill(player);
        if (skillState == null) {
            return false;
        }
        JujutsucraftModVariables.PlayerVariables playerVars = JjaJujutsucraftCompat.jjaGetPlayerVariables(player);
        if (playerVars == null) {
            return false;
        }
        int ctId = JjaJujutsucraftCompat.jjaGetActiveCurseTechniqueId(playerVars);
        String storedTechniqueName = registeredTechniqueName == null ? "" : registeredTechniqueName;
        if (ctId == GETO_CT_ID) {
            storedTechniqueName = normalizeGetoTechniqueName(storedTechniqueName);
        }
        if (registeredValue <= 0 || storedTechniqueName.isEmpty()) {
            skillState.clearRegisteredCurseTechnique(slot, ctId);
            skillState.clearRegisteredCurseTechniqueName(slot, ctId);
            JjaPlayerStateSync.sync(player);
            return true;
        }
        skillState.setRegisteredCurseTechnique(slot, ctId, registeredValue);
        skillState.setRegisteredCurseTechniqueName(slot, ctId, storedTechniqueName);
        JjaPlayerStateSync.sync(player);
        return true;
    }

    public static SelectOverride resolveSelectOverride(Entity entity, double currentCt) {
        PlayerSkillState skillState = PlayerStateAccess.skill(entity);
        if (skillState == null) {
            return SelectOverride.none();
        }
        int slot = skillState.getPressedSlot();
        if (slot == 0) {
            return SelectOverride.none();
        }
        if (slot < 0 || slot > 10) {
            return SelectOverride.cancel();
        }
        int ctId = toCtId(currentCt);
        if (!skillState.hasRegisteredCurseTechnique(slot, ctId)) {
            return SelectOverride.cancel();
        }
        int registeredValue = skillState.getRegisteredCurseTechnique(slot, ctId);
        int selectedTechnique = registeredValue % 100;
        if (entity.isShiftKeyDown()
            && (selectedTechnique == 11 || selectedTechnique == 18)
            && isUntamedMegumiSelection(entity, ctId, selectedTechnique)) {
            return SelectOverride.cancel();
        }
        return SelectOverride.apply(selectedTechnique);
    }

    public static TechniqueSelectionCandidate resolveAddonCandidateForSelection(double currentCt, double selectedTechnique) {
        int ctId = toCtId(currentCt);
        int selectTechniqueId = ((int) Math.round(selectedTechnique)) % 100;
        return JjaAddonTechniqueSelectionCatalog.resolveCandidate(ctId, selectTechniqueId);
    }

    public static String resolveRegisteredTechniqueName(Entity entity, String currentName) {
        if (entity == null) {
            return currentName;
        }
        PlayerSkillState skillState = PlayerStateAccess.skill(entity);
        if (skillState == null) {
            return currentName;
        }
        int slot = skillState.getPressedSlot();
        if (slot == 0 || slot < 0 || slot > 10) {
            return currentName;
        }
        JujutsucraftModVariables.PlayerVariables playerVars = JjaJujutsucraftCompat.jjaGetPlayerVariables(entity);
        if (playerVars == null) {
            return currentName;
        }
        int ctId = JjaJujutsucraftCompat.jjaGetActiveCurseTechniqueId(playerVars);
        if (!skillState.hasRegisteredCurseTechnique(slot, ctId)) {
            return currentName;
        }
        String name = skillState.getRegisteredCurseTechniqueName(slot, ctId);
        if (name == null || name.isEmpty()) {
            return currentName;
        }
        boolean untamedMegumiSelection = isUntamedMegumiSelection(
            entity,
            ctId,
            (int) Math.round(playerVars.PlayerSelectCurseTechnique)
        );
        return resolveMegumiTechniqueName(name, entity.isShiftKeyDown(), untamedMegumiSelection);
    }

    public static String resolveGetoRegisteredTechniqueName(Entity entity) {
        if (entity == null) {
            return null;
        }
        PlayerSkillState skillState = PlayerStateAccess.skill(entity);
        if (skillState == null) {
            return null;
        }
        int slot = skillState.getPressedSlot();
        if (slot < 1 || slot > 10) {
            return null;
        }
        JujutsucraftModVariables.PlayerVariables playerVars = JjaJujutsucraftCompat.jjaGetPlayerVariables(entity);
        if (playerVars == null) {
            return null;
        }
        int ctId = JjaJujutsucraftCompat.jjaGetActiveCurseTechniqueId(playerVars);
        if (ctId != GETO_CT_ID) {
            return null;
        }
        if (!skillState.hasRegisteredCurseTechnique(slot, ctId)) {
            return null;
        }
        String name = skillState.getRegisteredCurseTechniqueName(slot, ctId);
        name = normalizeGetoTechniqueName(name);
        return name == null || name.isEmpty() ? null : name;
    }

    public static boolean hasGetoCountSuffix(String name) {
        return getGetoCountSuffixIndex(name) >= 0;
    }

    public static String normalizeGetoTechniqueName(String name) {
        int suffixIndex = getGetoCountSuffixIndex(name);
        return suffixIndex >= 0 ? name.substring(0, suffixIndex) : name;
    }

    private static int toCtId(double ctId) {
        return (int) Math.round(ctId);
    }

    static String resolveMegumiTechniqueName(String name, boolean shiftDown, boolean untamedSelection) {
        String normalizedName = normalizeMegumiTechniqueName(name);
        if (untamedSelection) {
            return normalizedName;
        }
        if (shiftDown) {
            if (NUE_TECHNIQUE_NAME.equals(normalizedName)) {
                return SHIFT_NUE_TECHNIQUE_NAME;
            }
            if (MAX_ELEPHANT_TECHNIQUE_NAME.equals(normalizedName)) {
                return SHIFT_MAX_ELEPHANT_TECHNIQUE_NAME;
            }
            if (MAHORAGA_TECHNIQUE_NAME.equals(normalizedName)) {
                return SHIFT_MAHORAGA_TECHNIQUE_NAME;
            }
        }
        return normalizedName;
    }

    private static boolean isUntamedMegumiSelection(Entity entity, int ctId, int selectedTechnique) {
        if (entity == null || ctId != MEGUMI_CT_ID || selectedTechnique <= 0) {
            return false;
        }
        int skillId = ctId * 100 + selectedTechnique;
        return switch (skillId) {
            case 608 -> !isTamed(entity, 4, "skill_nue");
            case 609 -> !isTamed(entity, 5, "skill_great_serpent");
            case 610 -> !isTamed(entity, 6, "skill_toad");
            case 611 -> !isTamed(entity, 7, "skill_max_elephant");
            case 612 -> !isTamed(entity, 8, "skill_rabbit_escape");
            case 613 -> !isTamed(entity, 9, "skill_round_deer");
            case 614 -> !isTamed(entity, 10, "skill_piercing_ox");
            case 615 -> !isTamed(entity, 11, "skill_tiger_funeral");
            case 618 -> !isTamed(entity, 14, "skill_mahoraga");
            default -> false;
        };
    }

    private static boolean isTamed(Entity entity, int techniqueIndex, String advancementPath) {
        return entity.getPersistentData().getDouble("TenShadowsTechnique" + techniqueIndex) == 1.0
            || hasAdvancement(entity, advancementPath);
    }

    private static boolean hasAdvancement(Entity entity, String advancementPath) {
        if (!(entity instanceof ServerPlayer player)) {
            return false;
        }
        Advancement advancement = player.server.getAdvancements().getAdvancement(
            Objects.requireNonNull(ResourceLocation.fromNamespaceAndPath("jujutsucraft", Objects.requireNonNull(advancementPath, "advancementPath")))
        );
        return advancement != null && player.getAdvancements().getOrStartProgress(advancement).isDone();
    }

    private static String normalizeMegumiTechniqueName(String name) {
        if (SHIFT_NUE_TECHNIQUE_NAME.equals(name)) {
            return NUE_TECHNIQUE_NAME;
        }
        if (SHIFT_MAX_ELEPHANT_TECHNIQUE_NAME.equals(name)) {
            return MAX_ELEPHANT_TECHNIQUE_NAME;
        }
        if (SHIFT_MAHORAGA_TECHNIQUE_NAME.equals(name)) {
            return MAHORAGA_TECHNIQUE_NAME;
        }
        return name;
    }

    private static int getGetoCountSuffixIndex(String name) {
        if (name == null || name.isEmpty()) {
            return -1;
        }
        int suffixIndex = name.lastIndexOf(" ×");
        if (suffixIndex < 0 || suffixIndex + 2 >= name.length()) {
            return -1;
        }
        for (int index = suffixIndex + 2; index < name.length(); index++) {
            if (!Character.isDigit(name.charAt(index))) {
                return -1;
            }
        }
        return suffixIndex;
    }

    public enum SelectOverrideType {
        NONE,
        APPLY,
        CANCEL
    }

    public record SelectOverride(SelectOverrideType type, double select) {
        public static SelectOverride none() {
            return new SelectOverride(SelectOverrideType.NONE, 0.0);
        }

        public static SelectOverride apply(double select) {
            return new SelectOverride(SelectOverrideType.APPLY, select);
        }

        public static SelectOverride cancel() {
            return new SelectOverride(SelectOverrideType.CANCEL, 0.0);
        }
    }
}
