package com.arf8vhg7.jja.mixin.jujutsucraft;

import com.arf8vhg7.jja.hook.jujutsucraft.PrayerSongOnEffectActiveTickProcedureHook;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.mcreator.jujutsucraft.procedures.PrayerSongOnEffectActiveTickProcedure;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = PrayerSongOnEffectActiveTickProcedure.class, remap = false)
public abstract class PrayerSongOnEffectActiveTickProcedureMixin {
    @ModifyExpressionValue(
        method = "execute",
        at = @At(
            value = "INVOKE",
            target = "Lnet/mcreator/jujutsucraft/procedures/LogicAttackProcedure;execute(Lnet/minecraft/world/level/LevelAccessor;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/entity/Entity;)Z"
        ),
        remap = false,
        require = 1
    )
    private static boolean jja$allowPrayerSongWeaknessOnlyForRegisteredTargets(
        boolean original,
        @Local(argsOnly = true) LevelAccessor world,
        @Local(argsOnly = true, ordinal = 0) Entity entity,
        @Local(name = "entityiterator") Entity entityIterator
    ) {
        return PrayerSongOnEffectActiveTickProcedureHook.allowWeakness(original, world, entity, entityIterator);
    }
}