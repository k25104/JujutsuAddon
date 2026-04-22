package com.arf8vhg7.jja.feature.jja.technique.family.mahoraga;

import com.arf8vhg7.jja.JujutsuAddon;
import com.arf8vhg7.jja.compat.pehkui.JjaPehkuiCompat;
import com.arf8vhg7.jja.feature.equipment.curios.CuriosEquipmentReadService;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(modid = JujutsuAddon.MODID)
public final class MahoragaPehkuiScaleEvents {
    private static final String KEY_SCALE_CONTROLLED = "jja_mahoraga_pehkui_scale_controlled";
    private static final String KEY_TO_LIVING = "toLiving";
    private static final float DEFAULT_SCALE = 1.0F;

    private MahoragaPehkuiScaleEvents() {
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !(event.player instanceof ServerPlayer player) || !JjaPehkuiCompat.isPehkuiLoaded()) {
            return;
        }

        CompoundTag data = player.getPersistentData();
        ItemStack helmet = resolveManagedHelmet(player);
        if (!shouldManageScale(player, helmet)) {
            resetControlledScale(player, data);
            return;
        }

        float targetScale = resolveTargetScale(helmet);
        boolean scaleControlled = data.getBoolean(KEY_SCALE_CONTROLLED);
        if (!scaleControlled) {
            if (shouldRestoreControlledScale(scaleControlled, targetScale)) {
                applyControlledScale(player, data, targetScale);
            }
            return;
        }

        if (!hasControlledScaleTarget(targetScale)) {
            resetControlledScale(player, data);
            return;
        }

        clampControlledScale(player, targetScale);
    }

    static void applyStageScale(Entity entity, CompoundTag helmetTag) {
        if (!(entity instanceof ServerPlayer player) || helmetTag == null || !JjaPehkuiCompat.isPehkuiLoaded()) {
            return;
        }

        float targetScale = resolveTargetScale(helmetTag);
        applyControlledScale(player, player.getPersistentData(), targetScale);
    }

    static boolean shouldRestoreControlledScale(boolean scaleControlled, float targetScale) {
        return !scaleControlled && hasControlledScaleTarget(targetScale);
    }

    private static void resetControlledScale(ServerPlayer player, CompoundTag data) {
        // Only undo scales that this feature previously controlled.
        if (!data.getBoolean(KEY_SCALE_CONTROLLED)) {
            return;
        }
        JjaPehkuiCompat.resetBaseScale(player);
        data.remove(KEY_SCALE_CONTROLLED);
    }

    private static void applyControlledScale(ServerPlayer player, CompoundTag data, float targetScale) {
        if (!hasControlledScaleTarget(targetScale)) {
            resetControlledScale(player, data);
            return;
        }

        JjaPehkuiCompat.setBaseScale(player, targetScale);
        data.putBoolean(KEY_SCALE_CONTROLLED, true);
    }

    private static ItemStack resolveManagedHelmet(ServerPlayer player) {
        return CuriosEquipmentReadService.resolveEquipmentRead(player, EquipmentSlot.HEAD, player.getItemBySlot(EquipmentSlot.HEAD));
    }

    private static boolean shouldManageScale(ServerPlayer player, ItemStack helmet) {
        return MahoragaAdaptation.isMahoragaUser(player) && MahoragaAdaptation.isMahoragaHelmet(helmet);
    }

    private static boolean hasControlledScaleTarget(float targetScale) {
        return targetScale > DEFAULT_SCALE;
    }

    private static void clampControlledScale(ServerPlayer player, float targetScale) {
        if (JjaPehkuiCompat.getBaseScale(player) > targetScale) {
            JjaPehkuiCompat.setBaseScale(player, targetScale);
        }
    }

    private static float resolveTargetScale(ItemStack helmet) {
        return resolveTargetScale(helmet.getOrCreateTag());
    }

    private static float resolveTargetScale(CompoundTag helmetTag) {
        int stage = (int) Math.floor(Math.max(helmetTag.getDouble(KEY_TO_LIVING), 0.0D) / 100.0D);
        return DEFAULT_SCALE + 0.1F * stage;
    }
}
