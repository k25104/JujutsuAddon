package com.arf8vhg7.jja.hook.jujutsucraft;

import com.arf8vhg7.jja.feature.equipment.curios.CuriosEquipmentReadService;
import com.arf8vhg7.jja.feature.jja.domain.da.DomainAmplificationWitnessService;
import com.arf8vhg7.jja.feature.jja.domain.da.DisableUnstableDuringDA;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public final class DomainAmplificationOnEffectActiveTickProcedureHook {
    private DomainAmplificationOnEffectActiveTickProcedureHook() {
    }

    public static int modifyTickInterval(int original) {
        return 1;
    }

    public static double modifyCursePowerDrain(double original) {
        return 1.0;
    }

    public static void onActiveTick(Entity entity) {
        DomainAmplificationWitnessService.witness(entity);
    }

    public static boolean addEffect(LivingEntity livingEntity, MobEffectInstance effectInstance) {
        return DisableUnstableDuringDA.addEffect(livingEntity, effectInstance);
    }

    public static ItemStack resolveEquipmentRead(LivingEntity livingEntity, EquipmentSlot equipmentSlot, ItemStack original) {
        return CuriosEquipmentReadService.resolveEquipmentRead(livingEntity, equipmentSlot, original);
    }
}
