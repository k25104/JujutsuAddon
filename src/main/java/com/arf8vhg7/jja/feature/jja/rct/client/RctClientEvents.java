package com.arf8vhg7.jja.feature.jja.rct.client;

import com.arf8vhg7.jja.JujutsuAddon;
import com.arf8vhg7.jja.config.JjaCommonConfig;
import com.arf8vhg7.jja.feature.jja.rct.network.JjaBrainDestructionHoldMessage;
import com.arf8vhg7.jja.network.JjaNetwork;
import net.mcreator.jujutsucraft.init.JujutsucraftModMobEffects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(modid = JujutsuAddon.MODID, value = Dist.CLIENT)
public final class RctClientEvents {
    private RctClientEvents() {
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent event) {
        if (event.phase != net.minecraftforge.event.TickEvent.Phase.END) {
            return;
        }
        Minecraft minecraft = Minecraft.getInstance();
        if (!JjaCommonConfig.BRAIN_DESTRUCTION_ENABLED.get()) {
            if (RctClientState.isBrainDestructionHolding()) {
                RctClientState.stopBrainDestructionHold();
                clearBrainDestructionMessage(minecraft.player);
            }
            return;
        }
        if (!RctClientState.isBrainDestructionHolding()) {
            return;
        }
        if (!canContinueBrainDestructionHold(minecraft)) {
            stopBrainDestructionHold();
            return;
        }
        RctClientState.tickBrainDestructionHold();
        showBrainDestructionMessage(minecraft.player);
    }

    @SubscribeEvent
    public static void onScreenOpening(ScreenEvent.Opening event) {
        if (event.getNewScreen() != null && RctClientState.isBrainDestructionHolding()) {
            stopBrainDestructionHold();
        }
    }

    public static void startBrainDestructionHold() {
        Minecraft minecraft = Minecraft.getInstance();
        if (!JjaCommonConfig.BRAIN_DESTRUCTION_ENABLED.get()) {
            return;
        }
        if (!canContinueBrainDestructionHold(minecraft)) {
            return;
        }
        JjaNetwork.CHANNEL.sendToServer(new JjaBrainDestructionHoldMessage(true));
    }

    public static void stopBrainDestructionHold() {
        LocalPlayer player = Minecraft.getInstance().player;
        RctClientState.stopBrainDestructionHold();
        clearBrainDestructionMessage(player);
        JjaNetwork.CHANNEL.sendToServer(new JjaBrainDestructionHoldMessage(false));
    }

    public static void applyBrainDestructionHoldState(boolean holding) {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        if (!JjaCommonConfig.BRAIN_DESTRUCTION_ENABLED.get()) {
            RctClientState.stopBrainDestructionHold();
            clearBrainDestructionMessage(player);
            return;
        }
        if (!holding) {
            RctClientState.stopBrainDestructionHold();
            clearBrainDestructionMessage(player);
            return;
        }
        if (!RctKeyMappings.KEY_BRAIN_DESTRUCTION.isDown() || !canContinueBrainDestructionHold(minecraft)) {
            stopBrainDestructionHold();
            return;
        }
        RctClientState.startBrainDestructionHold();
        showBrainDestructionMessage(player);
    }

    private static void showBrainDestructionMessage(LocalPlayer player) {
        if (player != null) {
            player.displayClientMessage(RctUiRenderer.buildBrainDestructionMessage(RctClientState.getBrainDestructionTicks()), true);
        }
    }

    private static void clearBrainDestructionMessage(LocalPlayer player) {
        if (player != null) {
            player.displayClientMessage(Component.literal(""), true);
        }
    }

    private static boolean canContinueBrainDestructionHold(Minecraft minecraft) {
        LocalPlayer player = minecraft.player;
        return player != null
            && minecraft.level != null
            && minecraft.screen == null
            && player.isAlive()
            && JjaCommonConfig.BRAIN_DESTRUCTION_ENABLED.get()
            && player.hasEffect(JujutsucraftModMobEffects.UNSTABLE.get());
    }
}
