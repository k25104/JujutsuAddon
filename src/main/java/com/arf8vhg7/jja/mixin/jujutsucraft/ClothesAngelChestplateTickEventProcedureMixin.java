package com.arf8vhg7.jja.mixin.jujutsucraft;

import com.arf8vhg7.jja.hook.jujutsucraft.ClothesAngelChestplateTickEventProcedureHook;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import javax.annotation.Nullable;
import net.mcreator.jujutsucraft.procedures.ClothesAngelChestplateTickEventProcedure;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.Event;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ClothesAngelChestplateTickEventProcedure.class, remap = false)
public abstract class ClothesAngelChestplateTickEventProcedureMixin {
    private static final String JJA_FLY_TICK_EXECUTE_METHOD =
        "execute(Lnet/minecraftforge/eventbus/api/Event;Lnet/minecraft/world/entity/Entity;)V";

    @Inject(
        method = JJA_FLY_TICK_EXECUTE_METHOD,
        at = @At("HEAD"),
        cancellable = true,
        remap = false
    ,
        require = 1
    )
    private static void jja$cancelMismatchedFlyTick(@Nullable Event event, Entity entity, CallbackInfo ci) {
        if (ClothesAngelChestplateTickEventProcedureHook.shouldCancel(entity)) {
            ci.cancel();
        }
    }

    @WrapOperation(
        method = JJA_FLY_TICK_EXECUTE_METHOD,
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
        return ClothesAngelChestplateTickEventProcedureHook.resolveEquipmentRead(
            livingEntity,
            equipmentSlot,
            original.call(livingEntity, equipmentSlot)
        );
    }

    @Inject(
        method = JJA_FLY_TICK_EXECUTE_METHOD,
        at = @At("TAIL"),
        remap = false,
        require = 1
    )
    private static void jja$applyHitenFlyEffect(@Nullable Event event, Entity entity, CallbackInfo ci) {
        ClothesAngelChestplateTickEventProcedureHook.applyHitenFlyEffect(entity);
    }
}
