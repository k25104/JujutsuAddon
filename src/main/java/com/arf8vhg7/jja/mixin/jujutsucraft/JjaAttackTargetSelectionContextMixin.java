package com.arf8vhg7.jja.mixin.jujutsucraft;

import com.arf8vhg7.jja.hook.jujutsucraft.JjaAttackTargetSelectionContextHook;
import net.mcreator.jujutsucraft.procedures.AIEntityJacobsLadderProcedure;
import net.mcreator.jujutsucraft.procedures.GraveyardDomainActiveProcedure;
import net.mcreator.jujutsucraft.procedures.IdleTransfigurationProcedure;
import net.mcreator.jujutsucraft.procedures.SelfEmbodimentOfPerfectionActiveProcedure;
import net.mcreator.jujutsucraft.procedures.TechniqueJacobsLadderProcedure;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(
    value = {
        TechniqueJacobsLadderProcedure.class,
        AIEntityJacobsLadderProcedure.class,
        IdleTransfigurationProcedure.class,
        SelfEmbodimentOfPerfectionActiveProcedure.class,
        GraveyardDomainActiveProcedure.class
    },
    remap = false
)
public abstract class JjaAttackTargetSelectionContextMixin {
    @Inject(method = "execute", at = @At("HEAD"), remap = false, require = 1)
    private static void jja$enterAttackTargetSelectionContext(CallbackInfo ci) {
        JjaAttackTargetSelectionContextHook.jjaEnterAttackTargetSelectionContext();
    }

    @Inject(method = "execute", at = @At("RETURN"), remap = false, require = 1)
    private static void jja$exitAttackTargetSelectionContext(CallbackInfo ci) {
        JjaAttackTargetSelectionContextHook.jjaExitAttackTargetSelectionContext();
    }
}