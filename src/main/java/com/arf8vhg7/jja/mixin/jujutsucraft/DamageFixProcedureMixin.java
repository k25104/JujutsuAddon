package com.arf8vhg7.jja.mixin.jujutsucraft;

import com.arf8vhg7.jja.hook.jujutsucraft.DamageFixProcedureHook;
import com.arf8vhg7.jja.feature.jja.traits.twinnedbody.TwinnedBodyCombatPassContext;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.mcreator.jujutsucraft.procedures.DamageFixProcedure;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = DamageFixProcedure.class, remap = false)
public abstract class DamageFixProcedureMixin {
    @Definition(id = "MobEffect", type = MobEffect.class)
    @Definition(id = "get", method = "Lnet/minecraftforge/registries/RegistryObject;get()Ljava/lang/Object;")
    @Definition(
        id = "ZONE",
        field = "Lnet/mcreator/jujutsucraft/init/JujutsucraftModMobEffects;ZONE:Lnet/minecraftforge/registries/RegistryObject;"
    )
    @Definition(id = "hasEffect", method = "Lnet/minecraft/world/entity/LivingEntity;m_21023_(Lnet/minecraft/world/effect/MobEffect;)Z")
    @Expression("?.hasEffect((MobEffect) ZONE.get())")
    @ModifyExpressionValue(method = "execute", at = @At("MIXINEXTRAS:EXPRESSION"), require = 1)
    private static boolean jja$disableZoneDamageBoost(boolean original) {
        return DamageFixProcedureHook.shouldApplyZoneDamageBoost(original);
    }

    @Inject(
        method = "execute",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/nbt/CompoundTag;m_128471_(Ljava/lang/String;)Z"
        ),
        remap = false
    ,
        require = 1
    )
    private static void jja$applyDomainDamage(Entity entity, CallbackInfo callbackInfo) {
        DamageFixProcedureHook.applyDomainDamage(entity);
    }

    @ModifyExpressionValue(
        method = "execute",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/ai/attributes/AttributeInstance;m_22115_()D"
        ),
        remap = false,
        require = 1
    )
    private static double jja$stripTwinnedBodyHeldItemAttackDamageBonus(double original, @Local(argsOnly = true) Entity entity) {
        return DamageFixProcedureHook.jja$stripHeldItemAttackDamageBonus(
            entity,
            original,
            TwinnedBodyCombatPassContext.isExtraArmAttack()
        );
    }
}
