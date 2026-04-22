package com.arf8vhg7.jja.hook.minecraft;

import com.arf8vhg7.jja.feature.jja.resource.ce.CEParticleRemapService;
import javax.annotation.Nullable;

public final class CommandsHook {
    private CommandsHook() {
    }

    public static @Nullable String remapCeParticleCommand(@Nullable String command) {
        return CEParticleRemapService.remapCursePowerParticleCommand(command);
    }
}
