package com.arf8vhg7.jja.feature.player.progression.fame.network;

import com.arf8vhg7.jja.feature.player.progression.fame.client.FameChatMessageRenderer;
import com.arf8vhg7.jja.network.JjaPacketHandlers;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent.Context;

public final class JjaFameGainClientMessage {
    private final TargetType targetType;
    private final String resultKey;
    private final long fameAmount;
    private final boolean mvp;

    public JjaFameGainClientMessage(TargetType targetType, String resultKey, long fameAmount, boolean mvp) {
        this.targetType = targetType;
        this.resultKey = resultKey;
        this.fameAmount = fameAmount;
        this.mvp = mvp;
    }

    public JjaFameGainClientMessage(FriendlyByteBuf buffer) {
        this.targetType = decodeTargetType(buffer.readVarInt());
        this.resultKey = buffer.readUtf();
        this.fameAmount = buffer.readVarLong();
        this.mvp = buffer.readBoolean();
    }

    public static void encode(JjaFameGainClientMessage message, FriendlyByteBuf buffer) {
        buffer.writeVarInt(message.targetType.ordinal());
        buffer.writeUtf(message.resultKey);
        buffer.writeVarLong(message.fameAmount);
        buffer.writeBoolean(message.mvp);
    }

    public static void handle(JjaFameGainClientMessage message, Supplier<Context> contextSupplier) {
        JjaPacketHandlers.handleClient(contextSupplier, () -> FameChatMessageRenderer.display(message));
    }

    public TargetType targetType() {
        return this.targetType;
    }

    public String resultKey() {
        return this.resultKey;
    }

    public long fameAmount() {
        return this.fameAmount;
    }

    public boolean mvp() {
        return this.mvp;
    }

    private static TargetType decodeTargetType(int ordinal) {
        TargetType[] values = TargetType.values();
        return ordinal >= 0 && ordinal < values.length ? values[ordinal] : TargetType.JJK_CHARA;
    }

    public enum TargetType {
        CURSED_SPIRIT("jujutsu.message.kill1_1"),
        CURSE_USER("jujutsu.message.kill1_2"),
        JUJUTSU_SORCERER("jujutsu.message.kill1_3"),
        JJK_CHARA("");

        private final String prefixKey;

        TargetType(String prefixKey) {
            this.prefixKey = prefixKey;
        }

        public String prefixKey() {
            return this.prefixKey;
        }
    }
}
