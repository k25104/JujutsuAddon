package com.arf8vhg7.jja.hook.jujutsucraft;

import com.arf8vhg7.jja.feature.jja.traits.twinnedbody.TwinnedBodyRuntimeStateAccess;
import com.arf8vhg7.jja.feature.jja.traits.twinnedbody.TwinnedBodySyncService;
import com.arf8vhg7.jja.feature.player.health.firstaid.FirstAidResetService;
import com.arf8vhg7.jja.util.JjaAdvancementHelper;
import java.util.Objects;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

public final class SelectedProcedureHook {
    private static final ResourceLocation TWINNED_BODY_ADVANCEMENT_ID = Objects.requireNonNull(
        ResourceLocation.fromNamespaceAndPath("jja", "twinned_body")
    );

    private SelectedProcedureHook() {
    }

    public static void beginFirstAidReset(Entity entity) {
        FirstAidResetService.beginResetTransaction(entity);
    }

    public static void finishFirstAidReset(Entity entity) {
        FirstAidResetService.finishResetTransaction(entity);
    }

    public static void applyTwinnedBodySelectionReward(Entity entity) {
        if (!(entity instanceof ServerPlayer player)) {
            return;
        }

        TwinnedBodyRuntimeStateAccess.clearTwinnedBody(player);
        JjaAdvancementHelper.revoke(player, TWINNED_BODY_ADVANCEMENT_ID);

        if (Math.random() < 0.01) {
            TwinnedBodyRuntimeStateAccess.markTwinnedBody(player);
            JjaAdvancementHelper.award(player, TWINNED_BODY_ADVANCEMENT_ID);
        }

        TwinnedBodySyncService.syncTrackingState(player);
    }
}
