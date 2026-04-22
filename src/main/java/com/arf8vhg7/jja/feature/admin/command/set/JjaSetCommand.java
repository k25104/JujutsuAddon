package com.arf8vhg7.jja.feature.admin.command.set;

import com.arf8vhg7.jja.JujutsuAddon;
import com.arf8vhg7.jja.compat.jujutsucraft.JjaJujutsucraftCompat;
import com.arf8vhg7.jja.feature.jja.domain.de.DomainExpansionProgression;
import com.arf8vhg7.jja.feature.jja.domain.de.curtain.CurtainSyncService;
import com.arf8vhg7.jja.feature.jja.domain.sd.HollowWickerBasketProgression;
import com.arf8vhg7.jja.feature.jja.resource.ce.JjaCursePowerAccountingService;
import com.arf8vhg7.jja.feature.jja.resource.ce.CEColorService;
import com.arf8vhg7.jja.feature.jja.rct.RctHealTracker;
import com.arf8vhg7.jja.feature.jja.traits.twinnedbody.TwinnedBodyRuntimeStateAccess;
import com.arf8vhg7.jja.feature.jja.traits.twinnedbody.TwinnedBodySyncService;
import com.arf8vhg7.jja.feature.player.progression.grade.SorcererGradeAdvancementHelper;
import com.arf8vhg7.jja.feature.player.state.AddonStatCounter;
import com.arf8vhg7.jja.feature.player.state.AddonStatsAccess;
import com.arf8vhg7.jja.feature.player.state.JjaPlayerStateSync;
import com.arf8vhg7.jja.feature.player.state.PlayerStateAccess;
import com.arf8vhg7.jja.feature.player.state.model.PlayerAddonStatsState;
import com.arf8vhg7.jja.feature.player.state.model.PlayerRctState;
import com.arf8vhg7.jja.feature.player.state.model.PlayerReviveState;
import com.arf8vhg7.jja.feature.player.revive.ReviveSyncService;
import com.arf8vhg7.jja.util.JjaCommandHelper;
import com.arf8vhg7.jja.util.JjaAdvancementHelper;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import java.util.Objects;
import javax.annotation.Nullable;
import net.mcreator.jujutsucraft.init.JujutsucraftModGameRules;
import net.mcreator.jujutsucraft.init.JujutsucraftModMobEffects;
import net.mcreator.jujutsucraft.network.JujutsucraftModVariables;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(modid = JujutsuAddon.MODID)
public final class JjaSetCommand {
    private static final ResourceLocation SIX_EYES_ADVANCEMENT_ID = ResourceLocation.fromNamespaceAndPath("jujutsucraft", "advancement_six_eyes");
    private static final ResourceLocation TWINNED_BODY_ADVANCEMENT_ID = ResourceLocation.fromNamespaceAndPath("jja", "twinned_body");

    private JjaSetCommand() {
    }

    @SubscribeEvent
    public static void registerCommand(RegisterCommandsEvent event) {
        LiteralArgumentBuilder<CommandSourceStack> root = Commands.literal("jja");
        root.then(
            Commands.literal("set")
                .then(
                    Commands.literal("attack_target")
                        .then(
                            Commands.argument("player", Objects.requireNonNull(EntityArgument.player()))
                                .executes(ctx -> executeAttackTargetToggle(ctx.getSource(), EntityArgument.getPlayer(ctx, "player")))
                        )
                )
                .then(createIntSetter("bf_randed", JjaSetCommand::setBfRanded))
                .then(createCeColorSetter())
                .then(createIntSetter("curtain_radius", 1, JjaSetCommand::setCurtainRadius))
                .then(createIntSetter("ct_used", JjaSetCommand::setCtUsed))
                .then(createIntSetter("de_used", JjaSetCommand::setDeUsed))
                .then(createDoubleSetter("fame", JjaSetCommand::setFame))
                .then(createBooleanSetter("six_eyes", JjaSetCommand::setSixEyes))
                .then(createBooleanSetter("twinned_body", JjaSetCommand::setTwinnedBody))
                .then(createIntSetter("rct_used", JjaSetCommand::setRctUsed))
                .then(createIntSetter("remaining_revives", JjaSetCommand::setRemainingRevives))
                .then(createIntSetter("sd_used", JjaSetCommand::setSdUsed))
                .then(createIntSetter("fbe_used", JjaSetCommand::setFbeUsed))
        );
        event.getDispatcher().register(root);
    }

