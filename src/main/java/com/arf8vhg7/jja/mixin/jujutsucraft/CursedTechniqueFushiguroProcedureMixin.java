package com.arf8vhg7.jja.mixin.jujutsucraft;

import com.arf8vhg7.jja.hook.jujutsucraft.CursedTechniqueFushiguroProcedureHook;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.NonNullList;
import net.mcreator.jujutsucraft.init.JujutsucraftModItems;
import net.mcreator.jujutsucraft.procedures.CursedTechniqueFushiguroProcedure;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.LevelAccessor;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = CursedTechniqueFushiguroProcedure.class, remap = false)
@SuppressWarnings("deprecation")
public abstract class CursedTechniqueFushiguroProcedureMixin {
    @Inject(
        method = "execute",
        at = @At("HEAD"),
        cancellable = true,
        remap = false,
        require = 1
    )
    private static void jja$handleShadowTechnique(LevelAccessor world, double x, double y, double z, Entity entity, CallbackInfo ci) {
        if (CursedTechniqueFushiguroProcedureHook.handleShadowTechnique(world, x, y, z, entity)) {
            ci.cancel();
        }
    }

    @WrapOperation(
        method = "execute",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/network/chat/MutableComponent;getString()Ljava/lang/String;"),
        remap = false
    ,
        require = 1
    )
    private static String jja$getKeyOrString(MutableComponent component, Operation<String> original) {
        return CursedTechniqueFushiguroProcedureHook.jjaGetKeyOrString(component);
    }

    @WrapOperation(
        method = "execute",
        at = @At(
            value = "INVOKE",
            target = "Lnet/mcreator/jujutsucraft/procedures/ReturnShadowProcedure;execute(Lnet/minecraft/world/level/LevelAccessor;DDDLnet/minecraft/world/entity/Entity;)V"
        ),
        remap = false,
        require = 1
    )
    private static void jja$handleStepwiseRecall(
        LevelAccessor world,
        double x,
        double y,
        double z,
        Entity entity,
        Operation<Void> original
    ) {
        CursedTechniqueFushiguroProcedureHook.handleReturnShadow(entity, () -> original.call(world, x, y, z, entity));
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
        return CursedTechniqueFushiguroProcedureHook.resolveEquipmentRead(
            livingEntity,
            equipmentSlot,
            original.call(livingEntity, equipmentSlot)
        );
    }

    @WrapOperation(
        method = "execute",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraftforge/items/ItemHandlerHelper;giveItemToPlayer(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/item/ItemStack;)V"
        ),
        remap = false,
        require = 1
    )
    private static void jja$returnPreviousHeadArmor(
        Player player,
        ItemStack stack,
        Operation<Void> original,
        @Local(argsOnly = true) Entity entity
    ) {
        if (CursedTechniqueFushiguroProcedureHook.shouldReturnPreviousArmor(
            entity,
            3,
            new ItemStack((ItemLike) JujutsucraftModItems.MAHORAGA_WHEEL_HELMET.get())
        )) {
            original.call(player, stack);
        }
    }

    @WrapOperation(
        method = "execute",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/core/NonNullList;set(ILjava/lang/Object;)Ljava/lang/Object;"
        ),
        remap = false,
        require = 1
    )
    private static Object jja$equipManagedViaCuriosOnPlayerSet(
        NonNullList<?> stacks,
        int armorIndex,
        Object stack,
        Operation<Object> original,
        @Local(argsOnly = true) Entity entity
    ) {
        if (stack instanceof ItemStack itemStack
            && CursedTechniqueFushiguroProcedureHook.handlePlayerArmorInventorySet(entity, armorIndex, itemStack)) {
            return stack;
        }

        return original.call(stacks, armorIndex, stack);
    }

    @WrapOperation(
        method = "execute",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/LivingEntity;m_8061_(Lnet/minecraft/world/entity/EquipmentSlot;Lnet/minecraft/world/item/ItemStack;)V"
        ),
        remap = false,
        require = 1
    )
    private static void jja$equipManagedViaCuriosOnLivingSet(
        LivingEntity livingEntity,
        EquipmentSlot equipmentSlot,
        ItemStack stack,
        Operation<Void> original
    ) {
        if (!CursedTechniqueFushiguroProcedureHook.handleLivingArmorSet(livingEntity, equipmentSlot, stack)) {
            original.call(livingEntity, equipmentSlot, stack);
        }
    }

    @ModifyExpressionValue(
        method = "execute",
        at = @At(
            value = "FIELD",
            target = "Lnet/mcreator/jujutsucraft/network/JujutsucraftModVariables$PlayerVariables;PlayerSelectCurseTechniqueName:Ljava/lang/String;",
            opcode = Opcodes.GETFIELD
        ),
        remap = false
    ,
        require = 1
    )
    private static String jja$resolveSelectTechniqueName(String original, @Local(argsOnly = true) Entity entity) {
        return CursedTechniqueFushiguroProcedureHook.resolveSelectTechniqueName(entity, original);
    }
}
