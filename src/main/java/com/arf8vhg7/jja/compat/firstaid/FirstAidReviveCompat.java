package com.arf8vhg7.jja.compat.firstaid;

import javax.annotation.Nullable;
import net.minecraft.world.entity.player.Player;

public final class FirstAidReviveCompat {
    private FirstAidReviveCompat() {
    }

    public static boolean installReviveCompat(@Nullable FallbackKnockoutBridge bridge) {
        return FirstAidCompatRuntime.installReviveCompat(bridge);
    }

    public static void setBeingRevived(@Nullable Player player, boolean beingRevived) {
        FirstAidCompatRuntime.setBeingRevived(player, beingRevived);
    }

    public static void reviveDamageModel(@Nullable Player player) {
        FirstAidCompatRuntime.reviveDamageModel(player);
    }

    public interface FallbackKnockoutBridge extends FirstAidCompatRuntime.FallbackKnockoutBridge {
    }
}
