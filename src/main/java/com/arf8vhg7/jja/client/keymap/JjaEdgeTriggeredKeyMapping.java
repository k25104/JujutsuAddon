package com.arf8vhg7.jja.client.keymap;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;

public abstract class JjaEdgeTriggeredKeyMapping extends KeyMapping {
    private boolean wasDown;

    protected JjaEdgeTriggeredKeyMapping(String translationKey) {
        super(translationKey, InputConstants.UNKNOWN.getValue(), JjaKeyMappingSupport.CATEGORY);
    }

    protected JjaEdgeTriggeredKeyMapping(String translationKey, int defaultKey) {
        super(translationKey, defaultKey, JjaKeyMappingSupport.CATEGORY);
    }

    @Override
    public final void setDown(boolean isDown) {
        super.setDown(isDown);
        if (this.wasDown != isDown) {
            onEdgeChange(isDown);
        }
        this.wasDown = isDown;
    }

    protected abstract void onEdgeChange(boolean isDown);
}
