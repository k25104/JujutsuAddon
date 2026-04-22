package com.arf8vhg7.jja.feature.world.time;

import com.arf8vhg7.jja.JujutsuAddon;
import java.util.Map;
import java.util.WeakHashMap;
import net.minecraft.network.protocol.game.ClientboundSetTimePacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameRules;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(modid = JujutsuAddon.MODID)
public final class JjaTimeSpeedEvents {
    private static final long DAY_LENGTH = 24000L;
    private static final long NIGHT_START = 13000L;
    private static final long NIGHT_END = 23000L;
    private static final Map<ServerLevel, TimeState> TIME_STATES = new WeakHashMap<>();

    private JjaTimeSpeedEvents() {
    }

    @SubscribeEvent
    public static void onLevelTick(TickEvent.LevelTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        if (!(event.level instanceof ServerLevel level)) {
            return;
        }
        if (!level.dimensionType().hasSkyLight()) {
            return;
        }

        TimeState state = TIME_STATES.computeIfAbsent(level, ignored -> new TimeState());
        long currentDayTime = level.getDayTime();
        if (!state.initialized) {
            state.reset(currentDayTime);
            sendTimePacket(level);
            return;
        }

        GameRules gameRules = level.getGameRules();
        if (!gameRules.getBoolean(GameRules.RULE_DAYLIGHT)) {
            state.reset(currentDayTime);
            sendTimePacket(level);
            return;
        }

        long expectedDayTime = state.lastApplied + 1L;
        if (currentDayTime != expectedDayTime) {
            state.reset(currentDayTime);
            sendTimePacket(level);
            return;
        }

        long timeOfNight = Math.floorMod(state.lastApplied, DAY_LENGTH);
        boolean isNight = timeOfNight >= NIGHT_START && timeOfNight <= NIGHT_END;
        int speedValue = isNight
            ? gameRules.getInt(JjaTimeGameRules.JJA_NIGHT_TIME_SPEED)
            : gameRules.getInt(JjaTimeGameRules.JJA_DAY_TIME_SPEED);
        double speed = Math.max(0, speedValue) / 100.0;

        double total = state.remainder + speed;
        long delta = (long) total;
        state.remainder = total - delta;

        long newDayTime = state.lastApplied + delta;
        if (newDayTime != currentDayTime) {
            level.setDayTime(newDayTime);
        }
        state.lastApplied = newDayTime;

        sendTimePacket(level);
    }

    private static void sendTimePacket(ServerLevel level) {
        if (level.players().isEmpty()) {
            return;
        }
        ClientboundSetTimePacket packet = new ClientboundSetTimePacket(
            level.getGameTime(),
            level.getDayTime(),
            level.getGameRules().getBoolean(GameRules.RULE_DAYLIGHT)
        );
        for (ServerPlayer player : level.players()) {
            player.connection.send(packet);
        }
    }

    private static final class TimeState {
        private boolean initialized;
        private double remainder;
        private long lastApplied;

        private void reset(long dayTime) {
            initialized = true;
            remainder = 0.0;
            lastApplied = dayTime;
        }
    }
}
