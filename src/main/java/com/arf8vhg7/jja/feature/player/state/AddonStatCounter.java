package com.arf8vhg7.jja.feature.player.state;

import com.arf8vhg7.jja.feature.player.state.model.PlayerAddonStatsState;

public enum AddonStatCounter {
    BF_RANDED(PlayerAddonStatsState::getBfRanded, PlayerAddonStatsState::setBfRanded, PlayerAddonStatsState::incrementBfRanded),
    DE_USED(PlayerAddonStatsState::getDeUsed, PlayerAddonStatsState::setDeUsed, PlayerAddonStatsState::incrementDeUsed),
    SIMPLE_DOMAIN_USED(
        PlayerAddonStatsState::getSimpleDomainUsed,
        PlayerAddonStatsState::setSimpleDomainUsed,
        PlayerAddonStatsState::incrementSimpleDomainUsed
    ),
    FBE_USED(PlayerAddonStatsState::getFbeUsed, PlayerAddonStatsState::setFbeUsed, PlayerAddonStatsState::incrementFbeUsed);

    private final Getter getter;
    private final Setter setter;
    private final Incrementer incrementer;

    AddonStatCounter(Getter getter, Setter setter, Incrementer incrementer) {
        this.getter = getter;
        this.setter = setter;
        this.incrementer = incrementer;
    }

    public int get(PlayerAddonStatsState addonStats) {
        return this.getter.get(addonStats);
    }

    public void set(PlayerAddonStatsState addonStats, int value) {
        this.setter.set(addonStats, value);
    }

    public void increment(PlayerAddonStatsState addonStats) {
        this.incrementer.increment(addonStats);
    }

    @FunctionalInterface
    private interface Getter {
        int get(PlayerAddonStatsState addonStats);
    }

    @FunctionalInterface
    private interface Setter {
        void set(PlayerAddonStatsState addonStats, int value);
    }

    @FunctionalInterface
    private interface Incrementer {
        void increment(PlayerAddonStatsState addonStats);
    }
}
