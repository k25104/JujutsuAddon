package com.arf8vhg7.jja.feature.jja.technique.family.naoya;

import com.arf8vhg7.jja.JujutsuAddon;
import com.arf8vhg7.jja.util.JjaCommandHelper;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(modid = JujutsuAddon.MODID)
public final class JjaNaoyaCtCommand {
    private JjaNaoyaCtCommand() {
    }

    @SubscribeEvent
    public static void registerCommand(RegisterCommandsEvent event) {
        LiteralArgumentBuilder<CommandSourceStack> root = Commands.literal("jja");
        root.then(
            Commands.literal("ct")
                .then(
                    Commands.literal("naoya")
                        .executes(context -> executeToggle(context.getSource(), null))
                        .then(
                            Commands.argument("player", Objects.requireNonNull(EntityArgument.player()))
                                .requires(source -> source.hasPermission(2))
                                .executes(context -> executeToggle(context.getSource(), EntityArgument.getPlayer(context, "player")))
                        )
                )
        );
        event.getDispatcher().register(root);
    }

    private static int executeToggle(CommandSourceStack source, @Nullable ServerPlayer target) {
        @Nullable ServerPlayer resolvedTarget = JjaCommandHelper.resolveTargetOrSelf(source, target);
        if (resolvedTarget == null) {
            source.sendFailure(Objects.requireNonNull(Component.translatable("message.jja.ct.naoya.player_only")));
            return 0;
        }
        boolean enabled = NaoyaProjectionSorceryService.toggleFastFrameGeneration(resolvedTarget);
        source.sendSuccess(
            () -> Objects.requireNonNull(Component.translatable("message.jja.ct.naoya.fast_frame_generation", Boolean.toString(enabled))),
            false
        );
        return 1;
    }
}
