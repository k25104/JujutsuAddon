package com.arf8vhg7.jja.feature.player.revive.client;

import com.arf8vhg7.jja.JujutsuAddon;
import com.arf8vhg7.jja.feature.player.revive.JjaReviveSpecialStage;
import com.arf8vhg7.jja.feature.player.revive.network.JjaReviveCoreClickMessage;
import com.arf8vhg7.jja.feature.player.revive.network.JjaReviveGiveUpMessage;
import com.arf8vhg7.jja.feature.player.revive.network.JjaReviveHoldMessage;
import com.arf8vhg7.jja.network.JjaNetwork;
import java.util.Objects;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(modid = JujutsuAddon.MODID, value = Dist.CLIENT)
public final class JjaReviveClientEvents {
    private JjaReviveClientEvents() {
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent event) {
        if (event.phase != net.minecraftforge.event.TickEvent.Phase.END) {
            return;
        }
        Minecraft minecraft = Minecraft.getInstance();
        JjaReviveClientState.tick(minecraft);
        tickRescueHold(minecraft);
    }

    @SubscribeEvent
    public static void onScreenOpening(ScreenEvent.Opening event) {
        if (!JjaReviveClientState.isWaiting()) {
            return;
        }
        if (event.getNewScreen() instanceof InventoryScreen) {
            JjaReviveClientState.markVisible();
            event.setNewScreen(new JjaReviveScreen());
        }
    }

    @SubscribeEvent
    public static void onRenderGui(RenderGuiEvent.Post event) {
        Minecraft minecraft = Minecraft.getInstance();
        GuiGraphics guiGraphics = event.getGuiGraphics();
        if (JjaReviveClientState.isWaiting() && JjaReviveClientState.isHiddenBackgroundOnly()) {
            if (JjaReviveClientState.isSpecialBranchActive()) {
                JjaReviveUiRenderer.renderBackground(
                    guiGraphics,
                    event.getWindow().getGuiScaledWidth(),
                    event.getWindow().getGuiScaledHeight()
                );
            } else {
                JjaReviveUiRenderer.renderHiddenOverlay(
                    guiGraphics,
                    minecraft.font,
                    event.getWindow().getGuiScaledWidth(),
                    event.getWindow().getGuiScaledHeight(),
                    JjaReviveClientState.getRemainingTicks(),
                    Component.translatable("screen.jja.revive_return_key", minecraft.options.keyInventory.getTranslatedKeyMessage())
                );
            }
        }
        if (JjaReviveClientState.isAssistActive()) {
            int seconds = JjaReviveClientState.toDisplaySeconds(JjaReviveClientState.getAssistRemainingTicks());
            Component text = Component.translatable("screen.jja.revive_respawn_in", seconds);
            guiGraphics.drawCenteredString(
                minecraft.font,
                text,
                event.getWindow().getGuiScaledWidth() / 2,
                event.getWindow().getGuiScaledHeight() / 2 - 20,
                0xFFFFFF
            );
        }
    }

    public static void sendGiveUp() {
        JjaNetwork.CHANNEL.sendToServer(new JjaReviveGiveUpMessage());
    }

    public static void handlePrimaryButtonPress() {
        if (JjaReviveClientState.isSpecialBranchActive()) {
            if (JjaReviveClientState.getSpecialStage() == JjaReviveSpecialStage.ESSENCE_READY) {
                JjaNetwork.CHANNEL.sendToServer(new JjaReviveCoreClickMessage());
            }
            return;
        }
        sendGiveUp();
    }

    private static void tickRescueHold(Minecraft minecraft) {
        LocalPlayer player = minecraft.player;
        boolean canTrack = player != null && minecraft.level != null && minecraft.screen == null && !JjaReviveClientState.isWaiting();
        boolean rightDown = canTrack && minecraft.options.keyUse.isDown();
        boolean wasDown = JjaReviveClientState.isRescueButtonHeld();

        if (!rightDown && wasDown) {
            JjaReviveClientState.setRescueButtonHeld(false);
            UUID sentTarget = JjaReviveClientState.getSentHoldTarget();
            if (sentTarget != null) {
                sendHold(sentTarget, false);
                JjaReviveClientState.setSentHoldTarget(null);
            }
            return;
        }

        if (!rightDown) {
            return;
        }

        if (!wasDown) {
            JjaReviveClientState.setRescueButtonHeld(true);
        }

        UUID targetId = resolveTarget(minecraft, player);
        UUID currentTarget = JjaReviveClientState.getSentHoldTarget();
        if (!Objects.equals(currentTarget, targetId)) {
            if (currentTarget != null) {
                sendHold(currentTarget, false);
            }
            if (targetId != null) {
                sendHold(targetId, true);
            }
            JjaReviveClientState.setSentHoldTarget(targetId);
        }
    }

    private static UUID resolveTarget(Minecraft minecraft, LocalPlayer player) {
        HitResult hitResult = minecraft.hitResult;
        if (!(hitResult instanceof EntityHitResult entityHitResult)) {
            return null;
        }
        if (!(entityHitResult.getEntity() instanceof Player target) || target == player) {
            return null;
        }
        return target.getUUID();
    }

    private static void sendHold(UUID targetId, boolean holding) {
        JjaNetwork.CHANNEL.sendToServer(new JjaReviveHoldMessage(targetId, holding));
    }
}
