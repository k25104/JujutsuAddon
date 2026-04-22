package com.arf8vhg7.jja.feature.jja.technique.shared.registration;

import com.arf8vhg7.jja.JujutsuAddon;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(modid = JujutsuAddon.MODID)
public final class JjaSkillManagementCommand {
    private JjaSkillManagementCommand() {
    }

    @SubscribeEvent
    public static void registerCommand(RegisterCommandsEvent event) {
        LiteralArgumentBuilder<CommandSourceStack> root = Commands.literal("jja");
        root.then(
            Commands.literal("skill_management")
                .then(
                    Commands.argument("skill_name", StringArgumentType.greedyString())
                        .suggests((ctx, builder) -> suggestSkills(ctx.getSource(), builder))
                        .executes(ctx -> execute(ctx.getSource(), StringArgumentType.getString(ctx, "skill_name")))
                )
        );
        event.getDispatcher().register(root);
    }

    private static int execute(CommandSourceStack source, String rawSkillName) {
        if (!(source.getEntity() instanceof ServerPlayer player)) {
            return 0;
        }
        String skillName = rawSkillName == null ? "" : rawSkillName.trim();
        if (skillName.isEmpty()) {
            source.sendFailure(Component.translatable("message.jja.skill_management.not_found", rawSkillName));
            return 0;
        }
        List<JjaSkillManagementProbe.DisplaySkillCandidate> candidates = JjaSkillManagementProbe.collectCandidates(player);
        List<String> canonicalNames = resolveCanonicalNames(candidates, skillName);
        if (canonicalNames.isEmpty()) {
            source.sendFailure(Component.translatable("message.jja.skill_management.not_found", skillName));
            return 0;
        }
        String displayLabel = resolveDisplayLabel(candidates, skillName);
        JjaSkillManagementService.ToggleResult result = JjaSkillManagementService.toggleHiddenSkills(player, canonicalNames);
        if (!result.changed()) {
            source.sendFailure(Component.translatable("message.jja.skill_management.not_found", skillName));
            return 0;
        }
        String messageKey = result.hidden() ? "message.jja.skill_management.hidden" : "message.jja.skill_management.unhidden";
        source.sendSuccess(() -> Component.translatable(messageKey, displayLabel), false);
        return 1;
    }

    private static List<String> resolveCanonicalNames(List<JjaSkillManagementProbe.DisplaySkillCandidate> candidates, String input) {
        List<String> exactDisplayMatches = new ArrayList<>();
        for (JjaSkillManagementProbe.DisplaySkillCandidate candidate : candidates) {
            if (candidate.displayName().equalsIgnoreCase(input)) {
                exactDisplayMatches.addAll(candidate.canonicalNames());
            }
        }
        if (!exactDisplayMatches.isEmpty()) {
            return exactDisplayMatches;
        }
        List<String> canonicalMatches = new ArrayList<>();
        for (JjaSkillManagementProbe.DisplaySkillCandidate candidate : candidates) {
            for (String canonicalName : candidate.canonicalNames()) {
                if (canonicalName.equalsIgnoreCase(input)) {
                    canonicalMatches.add(canonicalName);
                }
            }
        }
        return canonicalMatches;
    }

    private static String resolveDisplayLabel(List<JjaSkillManagementProbe.DisplaySkillCandidate> candidates, String input) {
        for (JjaSkillManagementProbe.DisplaySkillCandidate candidate : candidates) {
            if (candidate.displayName().equalsIgnoreCase(input)) {
                return candidate.displayName();
            }
            for (String canonicalName : candidate.canonicalNames()) {
                if (canonicalName.equalsIgnoreCase(input)) {
                    return candidate.displayName();
                }
            }
        }
        return input;
    }

    private static java.util.concurrent.CompletableFuture<com.mojang.brigadier.suggestion.Suggestions> suggestSkills(
        CommandSourceStack source,
        com.mojang.brigadier.suggestion.SuggestionsBuilder builder
    ) {
        if (!(source.getEntity() instanceof ServerPlayer player)) {
            return builder.buildFuture();
        }
        return SharedSuggestionProvider.suggest(
            JjaSkillManagementProbe.collectCandidates(player).stream().map(JjaSkillManagementProbe.DisplaySkillCandidate::displayName),
            builder
        );
    }
}
