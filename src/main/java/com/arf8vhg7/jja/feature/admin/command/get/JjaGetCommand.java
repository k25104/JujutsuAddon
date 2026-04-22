package com.arf8vhg7.jja.feature.admin.command.get;

import com.arf8vhg7.jja.JujutsuAddon;
import com.arf8vhg7.jja.compat.jujutsucraft.JjaJujutsucraftCompat;
import com.arf8vhg7.jja.feature.jja.resource.ce.CEColorService;
import com.arf8vhg7.jja.feature.jja.traits.twinnedbody.TwinnedBodyRuntimeStateAccess;
import com.arf8vhg7.jja.feature.player.state.AddonStatCounter;
import com.arf8vhg7.jja.feature.player.state.AddonStatsAccess;
import com.arf8vhg7.jja.feature.player.state.PlayerStateAccess;
import com.arf8vhg7.jja.feature.player.state.model.PlayerRctState;
import com.arf8vhg7.jja.feature.player.state.model.PlayerReviveState;
import com.arf8vhg7.jja.util.JjaCommandHelper;
import com.mojang.logging.LogUtils;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import javax.annotation.Nullable;
import net.mcreator.jujutsucraft.init.JujutsucraftModMobEffects;
import net.mcreator.jujutsucraft.network.JujutsucraftModVariables;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import org.slf4j.Logger;

@EventBusSubscriber(modid = JujutsuAddon.MODID)
public final class JjaGetCommand {
    private static final Logger LOGGER = LogUtils.getLogger();

    private JjaGetCommand() {
    }

    @SubscribeEvent
    public static void registerCommand(RegisterCommandsEvent event) {
        LiteralArgumentBuilder<CommandSourceStack> root = Commands.literal("jja");

        root.then(
            Commands.literal("get")
                .then(createNumericGetter("fame", JjaGetType.FAME))
                .then(createNumericGetter("ct_used", JjaGetType.CT_USED))
                .then(createNumericGetter("remaining_revives", JjaGetType.REMAINING_REVIVES))
                .then(createNumericGetter("rct_used", JjaGetType.RCT_USED))
                .then(createNumericGetter("bf_randed", JjaGetType.BF_RANDED))
                .then(createCeColorGetter())
                .then(createNumericGetter("de_used", JjaGetType.DE_USED))
                .then(createNumericGetter("sd_used", JjaGetType.SD_USED))
                .then(createNumericGetter("fbe_used", JjaGetType.FBE_USED))
                .then(createBooleanGetter("six_eyes", "effect.jujutsucraft.six_eyes", JjaGetCommand::hasSixEyes))
                .then(createBooleanGetter("twinned_body", "advancements.twinned_body.title", TwinnedBodyRuntimeStateAccess::isTwinnedBodyMarked))
                .then(
                    Commands.literal("attack_target")
                        .executes(ctx -> executeGetAttackTarget(ctx.getSource(), null))
                        .then(
                            Commands.argument("player", Objects.requireNonNull(EntityArgument.player()))
                                .requires(source -> source.hasPermission(2))
                                .executes(ctx -> executeGetAttackTarget(ctx.getSource(), EntityArgument.getPlayer(ctx, "player")))
                        )
                )
        );

        event.getDispatcher().register(root);
    }

    private static LiteralArgumentBuilder<CommandSourceStack> createBooleanGetter(
        String literal,
        String messageKey,
        BooleanGetter getter
    ) {
        LiteralArgumentBuilder<CommandSourceStack> builder = Commands.literal(Objects.requireNonNull(literal));
        builder.requires(source -> source.hasPermission(2));
        builder.executes(ctx -> executeGetBoolean(ctx.getSource(), null, messageKey, getter));
        builder.then(
            Commands.argument("player", Objects.requireNonNull(EntityArgument.player()))
                .executes(ctx -> executeGetBoolean(ctx.getSource(), EntityArgument.getPlayer(ctx, "player"), messageKey, getter))
        );
        return builder;
    }

    private static LiteralArgumentBuilder<CommandSourceStack> createNumericGetter(String literal, JjaGetType type) {
        LiteralArgumentBuilder<CommandSourceStack> builder = Commands.literal(Objects.requireNonNull(literal));
        if (type.requiresOp()) {
            builder.requires(source -> source.hasPermission(2));
        }
        builder.executes(ctx -> executeGetNumeric(ctx.getSource(), null, type));
        builder.then(
            Commands.argument("player", Objects.requireNonNull(EntityArgument.player()))
                .executes(ctx -> executeGetNumeric(ctx.getSource(), EntityArgument.getPlayer(ctx, "player"), type))
        );
        return builder;
    }

    private static LiteralArgumentBuilder<CommandSourceStack> createCeColorGetter() {
        LiteralArgumentBuilder<CommandSourceStack> builder = Commands.literal("ce_color");
        builder.requires(source -> source.hasPermission(2));
        builder.executes(ctx -> executeGetCeColor(ctx.getSource(), null));
        builder.then(
            Commands.argument("player", Objects.requireNonNull(EntityArgument.player()))
                .executes(ctx -> executeGetCeColor(ctx.getSource(), EntityArgument.getPlayer(ctx, "player")))
        );
        return builder;
    }

    private static int executeGetNumeric(CommandSourceStack source, @Nullable ServerPlayer target, JjaGetType type) {
        @Nullable ServerPlayer resolvedTarget = JjaCommandHelper.resolveTargetOrSelf(source, target);
        if (resolvedTarget == null) {
            return 0;
        }

        double value = type.extract(resolvedTarget);
        source.sendSuccess(() -> type.format(value), false);
        return 1;
    }

