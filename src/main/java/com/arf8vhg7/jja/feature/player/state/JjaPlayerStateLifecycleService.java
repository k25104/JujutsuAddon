package com.arf8vhg7.jja.feature.player.state;

import com.arf8vhg7.jja.feature.jja.rct.RctStateService;
import com.arf8vhg7.jja.feature.jja.technique.shared.menu.TechniqueSetupService;
import java.util.function.BooleanSupplier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public final class JjaPlayerStateLifecycleService {
    private JjaPlayerStateLifecycleService() {
    }

    public static void refreshRuntimeAndSync(Entity entity) {
        runRuntimeRefresh(
            () -> RctStateService.clearRuntimeState(entity),
            () -> entity instanceof Player p ? TechniqueSetupService.ensureInitialized(p) : false,
            () -> JjaPlayerStateSync.sync(entity)
        );
    }

    public static boolean ensureTechniqueSetupAndSync(ServerPlayer player) {
        return runTechniqueSetupMutation(
            () -> TechniqueSetupService.ensureInitialized(player),
            () -> false,
            () -> JjaPlayerStateSync.sync(player)
        );
    }

    public static boolean runTechniqueSetupMutation(ServerPlayer player, BooleanSupplier mutation) {
        return runTechniqueSetupMutation(
            () -> TechniqueSetupService.ensureInitialized(player),
            mutation,
            () -> JjaPlayerStateSync.sync(player)
        );
    }

    static void runRuntimeRefresh(Runnable clearRuntimeState, BooleanSupplier ensureInitialized, Runnable syncAction) {
        clearRuntimeState.run();
        ensureInitialized.getAsBoolean();
        syncAction.run();
    }

    static boolean runTechniqueSetupMutation(
        BooleanSupplier ensureInitialized,
        BooleanSupplier mutation,
        Runnable syncAction
    ) {
        boolean changed = ensureInitialized.getAsBoolean();
        changed |= mutation.getAsBoolean();
        if (changed) {
            syncAction.run();
        }
        return changed;
    }
}
