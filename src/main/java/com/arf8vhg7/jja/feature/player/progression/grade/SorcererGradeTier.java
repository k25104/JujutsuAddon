package com.arf8vhg7.jja.feature.player.progression.grade;

import net.minecraft.resources.ResourceLocation;

public enum SorcererGradeTier {
    GRADE_4("sorcerer_grade_4", 0, 2.0, 0),
    GRADE_3("sorcerer_grade_3", 1, 4.0, 0),
    GRADE_2_SEMI("sorcerer_grade_2_semi", 2, 7.0, 0),
    GRADE_2("sorcerer_grade_2", 3, 9.0, 0),
    GRADE_1_SEMI("sorcerer_grade_1_semi", 4, 11.0, 0),
    GRADE_1("sorcerer_grade_1", 5, 13.0, 0),
    SPECIAL("sorcerer_grade_special", 6, 20.0, 1),
    SPECIAL_1("jja", "sorcerer_grade_special_1", 7, 22.0, 2),
    SPECIAL_2("jja", "sorcerer_grade_special_2", 8, 24.0, 3),
    SPECIAL_3("jja", "sorcerer_grade_special_3", 9, 26.0, 4),
    SPECIAL_4("jja", "sorcerer_grade_special_4", 10, 28.0, 5),
    SPECIAL_5("jja", "sorcerer_grade_special_5", 11, 30.0, 6);

    private final ResourceLocation advancementId;
    private final int rank;
    private final double playerLevel;
    private final int specialFameMultiplier;

    SorcererGradeTier(String path, int rank, double playerLevel, int specialFameMultiplier) {
        this("jujutsucraft", path, rank, playerLevel, specialFameMultiplier);
    }

    SorcererGradeTier(String namespace, String path, int rank, double playerLevel, int specialFameMultiplier) {
        this.advancementId = ResourceLocation.fromNamespaceAndPath(namespace, path);
        this.rank = rank;
        this.playerLevel = playerLevel;
        this.specialFameMultiplier = specialFameMultiplier;
    }

    public ResourceLocation advancementId() {
        return this.advancementId;
    }

    public int rank() {
        return this.rank;
    }

    public double playerLevel() {
        return this.playerLevel;
    }

    public boolean isSpecialTier() {
        return this.specialFameMultiplier > 0;
    }

    public double fameThreshold(double difficulty) {
        if (!this.isSpecialTier()) {
            throw new IllegalStateException("Not a special tier: " + this.name());
        }
        return 4000.0 * difficulty * this.specialFameMultiplier;
    }
}
