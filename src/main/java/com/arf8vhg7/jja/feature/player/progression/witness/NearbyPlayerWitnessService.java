package com.arf8vhg7.jja.feature.player.progression.witness;

import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public final class NearbyPlayerWitnessService {
    public static final double DEFAULT_RADIUS = 32.0D;

    private NearbyPlayerWitnessService() {
    }

    public static void forEachNearbyServerPlayer(@Nullable Entity source, Consumer<ServerPlayer> consumer) {
        forEachNearbyServerPlayer(source, DEFAULT_RADIUS, consumer);
    }

    public static void forEachNearbyServerPlayer(@Nullable Entity source, double radius, Consumer<ServerPlayer> consumer) {
        if (source == null || !(source.level() instanceof ServerLevel level) || consumer == null || radius <= 0.0D) {
            return;
        }

        Vec3 center = source.position();
        AABB area = new AABB(center, center).inflate(radius);
        for (ServerPlayer player : level.getEntitiesOfClass(ServerPlayer.class, area, candidate -> candidate.isAlive())) {
            if (isWithinRadius(player.distanceToSqr(center.x, center.y, center.z), radius)) {
                consumer.accept(player);
            }
        }
    }

    static boolean isWithinRadius(double distanceSqr, double radius) {
        return distanceSqr <= radius * radius;
    }
}
