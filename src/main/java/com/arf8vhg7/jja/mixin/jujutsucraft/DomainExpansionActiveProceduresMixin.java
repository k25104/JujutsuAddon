package com.arf8vhg7.jja.mixin.jujutsucraft;

import com.arf8vhg7.jja.hook.jujutsucraft.DomainExpansionActiveProceduresHook;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.mcreator.jujutsucraft.procedures.AuthenticMutualLoveActiveProcedure;
import net.mcreator.jujutsucraft.procedures.CeremonialSeaofLightActiveProcedure;
import net.mcreator.jujutsucraft.procedures.ChimeraShadowGardenActiveProcedure;
import net.mcreator.jujutsucraft.procedures.ChosoDomainActiveProcedure;
import net.mcreator.jujutsucraft.procedures.CoffinoftheIronMountainActiveProcedure;
import net.mcreator.jujutsucraft.procedures.DomainExpansionTodoActiveProcedure;
import net.mcreator.jujutsucraft.procedures.GraveyardDomainActiveProcedure;
import net.mcreator.jujutsucraft.procedures.HorizonOfTheCaptivatingSkandhaActiveProcedure;
import net.mcreator.jujutsucraft.procedures.ItadoriDomainActiveProcedure;
import net.mcreator.jujutsucraft.procedures.JunpeiDomainExpansionActiveProcedure;
import net.mcreator.jujutsucraft.procedures.KashimoDomainActiveProcedure;
import net.mcreator.jujutsucraft.procedures.KurourushiDomainExpansionActiveProcedure;
import net.mcreator.jujutsucraft.procedures.MeimeiDomainActiveProcedure;
import net.mcreator.jujutsucraft.procedures.NanamiDomainExpansionActiveProcedure;
import net.mcreator.jujutsucraft.procedures.NishimiyaDomainExpansionActiveProcedure;
import net.mcreator.jujutsucraft.procedures.OgiDomainActiveProcedure;
import net.mcreator.jujutsucraft.procedures.RozetsuDomainExpansionActiveProcedure;
import net.mcreator.jujutsucraft.procedures.SelfEmbodimentOfPerfectionActiveProcedure;
import net.mcreator.jujutsucraft.procedures.TakumaInoDomainExpansionActiveProcedure;
import net.mcreator.jujutsucraft.procedures.TimeCellMoonPalaceActiveProcedure;
import net.mcreator.jujutsucraft.procedures.TsukumoDomainExpansionActiveProcedure;
import net.mcreator.jujutsucraft.procedures.UnlimitedVoidActiveProcedure;
import net.mcreator.jujutsucraft.procedures.UraumeDomainActiveProcedure;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(
    value = {
        AuthenticMutualLoveActiveProcedure.class,
        CeremonialSeaofLightActiveProcedure.class,
        ChimeraShadowGardenActiveProcedure.class,
        ChosoDomainActiveProcedure.class,
        CoffinoftheIronMountainActiveProcedure.class,
        DomainExpansionTodoActiveProcedure.class,
        GraveyardDomainActiveProcedure.class,
        HorizonOfTheCaptivatingSkandhaActiveProcedure.class,
        ItadoriDomainActiveProcedure.class,
        JunpeiDomainExpansionActiveProcedure.class,
        KashimoDomainActiveProcedure.class,
        KurourushiDomainExpansionActiveProcedure.class,
        MeimeiDomainActiveProcedure.class,
        NanamiDomainExpansionActiveProcedure.class,
        NishimiyaDomainExpansionActiveProcedure.class,
        OgiDomainActiveProcedure.class,
        RozetsuDomainExpansionActiveProcedure.class,
        SelfEmbodimentOfPerfectionActiveProcedure.class,
        TakumaInoDomainExpansionActiveProcedure.class,
        TimeCellMoonPalaceActiveProcedure.class,
        TsukumoDomainExpansionActiveProcedure.class,
        UnlimitedVoidActiveProcedure.class,
        UraumeDomainActiveProcedure.class
    },
    remap = false
)
public abstract class DomainExpansionActiveProceduresMixin {
    @WrapOperation(
        method = "execute",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/LivingEntity;m_21124_(Lnet/minecraft/world/effect/MobEffect;)Lnet/minecraft/world/effect/MobEffectInstance;"
        ),
        remap = false,
        require = 1
    )
    private static MobEffectInstance jja$replaceDomainDuration(
        LivingEntity livingEntity,
        MobEffect effect,
        Operation<MobEffectInstance> original
    ) {
        return DomainExpansionActiveProceduresHook.getEffect(livingEntity, effect);
    }

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
    private static double jja$adjustDomainRange(double radius, @Local(argsOnly = true) Entity entity) {
        return DomainExpansionActiveProceduresHook.adjustDomainRange(entity instanceof LivingEntity livingEntity ? livingEntity : null, radius);
    }
}
