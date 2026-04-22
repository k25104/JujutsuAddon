package com.arf8vhg7.jja.feature.player.state;

import com.arf8vhg7.jja.feature.player.state.model.PlayerAddonStatsState;
import com.arf8vhg7.jja.feature.player.state.model.PlayerRctState;
import com.arf8vhg7.jja.feature.player.state.model.PlayerReviveState;
import com.arf8vhg7.jja.feature.player.state.model.PlayerSkillState;
import net.minecraft.world.entity.Entity;

public final class PlayerStateAccess {
    private PlayerStateAccess() {
    }

    public static JjaPlayerState get(Entity entity) {
        return JjaPlayerCapability.get(entity);
    }

    public static PlayerSkillState skill(Entity entity) {
        JjaPlayerState state = get(entity);
        return state == null ? null : state.skillState();
    }

    public static PlayerReviveState revive(Entity entity) {
        JjaPlayerState state = get(entity);
        return state == null ? null : state.reviveState();
    }

    public static PlayerRctState rct(Entity entity) {
        JjaPlayerState state = get(entity);
        return state == null ? null : state.rctState();
    }

    public static PlayerAddonStatsState addonStats(Entity entity) {
        JjaPlayerState state = get(entity);
        return state == null ? null : state.addonStatsState();
    }
}
