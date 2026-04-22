package com.arf8vhg7.jja.mixin.jujutsucraft;

import com.arf8vhg7.jja.hook.jujutsucraft.HollowPurpleProcedureHook;
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
import net.mcreator.jujutsucraft.procedures.HollowPurpleProcedure;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = HollowPurpleProcedure.class, remap = false)
public abstract class HollowPurpleProcedureMixin {
    @Definition(id = "getPersistentData", method = "Lnet/minecraft/world/entity/Entity;getPersistentData()Lnet/minecraft/nbt/CompoundTag;")
    @Definition(id = "getDouble", method = "Lnet/minecraft/nbt/CompoundTag;m_128459_(Ljava/lang/String;)D")
    @Expression("?.getPersistentData().getDouble('cnt5') > 20.0")
    @ModifyExpressionValue(method = "execute", at = @At("MIXINEXTRAS:EXPRESSION"), require = 1)
    private static boolean jja$shortenPurpleChantStep(boolean original, @Local(argsOnly = true) Entity entity) {
        return HollowPurpleProcedureHook.isChantStepReady(entity, original);
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
    private static void jja$useTranslatedPurpleChant(
        Player player,
        Component message,
        boolean actionBar,
        Operation<Void> original,
        @Local(argsOnly = true) Entity entity
    ) {
        int chantStep = (int) Math.round(entity.getPersistentData().getDouble("cnt6"));
        MutableComponent translatedChant = Objects.requireNonNull(HollowPurpleProcedureHook.buildChantMessage(chantStep));
        MutableComponent translatedMessage = Objects.requireNonNull(Component.literal("\""));
        translatedMessage.append(Objects.requireNonNull(translatedChant));
        translatedMessage.append(Objects.requireNonNull(Component.literal("\"")));
        translatedMessage.withStyle(ChatFormatting.BOLD);
        original.call(player, translatedMessage, actionBar);
    }

    @ModifyExpressionValue(
        method = "execute",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/LivingEntity;m_21223_()F"
        ),
        remap = false
    ,
        require = 1
    )
    private static float jja$useFirstAidAwareHealthForLowHpPurpleBranch(float currentHealth, @Local(argsOnly = true) Entity entity) {
        return HollowPurpleProcedureHook.getEffectiveHealth(entity, currentHealth);
    }
}
