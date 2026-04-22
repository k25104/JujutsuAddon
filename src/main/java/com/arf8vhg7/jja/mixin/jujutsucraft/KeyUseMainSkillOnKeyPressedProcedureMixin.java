package com.arf8vhg7.jja.mixin.jujutsucraft;

import com.arf8vhg7.jja.feature.jja.technique.shared.slot.RegisteredCurseTechniqueSlots.SelectOverride;
import com.arf8vhg7.jja.feature.jja.technique.shared.slot.RegisteredCurseTechniqueSlots.SelectOverrideType;
import com.arf8vhg7.jja.hook.jujutsucraft.KeyUseMainSkillOnKeyPressedProcedureHook;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalDoubleRef;
import net.mcreator.jujutsucraft.procedures.KeyUseMainSkillOnKeyPressedProcedure;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = KeyUseMainSkillOnKeyPressedProcedure.class, remap = false)
public abstract class KeyUseMainSkillOnKeyPressedProcedureMixin {
    @Inject(
        method = "execute(Lnet/minecraft/world/level/LevelAccessor;DDDLnet/minecraft/world/entity/Entity;)V",
        at = @At(value = "INVOKE", target = "Lnet/minecraftforge/common/util/LazyOptional;ifPresent(Lnet/minecraftforge/common/util/NonNullConsumer;)V", ordinal = 0),
        cancellable = true,
        remap = false
    ,
        require = 1
    )
    private static void jja$overrideSelect(
        LevelAccessor world,
        double x,
        double y,
        double z,
        Entity entity,
        CallbackInfo ci,
        @Local(index = 8) double pSkill1,
        @Local(index = 12) LocalDoubleRef select
    ) {
        SelectOverride result = KeyUseMainSkillOnKeyPressedProcedureHook.resolveSelectOverride(world, entity, pSkill1, select.get());
        if (result.type() == SelectOverrideType.CANCEL) {
            ci.cancel();
            return;
        }
        if (result.type() == SelectOverrideType.APPLY) {
            select.set(result.select());
        }
    }
}
