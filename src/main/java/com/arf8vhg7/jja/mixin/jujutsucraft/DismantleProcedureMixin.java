package com.arf8vhg7.jja.mixin.jujutsucraft;

import com.arf8vhg7.jja.hook.jujutsucraft.DismantleProcedureHook;
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
import net.mcreator.jujutsucraft.procedures.DismantleProcedure;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = DismantleProcedure.class, remap = false)
public abstract class DismantleProcedureMixin {
    @Inject(method = "execute", at = @At("HEAD"), remap = false, require = 1)
    private static void jja$observeCutTheWorld(LevelAccessor world, double x, double y, double z, Entity entity, CallbackInfo ci) {
        DismantleProcedureHook.observeCutTheWorld(entity);
    }

    @Definition(id = "getPersistentData", method = "Lnet/minecraft/world/entity/Entity;getPersistentData()Lnet/minecraft/nbt/CompoundTag;")
    @Definition(id = "getDouble", method = "Lnet/minecraft/nbt/CompoundTag;m_128459_(Ljava/lang/String;)D")
    @Expression("?.getPersistentData().getDouble('cnt5') > 20.0")
    @ModifyExpressionValue(method = "execute", at = @At("MIXINEXTRAS:EXPRESSION"), require = 1)
    private static boolean jja$shortenDismantleChantStep(boolean original, @Local(argsOnly = true) Entity entity) {
        return DismantleProcedureHook.isChantStepReady(entity, original);
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
    private static void jja$useTranslatedDismantleChant(
        Player player,
        Component message,
        boolean actionBar,
        Operation<Void> original,
        @Local(argsOnly = true) Entity entity
    ) {
        int chantStep = (int) Math.round(entity.getPersistentData().getDouble("cnt6"));
        MutableComponent translatedChant = Objects.requireNonNull(DismantleProcedureHook.buildChantMessage(chantStep));
        MutableComponent translatedMessage = Objects.requireNonNull(Component.literal("\""));
        translatedMessage.append(Objects.requireNonNull(translatedChant));
        translatedMessage.append(Objects.requireNonNull(Component.literal("\"")));
        translatedMessage.withStyle(ChatFormatting.BOLD);
        original.call(player, translatedMessage, actionBar);
    }
}
