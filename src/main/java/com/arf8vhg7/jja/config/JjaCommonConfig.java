package com.arf8vhg7.jja.config;

import net.minecraftforge.common.ForgeConfigSpec;

public final class JjaCommonConfig {
    public static final ForgeConfigSpec SPEC;
    public static final ForgeConfigSpec.BooleanValue CE_EFFICIENCY_SCALING;
    public static final ForgeConfigSpec.BooleanValue CE_POOL_SCALING;
    public static final ForgeConfigSpec.IntValue CURSED_TECHNIQUE_CHANGER_COUNT;
    public static final ForgeConfigSpec.IntValue PROFESSION_CHANGER_COUNT;
    public static final ForgeConfigSpec.BooleanValue ATTACK_NONHOSTILE;
    public static final ForgeConfigSpec.BooleanValue SD_ITEM_ONLY;
    public static final ForgeConfigSpec.BooleanValue FBE_ITEM_ONLY;
    public static final ForgeConfigSpec.BooleanValue DA_ITEM_ONLY;
    public static final ForgeConfigSpec.BooleanValue ENABLE_SUKUNA_FAME;
    public static final ForgeConfigSpec.BooleanValue ENABLE_DEBUG;
    public static final ForgeConfigSpec.IntValue RCT_FATIGUE_RATE;
    public static final ForgeConfigSpec.IntValue BRAIN_HEALING_FATIGUE_AMOUNT;
    public static final ForgeConfigSpec.BooleanValue RCT_OUTPUT_ENABLED;
    public static final ForgeConfigSpec.BooleanValue BRAIN_DESTRUCTION_ENABLED;
    public static final ForgeConfigSpec.BooleanValue BRAIN_REGENERATION_ENABLED;
    public static final ForgeConfigSpec.BooleanValue AUTO_RCT_ENABLED;
    public static final ForgeConfigSpec.IntValue DOMAIN_EXPANSION_RADIUS;
    public static final ForgeConfigSpec.IntValue DOMAIN_EXPANSION_DURATION;
    public static final ForgeConfigSpec.IntValue UNSTABLE_DURATION;
    public static final ForgeConfigSpec.IntValue BUFF_INCREASE_INTERVAL;
    public static final ForgeConfigSpec.BooleanValue MALEVOLENT_SHRINE_TERRAIN_DESTRUCTION_SCALING;
    public static final ForgeConfigSpec.BooleanValue WEAKEST_PLAYER_SCALING;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        builder.push("debug");
        ENABLE_DEBUG = builder
            .comment("Write black flash and damage trace logs for combat debugging.")
            .define("enable_debug", false);
        builder.pop();
        
        builder.push("jja");
        builder.push("ce");
        CE_EFFICIENCY_SCALING = builder
            .comment("Enable PlayerTechniqueUsedNumber-based cursed energy efficiency scaling and the addon Sukuna/SixEyes technique cost adjustments tied to it.")
            .define("ce_efficiency_scaling", true);
        CE_POOL_SCALING = builder
            .comment("Enable PlayerTechniqueUsedNumber-based PlayerCursePowerFormer and PlayerCursePowerMAX recalculation.")
            .define("ce_pool_scaling", true);
        builder.pop();

        builder.push("rct");
        BRAIN_HEALING_FATIGUE_AMOUNT = builder
            .comment("How many fatigue ticks brain healing adds while repairing brain damage.")
            .defineInRange("brain_healing_fatigue_amount", 20, 0, Integer.MAX_VALUE);
        RCT_OUTPUT_ENABLED = builder
            .comment("Allow addon RCT output healing to run.")
            .define("output", true);
        BRAIN_DESTRUCTION_ENABLED = builder
            .comment("Allow brain destruction hold and completion to run.")
            .define("brain_destruction", true);
        BRAIN_REGENERATION_ENABLED = builder
            .comment("Allow addon brain regeneration to run while RCT is active.")
            .define("brain_regeneration", true);
        AUTO_RCT_ENABLED = builder
            .comment("Allow Auto RCT to start and maintain the channel.")
            .define("auto_rct", true);
        builder.pop();
        builder.pop();

