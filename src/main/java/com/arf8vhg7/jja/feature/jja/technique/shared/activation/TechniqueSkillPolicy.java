package com.arf8vhg7.jja.feature.jja.technique.shared.activation;

public record TechniqueSkillPolicy(
    TechniqueHandRestriction handRestriction,
    boolean disableHeldItemDamageBonus
) {
    public static TechniqueSkillPolicy unrestricted() {
        return new TechniqueSkillPolicy(TechniqueHandRestriction.NONE, false);
    }

    public static TechniqueSkillPolicy restricted(TechniqueHandRestriction restriction) {
        return new TechniqueSkillPolicy(restriction, true);
    }
}
