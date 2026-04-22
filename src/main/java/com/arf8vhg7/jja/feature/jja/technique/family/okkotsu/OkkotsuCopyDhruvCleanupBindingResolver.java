package com.arf8vhg7.jja.feature.jja.technique.family.okkotsu;

import com.arf8vhg7.jja.compat.jujutsucraft.JjaJujutsucraftDataAccess;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;

public final class OkkotsuCopyDhruvCleanupBindingResolver {
    private static final String SHIKIGAMI_RIKA_DHRUV_CLASS_NAME = "net.mcreator.jujutsucraft.entity.ShikigamiRikaDhruvEntity";
    private static final String RIKA_CLASS_NAME = "net.mcreator.jujutsucraft.entity.RikaEntity";
    private static final String RIKA2_CLASS_NAME = "net.mcreator.jujutsucraft.entity.Rika2Entity";
    private static final String RIKA_UUID_KEY = "RIKA_UUID";

    private OkkotsuCopyDhruvCleanupBindingResolver() {
    }

    @Nullable
    public static UUID resolveCleanupBindingUuid(@Nullable LevelAccessor world, @Nullable Entity trailPlacer) {
        if (world == null || trailPlacer == null) {
            return null;
        }
        Entity owner = JjaJujutsucraftDataAccess.jjaResolveDirectOwner(world, trailPlacer);
        String ownerUuid = owner == null ? "" : owner.getStringUUID();
        String rikaUuid = owner == null ? "" : owner.getPersistentData().getString(RIKA_UUID_KEY);
        Entity manifestation = JjaJujutsucraftDataAccess.jjaResolveOwnerByUuid(world, rikaUuid);
        CleanupBindingCandidate candidate = new CleanupBindingCandidate(
            trailPlacer.getClass().getName(),
            rikaUuid,
            ownerUuid,
            JjaJujutsucraftDataAccess.jjaGetFriendNum(owner),
            manifestation == null ? null : manifestation.getUUID(),
            manifestation == null ? "" : manifestation.getClass().getName(),
            JjaJujutsucraftDataAccess.jjaGetOwnerUuid(manifestation),
            JjaJujutsucraftDataAccess.jjaGetFriendNum(manifestation),
            manifestation != null && manifestation.isAlive(),
            manifestation != null && manifestation.isRemoved()
        );
        return resolveCleanupBindingUuid(candidate);
    }

    @Nullable
    static UUID resolveCleanupBindingUuid(CleanupBindingCandidate candidate) {
        if (candidate == null
            || !isCopyDhruvTrailPlacerClassName(candidate.trailPlacerClassName())
            || candidate.rikaUuid() == null
            || candidate.rikaUuid().isBlank()
            || candidate.manifestationUuid() == null
            || !isRikaManifestationClassName(candidate.manifestationClassName())
            || !candidate.manifestationAlive()
            || candidate.manifestationRemoved()
            || !candidate.ownerUuid().equals(candidate.manifestationOwnerUuid())
            || Double.compare(candidate.ownerFriendNum(), candidate.manifestationFriendNum()) != 0) {
            return null;
        }
        return candidate.manifestationUuid();
    }

    static boolean isCopyDhruvTrailPlacerClassName(String className) {
        return SHIKIGAMI_RIKA_DHRUV_CLASS_NAME.equals(className);
    }

    static boolean isRikaManifestationClassName(String className) {
        return RIKA_CLASS_NAME.equals(className) || RIKA2_CLASS_NAME.equals(className);
    }

    static record CleanupBindingCandidate(
        String trailPlacerClassName,
        String rikaUuid,
        String ownerUuid,
        double ownerFriendNum,
        @Nullable UUID manifestationUuid,
        String manifestationClassName,
        String manifestationOwnerUuid,
        double manifestationFriendNum,
        boolean manifestationAlive,
        boolean manifestationRemoved
    ) {
    }
}
