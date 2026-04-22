package com.arf8vhg7.jja.feature.jja.technique.family.mahoraga;

import com.arf8vhg7.jja.compat.jujutsucraft.JjaJujutsucraftDataAccess;
import java.util.LinkedHashSet;
import java.util.Set;
import net.mcreator.jujutsucraft.entity.EightHandledSwordDivergentSilaDivineGeneralMahoragaEntity;
import net.mcreator.jujutsucraft.entity.MahoragaDogEntity;
import net.mcreator.jujutsucraft.entity.MahoragaFrogEntity;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public final class UntamedMahoragaRitualService {
    public static final String KEY_PARTICIPANTS_INITIALIZED = "jjaUntamedMahoragaParticipantsInitialized";
    public static final String KEY_PARTICIPANTS = "jjaUntamedMahoragaParticipants";

    private static final double SNAPSHOT_RADIUS = 16.0D;

    private UntamedMahoragaRitualService() {
    }

    public static void initializeParticipants(Entity entity) {
        if (!(entity instanceof EightHandledSwordDivergentSilaDivineGeneralMahoragaEntity) || entity.level().isClientSide()) {
            return;
        }
        Entity mahoraga = entity;

        if (!isUntamedMahoraga(mahoraga)) {
            return;
        }

        if (mahoraga.getPersistentData().getBoolean(KEY_PARTICIPANTS_INITIALIZED)) {
            return;
        }

        Set<String> participantIds = new LinkedHashSet<>();
        for (LivingEntity candidate : mahoraga.level().getEntitiesOfClass(
            LivingEntity.class,
            mahoraga.getBoundingBox().inflate(SNAPSHOT_RADIUS),
            living -> shouldRememberParticipant(mahoraga, living)
        )) {
            participantIds.add(candidate.getStringUUID());
        }

        ListTag participants = new ListTag();
        for (String participantId : participantIds) {
            participants.add(StringTag.valueOf(participantId));
        }

        mahoraga.getPersistentData().putBoolean(KEY_PARTICIPANTS_INITIALIZED, true);
        mahoraga.getPersistentData().put(KEY_PARTICIPANTS, participants);
        markForDespawnIfComplete(mahoraga);
    }

    public static void handleParticipantDeath(LivingEntity entity) {
        if (entity == null || entity.level().isClientSide()) {
            return;
        }

        MinecraftServer server = entity.level().getServer();
        if (server == null) {
            return;
        }

        String deadId = entity.getStringUUID();
        for (ServerLevel level : server.getAllLevels()) {
            for (Entity candidate : level.getAllEntities()) {
                if (!isUntamedMahoraga(candidate)) {
                    continue;
                }
                removeParticipant(candidate, deadId);
            }
        }
    }

    public static boolean isUntamedMahoraga(Entity entity) {
        if (!(entity instanceof EightHandledSwordDivergentSilaDivineGeneralMahoragaEntity)) {
            return false;
        }
        return JjaJujutsucraftDataAccess.jjaGetFriendNum(entity) == 0.0D
            && JjaJujutsucraftDataAccess.jjaGetFriendNumWorker(entity) != 0.0D
            && !JjaJujutsucraftDataAccess.jjaGetTargetUuid(entity).isEmpty();
    }

    private static void removeParticipant(Entity mahoraga, String participantId) {
        if (!mahoraga.getPersistentData().contains(KEY_PARTICIPANTS, Tag.TAG_LIST)) {
            return;
        }

        ListTag currentParticipants = mahoraga.getPersistentData().getList(KEY_PARTICIPANTS, Tag.TAG_STRING);
        ListTag updatedParticipants = new ListTag();
        boolean removed = false;
        for (int index = 0; index < currentParticipants.size(); index++) {
            String currentId = currentParticipants.getString(index);
            if (participantId.equals(currentId)) {
                removed = true;
                continue;
            }
            updatedParticipants.add(StringTag.valueOf(currentId));
        }

        if (!removed) {
            return;
        }

        mahoraga.getPersistentData().put(KEY_PARTICIPANTS, updatedParticipants);
        markForDespawnIfComplete(mahoraga);
    }

    private static void markForDespawnIfComplete(Entity mahoraga) {
        if (!mahoraga.isAlive()) {
            return;
        }

        if (!mahoraga.getPersistentData().getBoolean(KEY_PARTICIPANTS_INITIALIZED)) {
            return;
        }

        if (mahoraga.getPersistentData().getList(KEY_PARTICIPANTS, Tag.TAG_STRING).isEmpty()) {
            mahoraga.getPersistentData().putBoolean("flag_despawn", true);
        }
    }

    private static boolean shouldRememberParticipant(Entity mahoraga, LivingEntity candidate) {
        if (candidate == mahoraga || !candidate.isAlive()) {
            return false;
        }
        if (candidate instanceof MahoragaDogEntity || candidate instanceof MahoragaFrogEntity) {
            return false;
        }
        if (candidate instanceof Player player) {
            return !player.isSpectator() && !player.isCreative();
        }
        return true;
    }
}
