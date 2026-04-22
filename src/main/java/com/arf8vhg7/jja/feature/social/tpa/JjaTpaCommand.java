package com.arf8vhg7.jja.feature.social.tpa;

import com.arf8vhg7.jja.JujutsuAddon;
import com.arf8vhg7.jja.compat.jujutsucraft.JjaJujutsucraftCompat;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(modid = JujutsuAddon.MODID)
public final class JjaTpaCommand {
    private JjaTpaCommand() {
    }

    @SubscribeEvent
    public static void registerCommand(RegisterCommandsEvent event) {
        event.getDispatcher()
            .register(
                Commands.literal("tpa")
                    .then(
                        Commands.argument("player", EntityArgument.player())
                            .executes(ctx -> executeRequest(ctx.getSource(), EntityArgument.getPlayer(ctx, "player"), JjaTpaRequestType.TPA))
                    )
            );

        event.getDispatcher()
            .register(
                Commands.literal("tpahere")
                    .then(
                        Commands.argument("player", EntityArgument.player())
                            .executes(ctx -> executeRequest(ctx.getSource(), EntityArgument.getPlayer(ctx, "player"), JjaTpaRequestType.TPAHERE))
                    )
            );

        event.getDispatcher()
            .register(
                Commands.literal("tpaccept")
                    .executes(ctx -> executeAcceptAll(ctx.getSource()))
                    .then(
                        Commands.argument("player", StringArgumentType.word())
                            .suggests((ctx, builder) -> SharedSuggestionProvider.suggest(
                                ctx.getSource().getServer().getPlayerList().getPlayers().stream()
                                    .map(player -> player.getGameProfile().getName()),
                                builder
                            ))
                            .executes(ctx -> executeAcceptOne(ctx.getSource(), StringArgumentType.getString(ctx, "player")))
                    )
            );
    }

    @SubscribeEvent
    public static void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof ServerPlayer player)) {
            return;
        }
        JjaTpaRequestStore.removeAllForReceiver(player.getUUID());
        JjaTpaRequestStore.removeAllFromSender(player.getUUID());
    }

    private static int executeRequest(CommandSourceStack source, ServerPlayer receiver, JjaTpaRequestType type) {
        Entity entity = source.getEntity();
        if (!(entity instanceof ServerPlayer sender)) {
            return 0;
        }
        if (sender.getUUID().equals(receiver.getUUID())) {
            return 0;
        }

        JjaTpaRequestStore.upsert(receiver.getUUID(), sender.getUUID(), sender.getGameProfile().getName(), type);

        sender.displayClientMessage(
            Component.translatable("message.jja.tpa_sent", type.name(), receiver.getDisplayName()),
            false
        );

        Component message = Component.translatable(
            "message.jja.tpa_received",
            type.name(),
            sender.getDisplayName()
        ).withStyle(
            Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpaccept " + sender.getGameProfile().getName()))
        );
        receiver.displayClientMessage(message, false);
        return 0;
    }

    private static int executeAcceptAll(CommandSourceStack source) {
        Entity entity = source.getEntity();
        if (!(entity instanceof ServerPlayer receiver)) {
            return 0;
        }
        if (JjaJujutsucraftCompat.jjaHasNeutralization(receiver)) {
            receiver.displayClientMessage(Component.translatable("message.jja.tpa_blocked_domain"), false);
            return 0;
        }

        for (JjaTpaRequest request : JjaTpaRequestStore.snapshot(receiver.getUUID()).values()) {
            handleAcceptRequest(source, receiver, request);
        }
        return 0;
    }

    private static int executeAcceptOne(CommandSourceStack source, String senderName) {
        Entity entity = source.getEntity();
        if (!(entity instanceof ServerPlayer receiver)) {
            return 0;
        }
        if (JjaJujutsucraftCompat.jjaHasNeutralization(receiver)) {
            receiver.displayClientMessage(Component.translatable("message.jja.tpa_blocked_domain"), false);
            return 0;
        }

        ServerPlayer sender = source.getServer().getPlayerList().getPlayerByName(senderName);
        if (sender != null) {
            JjaTpaRequest request = JjaTpaRequestStore.get(receiver.getUUID(), sender.getUUID());
            if (request != null) {
                handleAcceptRequest(source, receiver, request);
            }
            return 0;
        }

        for (JjaTpaRequest request : JjaTpaRequestStore.snapshot(receiver.getUUID()).values()) {
            if (request.senderName().equalsIgnoreCase(senderName)) {
                JjaTpaRequestStore.remove(receiver.getUUID(), request.senderId());
            }
        }
        return 0;
    }

    private static void handleAcceptRequest(CommandSourceStack source, ServerPlayer receiver, JjaTpaRequest request) {
        ServerPlayer sender = source.getServer().getPlayerList().getPlayer(request.senderId());
        if (sender == null) {
            JjaTpaRequestStore.remove(receiver.getUUID(), request.senderId());
            return;
        }

        if (request.type() == JjaTpaRequestType.TPA) {
            teleportPlayer(sender, receiver);
        } else if (request.type() == JjaTpaRequestType.TPAHERE) {
            teleportPlayer(receiver, sender);
        }

        JjaTpaRequestStore.remove(receiver.getUUID(), request.senderId());
    }

    private static void teleportPlayer(ServerPlayer mover, ServerPlayer reference) {
        Vec3 direction = reference.getLookAngle();
        Vec3 horizontal = new Vec3(direction.x, 0.0, direction.z);
        if (horizontal.lengthSqr() < 1.0E-6) {
            horizontal = new Vec3(reference.getDirection().getStepX(), 0.0, reference.getDirection().getStepZ());
        }
        Vec3 offset = horizontal.normalize();
        double destX = reference.getX() + offset.x;
        double destY = reference.getY();
        double destZ = reference.getZ() + offset.z;
        ServerLevel targetLevel = reference.serverLevel();
        mover.teleportTo(targetLevel, destX, destY, destZ, mover.getYRot(), mover.getXRot());
    }
}
