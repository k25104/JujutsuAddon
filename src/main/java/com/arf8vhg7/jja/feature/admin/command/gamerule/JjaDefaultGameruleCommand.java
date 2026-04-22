package com.arf8vhg7.jja.feature.admin.command.gamerule;

import com.arf8vhg7.jja.JujutsuAddon;
import com.arf8vhg7.jja.feature.world.time.JjaTimeGameRules;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ResultConsumer;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import java.util.List;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(modid = JujutsuAddon.MODID)
public final class JjaDefaultGameruleCommand {
    static final int REQUIRED_PERMISSION_LEVEL = 4;
    private static final List<String> DEFAULT_GAMERULE_COMMANDS = List.of(
        "gamerule doEntityDrops false",
        "gamerule doMobLoot false",
        "gamerule doTileDrops false",
        "gamerule doWeatherCycle false",
        "gamerule " + JjaTimeGameRules.JJA_NIGHT_TIME_SPEED_RULE + " 0",
        "gamerule " + JjaTimeGameRules.JJA_DAY_TIME_SPEED_RULE + " 300",
        "gamerule keepInventory true",
        "gamerule spawnRadius 0"
    );

    private JjaDefaultGameruleCommand() {
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
                    Commands.literal("default_gamerule")
                        .requires(source -> source.hasPermission(REQUIRED_PERMISSION_LEVEL))
                        .executes(ctx -> executeHidden(ctx.getSource()))
                )
        );
        dispatcher.register(root);
    }

    static List<String> defaultGameruleCommands() {
        return DEFAULT_GAMERULE_COMMANDS;
    }

    static int executeCommands(CommandSourceStack source, List<String> commands, CommandRunner runner) {
        for (String command : commands) {
            if (!runner.run(source, command).isSuccessful()) {
                return 0;
            }
        }
        return 1;
    }

    public static int executeHidden(CommandSourceStack source) {
        if (!source.hasPermission(REQUIRED_PERMISSION_LEVEL)) {
            return 0;
        }
        return execute(source);
    }

    private static int execute(CommandSourceStack source) {
        MinecraftServer server = source.getServer();
        if (server == null) {
            return 0;
        }

        return executeCommands(source.withSuppressedOutput(), DEFAULT_GAMERULE_COMMANDS, JjaDefaultGameruleCommand::runObservedCommand);
    }

    private static CommandExecutionResult runObservedCommand(CommandSourceStack source, String command) {
        CommandExecutionCapture capture = new CommandExecutionCapture();
        CommandSourceStack observedSource = source.withCallback(
            (context, success, result) -> capture.complete(success, result),
            JjaDefaultGameruleCommand::mergeCallbacks
        );
        int returnedResult = source.getServer().getCommands().performPrefixedCommand(observedSource, command);
        return capture.toResult(returnedResult);
    }

    private static ResultConsumer<CommandSourceStack> mergeCallbacks(
        ResultConsumer<CommandSourceStack> existing,
        ResultConsumer<CommandSourceStack> additional
    ) {
        return (context, success, result) -> {
            if (existing != null) {
                existing.onCommandComplete(context, success, result);
            }
            additional.onCommandComplete(context, success, result);
        };
    }

    @FunctionalInterface
    interface CommandRunner {
        CommandExecutionResult run(CommandSourceStack source, String command);
    }

    static record CommandExecutionResult(boolean callbackInvoked, boolean success, int result) {
        boolean isSuccessful() {
            return this.callbackInvoked && this.success;
        }
    }

    private static final class CommandExecutionCapture {
        private boolean callbackInvoked;
        private boolean success;
        private int result;

        private void complete(boolean commandSucceeded, int commandResult) {
            this.callbackInvoked = true;
            this.success = commandSucceeded;
            this.result = commandResult;
        }

        private CommandExecutionResult toResult(int fallbackResult) {
            return new CommandExecutionResult(this.callbackInvoked, this.success, this.callbackInvoked ? this.result : fallbackResult);
        }
    }
}
