package com.arf8vhg7.jja.util;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.level.ServerPlayer;

public final class JjaAdvancementHelper {
    private JjaAdvancementHelper() {
    }

    public static Advancement get(ServerPlayer player, ResourceLocation advancementId) {
        if (player == null || player.server == null || advancementId == null) {
            return null;
        }
        return player.server.getAdvancements().getAdvancement(advancementId);
    }

    public static boolean has(ServerPlayer player, ResourceLocation advancementId) {
        Advancement advancement = get(player, advancementId);
        return advancement != null && player.getAdvancements().getOrStartProgress(advancement).isDone();
    }

    public static boolean award(ServerPlayer player, ResourceLocation advancementId) {
        Advancement advancement = get(player, advancementId);
        if (advancement == null) {
            return false;
        }
        PlayerAdvancements playerAdvancements = player.getAdvancements();
        AdvancementProgress progress = playerAdvancements.getOrStartProgress(advancement);
        if (progress.isDone()) {
            return false;
        }
        boolean changed = false;
        for (String criterion : progress.getRemainingCriteria()) {
            changed |= playerAdvancements.award(advancement, criterion);
        }
        return changed;
    }

    public static boolean revoke(ServerPlayer player, ResourceLocation advancementId) {
        Advancement advancement = get(player, advancementId);
        if (advancement == null) {
            return false;
        }
        PlayerAdvancements playerAdvancements = player.getAdvancements();
        AdvancementProgress progress = playerAdvancements.getOrStartProgress(advancement);
        if (!progress.getCompletedCriteria().iterator().hasNext()) {
            return false;
        }
        boolean changed = false;
        for (String criterion : progress.getCompletedCriteria()) {
            changed |= playerAdvancements.revoke(advancement, criterion);
        }
        return changed;
    }
}