        builder.push("jjc");
        builder.push("rct");
        RCT_FATIGUE_RATE = builder
            .comment("How many fatigue ticks RCT adds when it spends fatigue.")
            .defineInRange("rct_fatigue_rate", 20, 0, Integer.MAX_VALUE);
            builder.pop();
            
        builder.push("start");
        CURSED_TECHNIQUE_CHANGER_COUNT = builder
            .comment("How many cursed technique changers to grant when jujutsucraft:start_jujutsu_craft is earned.")
            .defineInRange("cursed_technique_changer_count", 1, 0, 64);
        PROFESSION_CHANGER_COUNT = builder
            .comment("How many profession changers to grant when jujutsucraft:start_jujutsu_craft is earned.")
            .defineInRange("profession_changer_count", 0, 0, 64);
        builder.pop();

        builder.push("combat");
        ATTACK_NONHOSTILE = builder
            .comment("Allow attacks that LogicAttackProcedure would otherwise deny only because of profession or forge:group_1..5.")
            .define("attack_nonhostile", true);
        builder.pop();

        builder.push("fame");
        ENABLE_SUKUNA_FAME = builder
            .comment("Allow players with the Sukuna effect to gain fame.")
            .define("enable_sukuna_fame", false);
        builder.pop();

        builder.push("domain_related");
        SD_ITEM_ONLY = builder
            .comment("Keep Simple Domain mastery item-only. When false, PlayerTechniqueUsedNumber progression also unlocks it.")
            .define("sd_item_only", true);
        FBE_ITEM_ONLY = builder
            .comment("Keep Falling Blossom Emotion mastery item-only. When false, Simple Domain mastery also unlocks it.")
            .define("fbe_item_only", true);
        DA_ITEM_ONLY = builder
            .comment("Keep Domain Amplification mastery restricted to its item and addon witness unlocks. When false, legacy non-item grants stay enabled.")
            .define("da_item_only", true);

        DOMAIN_EXPANSION_RADIUS = builder
            .comment("Base radius used for domain expansion logic. Active domains snapshot this value when they start.")
            .defineInRange("domain_expansion_radius", 22, 1, Integer.MAX_VALUE);
        DOMAIN_EXPANSION_DURATION = builder
            .comment("Duration applied to DOMAIN_EXPANSION when a domain starts. Set to -1 to keep the current effectively infinite addon behavior.")
            .defineInRange("domain_expansion_duration", -1, -1, Integer.MAX_VALUE);
        UNSTABLE_DURATION = builder
            .comment("Base duration applied to UNSTABLE when a domain is released. Cursed spirits receive half of this value.")
            .defineInRange("unstable_duration", 1200, 0, Integer.MAX_VALUE);
        MALEVOLENT_SHRINE_TERRAIN_DESTRUCTION_SCALING = builder
            .comment("Terrain destruction will be attempted multiple times based on the level of the strength effect. Note: This can get very laggy.")
            .define("malevolent_shrine_terrain_destruction_scaling", false);
        builder.pop();

        builder.push("physical_ability");
        BUFF_INCREASE_INTERVAL = builder
            .comment("How many ticks it takes for player speed and crouching jump boost to gain one amplifier level.")
            .defineInRange("buff_increase_interval", 6, 1, Integer.MAX_VALUE);
        builder.pop();

        builder.push("spawn_scaling");
        WEAKEST_PLAYER_SCALING = builder
            .comment("Make random entity spawns scale based on the weakest player instead of the strongest player.")
            .define("weakest_player_scaling", true);
        builder.pop();
        builder.pop();
        SPEC = builder.build();
    }

    private JjaCommonConfig() {
    }
}