    private static LiteralArgumentBuilder<CommandSourceStack> createIntSetter(String literal, IntSetter setter) {
        return createIntSetter(literal, 0, setter);
    }

    private static LiteralArgumentBuilder<CommandSourceStack> createIntSetter(String literal, int minValue, IntSetter setter) {
        return Commands.literal(Objects.requireNonNull(literal))
            .requires(source -> source.hasPermission(2))
            .then(
                Commands.argument("value", Objects.requireNonNull(IntegerArgumentType.integer(minValue)))
                    .executes(ctx -> executeIntSet(ctx.getSource(), null, IntegerArgumentType.getInteger(ctx, "value"), setter))
                    .then(
                        Commands.argument("player", Objects.requireNonNull(EntityArgument.player()))
                            .executes(
                                ctx -> executeIntSet(
                                    ctx.getSource(),
                                    EntityArgument.getPlayer(ctx, "player"),
                                    IntegerArgumentType.getInteger(ctx, "value"),
                                    setter
                                )
                            )
                    )
            );
    }

    private static LiteralArgumentBuilder<CommandSourceStack> createDoubleSetter(String literal, DoubleSetter setter) {
        return Commands.literal(Objects.requireNonNull(literal))
            .requires(source -> source.hasPermission(2))
            .then(
                Commands.argument("value", Objects.requireNonNull(DoubleArgumentType.doubleArg(0.0D)))
                    .executes(ctx -> executeDoubleSet(ctx.getSource(), null, DoubleArgumentType.getDouble(ctx, "value"), setter))
                    .then(
                        Commands.argument("player", Objects.requireNonNull(EntityArgument.player()))
                            .executes(
                                ctx -> executeDoubleSet(
                                    ctx.getSource(),
                                    EntityArgument.getPlayer(ctx, "player"),
                                    DoubleArgumentType.getDouble(ctx, "value"),
                                    setter
                                )
                            )
                    )
            );
    }

    private static LiteralArgumentBuilder<CommandSourceStack> createBooleanSetter(String literal, BooleanSetter setter) {
        return Commands.literal(Objects.requireNonNull(literal))
            .requires(source -> source.hasPermission(2))
            .then(
                Commands.argument("value", Objects.requireNonNull(BoolArgumentType.bool()))
                    .executes(ctx -> executeBooleanSet(ctx.getSource(), null, BoolArgumentType.getBool(ctx, "value"), setter))
                    .then(
                        Commands.argument("player", Objects.requireNonNull(EntityArgument.player()))
                            .executes(
                                ctx -> executeBooleanSet(
                                    ctx.getSource(),
                                    EntityArgument.getPlayer(ctx, "player"),
                                    BoolArgumentType.getBool(ctx, "value"),
                                    setter
                                )
                            )
                    )
            );
    }

    private static LiteralArgumentBuilder<CommandSourceStack> createCeColorSetter() {
        LiteralArgumentBuilder<CommandSourceStack> builder = Commands.literal("ce_color").requires(source -> source.hasPermission(2));
        addCeColorLiteral(builder, "red");
        addCeColorLiteral(builder, "orange");
        addCeColorLiteral(builder, "blue");
        addCeColorLiteral(builder, "green");
        addCeColorLiteral(builder, "purple");
        return builder;
    }

