package com.arf8vhg7.jja.mixin.jujutsucraft;

import com.arf8vhg7.jja.hook.jujutsucraft.CuriosEquipmentReadProceduresHook;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.mcreator.jujutsucraft.procedures.AIBlueProcedure;
import net.mcreator.jujutsucraft.procedures.AITornadeProcedure;
import net.mcreator.jujutsucraft.procedures.AntiInfinityProcedure;
import net.mcreator.jujutsucraft.procedures.CanSeeSukunaSlashProcedure;
import net.mcreator.jujutsucraft.procedures.LogicAttackDomainProcedure;
import net.mcreator.jujutsucraft.procedures.LogicSwordProcedure;
import net.mcreator.jujutsucraft.procedures.TechniqueBluePunchProcedure;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(
    value = {
        AIBlueProcedure.class,
        AITornadeProcedure.class,
        AntiInfinityProcedure.class,
        CanSeeSukunaSlashProcedure.class,
        LogicAttackDomainProcedure.class,
        LogicSwordProcedure.class,
        TechniqueBluePunchProcedure.class
    },
    remap = false
)
public abstract class CuriosEquipmentReadProceduresMixin {
    @WrapOperation(
        method = "execute",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/LivingEntity;m_6844_(Lnet/minecraft/world/entity/EquipmentSlot;)Lnet/minecraft/world/item/ItemStack;"
        ),
        remap = false,
        require = 1
    )
    private static ItemStack jja$resolveCuriosEquipmentRead(
        LivingEntity livingEntity,
        EquipmentSlot equipmentSlot,
        Operation<ItemStack> original
    ) {
        return CuriosEquipmentReadProceduresHook.resolveEquipmentRead(livingEntity, equipmentSlot, original.call(livingEntity, equipmentSlot));
    }
}