    private static int executeGetCeColor(CommandSourceStack source, @Nullable ServerPlayer target) {
        @Nullable ServerPlayer resolvedTarget = JjaCommandHelper.resolveTargetOrSelf(source, target);
        if (resolvedTarget == null) {
            return 0;
        }

        String colorLiteral = CEColorService.resolveCurrentColorLiteral(resolvedTarget);
        String displayValue = colorLiteral != null ? colorLiteral : Component.translatable("message.jja.get.none").getString();
        source.sendSuccess(() -> Component.translatable("message.jja.get.ce_color", displayValue), false);
        return 1;
    }

    private static int executeGetBoolean(CommandSourceStack source, @Nullable ServerPlayer target, String messageKey, BooleanGetter getter) {
        @Nullable ServerPlayer resolvedTarget = JjaCommandHelper.resolveTargetOrSelf(source, target);
        if (resolvedTarget == null) {
            return 0;
        }

        boolean value = getter.extract(resolvedTarget);
        source.sendSuccess(() -> formatBoolean(messageKey, value), false);
        return 1;
    }

    private static int executeGetAttackTarget(CommandSourceStack source, @Nullable ServerPlayer target) {
        @Nullable ServerPlayer resolvedTarget = JjaCommandHelper.resolveTargetOrSelf(source, target);
        if (resolvedTarget == null) {
            return 0;
        }

        PlayerRctState rctState = PlayerStateAccess.rct(resolvedTarget);
        if (rctState == null) {
            return 0;
        }

        List<String> targets = rctState.getAttackTargetKeys().stream().map(key -> resolveAttackTargetLabel(source, key)).sorted(
            String.CASE_INSENSITIVE_ORDER
        ).toList();
        Component message = targets.isEmpty()
            ? Component.translatable("message.jja.get.none")
            : Component.literal(String.join("\n", targets));
        source.sendSuccess(() -> message, false);
        return 1;
    }

    private static String resolveAttackTargetLabel(CommandSourceStack source, String rawKey) {
        try {
            UUID targetId = Objects.requireNonNull(UUID.fromString(rawKey));
            if (source.getServer() != null) {
                ServerPlayer target = Objects.requireNonNull(source.getServer()).getPlayerList().getPlayer(targetId);
                if (target != null) {
                    return target.getGameProfile().getName();
                }
            }
        } catch (IllegalArgumentException exception) {
            LOGGER.warn("Invalid attack target UUID '{}' in RCT state; showing the raw key instead.", rawKey, exception);
            return rawKey;
        }
        return rawKey;
    }

    private static boolean hasSixEyes(ServerPlayer player) {
        @Nullable JujutsucraftModVariables.PlayerVariables variables = JjaJujutsucraftCompat.jjaGetPlayerVariables(player);
        if (variables != null && variables.FlagSixEyes) {
            return true;
        }
        return player.hasEffect(Objects.requireNonNull(JujutsucraftModMobEffects.SIX_EYES.get()));
    }

    private static Component formatBoolean(String messageKey, boolean value) {
        return Objects.requireNonNull(Component.translatable(Objects.requireNonNull(messageKey)))
            .append(Objects.requireNonNull(Component.literal(": ")))
            .append(Objects.requireNonNull(Component.translatable(value ? "options.on" : "options.off")));
    }

    private enum JjaGetType {
        FAME("message.jja.get.fame", true) {
            @Override
            double extract(ServerPlayer player) {
                return JjaJujutsucraftCompat.jjaGetPlayerFame(player);
            }
        },
        CT_USED("message.jja.get.ct_used", true) {
            @Override
            double extract(ServerPlayer player) {
                return JjaJujutsucraftCompat.jjaGetPlayerTechniqueUsedNumber(player);
            }
        },
        REMAINING_REVIVES("message.jja.get.remaining_revives", false) {
            @Override
            double extract(ServerPlayer player) {
                PlayerReviveState reviveState = PlayerStateAccess.revive(player);
                return reviveState == null ? 0.0D : reviveState.getRemainingRevives();
            }
        },
        RCT_USED("message.jja.get.rct_used", true) {
            @Override
            double extract(ServerPlayer player) {
                PlayerRctState rctState = PlayerStateAccess.rct(player);
                return rctState == null ? 0.0D : rctState.getRctHealed();
            }
        },
        BF_RANDED("message.jja.get.bf_randed", true) {
            @Override
            double extract(ServerPlayer player) {
                return AddonStatsAccess.getCounter(player, AddonStatCounter.BF_RANDED);
            }
        },
        DE_USED("message.jja.get.de_used", true) {
            @Override
            double extract(ServerPlayer player) {
                return AddonStatsAccess.getCounter(player, AddonStatCounter.DE_USED);
            }
        },
        SD_USED("message.jja.get.sd_used", true) {
            @Override
            double extract(ServerPlayer player) {
                return AddonStatsAccess.getCounter(player, AddonStatCounter.SIMPLE_DOMAIN_USED);
            }
        },
        FBE_USED("message.jja.get.fbe_used", true) {
            @Override
            double extract(ServerPlayer player) {
                return AddonStatsAccess.getCounter(player, AddonStatCounter.FBE_USED);
            }
        };

        private final String messageKey;
        private final boolean requiresOp;

        JjaGetType(String messageKey, boolean requiresOp) {
            this.messageKey = messageKey;
            this.requiresOp = requiresOp;
        }

        abstract double extract(ServerPlayer player);

        boolean requiresOp() {
            return this.requiresOp;
        }

        Component format(double value) {
            return Component.translatable(Objects.requireNonNull(this.messageKey), Math.round(value));
        }
    }

    @FunctionalInterface
    private interface BooleanGetter {
        boolean extract(ServerPlayer player);
    }
}
