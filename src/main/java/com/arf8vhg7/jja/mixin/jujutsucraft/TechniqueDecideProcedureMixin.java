package com.arf8vhg7.jja.mixin.jujutsucraft;

import com.arf8vhg7.jja.hook.jujutsucraft.TechniqueDecideProcedureHook;
import com.arf8vhg7.jja.feature.jja.technique.shared.selection.JjaSkillManagementProbeContext;
import net.mcreator.jujutsucraft.procedures.TechniqueDecideProcedure;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = TechniqueDecideProcedure.class, remap = false)
public abstract class TechniqueDecideProcedureMixin {
    @Inject(
        method = "execute(Lnet/minecraft/world/entity/Entity;ZZDDDLjava/lang/String;)V",
        at = @At("HEAD"),
        cancellable = true,
        remap = false
    ,
        require = 1
    )
    private static void jja$cancelDuringProbe(
        Entity entity,
        boolean passive,
        boolean physical,
        double cost,
        double playerCt,
        double playerSelect,
        String name,
        CallbackInfo ci
    ) {
        if (TechniqueDecideProcedureHook.shouldCancel()) {
            JjaSkillManagementProbeContext.captureProbeCost(cost);
            ci.cancel();
        }
    }

    @Inject(
        method = "execute(Lnet/minecraft/world/entity/Entity;ZZDDDLjava/lang/String;)V",
        at = @At("TAIL"),
        remap = false,
        require = 1
    )
    private static void jja$applySummonEnhancementPreview(
        Entity entity,
        boolean passive,
        boolean physical,
        double cost,
        double playerCt,
        double playerSelect,
        String name,
        CallbackInfo ci
    ) {
        TechniqueDecideProcedureHook.onTechniqueDecide(entity, cost, playerCt, playerSelect, name);
    }
}
