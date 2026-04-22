package com.arf8vhg7.jja.feature.combat.strike;

public record JjaAttackStrikeParticleDescriptor(
    double x,
    double y,
    double z,
    JjaAttackStrikeAnimation animation,
    float renderScale,
    float yawDegrees,
    float pitchDegrees
) {
    public JjaAttackStrikeParticleOptions asParticleOptions() {
        return new JjaAttackStrikeParticleOptions(this.animation, this.renderScale, this.yawDegrees, this.pitchDegrees);
    }
}
