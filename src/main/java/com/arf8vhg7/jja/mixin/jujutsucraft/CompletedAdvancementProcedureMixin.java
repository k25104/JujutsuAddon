package com.arf8vhg7.jja.mixin.jujutsucraft;

import com.arf8vhg7.jja.hook.jujutsucraft.CompletedAdvancementProcedureHook;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import javax.annotation.Nullable;
import net.mcreator.jujutsucraft.procedures.CompletedAdvancementProcedure;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;
import net.minecraftforge.eventbus.api.Event;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = CompletedAdvancementProcedure.class, remap = false)
public abstract class CompletedAdvancementProcedureMixin {
    @ModifyExpressionValue(
        method = "execute(Lnet/minecraftforge/eventbus/api/Event;Lnet/minecraft/world/level/LevelAccessor;DDDLnet/minecraft/world/entity/Entity;)V",
        at = @At(
            value = "FIELD",
            target = "Lnet/mcreator/jujutsucraft/network/JujutsucraftModVariables$PlayerVariables;PlayerLevel:D",
            opcode = Opcodes.GETFIELD,
            ordinal = 3
        ),
        remap = false,
        require = 1
    )
    private static double jja$resolveManagedPlayerLevelForRespawnCheck(double original, @Nullable Event event, LevelAccessor world, double x, double y, double z, Entity entity) {
        return CompletedAdvancementProcedureHook.resolveManagedPlayerLevelForComparison(entity, original);
    }

    @Inject(
        method = "execute(Lnet/minecraftforge/eventbus/api/Event;Lnet/minecraft/world/level/LevelAccessor;DDDLnet/minecraft/world/entity/Entity;)V",
        at = @At("TAIL"),
        remap = false
    ,
        require = 1
    )
    private static void jja$postProcessCompletedAdvancement(
        @Nullable Event event,
        LevelAccessor world,
        double x,
        double y,
        double z,
        Entity entity,
        CallbackInfo ci
    ) {
        CompletedAdvancementProcedureHook.postProcess(event, world, x, y, z, entity);
    }
}
