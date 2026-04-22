package com.arf8vhg7.jja.mixin.jujutsucraft;

import com.arf8vhg7.jja.hook.jujutsucraft.DeadlySentencingActiveProcedureHook;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.mcreator.jujutsucraft.procedures.DeadlySentencingActiveProcedure;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemCooldowns;
import net.minecraft.world.level.LevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import net.mcreator.jujutsucraft.network.JujutsucraftModVariables;
import org.objectweb.asm.Opcodes;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(value = DeadlySentencingActiveProcedure.class, remap = false)
public abstract class DeadlySentencingActiveProcedureMixin {
    @WrapOperation(
        method = "execute",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/LivingEntity;m_21124_(Lnet/minecraft/world/effect/MobEffect;)Lnet/minecraft/world/effect/MobEffectInstance;"
        ),
        remap = false
    ,
        require = 1
    )
    private static MobEffectInstance jja$replaceDomainDuration(LivingEntity livingEntity, MobEffect effect, Operation<MobEffectInstance> original) {
        return DeadlySentencingActiveProcedureHook.getEffect(livingEntity, effect);
    }

    @WrapOperation(
        method = "execute",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/effect/MobEffectInstance;m_19557_()I",
            ordinal = 1
        ),
        remap = false,
        require = 1
    )
    private static int jja$delayJudgementStart(
        MobEffectInstance effectInstance,
        Operation<Integer> original,
        @Local(argsOnly = true) Entity entity
    ) {
        return DeadlySentencingActiveProcedureHook.resolveJudgementStartDuration(entity, original.call(effectInstance));
    }

    @ModifyExpressionValue(
        method = "execute",
        at = @At(
            value = "FIELD",
            target = "Lnet/mcreator/jujutsucraft/network/JujutsucraftModVariables$MapVariables;DomainExpansionRadius:D",
            opcode = Opcodes.GETFIELD
        ),
        remap = false
    ,
        require = 1
    )
    private static double jja$adjustDomainRange(
        double radius,
        LevelAccessor world,
        double x,
        double y,
        double z,
        Entity entity
    ) {
        return DeadlySentencingActiveProcedureHook.adjustDomainRange(entity instanceof LivingEntity livingEntity ? livingEntity : null, radius);
    }

    @ModifyVariable(
        method = "execute",
        at = @At(value = "STORE", ordinal = 1),
        index = 26,
        remap = false,
        require = 1
    )
    private static double jja$useFeetAnchorDistanceForInitialOccupantScan(
        double originalDistance,
        @Local(argsOnly = true) Entity entity,
        @Local(name = "entityiterator") Entity target
    ) {
        return DeadlySentencingActiveProcedureHook.resolveFeetAnchorDistanceSquared(entity, target, originalDistance);
    }

    @ModifyVariable(
        method = "execute",
        at = @At(value = "STORE", ordinal = 2),
        index = 26,
        remap = false,
        require = 1
    )
    private static double jja$useFeetAnchorDistanceForJudgementOccupantScan(
        double originalDistance,
        @Local(argsOnly = true) Entity entity,
        @Local(name = "entityiterator") Entity target
    ) {
        return DeadlySentencingActiveProcedureHook.resolveFeetAnchorDistanceSquared(entity, target, originalDistance);
    }

    @ModifyVariable(
        method = "execute",
        at = @At(value = "STORE", ordinal = 3),
        index = 26,
        remap = false,
        require = 1
    )
    private static double jja$useFeetAnchorDistanceForAnswerBroadcast(
        double originalDistance,
        @Local(argsOnly = true) Entity entity,
        @Local(name = "entityiterator") Entity target
    ) {
        return DeadlySentencingActiveProcedureHook.resolveFeetAnchorDistanceSquared(entity, target, originalDistance);
    }

    @WrapOperation(
        method = "execute",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/LivingEntity;m_7292_(Lnet/minecraft/world/effect/MobEffectInstance;)Z"
        ),
        remap = false,
        require = 1
    )
    private static boolean jja$extendConfiscationPenaltyEffects(
        LivingEntity livingEntity,
        MobEffectInstance effectInstance,
        Operation<Boolean> original
    ) {
        return DeadlySentencingActiveProcedureHook.addConfiscationPenaltyEffect(livingEntity, effectInstance, original);
    }

    @WrapOperation(
        method = "execute",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/item/ItemCooldowns;m_41524_(Lnet/minecraft/world/item/Item;I)V"
        ),
        remap = false,
        require = 1
    )
    private static void jja$extendConfiscationCooldown(
        ItemCooldowns cooldowns,
        Item item,
        int ticks,
        Operation<Void> original
    ) {
        original.call(cooldowns, item, DeadlySentencingActiveProcedureHook.adjustConfiscationCooldown(item, ticks));
    }
}
