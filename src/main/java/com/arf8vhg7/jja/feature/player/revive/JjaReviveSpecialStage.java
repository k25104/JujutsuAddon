package com.arf8vhg7.jja.feature.player.revive;

public enum JjaReviveSpecialStage {
    NONE(0),
    ELLIPSIS(1),
    GRASPED(2),
    ESSENCE_READY(3),
    ESSENCE_TRIGGERED(4);

    private final int id;

    JjaReviveSpecialStage(int id) {
        this.id = id;
    }

    public int id() {
        return this.id;
    }

    public boolean isActive() {
        return this != NONE;
    }

    public static JjaReviveSpecialStage fromId(int id) {
        for (JjaReviveSpecialStage stage : values()) {
            if (stage.id == id) {
                return stage;
            }
        }
        return NONE;
    }
}
