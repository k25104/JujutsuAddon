package com.arf8vhg7.jja.mixin.jujutsucraft;

import com.arf8vhg7.jja.hook.jujutsucraft.KeyDomainAmplificationOnKeyPressedProcedureHook;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.mcreator.jujutsucraft.procedures.KeyDomainAmplificationOnKeyPressedProcedure;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = KeyDomainAmplificationOnKeyPressedProcedure.class, remap = false)
public abstract class KeyDomainAmplificationOnKeyPressedProcedureMixin {
    @WrapOperation(
        method = "execute",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/player/Player;m_5661_(Lnet/minecraft/network/chat/Component;Z)V",
            ordinal = 0
        ),
        remap = false,
        require = 1
    )
    private static void jja$sendDomainAmplificationOffMessage(
        Player player,
        Component component,
        boolean actionBar,
        Operation<Void> original
    ) {
        original.call(player, KeyDomainAmplificationOnKeyPressedProcedureHook.buildDomainAmplificationStateMessage(false), actionBar);
    }

    @WrapOperation(
        method = "execute",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/player/Player;m_5661_(Lnet/minecraft/network/chat/Component;Z)V",
            ordinal = 1
        ),
        remap = false,
        require = 1
    )
    private static void jja$sendDomainAmplificationOnMessage(
        Player player,
        Component component,
        boolean actionBar,
        Operation<Void> original
    ) {
        original.call(player, KeyDomainAmplificationOnKeyPressedProcedureHook.buildDomainAmplificationStateMessage(true), actionBar);
    }

    @WrapOperation(
        method = "execute",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/player/Player;m_5661_(Lnet/minecraft/network/chat/Component;Z)V",
            ordinal = 2
        ),
        remap = false,
        require = 1
    )
    private static void jja$sendDomainAmplificationNotMasteredMessage(
        Player player,
        Component component,
        boolean actionBar,
        Operation<Void> original
    ) {
        original.call(player, KeyDomainAmplificationOnKeyPressedProcedureHook.buildNotMasteredMessage(), actionBar);
    }
}
