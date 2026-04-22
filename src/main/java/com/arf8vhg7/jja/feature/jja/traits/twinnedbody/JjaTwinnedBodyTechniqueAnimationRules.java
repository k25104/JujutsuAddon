package com.arf8vhg7.jja.feature.jja.traits.twinnedbody;

public final class JjaTwinnedBodyTechniqueAnimationRules {
    private JjaTwinnedBodyTechniqueAnimationRules() {
    }

    public static boolean jjaIsAnimationReset(String animationName) {
        return animationName != null && ("cancel".equals(animationName) || "empty".equals(animationName));
    }

    public static boolean jjaIsExtraArmOnlyAnimation(String animationName) {
        return animationName != null
            && (animationName.startsWith("charge")
                || animationName.startsWith("breath")
                || animationName.startsWith("domain_expansion")
                || "open".equals(animationName)
                || "red".equals(animationName)
                || "energy_charge".equals(animationName));
    }

    public static boolean jjaShouldMirrorOnExtraArms(String animationName) {
        return animationName != null
            && (animationName.startsWith("sword_to_")
                || animationName.startsWith("punch_")
                || animationName.startsWith("kick_")
                || animationName.startsWith("combo")
                || animationName.startsWith("simple_domain")
                || "clap".equals(animationName)
                || "sword_overhead".equals(animationName)
                || "sword_overhead2".equals(animationName));
    }
}
