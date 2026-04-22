package com.arf8vhg7.jja.client.keymap;

import com.arf8vhg7.jja.network.JjaNetwork;
import java.util.function.Supplier;

public final class JjaPacketKeyMapping extends JjaEdgeTriggeredKeyMapping {
    private final Supplier<Object> messageFactory;

    public JjaPacketKeyMapping(String translationKey, Supplier<Object> messageFactory) {
        super(translationKey);
        this.messageFactory = messageFactory;
    }

    @Override
    protected void onEdgeChange(boolean isDown) {
        if (isDown && JjaKeyMappingSupport.canProcessInput()) {
            JjaNetwork.CHANNEL.sendToServer(this.messageFactory.get());
        }
    }
}
