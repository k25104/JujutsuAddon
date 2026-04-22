package com.arf8vhg7.jja.feature.jja.resource.ce;

public final class CursePowerScalingRules {
    private static final double OKKOTSU_CURSE_TECHNIQUE = 5.0D;
    private static final double MAKI_CURSE_TECHNIQUE = -1.0D;
    private static final double BASE_CURSE_POWER_FORMER = 200.0D;
    private static final double MAX_CURSE_POWER_FORMER = 400.0D;
    private static final double FORMER_PROGRESS_DIVISOR = 600.0D;
    private static final double EFFICIENCY_PROGRESS_DIVISOR = 240000.0D;
    private static final double BASE_FLOOR = 0.5D;
    private static final double SUKUNA_BASE_MULTIPLIER = 0.5D;

    private CursePowerScalingRules() {
    }

    public static double resolveDifficultyScale(int jujutsuUpgradeDifficulty) {
        return 1.0D + jujutsuUpgradeDifficulty / 10.0D;
    }

    static boolean isMakiCurseTechnique(double playerCurseTechnique) {
        return Double.compare(playerCurseTechnique, MAKI_CURSE_TECHNIQUE) == 0;
    }

    public static double resolvePlayerCursePowerFormer(
        double playerCurseTechnique,
        double playerTechniqueUsedNumber,
        int jujutsuUpgradeDifficulty,
        double currentFormer
    ) {
        return resolvePlayerCursePowerFormer(
            playerCurseTechnique,
            playerTechniqueUsedNumber,
            jujutsuUpgradeDifficulty,
            currentFormer,
            CeScalingConfig.isCePoolScalingEnabled()
        );
    }

    static double resolvePlayerCursePowerFormer(
        double playerCurseTechnique,
        double playerTechniqueUsedNumber,
        int jujutsuUpgradeDifficulty,
        double currentFormer,
        boolean cePoolScalingEnabled
    ) {
        return cePoolScalingEnabled
            ? resolveAddonPlayerCursePowerFormer(playerCurseTechnique, playerTechniqueUsedNumber, jujutsuUpgradeDifficulty)
            : resolveUpstreamPlayerCursePowerFormer(playerCurseTechnique, currentFormer);
    }

    private static double resolveAddonPlayerCursePowerFormer(
        double playerCurseTechnique,
        double playerTechniqueUsedNumber,
        int jujutsuUpgradeDifficulty
    ) {
        if (Double.compare(playerCurseTechnique, OKKOTSU_CURSE_TECHNIQUE) == 0) {
            return MAX_CURSE_POWER_FORMER;
        }

        double difficultyScale = resolveDifficultyScale(jujutsuUpgradeDifficulty);
        double scaledProgress = playerTechniqueUsedNumber / (FORMER_PROGRESS_DIVISOR * difficultyScale);
        return Math.min(MAX_CURSE_POWER_FORMER, BASE_CURSE_POWER_FORMER + scaledProgress);
    }

    static double resolveUpstreamPlayerCursePowerFormer(double playerCurseTechnique, double currentFormer) {
        if (Double.compare(playerCurseTechnique, OKKOTSU_CURSE_TECHNIQUE) == 0) {
            return MAX_CURSE_POWER_FORMER;
        }
        if (playerCurseTechnique > 0.0D) {
            return BASE_CURSE_POWER_FORMER;
        }
        return currentFormer;
    }

    public static double resolvePlayerCursePowerMax(
        double playerCursePowerFormer,
        double playerLevel,
        boolean hasSukunaEffect,
        int sukunaEffectAmplifier
    ) {
        double playerCursePowerMax = playerCursePowerFormer * Math.round((2.0D + playerLevel) / 1.1D);
        if (!hasSukunaEffect) {
            return playerCursePowerMax;
        }

        int sukunaLevel = Math.min(sukunaEffectAmplifier + 1, 20);
        double sukunaPowerMax = sukunaLevel <= 9
            ? 3000.0D + (sukunaLevel - 1.0D) * 300.0D
            : 8000.0D + (sukunaLevel - 10.0D) * 1000.0D;
        return Math.max(playerCursePowerMax, sukunaPowerMax);
    }

    public static double resolveTechniqueEfficiencyMultiplier(
        boolean hasSixEyes,
        int sixEyesAmplifier,
        boolean hasSukunaEffect,
        double playerTechniqueUsedNumber,
        int jujutsuUpgradeDifficulty
    ) {
        return resolveTechniqueEfficiencyMultiplier(
            hasSixEyes,
            sixEyesAmplifier,
            hasSukunaEffect,
            playerTechniqueUsedNumber,
            jujutsuUpgradeDifficulty,
            CeScalingConfig.isCeEfficiencyScalingEnabled()
        );
    }

    public static double resolveTechniqueEfficiencyMultiplier(
        boolean hasSixEyes,
        int sixEyesAmplifier,
        boolean hasSukunaEffect,
        double playerTechniqueUsedNumber,
        int jujutsuUpgradeDifficulty,
        boolean ceEfficiencyScalingEnabled
    ) {
        return ceEfficiencyScalingEnabled
            ? resolveAddonTechniqueEfficiencyMultiplier(
                hasSixEyes,
                sixEyesAmplifier,
                hasSukunaEffect,
                playerTechniqueUsedNumber,
                jujutsuUpgradeDifficulty
            )
            : resolveUpstreamTechniqueEfficiencyMultiplier(hasSixEyes, sixEyesAmplifier, hasSukunaEffect);
    }

    private static double resolveAddonTechniqueEfficiencyMultiplier(
        boolean hasSixEyes,
        int sixEyesAmplifier,
        boolean hasSukunaEffect,
        double playerTechniqueUsedNumber,
        int jujutsuUpgradeDifficulty
    ) {
        double difficultyScale = resolveDifficultyScale(jujutsuUpgradeDifficulty);
        int normalizedSixEyesAmplifier = hasSixEyes ? Math.max(sixEyesAmplifier, 0) : -1;
        double sixEyesMultiplier = Math.pow(10.0D, normalizedSixEyesAmplifier + 1.0D);
        double efficiency = hasSukunaEffect
            ? SUKUNA_BASE_MULTIPLIER
            : 1.0D - playerTechniqueUsedNumber * (sixEyesMultiplier / (EFFICIENCY_PROGRESS_DIVISOR * difficultyScale));
        double floor = hasSixEyes ? Math.pow(0.1D, normalizedSixEyesAmplifier + 1.0D) : BASE_FLOOR;
        return Math.max(floor, efficiency);
    }

    static double resolveUpstreamTechniqueEfficiencyMultiplier(boolean hasSixEyes, int sixEyesAmplifier, boolean hasSukunaEffect) {
        int normalizedSixEyesAmplifier = hasSixEyes ? Math.max(sixEyesAmplifier, 0) : -1;
        double multiplier = hasSukunaEffect ? SUKUNA_BASE_MULTIPLIER : 1.0D;
        if (hasSixEyes) {
            multiplier *= Math.pow(0.1D, normalizedSixEyesAmplifier + 1.0D);
        }
        return multiplier;
    }
}
