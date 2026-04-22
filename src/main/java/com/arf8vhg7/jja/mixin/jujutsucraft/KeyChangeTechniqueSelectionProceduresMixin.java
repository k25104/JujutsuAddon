package com.arf8vhg7.jja.mixin.jujutsucraft;

import com.arf8vhg7.jja.hook.jujutsucraft.ChangeTechniqueTestProcedureHook;
import com.arf8vhg7.jja.hook.jujutsucraft.KeyChangeTechniqueSelectionProceduresHook;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import net.mcreator.jujutsucraft.procedures.KeyChangeTechniqueOnKeyPressed2Procedure;
import net.mcreator.jujutsucraft.procedures.KeyChangeTechniqueOnKeyPressedProcedure;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = {KeyChangeTechniqueOnKeyPressedProcedure.class, KeyChangeTechniqueOnKeyPressed2Procedure.class}, remap = false)
public abstract class KeyChangeTechniqueSelectionProceduresMixin {
    @WrapOperation(
        method = "execute",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/network/chat/MutableComponent;getString()Ljava/lang/String;"),
        remap = false,
        require = 1
    )
    private static String jja$getKeyOrString(MutableComponent component, Operation<String> original) {
        return KeyChangeTechniqueSelectionProceduresHook.jjaGetKeyOrString(component);
    }

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
        return KeyChangeTechniqueSelectionProceduresHook.resolveEquipmentRead(
            livingEntity,
            equipmentSlot,
            original.call(livingEntity, equipmentSlot)
        );
    }

    @WrapOperation(
        method = "execute",
        at = @At(
            value = "INVOKE",
            target = "Lnet/mcreator/jujutsucraft/procedures/ChangeTechniqueTestProcedure;execute(Lnet/minecraft/world/level/LevelAccessor;DDDLnet/minecraft/world/entity/Entity;DD)Z"
        ),
        remap = false,
        require = 1
    )
    private static boolean jja$evaluateCandidate(
        LevelAccessor world,
        double x,
        double y,
        double z,
        Entity entity,
        double playerCt,
        double playerSelect,
        Operation<Boolean> original,
        @Local(ordinal = 0) String name
    ) {
        return ChangeTechniqueTestProcedureHook.evaluateCandidate(
            world,
            x,
            y,
            z,
            entity,
            playerCt,
            playerSelect,
            name,
            () -> original.call(world, x, y, z, entity, playerCt, playerSelect)
        );
    }
}
