package com.arf8vhg7.jja.mixin.jujutsucraft;

import com.arf8vhg7.jja.hook.jujutsucraft.WhenRespawnProcedureHook;
import net.mcreator.jujutsucraft.procedures.WhenRespawnProcedure;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;
import net.minecraftforge.eventbus.api.Event;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = WhenRespawnProcedure.class, remap = false)
public abstract class WhenRespawnProcedureMixin {
    @Inject(
        method = "execute(Lnet/minecraftforge/eventbus/api/Event;Lnet/minecraft/world/level/LevelAccessor;DDDLnet/minecraft/world/entity/Entity;)V",
        at = @At(
            value = "FIELD",
            target = "Lnet/mcreator/jujutsucraft/network/JujutsucraftModVariables$PlayerVariables;PlayerCursePowerFormer:D",
            opcode = Opcodes.PUTFIELD,
            ordinal = 2,
            shift = At.Shift.AFTER
        ),
        remap = false,
        require = 1
    )
    private static void jja$applyScaledPlayerCursePowerFormer(
        Event event,
        LevelAccessor world,
        double x,
        double y,
        double z,
        Entity entity,
        CallbackInfo ci
    ) {
        WhenRespawnProcedureHook.applyScaledPlayerCursePowerFormer(entity);
    }
}
