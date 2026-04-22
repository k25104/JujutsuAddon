package com.arf8vhg7.jja.mixin.jujutsucraft;

import com.arf8vhg7.jja.hook.jujutsucraft.TechniqueBlueProcedureHook;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import java.util.Objects;
import com.arf8vhg7.jja.util.JjaCommandHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.mcreator.jujutsucraft.procedures.TechniqueBlueProcedure;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = TechniqueBlueProcedure.class, remap = false)
public abstract class TechniqueBlueProcedureMixin {
    @Definition(id = "getPersistentData", method = "Lnet/minecraft/world/entity/Entity;getPersistentData()Lnet/minecraft/nbt/CompoundTag;")
    @Definition(id = "getDouble", method = "Lnet/minecraft/nbt/CompoundTag;m_128459_(Ljava/lang/String;)D")
    @Expression("?.getPersistentData().getDouble('cnt5') > 20.0")
    @ModifyExpressionValue(method = "execute", at = @At("MIXINEXTRAS:EXPRESSION"), require = 1)
    private static boolean jja$shortenBlueChantStep(boolean original, @Local(argsOnly = true) Entity entity) {
        return TechniqueBlueProcedureHook.isChantStepReady(entity, original);
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
    private static void jja$useTranslatedBlueChant(
        Player player,
        Component message,
        boolean actionBar,
        Operation<Void> original,
        @Local(argsOnly = true) Entity entity
    ) {
        int chantStep = (int) Math.round(entity.getPersistentData().getDouble("cnt6"));
        MutableComponent translatedChant = Objects.requireNonNull(TechniqueBlueProcedureHook.buildChantMessage(chantStep));
        MutableComponent translatedMessage = Objects.requireNonNull(Component.literal("\""));
        translatedMessage.append(Objects.requireNonNull(translatedChant));
        translatedMessage.append(Objects.requireNonNull(Component.literal("\"")));
        translatedMessage.withStyle(ChatFormatting.BOLD);
        original.call(player, translatedMessage, actionBar);
    }

    @WrapOperation(
        method = "execute",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/server/level/ServerLevel;m_7967_(Lnet/minecraft/world/entity/Entity;)Z"
        ),
        remap = false,
        require = 1
    )
    private static boolean jja$makeBlueInvulnerableBeforeSpawn(
        ServerLevel serverLevel,
        Entity summoned,
        Operation<Boolean> original
    ) {
        JjaCommandHelper.executeAsEntity(summoned, TechniqueBlueProcedureHook.getBlueInvulnerableCommand());
        return original.call(serverLevel, summoned);
    }
}
