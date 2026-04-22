package com.arf8vhg7.jja.feature.player.progression.fame.client;

import com.arf8vhg7.jja.feature.player.progression.fame.network.JjaFameGainClientMessage;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public final class FameChatMessageRenderer {
    private static final String POINT_PLACEHOLDER = "[point]";

    private FameChatMessageRenderer() {
    }

    public static void display(JjaFameGainClientMessage message) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }
        player.displayClientMessage(render(message), false);
    }

    public static MutableComponent render(JjaFameGainClientMessage message) {
        return buildMessage(localizePrefix(message.targetType()), I18n.get(message.resultKey()), message.fameAmount(), message.mvp());
    }

    static MutableComponent buildMessage(String localizedPrefix, String localizedResult, long fameAmount, boolean mvp) {
        MutableComponent message = Component.empty();
        if (mvp) {
            message.append(Component.literal("[MVP]"));
        }
        if (!localizedPrefix.isEmpty()) {
            message.append(Component.literal(localizedPrefix));
        }
        appendLocalizedResult(message, localizedResult, fameAmount);
        return message;
    }

    private static void appendLocalizedResult(MutableComponent message, String localizedResult, long fameAmount) {
        int pointIndex = localizedResult.indexOf(POINT_PLACEHOLDER);
        if (pointIndex < 0) {
            if (!localizedResult.isEmpty()) {
                message.append(Component.literal(localizedResult));
                message.append(Component.literal(" "));
            }
            message.append(fameAmountComponent(fameAmount));
            return;
        }

        String before = localizedResult.substring(0, pointIndex);
        String after = localizedResult.substring(pointIndex + POINT_PLACEHOLDER.length());
        if (!before.isEmpty()) {
            message.append(Component.literal(before));
        }
        message.append(fameAmountComponent(fameAmount));
        if (!after.isEmpty()) {
            message.append(Component.literal(after));
        }
    }

    private static MutableComponent fameAmountComponent(long fameAmount) {
        return Component.literal(Long.toString(fameAmount)).withStyle(ChatFormatting.BOLD);
    }

    private static String localizePrefix(JjaFameGainClientMessage.TargetType targetType) {
        if (targetType == JjaFameGainClientMessage.TargetType.JJK_CHARA) {
            return " ";
        }
        return I18n.get(targetType.prefixKey());
    }
}
