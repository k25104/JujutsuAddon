package com.arf8vhg7.jja.feature.jja.technique.family.dhruv;

import javax.annotation.Nullable;
import net.minecraft.server.level.ServerPlayer;

public final class DhruvTechniqueRegistrationDisplayService {
    private static final int DHRUV_CURSE_TECHNIQUE_ID = 37;
    private static final int SELECT_HETEROCEPHALUS_GLABER = 5;
    private static final int SELECT_PTEROSAUR = 6;

    private DhruvTechniqueRegistrationDisplayService() {
    }

    public static String appendSummonCountSuffix(ServerPlayer player, int curseTechniqueId, int selectTechniqueId, String displayName) {
        if (player == null || displayName == null || curseTechniqueId != DHRUV_CURSE_TECHNIQUE_ID) {
            return displayName;
        }

        DhruvSummonService.SummonKind summonKind = resolveSummonKind(selectTechniqueId);
        if (summonKind == null) {
            return displayName;
        }

        int currentCount = DhruvSummonService.countActiveSummons(player.serverLevel(), player, summonKind);
        int maxCount = DhruvSummonService.resolveMaxSummonsForOwner(player, summonKind);
        return displayName + "(" + currentCount + "/" + maxCount + ")";
    }

    @Nullable
    private static DhruvSummonService.SummonKind resolveSummonKind(int selectTechniqueId) {
        return switch (selectTechniqueId) {
            case SELECT_HETEROCEPHALUS_GLABER -> DhruvSummonService.SummonKind.HETEROCEPHALUS_GLABER;
            case SELECT_PTEROSAUR -> DhruvSummonService.SummonKind.PTEROSAUR;
            default -> null;
        };
    }
}
