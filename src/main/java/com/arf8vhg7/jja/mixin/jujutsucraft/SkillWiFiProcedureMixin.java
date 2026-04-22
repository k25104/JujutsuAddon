package com.arf8vhg7.jja.mixin.jujutsucraft;

import com.arf8vhg7.jja.hook.jujutsucraft.SkillWiFiProcedureHook;
import com.llamalad7.mixinextras.sugar.Local;
import java.util.Objects;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.mcreator.jujutsucraft.procedures.SkillWiFiProcedure;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import javax.annotation.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = SkillWiFiProcedure.class, remap = false)
public abstract class SkillWiFiProcedureMixin {
    @WrapOperation(
        method = "execute",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/LivingEntity;m_21153_(F)V"
        ),
        remap = false
    ,
        require = 1
    )
    private static void jja$useFirstAidAwareSelfHeal(LivingEntity livingEntity, float health, Operation<Void> original) {
        SkillWiFiProcedureHook.applySelfHeal(livingEntity, health, original);
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
    private static void jja$useTranslatedTakabaMessage(
        Player player,
        Component message,
        boolean actionBar,
        Operation<Void> original
    ) {
        MutableComponent translatedMessage = Objects.requireNonNull(SkillWiFiProcedureHook.buildTakabaMessage());
        original.call(player, translatedMessage, actionBar);
    }

    @WrapOperation(
        method = "execute",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/commands/Commands;m_230957_(Lnet/minecraft/commands/CommandSourceStack;Ljava/lang/String;)I"
        ),
        remap = false,
        require = 1
    )
    private static int jja$useTranslatedTakabaBroadcast(
        Commands commands,
        CommandSourceStack commandSourceStack,
        @Nullable String command,
        Operation<Integer> original,
        @Local(name = "entityiterator") Entity entityiterator
    ) {
        if (command == null) {
            return 0;
        }

        if (command.startsWith("tell ") && entityiterator instanceof Player player) {
            MutableComponent translatedMessage = Objects.requireNonNull(SkillWiFiProcedureHook.buildTakabaMessage());
            player.displayClientMessage(translatedMessage, false);
            return 1;
        }
        return original.call(commands, commandSourceStack, command);
    }
}
