package com.arf8vhg7.jja.mixin.jujutsucraft;

import com.arf8vhg7.jja.hook.jujutsucraft.TechniqueRedProcedureHook;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import java.util.Objects;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.mcreator.jujutsucraft.procedures.TechniqueRedProcedure;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = TechniqueRedProcedure.class, remap = false)
public abstract class TechniqueRedProcedureMixin {
    @Definition(id = "getPersistentData", method = "Lnet/minecraft/world/entity/Entity;getPersistentData()Lnet/minecraft/nbt/CompoundTag;")
    @Definition(id = "getDouble", method = "Lnet/minecraft/nbt/CompoundTag;m_128459_(Ljava/lang/String;)D")
    @Expression("?.getPersistentData().getDouble('cnt1') >= 20.0")
    @ModifyExpressionValue(method = "execute", at = @At("MIXINEXTRAS:EXPRESSION"), require = 1)
    private static boolean jja$startRedChargeEarlier(boolean original, @Local(argsOnly = true) Entity entity) {
        return TechniqueRedProcedureHook.isChargeWindowReady(entity, original);
    }

    @WrapOperation(
        method = "execute",
        at = @At(
            value = "INVOKE",
            target = "Ljava/lang/Math;min(DD)D"
        ),
        remap = false
    ,
        require = 1
    )
    private static double jja$scaleRedChargeClamp(double value, double max, Operation<Double> original, @Local(argsOnly = true) Entity entity) {
        return original.call(value, TechniqueRedProcedureHook.getChargeWindowClamp(entity, max));
    }

    @Definition(id = "getPersistentData", method = "Lnet/minecraft/world/entity/Entity;getPersistentData()Lnet/minecraft/nbt/CompoundTag;")
    @Definition(id = "getDouble", method = "Lnet/minecraft/nbt/CompoundTag;m_128459_(Ljava/lang/String;)D")
    @Expression("?.getPersistentData().getDouble('cnt5') > 20.0")
    @ModifyExpressionValue(method = "execute", at = @At("MIXINEXTRAS:EXPRESSION"), require = 1)
    private static boolean jja$shortenRedChantStep(boolean original, @Local(argsOnly = true) Entity entity) {
        return TechniqueRedProcedureHook.isChantStepReady(entity, original);
    }

    @WrapOperation(
        method = "execute",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/player/Player;m_5661_(Lnet/minecraft/network/chat/Component;Z)V"
        ),
        remap = false,
        require = 1
    )
    private static void jja$useTranslatedRedChant(
        Player player,
        Component message,
        boolean actionBar,
        Operation<Void> original,
        @Local(argsOnly = true) Entity entity
    ) {
        int chantStep = (int) Math.round(entity.getPersistentData().getDouble("cnt6"));
        MutableComponent translatedChant = Objects.requireNonNull(TechniqueRedProcedureHook.buildChantMessage(chantStep));
        MutableComponent translatedMessage = Objects.requireNonNull(Component.literal("\""));
        translatedMessage.append(Objects.requireNonNull(translatedChant));
        translatedMessage.append(Objects.requireNonNull(Component.literal("\"")));
        translatedMessage.withStyle(ChatFormatting.BOLD);
        original.call(player, translatedMessage, actionBar);
    }

    @Definition(id = "getPersistentData", method = "Lnet/minecraft/world/entity/Entity;getPersistentData()Lnet/minecraft/nbt/CompoundTag;")
    @Definition(id = "getDouble", method = "Lnet/minecraft/nbt/CompoundTag;m_128459_(Ljava/lang/String;)D")
    @Expression("?.getPersistentData().getDouble('cnt1') > 20.0")
    @ModifyExpressionValue(method = "execute", at = @At("MIXINEXTRAS:EXPRESSION"), require = 1)
    private static boolean jja$endRedChargeEarlier(boolean original, @Local(argsOnly = true) Entity entity) {
        return TechniqueRedProcedureHook.isChargeWindowExpired(entity, original);
    }
}
