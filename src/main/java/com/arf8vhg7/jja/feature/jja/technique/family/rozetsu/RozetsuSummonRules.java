package com.arf8vhg7.jja.feature.jja.technique.family.rozetsu;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

final class RozetsuSummonRules {
    private RozetsuSummonRules() {
    }

    static int resolveMaxPoints(int strengthAmplifier) {
        return Math.max(1, strengthAmplifier + 1);
    }

    static int resolveNormalSpawnCount(int currentPoints, int maxPoints, int requestedCount) {
        return Math.min(Math.max(maxPoints - currentPoints, 0), Math.max(requestedCount, 0));
    }

    static boolean canSpawnVessel(int currentPoints, int maxPoints) {
        return maxPoints - currentPoints >= SummonKind.VESSEL.cost();
    }

    static List<SummonOrder> selectOldestRemovalsForCapacity(List<SummonOrder> activeSummons, int currentPoints, int maxPoints, int incomingUnitCost) {
        int points = currentPoints;
        List<SummonOrder> selected = new ArrayList<>();
        List<SummonOrder> sorted = activeSummons.stream()
            .sorted(Comparator.comparingLong(SummonOrder::summonedGameTime).thenComparingInt(SummonOrder::entityId))
            .toList();
        for (SummonOrder order : sorted) {
            if (points + incomingUnitCost <= maxPoints) {
                break;
            }
            selected.add(order);
            points -= order.cost();
        }
        return selected;
    }

    enum SummonKind {
        NORMAL(1),
        VESSEL(2),
        VESSEL_2(2);

        private final int cost;

        SummonKind(int cost) {
            this.cost = cost;
        }

        int cost() {
            return this.cost;
        }
    }

    record SummonOrder(long summonedGameTime, int entityId, int cost) {
    }
}
