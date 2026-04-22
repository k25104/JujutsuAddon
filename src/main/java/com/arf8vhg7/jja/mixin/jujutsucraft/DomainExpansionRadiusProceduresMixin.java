package com.arf8vhg7.jja.mixin.jujutsucraft;

import com.arf8vhg7.jja.hook.jujutsucraft.DomainExpansionRadiusProceduresHook;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.mcreator.jujutsucraft.procedures.AngelDomainExpansionProcedure;
import net.mcreator.jujutsucraft.procedures.AuthenticMutualLoveProcedure;
import net.mcreator.jujutsucraft.procedures.ChimeraShadowGardenProcedure;
import net.mcreator.jujutsucraft.procedures.ChosoDomainExpansionProcedure;
import net.mcreator.jujutsucraft.procedures.DeadlySentencingProcedure;
import net.mcreator.jujutsucraft.procedures.GraveyardDomainProcedure;
import net.mcreator.jujutsucraft.procedures.HorizonOfTheCaptivatingSkandhaProcedure;
import net.mcreator.jujutsucraft.procedures.IdleDeathGambleProcedure;
import net.mcreator.jujutsucraft.procedures.InumakiDomainExpansionProcedure;
import net.mcreator.jujutsucraft.procedures.IshigoriDomainExpansionProcedure;
import net.mcreator.jujutsucraft.procedures.ItadoriDomainExpansionProcedure;
import net.mcreator.jujutsucraft.procedures.JinichiDomainProcedure;
import net.mcreator.jujutsucraft.procedures.KugisakiDomainExpansionProcedure;
import net.mcreator.jujutsucraft.procedures.KurourushiDomainExpansionProcedure;
import net.mcreator.jujutsucraft.procedures.MeimeiDomainExpansionProcedure;
import net.mcreator.jujutsucraft.procedures.NanamiDomainExpansionProcedure;
import net.mcreator.jujutsucraft.procedures.RozetsuDomainExpansionProcedure;
import net.mcreator.jujutsucraft.procedures.SelfEmbodimentOfPerfectionProcedure;
import net.mcreator.jujutsucraft.procedures.ThreefoldAfflictionProcedure;
import net.mcreator.jujutsucraft.procedures.TimeCellMoonPalaceProcedure;
import net.mcreator.jujutsucraft.procedures.TsukumoDomainExpansionProcedure;
import net.mcreator.jujutsucraft.procedures.UnlimitedVoidProcedure;
import net.minecraft.world.entity.Entity;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(
    value = {
        AngelDomainExpansionProcedure.class,
        AuthenticMutualLoveProcedure.class,
        ChimeraShadowGardenProcedure.class,
        ChosoDomainExpansionProcedure.class,
        DeadlySentencingProcedure.class,
        GraveyardDomainProcedure.class,
        HorizonOfTheCaptivatingSkandhaProcedure.class,
        IdleDeathGambleProcedure.class,
        InumakiDomainExpansionProcedure.class,
        IshigoriDomainExpansionProcedure.class,
        ItadoriDomainExpansionProcedure.class,
        JinichiDomainProcedure.class,
        KugisakiDomainExpansionProcedure.class,
        KurourushiDomainExpansionProcedure.class,
        MeimeiDomainExpansionProcedure.class,
        NanamiDomainExpansionProcedure.class,
        RozetsuDomainExpansionProcedure.class,
        SelfEmbodimentOfPerfectionProcedure.class,
        ThreefoldAfflictionProcedure.class,
        TimeCellMoonPalaceProcedure.class,
        TsukumoDomainExpansionProcedure.class,
        UnlimitedVoidProcedure.class
    },
    remap = false
)
public abstract class DomainExpansionRadiusProceduresMixin {
    @ModifyExpressionValue(
        method = "execute",
        at = @At(
            value = "FIELD",
            target = "Lnet/mcreator/jujutsucraft/network/JujutsucraftModVariables$MapVariables;DomainExpansionRadius:D",
            opcode = Opcodes.GETFIELD
        ),
        remap = false,
        require = 1
    )
    private static double jja$resolveCurrentRadius(double radius, @Local(argsOnly = true) Entity entity) {
        return DomainExpansionRadiusProceduresHook.resolveCurrentRadius(entity, radius);
    }
}
