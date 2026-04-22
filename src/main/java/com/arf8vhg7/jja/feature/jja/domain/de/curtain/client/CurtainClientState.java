package com.arf8vhg7.jja.feature.jja.domain.de.curtain.client;

import com.arf8vhg7.jja.feature.jja.domain.de.curtain.CurtainGeometry;
import com.arf8vhg7.jja.feature.jja.domain.de.curtain.CurtainShellVisionMode;
import com.arf8vhg7.jja.feature.jja.domain.de.curtain.CurtainShellVisibilityOverride;
import com.arf8vhg7.jja.feature.jja.domain.de.curtain.CurtainViewerRules;
import com.arf8vhg7.jja.feature.jja.domain.de.curtain.CurtainVisualState;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

public final class CurtainClientState {
    private static final Map<UUID, CurtainVisualState> VISUAL_STATES = new HashMap<>();
    private static volatile CurtainShellVisibilityOverride shellVisibilityOverride = CurtainShellVisibilityOverride.AUTO;
    private static volatile CurtainShellVisionMode shellVisionMode = CurtainShellVisionMode.TRANSPARENT;

    private CurtainClientState() {
    }

    public static void applySnapshot(Collection<CurtainVisualState> states, CurtainShellVisibilityOverride shellVisibilityOverride) {
        VISUAL_STATES.clear();
        for (CurtainVisualState state : states) {
            VISUAL_STATES.put(state.ownerId(), state);
        }
        CurtainClientState.shellVisibilityOverride = shellVisibilityOverride;
    }

    public static void clearAll() {
        VISUAL_STATES.clear();
        shellVisibilityOverride = CurtainShellVisibilityOverride.AUTO;
        shellVisionMode = CurtainShellVisionMode.TRANSPARENT;
    }

    public static boolean updateShellVisionMode(CurtainShellVisionMode nextMode) {
        if (shellVisionMode == nextMode) {
            return false;
        }
        shellVisionMode = nextMode;
        return true;
    }

    public static boolean shouldRenderShellBlocks() {
        return shellVisionMode == CurtainShellVisionMode.BLACK;
    }

    public static CurtainShellVisionMode resolveShellVisionMode(@Nullable LocalPlayer viewer) {
        return shellVisibilityOverride.apply(CurtainViewerRules.resolveShellVisionMode(viewer));
    }

    public static boolean canLocalPlayerPassThroughShell(@Nullable Entity entity, @Nullable BlockPos pos) {
        if (!(entity instanceof LocalPlayer localPlayer) || pos == null) {
            return false;
        }

        if (CurtainViewerRules.isCompletePhysicalGifted(localPlayer)) {
            return true;
        }

        CurtainVisualState curtain = findShellCurtain(localPlayer.level(), pos);
        return curtain != null && curtain.localViewerPassThrough();
    }

    public static boolean shouldSuppressEntityRender(@Nullable Entity target) {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer viewer = minecraft.player;
        Level level = minecraft.level;
        if (viewer == null || level == null || target == null || target == viewer || target.level() != level) {
            return false;
        }

        for (CurtainVisualState curtain : VISUAL_STATES.values()) {
            if (!matchesLevel(level, curtain) || !curtain.phase().blocksOuterVisibility()) {
                continue;
            }

            boolean targetInside = CurtainGeometry.isWithinCurtain(curtain.center(), curtain.radius(), target);
            if (!targetInside) {
                continue;
            }

            boolean viewerInside = CurtainGeometry.isWithinCurtain(curtain.center(), curtain.radius(), viewer);
            if (CurtainViewerRules.shouldHideTargetFromViewer(viewer, target, !viewerInside, true)) {
                return true;
            }
        }
        return false;
    }

    @Nullable
    private static CurtainVisualState findShellCurtain(Level level, BlockPos pos) {
        for (CurtainVisualState curtain : VISUAL_STATES.values()) {
            if (!matchesLevel(level, curtain)) {
                continue;
            }
            if (CurtainGeometry.isShellPosition(curtain.center(), curtain.radius(), pos)) {
                return curtain;
            }
        }
        return null;
    }

    private static boolean matchesLevel(Level level, CurtainVisualState curtain) {
        return level.dimension().location().equals(curtain.dimensionId());
    }
}
