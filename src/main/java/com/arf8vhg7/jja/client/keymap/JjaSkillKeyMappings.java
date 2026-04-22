package com.arf8vhg7.jja.client.keymap;

import com.arf8vhg7.jja.JujutsuAddon;
import com.arf8vhg7.jja.compat.jujutsucraft.KeyUseMainSkillMessageAccess;
import com.arf8vhg7.jja.feature.jja.technique.shared.slot.RegisteredCurseTechniqueSlots;
import com.arf8vhg7.jja.feature.player.state.PlayerStateAccess;
import com.arf8vhg7.jja.feature.player.state.model.PlayerSkillState;
import net.mcreator.jujutsucraft.JujutsucraftMod;
import net.mcreator.jujutsucraft.network.KeyUseMainSkillMessage;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(modid = JujutsuAddon.MODID, bus = Bus.MOD, value = Dist.CLIENT)
public final class JjaSkillKeyMappings {
    public static final KeyMapping KEY_SKILL_SLOT_01 = new SkillSlotKeyMapping(1);
    public static final KeyMapping KEY_SKILL_SLOT_02 = new SkillSlotKeyMapping(2);
    public static final KeyMapping KEY_SKILL_SLOT_03 = new SkillSlotKeyMapping(3);
    public static final KeyMapping KEY_SKILL_SLOT_04 = new SkillSlotKeyMapping(4);
    public static final KeyMapping KEY_SKILL_SLOT_05 = new SkillSlotKeyMapping(5);
    public static final KeyMapping KEY_SKILL_SLOT_06 = new SkillSlotKeyMapping(6);
    public static final KeyMapping KEY_SKILL_SLOT_07 = new SkillSlotKeyMapping(7);
    public static final KeyMapping KEY_SKILL_SLOT_08 = new SkillSlotKeyMapping(8);
    public static final KeyMapping KEY_SKILL_SLOT_09 = new SkillSlotKeyMapping(9);
    public static final KeyMapping KEY_SKILL_SLOT_10 = new SkillSlotKeyMapping(10);

    private JjaSkillKeyMappings() {
    }

    @SubscribeEvent
    public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
        JjaKeyMappingSupport.registerAll(
            event,
            KEY_SKILL_SLOT_01,
            KEY_SKILL_SLOT_02,
            KEY_SKILL_SLOT_03,
            KEY_SKILL_SLOT_04,
            KEY_SKILL_SLOT_05,
            KEY_SKILL_SLOT_06,
            KEY_SKILL_SLOT_07,
            KEY_SKILL_SLOT_08,
            KEY_SKILL_SLOT_09,
            KEY_SKILL_SLOT_10
        );
    }

    public static Component getSkillSlotKeyLabel(int slot) {
        return switch (slot) {
            case 1 -> KEY_SKILL_SLOT_01.getTranslatedKeyMessage();
            case 2 -> KEY_SKILL_SLOT_02.getTranslatedKeyMessage();
            case 3 -> KEY_SKILL_SLOT_03.getTranslatedKeyMessage();
            case 4 -> KEY_SKILL_SLOT_04.getTranslatedKeyMessage();
            case 5 -> KEY_SKILL_SLOT_05.getTranslatedKeyMessage();
            case 6 -> KEY_SKILL_SLOT_06.getTranslatedKeyMessage();
            case 7 -> KEY_SKILL_SLOT_07.getTranslatedKeyMessage();
            case 8 -> KEY_SKILL_SLOT_08.getTranslatedKeyMessage();
            case 9 -> KEY_SKILL_SLOT_09.getTranslatedKeyMessage();
            case 10 -> KEY_SKILL_SLOT_10.getTranslatedKeyMessage();
            default -> Component.translatable("key.keyboard.unknown");
        };
    }

    @EventBusSubscriber(modid = JujutsuAddon.MODID, value = Dist.CLIENT)
    public static class KeyEventListener {
        @SubscribeEvent
        public static void onClientTick(ClientTickEvent event) {
            if (Minecraft.getInstance().screen == null) {
                KEY_SKILL_SLOT_01.consumeClick();
                KEY_SKILL_SLOT_02.consumeClick();
                KEY_SKILL_SLOT_03.consumeClick();
                KEY_SKILL_SLOT_04.consumeClick();
                KEY_SKILL_SLOT_05.consumeClick();
                KEY_SKILL_SLOT_06.consumeClick();
                KEY_SKILL_SLOT_07.consumeClick();
                KEY_SKILL_SLOT_08.consumeClick();
                KEY_SKILL_SLOT_09.consumeClick();
                KEY_SKILL_SLOT_10.consumeClick();
            }
        }
    }

    private static void handleSlotPress(Player player, int slot) {
        PlayerSkillState skillState = PlayerStateAccess.skill(player);
        if (skillState == null) {
            return;
        }
        skillState.setPressedSlot(slot);
        KeyUseMainSkillMessage message = new KeyUseMainSkillMessage(0, 0);
        if (message instanceof KeyUseMainSkillMessageAccess access) {
            access.jja$setPressedSlot(slot);
        }
        JujutsucraftMod.PACKET_HANDLER.sendToServer(message);
        if (skillState.isPressSkillRegistrationToggle()) {
            RegisteredCurseTechniqueSlots.save(player, slot);
            skillState.setPressedSlot(0);
            return;
        }
        KeyUseMainSkillMessage.pressAction(player, 0, 0);
    }

    private static void handleSlotRelease(Player player, int dt) {
        KeyUseMainSkillMessage message = new KeyUseMainSkillMessage(1, dt);
        JujutsucraftMod.PACKET_HANDLER.sendToServer(message);
        KeyUseMainSkillMessage.pressAction(player, 1, dt);
    }

    private static final class SkillSlotKeyMapping extends JjaEdgeTriggeredKeyMapping {
        private final int slot;
        private long lastPress = 0L;

        private SkillSlotKeyMapping(int slot) {
            super(slot < 10 ? "key.jja.skill_slot_0" + slot : "key.jja.skill_slot_" + slot);
            this.slot = slot;
        }

        @Override
        protected void onEdgeChange(boolean isDown) {
            Player player = Minecraft.getInstance().player;
            if (player == null) {
                return;
            }
            if (isDown) {
                handleSlotPress(player, this.slot);
                this.lastPress = System.currentTimeMillis();
            } else {
                int dt = (int) (System.currentTimeMillis() - this.lastPress);
                handleSlotRelease(player, dt);
            }
        }
    }
}
