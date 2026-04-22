package com.arf8vhg7.jja.feature.jja.technique.family.okkotsu;

import com.arf8vhg7.jja.compat.jujutsucraft.JjaJujutsucraftCompat;
import com.arf8vhg7.jja.feature.jja.technique.shared.activation.TechniqueSkillResolver;
import com.arf8vhg7.jja.util.JjaAdvancementHelper;
import javax.annotation.Nullable;
import net.mcreator.jujutsucraft.entity.Rika2Entity;
import net.mcreator.jujutsucraft.entity.RikaEntity;
import net.mcreator.jujutsucraft.init.JujutsucraftModItems;
import net.mcreator.jujutsucraft.network.JujutsucraftModVariables;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public final class OkkotsuRikaRules {
    public static final ResourceLocation SKILL_CURSE_IS_LIFTED_ID = ResourceLocation.fromNamespaceAndPath(
        "jujutsucraft",
        "skill_curseis_lifted"
    );
    public static final ResourceLocation SKILL_RIKA_CONTROL_ID = ResourceLocation.fromNamespaceAndPath(
        "jujutsucraft",
        "skill_rika_control"
    );
    static final int OKKOTSU_TECHNIQUE_ID = 5;
    static final String JJA_PRESERVE_COPIED_TECHNIQUE_KEY = "jja_preserve_copied_technique";

    private OkkotsuRikaRules() {
    }

    public static void markCopiedTechniqueUseForPreservation(@Nullable Entity entity, @Nullable ItemStack itemStack) {
        if (!(entity instanceof ServerPlayer serverPlayer) || !shouldMarkCopiedTechniqueUse(entity, itemStack, serverPlayer)) {
            return;
        }
        itemStack.getOrCreateTag().putBoolean(JJA_PRESERVE_COPIED_TECHNIQUE_KEY, true);
    }

    public static boolean consumeCopiedTechniquePreserveMarker(@Nullable ItemStack itemStack) {
        MarkerConsumptionResult result = resolveCopiedTechniqueConsumption(hasCopiedTechniquePreserveMarker(itemStack));
        if (result.clearMarker()) {
            clearCopiedTechniquePreserveMarker(itemStack);
        }
        return result.skipShrink();
    }

    public static void clearStaleCopiedTechniquePreserveMarker(@Nullable ItemStack itemStack) {
        if (shouldClearCopiedTechniquePreserveMarker(hasCopiedTechniquePreserveMarker(itemStack), isCopiedTechniqueMarkedUsed(itemStack))) {
            clearCopiedTechniquePreserveMarker(itemStack);
        }
    }

    public static void queueFullCursePowerRecoveryOnRikaSummon(@Nullable Entity owner, @Nullable Entity summoned) {
        if (!(owner instanceof ServerPlayer serverPlayer) || !isRikaSummonEntity(summoned)) {
            return;
        }
        JujutsucraftModVariables.PlayerVariables playerVariables = JjaJujutsucraftCompat.jjaGetPlayerVariables(owner);
        if (playerVariables == null || !shouldQueueFullCursePowerRecoveryOnRikaSummon(isOkkotsu(serverPlayer), hasAdvancement(serverPlayer, SKILL_RIKA_CONTROL_ID))) {
            return;
        }
        playerVariables.PlayerCursePowerChange = resolveFullRecoveryQueuedChange(
            playerVariables.PlayerCursePower,
            playerVariables.PlayerCursePowerMAX
        );
        playerVariables.syncPlayerVariables(owner);
    }

    static boolean shouldMarkCopiedTechniqueUse(
        boolean successfulUse,
        boolean okkotsu,
        boolean curseIsLifted,
        boolean copiedTechnique,
        boolean preserveMarker
    ) {
        return successfulUse && okkotsu && !curseIsLifted && copiedTechnique && !preserveMarker;
    }

    static MarkerConsumptionResult resolveCopiedTechniqueConsumption(boolean preserveMarker) {
        return preserveMarker ? MarkerConsumptionResult.SKIP_AND_CLEAR : MarkerConsumptionResult.KEEP;
    }

    static boolean shouldClearCopiedTechniquePreserveMarker(boolean preserveMarker, boolean used) {
        return preserveMarker && !used;
    }

    static boolean shouldQueueFullCursePowerRecoveryOnRikaSummon(boolean okkotsu, boolean hasRikaControl) {
        return okkotsu && hasRikaControl;
    }

    static double resolveFullRecoveryQueuedChange(double currentCursePower, double maxCursePower) {
        return Math.max(maxCursePower - currentCursePower, 0.0D);
    }

    private static boolean shouldMarkCopiedTechniqueUse(Entity entity, ItemStack itemStack, ServerPlayer player) {
        return shouldMarkCopiedTechniqueUse(
            entity.getPersistentData().getBoolean("PRESS_Z") && isCopiedTechniqueMarkedUsed(itemStack),
            isOkkotsu(player),
            hasAdvancement(player, SKILL_CURSE_IS_LIFTED_ID),
            isCopiedTechniqueItem(itemStack),
            hasCopiedTechniquePreserveMarker(itemStack)
        );
    }

    private static boolean isOkkotsu(Player player) {
        return TechniqueSkillResolver.hasTechnique(player, OKKOTSU_TECHNIQUE_ID);
    }

    private static boolean hasAdvancement(Player player, ResourceLocation advancementId) {
        return player instanceof ServerPlayer serverPlayer && JjaAdvancementHelper.has(serverPlayer, advancementId);
    }

    private static boolean isRikaSummonEntity(@Nullable Entity entity) {
        return entity instanceof RikaEntity || entity instanceof Rika2Entity;
    }

    private static boolean isCopiedTechniqueItem(@Nullable ItemStack itemStack) {
        return itemStack != null
            && !itemStack.isEmpty()
            && itemStack.getItem() == JujutsucraftModItems.COPIED_CURSED_TECHNIQUE.get();
    }

    private static boolean isCopiedTechniqueMarkedUsed(@Nullable ItemStack itemStack) {
        return itemStack != null && !itemStack.isEmpty() && itemStack.hasTag() && itemStack.getOrCreateTag().getBoolean("Used");
    }

    private static boolean hasCopiedTechniquePreserveMarker(@Nullable ItemStack itemStack) {
        return itemStack != null
            && !itemStack.isEmpty()
            && itemStack.hasTag()
            && itemStack.getOrCreateTag().getBoolean(JJA_PRESERVE_COPIED_TECHNIQUE_KEY);
    }

    private static void clearCopiedTechniquePreserveMarker(@Nullable ItemStack itemStack) {
        if (itemStack == null || itemStack.isEmpty() || !itemStack.hasTag()) {
            return;
        }
        CompoundTag tag = itemStack.getTag();
        if (tag != null) {
            tag.remove(JJA_PRESERVE_COPIED_TECHNIQUE_KEY);
        }
    }

    static record MarkerConsumptionResult(boolean skipShrink, boolean clearMarker) {
        private static final MarkerConsumptionResult KEEP = new MarkerConsumptionResult(false, false);
        private static final MarkerConsumptionResult SKIP_AND_CLEAR = new MarkerConsumptionResult(true, true);
    }
}
