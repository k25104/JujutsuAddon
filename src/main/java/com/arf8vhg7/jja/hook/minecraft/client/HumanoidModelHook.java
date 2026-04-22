package com.arf8vhg7.jja.hook.minecraft.client;

import com.arf8vhg7.jja.compat.minecraft.HumanoidModelTwinnedBodyCarrierAccess;
import net.minecraft.client.model.HumanoidModel;

public final class HumanoidModelHook {
    private HumanoidModelHook() {
    }

    public static boolean isTwinnedBodyCarrier(HumanoidModel<?> model) {
        return model instanceof HumanoidModelTwinnedBodyCarrierAccess carrierAccess && carrierAccess.jja$isTwinnedBodyCarrier();
    }
}