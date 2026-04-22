package com.arf8vhg7.jja.feature.combat.strike;

import net.minecraft.util.StringRepresentable;

public enum JjaAttackStrikeAnimation implements StringRepresentable {
    IDLE1("idle1"),
    IDLE2("idle2"),
    IDLE3("idle3");

    public static final StringRepresentable.EnumCodec<JjaAttackStrikeAnimation> CODEC = StringRepresentable.fromEnum(
        JjaAttackStrikeAnimation::values
    );

    private final String serializedName;

    JjaAttackStrikeAnimation(String serializedName) {
        this.serializedName = serializedName;
    }

    public String animationName() {
        return this.serializedName;
    }

    @Override
    public String getSerializedName() {
        return this.serializedName;
    }

    public static JjaAttackStrikeAnimation fromCnt4(double cnt4, boolean combo) {
        if (!combo) {
            return IDLE3;
        }
        return switch ((int) Math.round(cnt4)) {
            case 1 -> IDLE1;
            case 2 -> IDLE2;
            default -> IDLE3;
        };
    }
}
