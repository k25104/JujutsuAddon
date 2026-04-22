package com.arf8vhg7.jja.hook.jujutsucraft;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public final class KeyDomainAmplificationOnKeyPressedProcedureHook {
    private KeyDomainAmplificationOnKeyPressedProcedureHook() {
    }

    public static MutableComponent buildDomainAmplificationStateMessage(boolean active) {
        return buildTechniqueStateMessage(Component.translatable("effect.domain_amplification"), active);
    }

    public static MutableComponent buildNotMasteredMessage() {
        return Component.translatable("jujutsu.message.not_mastered");
    }

    static MutableComponent buildTechniqueStateMessage(MutableComponent techniqueLabel, boolean active) {
        MutableComponent message = techniqueLabel == null ? Component.empty() : techniqueLabel.copy();
        message.append(Component.literal(": "));
        message.append(Component.literal(Boolean.toString(active)));
        return message;
    }
}
