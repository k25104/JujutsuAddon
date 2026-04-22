package com.arf8vhg7.jja.mixin.jujutsucraft;

import com.arf8vhg7.jja.hook.jujutsucraft.CursedTechniquePotionExpiresProcedureHook;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.mcreator.jujutsucraft.network.JujutsucraftModVariables;
import net.mcreator.jujutsucraft.procedures.CursedTechniquePotionExpiresProcedure;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.LazyOptional;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = CursedTechniquePotionExpiresProcedure.class, remap = false)
public abstract class CursedTechniquePotionExpiresProcedureMixin {
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
        return CursedTechniquePotionExpiresProcedureHook.resolveEquipmentRead(
            livingEntity,
            equipmentSlot,
            original.call(livingEntity, equipmentSlot)
        );
    }

    @WrapOperation(
        method = "execute",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraftforge/common/util/LazyOptional;orElse(Ljava/lang/Object;)Ljava/lang/Object;"
        ),
        remap = false,
        require = 1
    )
    private static Object jja$defaultMissingPlayerVariables(
        LazyOptional<?> optional,
        Object fallback,
        Operation<Object> original
    ) {
        return CursedTechniquePotionExpiresProcedureHook.resolvePlayerVariablesOrDefault(
            (JujutsucraftModVariables.PlayerVariables) original.call(optional, fallback)
        );
    }
}
