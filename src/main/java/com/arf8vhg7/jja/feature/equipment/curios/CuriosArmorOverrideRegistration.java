package com.arf8vhg7.jja.feature.equipment.curios;

import com.arf8vhg7.jja.JujutsuAddon;
import com.arf8vhg7.jja.compat.curios.JjaCuriosCompat;
import com.arf8vhg7.jja.compat.curios.JjaCuriosItemCompat;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@EventBusSubscriber(modid = JujutsuAddon.MODID, bus = Bus.MOD)
public final class CuriosArmorOverrideRegistration {
    private CuriosArmorOverrideRegistration() {
    }

    @SubscribeEvent
    public static void onCommonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            if (!JjaCuriosCompat.isCuriosLoaded()) {
                return;
            }
            CuriosEquipmentReadService.forEachArmorOverrideItem(item ->
                JjaCuriosItemCompat.registerCurio(
                    item,
                    (livingEntity, identifier, slotUuid, stack) ->
                        CuriosArmorOverrideService.buildCuriosAttributeModifiers(identifier, slotUuid, stack)
                )
            );
        });
    }
}
