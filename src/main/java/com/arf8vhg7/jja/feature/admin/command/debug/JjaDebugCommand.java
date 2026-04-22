package com.arf8vhg7.jja.feature.admin.command.debug;

import com.arf8vhg7.jja.JujutsuAddon;
import com.arf8vhg7.jja.feature.jja.domain.de.OpenBarrierMasteryReservationService;
import com.arf8vhg7.jja.feature.jja.domain.de.curtain.CurtainRuntimeService;
import com.arf8vhg7.jja.feature.jja.domain.de.curtain.CurtainSyncService;
import com.arf8vhg7.jja.feature.player.mobility.fly.ObservedDoubleJumpUnlockService;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(modid = JujutsuAddon.MODID)
public final class JjaDebugCommand {
    static final int REQUIRED_PERMISSION_LEVEL = 2;

    private JjaDebugCommand() {
    }

    @SubscribeEvent
    public static void registerCommand(RegisterCommandsEvent event) {
        register(event.getDispatcher());
    }

    static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralArgumentBuilder<CommandSourceStack> root = Commands.literal("jja");
        root.then(
            Commands.literal("debug")
                .then(
                    Commands.literal("curtain")
                        .requires(source -> source.hasPermission(REQUIRED_PERMISSION_LEVEL) && source.getEntity() instanceof ServerPlayer)
                        .then(
                            Commands.literal("use")
                                .executes(ctx -> executeUseCurtain(ctx.getSource()))
                        )
                        .then(
                            Commands.literal("visible")
                                .executes(ctx -> executeToggleCurtainVisible(ctx.getSource()))
                        )
                )
                .then(
                    Commands.literal("observe")
                        .requires(source -> source.hasPermission(REQUIRED_PERMISSION_LEVEL) && source.getEntity() instanceof ServerPlayer)
                        .then(
                            Commands.literal("double_jump")
                                .executes(ctx -> executeObserveDoubleJump(ctx.getSource()))
                        )
                        .then(
                            Commands.literal("higuruma_barrier")
                                .executes(ctx -> executeObserveHigurumaBarrier(ctx.getSource()))
                        )
                )
                .then(
                    Commands.literal("reserve")
                        .requires(source -> source.hasPermission(REQUIRED_PERMISSION_LEVEL) && source.getEntity() instanceof ServerPlayer)
                        .then(
                            Commands.literal("barrierless_domain")
                                .executes(ctx -> executeReserveBarrierlessDomain(ctx.getSource()))
                        )
                )
        );
        dispatcher.register(root);
    }

    private static int executeUseCurtain(CommandSourceStack source) {
        if (!(source.getEntity() instanceof ServerPlayer player)) {
            return 0;
        }

        if (!CurtainRuntimeService.use(player)) {
            player.displayClientMessage(Component.translatable("jujutsu.message.dont_use"), false);
            return 0;
        }
        return 1;
    }

    private static int executeToggleCurtainVisible(CommandSourceStack source) {
        if (!(source.getEntity() instanceof ServerPlayer player)) {
            return 0;
        }

        boolean enabled = CurtainRuntimeService.toggleShellVisibility(player);
        player.displayClientMessage(
            Component.translatable("message.jja.curtain.visible", Component.translatable(enabled ? "options.on" : "options.off")),
            false
        );
        CurtainSyncService.syncToPlayer(player);
        return 1;
    }

    private static int executeObserveDoubleJump(CommandSourceStack source) {
        return executeObserveDoubleJumpUnlock(source, "Double jump observation recorded.");
    }

    private static int executeObserveHigurumaBarrier(CommandSourceStack source) {
        return executeObserveDoubleJumpUnlock(source, "Higuruma barrier-start observation recorded.");
    }

    private static int executeObserveDoubleJumpUnlock(CommandSourceStack source, String successMessage) {
        if (!(source.getEntity() instanceof ServerPlayer player)) {
            return 0;
        }

        boolean alreadyUnlocked = ObservedDoubleJumpUnlockService.hasUnlock(player);
        if (!ObservedDoubleJumpUnlockService.debugObserve(player)) {
            player.displayClientMessage(
                Component.literal("Observation requires jja:sorcerer_grade_special_1 or current Higuruma with jujutsucraft:sorcerer_grade_1."),
                false
            );
            return 0;
        }

        player.displayClientMessage(Component.literal(alreadyUnlocked ? "Observation was already recorded." : successMessage), false);
        return 1;
    }

    private static int executeReserveBarrierlessDomain(CommandSourceStack source) {
        if (!(source.getEntity() instanceof ServerPlayer player)) {
            return 0;
        }

        if (OpenBarrierMasteryReservationService.hasAwarded(player)) {
            player.displayClientMessage(Component.literal("Barrierless Domain mastery is already achieved."), false);
            return 0;
        }

        if (OpenBarrierMasteryReservationService.debugReservePending(player)) {
            player.displayClientMessage(Component.literal("Barrierless Domain reservation recorded."), false);
            return 1;
        }

        player.displayClientMessage(Component.literal("Barrierless Domain reservation could not be recorded."), false);
        return 0;
    }
}
