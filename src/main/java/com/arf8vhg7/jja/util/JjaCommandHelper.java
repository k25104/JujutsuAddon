package com.arf8vhg7.jja.util;

import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

public final class JjaCommandHelper {
    private JjaCommandHelper() {
    }

    public static @Nullable ServerPlayer resolveTargetOrSelf(CommandSourceStack source, @Nullable ServerPlayer target) {
        if (target != null) {
            return target;
        }
        Entity entity = source.getEntity();
        return entity instanceof ServerPlayer player ? player : null;
    }

    public static boolean executeAsEntity(Entity entity, String command) {
        if (!(entity.level() instanceof ServerLevel level) || entity.getServer() == null) {
            return false;
        }
        Objects.requireNonNull(level.getServer())
            .getCommands()
            .performPrefixedCommand(
                new CommandSourceStack(
                    Objects.requireNonNull(CommandSource.NULL),
                    Objects.requireNonNull(entity.position()),
                    Objects.requireNonNull(entity.getRotationVector()),
                    level,
                    4,
                    Objects.requireNonNull(entity.getName().getString()),
                    Objects.requireNonNull(entity.getDisplayName()),
                    Objects.requireNonNull(level.getServer()),
                    entity
                ),
                Objects.requireNonNull(command)
            );
        return true;
    }
}
