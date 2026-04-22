package com.arf8vhg7.jja.feature.jja.technique.family.mahoraga;

import com.arf8vhg7.jja.JujutsuAddon;
import com.arf8vhg7.jja.feature.equipment.curios.CuriosEquipmentReadService;
import com.arf8vhg7.jja.feature.player.revive.ReviveFlowService;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(modid = JujutsuAddon.MODID)
public final class MahoragaWheelDeathReset {
    private MahoragaWheelDeathReset() {
    }

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof ServerPlayer player && ReviveFlowService.canEnterWaiting(player)) {
            return;
        }
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        resetAll(player);
    }

    public static void resetAll(Player player) {
        if (player == null) {
            return;
        }
        resetAll(player.getInventory());
        CuriosEquipmentReadService.visitManagedCuriosStacks(player, MahoragaAdaptation::clearHelmet);
    }

    public static void resetAll(Inventory inventory) {
        for (ItemStack stack : inventory.items) {
            MahoragaAdaptation.clearHelmet(stack);
        }
        for (ItemStack stack : inventory.armor) {
            MahoragaAdaptation.clearHelmet(stack);
        }
        for (ItemStack stack : inventory.offhand) {
            MahoragaAdaptation.clearHelmet(stack);
        }
    }
}
