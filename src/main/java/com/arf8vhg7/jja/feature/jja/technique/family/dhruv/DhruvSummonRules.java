package com.arf8vhg7.jja.feature.jja.technique.family.dhruv;

import java.util.Comparator;
import java.util.List;
import javax.annotation.Nullable;

final class DhruvSummonRules {
    private DhruvSummonRules() {
    }

    static int resolveMaxSummons(int baseCap, int strengthAmplifier) {
        return Math.max(1, (strengthAmplifier + 1) / baseCap);
    }

    @Nullable
    static SummonOrder selectOldestOrder(List<SummonOrder> orders) {
        return orders.stream()
            .min(Comparator.comparingLong(SummonOrder::summonedGameTime).thenComparingInt(SummonOrder::entityId))
            .orElse(null);
    }

    record SummonOrder(long summonedGameTime, int entityId) {
    }
}
