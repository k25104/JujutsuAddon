package com.arf8vhg7.jja.feature.combat.strike;

@FunctionalInterface
public interface JjaAttackStrikeJitterSampler {
    double sample(double min, double max);
}
