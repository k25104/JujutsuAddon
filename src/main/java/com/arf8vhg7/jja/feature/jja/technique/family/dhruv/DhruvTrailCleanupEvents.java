package com.arf8vhg7.jja.feature.jja.technique.family.dhruv;

import com.arf8vhg7.jja.JujutsuAddon;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.event.entity.EntityLeaveLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(modid = JujutsuAddon.MODID)
public final class DhruvTrailCleanupEvents {
    private DhruvTrailCleanupEvents() {
    }

    @SubscribeEvent
    public static void onEntityLeaveLevel(EntityLeaveLevelEvent event) {
        if (!(event.getLevel() instanceof ServerLevel level) || !DhruvTrailBlockService.shouldHandleCleanupOnLeave(event.getEntity())) {
            return;
        }
        DhruvTrailBlockService.cleanupTrackedTrailBlocks(level, event.getEntity());
    }
}
