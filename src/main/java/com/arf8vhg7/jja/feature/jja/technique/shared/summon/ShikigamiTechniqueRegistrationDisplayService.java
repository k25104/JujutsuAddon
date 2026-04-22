package com.arf8vhg7.jja.feature.jja.technique.shared.summon;

import com.arf8vhg7.jja.feature.jja.technique.family.dhruv.DhruvTechniqueRegistrationDisplayService;
import com.arf8vhg7.jja.feature.jja.technique.family.rozetsu.RozetsuSummonService;
import javax.annotation.Nullable;
import net.minecraft.server.level.ServerPlayer;

public final class ShikigamiTechniqueRegistrationDisplayService {
    private static final int ROZETSU_CURSE_TECHNIQUE_ID = 43;
    private static final int ROZETSU_SELECT_NORMAL = 5;
    private static final int ROZETSU_SELECT_VESSEL = 6;
    private static final int ROZETSU_SELECT_VESSEL_2 = 7;

    private ShikigamiTechniqueRegistrationDisplayService() {
    }

    public static String appendSummonCountSuffix(
        ServerPlayer player,
        int curseTechniqueId,
        int selectTechniqueId,
        @Nullable String canonicalName,
        String displayName
    ) {
        String dhruvDisplayName = DhruvTechniqueRegistrationDisplayService.appendSummonCountSuffix(
            player,
            curseTechniqueId,
            selectTechniqueId,
            displayName
        );
        if (player == null || dhruvDisplayName == null) {
            return dhruvDisplayName;
        }

        if (curseTechniqueId == ROZETSU_CURSE_TECHNIQUE_ID && isRozetsuSummonSelect(selectTechniqueId)) {
            int currentPoints = RozetsuSummonService.countActivePoints(player.serverLevel(), player);
            int maxPoints = RozetsuSummonService.resolveMaxPointsForOwner(player);
            return appendPointSuffix(dhruvDisplayName, currentPoints, maxPoints);
        }

        return dhruvDisplayName;
    }

    static String appendPointSuffix(String displayName, int currentPoints, int maxPoints) {
        return ShikigamiTechniqueDisplayRules.appendPointSuffix(displayName, currentPoints, maxPoints);
    }

    private static boolean isRozetsuSummonSelect(int selectTechniqueId) {
        return selectTechniqueId == ROZETSU_SELECT_NORMAL
            || selectTechniqueId == ROZETSU_SELECT_VESSEL
            || selectTechniqueId == ROZETSU_SELECT_VESSEL_2;
    }
}