    private static void addCeColorLiteral(LiteralArgumentBuilder<CommandSourceStack> builder, String literal) {
        builder.then(
            Commands.literal(literal)
                .executes(ctx -> executeCeColorValue(ctx.getSource(), null, literal))
                .then(
                    Commands.argument("player", Objects.requireNonNull(EntityArgument.player()))
                        .executes(ctx -> executeCeColorValue(ctx.getSource(), EntityArgument.getPlayer(ctx, "player"), literal))
                )
        );
    }

    private static int executeAttackTargetToggle(CommandSourceStack source, @Nullable ServerPlayer target) {
        if (!(source.getEntity() instanceof ServerPlayer player) || target == null) {
            return 0;
        }
        PlayerRctState rctState = PlayerStateAccess.rct(player);
        if (rctState == null) {
            return 0;
        }
        rctState.toggleAttackTarget(target.getUUID());
        JjaPlayerStateSync.sync(player);
        CurtainSyncService.syncAllPlayers(source.getServer());
        return 1;
    }

    private static int executeIntSet(CommandSourceStack source, @Nullable ServerPlayer target, int value, IntSetter setter) {
        @Nullable ServerPlayer resolvedTarget = JjaCommandHelper.resolveTargetOrSelf(source, target);
        if (resolvedTarget == null) {
            return 0;
        }
        return setter.apply(resolvedTarget, value) ? 1 : 0;
    }

    private static int executeDoubleSet(CommandSourceStack source, @Nullable ServerPlayer target, double value, DoubleSetter setter) {
        @Nullable ServerPlayer resolvedTarget = JjaCommandHelper.resolveTargetOrSelf(source, target);
        if (resolvedTarget == null) {
            return 0;
        }
        return setter.apply(resolvedTarget, value) ? 1 : 0;
    }

    private static int executeBooleanSet(CommandSourceStack source, @Nullable ServerPlayer target, boolean value, BooleanSetter setter) {
        @Nullable ServerPlayer resolvedTarget = JjaCommandHelper.resolveTargetOrSelf(source, target);
        if (resolvedTarget == null) {
            return 0;
        }
        return setter.apply(resolvedTarget, value) ? 1 : 0;
    }

    private static int executeCeColorValue(CommandSourceStack source, @Nullable ServerPlayer target, String literal) {
        @Nullable ServerPlayer resolvedTarget = JjaCommandHelper.resolveTargetOrSelf(source, target);
        if (resolvedTarget == null) {
            return 0;
        }
        Integer value = CEColorService.parseLiteralColorId(literal);
        if (value == null || !CEColorService.allowsOverride(resolvedTarget)) {
            return 0;
        }
        PlayerAddonStatsState addonStats = PlayerStateAccess.addonStats(resolvedTarget);
        if (addonStats == null) {
            return 0;
        }
        addonStats.setCeColorOverride(value.intValue());
        JjaPlayerStateSync.sync(resolvedTarget);
        return 1;
    }

    private static boolean setBfRanded(ServerPlayer target, int value) {
        if (!AddonStatsAccess.setCounter(target, AddonStatCounter.BF_RANDED, value)) {
            return false;
        }
        JjaPlayerStateSync.sync(target);
        return true;
    }

    private static boolean setCtUsed(ServerPlayer target, int value) {
        @Nullable JujutsucraftModVariables.PlayerVariables variables = JjaJujutsucraftCompat.jjaGetPlayerVariables(target);
        if (variables == null) {
            return false;
        }
        variables.PlayerTechniqueUsedNumber = value;
        JjaCursePowerAccountingService.refreshPlayerCursePowerFormer(target);
        HollowWickerBasketProgression.awardFromTechniqueUsage(target);
        DomainExpansionProgression.awardFromTechniqueUsage(target);
        return true;
    }

    private static boolean setDeUsed(ServerPlayer target, int value) {
        if (!AddonStatsAccess.setCounter(target, AddonStatCounter.DE_USED, value)) {
            return false;
        }
        JjaPlayerStateSync.sync(target);
        return true;
    }

    private static boolean setFame(ServerPlayer target, double value) {
        @Nullable JujutsucraftModVariables.PlayerVariables variables = JjaJujutsucraftCompat.jjaGetPlayerVariables(target);
        if (variables == null) {
            return false;
        }
        variables.PlayerFame = Math.max(0.0D, value);
        variables.syncPlayerVariables(target);
        double difficulty = target.level().getGameRules().getInt(Objects.requireNonNull(JujutsucraftModGameRules.JUJUTSUUPGRADEDIFFICULTY));
        SorcererGradeAdvancementHelper.syncSpecialTierFromFame(target, variables.PlayerFame, difficulty);
        return true;
    }

    private static boolean setSixEyes(ServerPlayer target, boolean value) {
        @Nullable JujutsucraftModVariables.PlayerVariables variables = JjaJujutsucraftCompat.jjaGetPlayerVariables(target);
        if (variables == null) {
            return false;
        }

        MobEffect sixEyesEffect = Objects.requireNonNull(JujutsucraftModMobEffects.SIX_EYES.get());
        variables.FlagSixEyes = value;
        variables.syncPlayerVariables(target);

        if (value) {
            MobEffectInstance effect = new MobEffectInstance(sixEyesEffect, Integer.MAX_VALUE, 0, false, false);
            target.addEffect(effect);
            JjaAdvancementHelper.award(target, SIX_EYES_ADVANCEMENT_ID);
        } else {
            target.removeEffect(sixEyesEffect);
            JjaAdvancementHelper.revoke(target, SIX_EYES_ADVANCEMENT_ID);
        }
        return true;
    }

    private static boolean setTwinnedBody(ServerPlayer target, boolean value) {
        if (value) {
            TwinnedBodyRuntimeStateAccess.markTwinnedBody(target);
            JjaAdvancementHelper.award(target, TWINNED_BODY_ADVANCEMENT_ID);
        } else {
            TwinnedBodyRuntimeStateAccess.clearTwinnedBody(target);
            JjaAdvancementHelper.revoke(target, TWINNED_BODY_ADVANCEMENT_ID);
        }

        TwinnedBodySyncService.syncTrackingState(target);
        return true;
    }

    private static boolean setRctUsed(ServerPlayer target, int value) {
        PlayerRctState rctState = PlayerStateAccess.rct(target);
        if (rctState == null) {
            return false;
        }
        rctState.setRctHealed(value);
        JjaPlayerStateSync.sync(target);
        RctHealTracker.awardProgression(target);
        return true;
    }

    private static boolean setCurtainRadius(ServerPlayer target, int value) {
        PlayerAddonStatsState addonStats = PlayerStateAccess.addonStats(target);
        if (addonStats == null) {
            return false;
        }
        addonStats.setCurtainRadius(value);
        JjaPlayerStateSync.sync(target);
        return true;
    }

    private static boolean setRemainingRevives(ServerPlayer target, int value) {
        PlayerReviveState reviveState = PlayerStateAccess.revive(target);
        if (reviveState == null) {
            return false;
        }
        reviveState.setRemainingRevives(value);
        JjaPlayerStateSync.sync(target);
        ReviveSyncService.syncWaitingState(target);
        return true;
    }

    private static boolean setSdUsed(ServerPlayer target, int value) {
        if (!AddonStatsAccess.setCounter(target, AddonStatCounter.SIMPLE_DOMAIN_USED, value)) {
            return false;
        }
        JjaPlayerStateSync.sync(target);
        return true;
    }

    private static boolean setFbeUsed(ServerPlayer target, int value) {
        if (!AddonStatsAccess.setCounter(target, AddonStatCounter.FBE_USED, value)) {
            return false;
        }
        JjaPlayerStateSync.sync(target);
        return true;
    }

    @FunctionalInterface
    private interface IntSetter {
        boolean apply(ServerPlayer target, int value);
    }

    @FunctionalInterface
    private interface DoubleSetter {
        boolean apply(ServerPlayer target, double value);
    }

    @FunctionalInterface
    private interface BooleanSetter {
        boolean apply(ServerPlayer target, boolean value);
    }
}
