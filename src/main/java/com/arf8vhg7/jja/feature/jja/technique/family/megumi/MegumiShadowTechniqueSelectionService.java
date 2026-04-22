package com.arf8vhg7.jja.feature.jja.technique.family.megumi;

import com.arf8vhg7.jja.compat.jujutsucraft.JjaJujutsucraftCompat;
import com.arf8vhg7.jja.feature.equipment.curios.CuriosEquipmentReadService;
import com.arf8vhg7.jja.feature.jja.technique.shared.selection.TechniqueSelectionCandidate;
import com.arf8vhg7.jja.feature.jja.technique.shared.selection.TechniqueSelectionSupport;
import com.arf8vhg7.jja.feature.jja.technique.shared.selection.TechniqueSelectionWindow;
import com.arf8vhg7.jja.util.JjaAdvancementHelper;
import net.mcreator.jujutsucraft.init.JujutsucraftModItems;
import net.mcreator.jujutsucraft.network.JujutsucraftModVariables;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;

public final class MegumiShadowTechniqueSelectionService {
    public static final int CURSE_TECHNIQUE_ID = 6;
    public static final int SHADOW_SELECT = 16;
    public static final int SHADOW_SKILL = CURSE_TECHNIQUE_ID * 100 + SHADOW_SELECT;
    public static final String SHADOW_NAME_KEY = "jujutsu.technique.jja_megumi_shadow";
    private static final String EMPTY_SELECTION_NAME = TechniqueSelectionCandidate.none(0.0D).name();
    private static final int MAX_SELECTION_STEPS = 25;
    private static final ResourceLocation SKILL_NUE_TITAN = jujutsucraft("skill_nue_titan");
    private static final ResourceLocation SKILL_MAX_ELEPHANT = jujutsucraft("skill_max_elephant");
    private static final ResourceLocation SKILL_MAX_ELEPHANT_PIERCING_BLOOD = jujutsucraft("skill_max_elephant_piercing_blood");
    private static final ResourceLocation SKILL_MAHORAGA = jujutsucraft("skill_mahoraga");
    private static final ResourceLocation SKILL_MAHORAGA_WHEEL = jujutsucraft("skill_mahoraga_wheel");

    private MegumiShadowTechniqueSelectionService() {
    }

    public static boolean tryHandlePageSelection(LevelAccessor world, double x, double y, double z, Entity entity) {
        if (entity == null) {
            return false;
        }

        JujutsucraftModVariables.PlayerVariables playerVariables = JjaJujutsucraftCompat.jjaGetPlayerVariables(entity);
        if (playerVariables == null) {
            return false;
        }

        double playerCt = playerVariables.SecondTechnique ? playerVariables.PlayerCurseTechnique2 : playerVariables.PlayerCurseTechnique;
        if ((int) Math.round(playerCt) != CURSE_TECHNIQUE_ID) {
            return false;
        }

        double select = resolveInitialSelect(playerVariables.PlayerSelectCurseTechnique, playerVariables.noChangeTechnique, entity.isShiftKeyDown());
        return TechniqueSelectionSupport.tryHandlePageSelection(
            world,
            x,
            y,
            z,
            entity,
            playerCt,
            select,
            MAX_SELECTION_STEPS,
            candidateSelect -> resolveSelection(entity, candidateSelect)
        );
    }

    static double resolveInitialSelect(double currentSelect, boolean noChangeTechnique, boolean reverse) {
        if (noChangeTechnique) {
            return currentSelect;
        }
        return TechniqueSelectionWindow.advance(currentSelect, reverse);
    }

    static TechniqueSelectionCandidate resolveSelection(Entity entity, double select) {
        TechniqueSelectionCandidate sharedCandidate = TechniqueSelectionSupport.resolveSharedCombatCandidate(select);
        if (sharedCandidate != null) {
            return sharedCandidate;
        }

        int selectionId = (int) Math.round(select);
        return switch (selectionId) {
            case 4 -> new TechniqueSelectionCandidate(select, "jujutsu.technique.cancel", true, true, 0.0D);
            case 5 -> new TechniqueSelectionCandidate(select, "entity.jujutsucraft.divine_dog_white", false, false, 100.0D);
            case 6 -> new TechniqueSelectionCandidate(select, "entity.jujutsucraft.divine_dog_black", false, false, 100.0D);
            case 7 -> new TechniqueSelectionCandidate(select, "entity.jujutsucraft.divine_dog_totality", false, false, 400.0D);
            case 8 -> resolveNueSelection(entity, select);
            case 9 -> new TechniqueSelectionCandidate(select, "entity.jujutsucraft.great_serpent", false, false, 300.0D);
            case 10 -> new TechniqueSelectionCandidate(select, "entity.jujutsucraft.toad", false, false, 150.0D);
            case 11 -> resolveMaxElephantSelection(entity, select);
            case 12 -> new TechniqueSelectionCandidate(select, "entity.jujutsucraft.rabbit_escape", false, false, 125.0D);
            case 13 -> new TechniqueSelectionCandidate(select, "entity.jujutsucraft.round_deer", false, false, 600.0D);
            case 14 -> new TechniqueSelectionCandidate(select, "entity.jujutsucraft.piercing_ox", false, false, 400.0D);
            case 15 -> new TechniqueSelectionCandidate(select, "entity.jujutsucraft.tiger_funeral", false, false, 400.0D);
            case SHADOW_SELECT -> new TechniqueSelectionCandidate(select, SHADOW_NAME_KEY, true, false, 0.0D);
            case 17 -> new TechniqueSelectionCandidate(select, "entity.jujutsucraft.merged_beast_agito", false, false, 600.0D);
            case 18 -> resolveMahoragaSelection(entity, select);
            case 20 -> new TechniqueSelectionCandidate(select, "jujutsu.technique.chimera_shadow_garden", false, false, 1250.0D);
            default -> TechniqueSelectionCandidate.none(select);
        };
    }

    public static boolean isShadowSelection(int curseTechniqueId, int selectTechniqueId) {
        return curseTechniqueId == CURSE_TECHNIQUE_ID && selectTechniqueId == SHADOW_SELECT;
    }

    public static TechniqueSelectionCandidate resolvePageOneSupplement(double playerCt, double playerSelect, String currentName) {
        if (!isShadowSelection((int) Math.round(playerCt), (int) Math.round(playerSelect))
            || !EMPTY_SELECTION_NAME.equals(currentName)) {
            return TechniqueSelectionCandidate.none(playerSelect);
        }
        return new TechniqueSelectionCandidate(playerSelect, SHADOW_NAME_KEY, true, false, 0.0D);
    }

    private static TechniqueSelectionCandidate resolveNueSelection(Entity entity, double select) {
        if (entity != null && entity.isShiftKeyDown() && (isCreative(entity) || hasAdvancement(entity, SKILL_NUE_TITAN))) {
            return new TechniqueSelectionCandidate(select, "entity.jujutsucraft.nue_totality", false, false, 500.0D);
        }
        return new TechniqueSelectionCandidate(select, "entity.jujutsucraft.nue", false, false, 250.0D);
    }

    private static TechniqueSelectionCandidate resolveMaxElephantSelection(Entity entity, double select) {
        if (canUsePiercingWater(entity)) {
            return new TechniqueSelectionCandidate(select, "jujutsu.technique.choso3", false, false, 200.0D);
        }
        return new TechniqueSelectionCandidate(select, "entity.jujutsucraft.max_elephant", false, false, 750.0D);
    }

    private static TechniqueSelectionCandidate resolveMahoragaSelection(Entity entity, double select) {
        if (isWearingMahoragaWheel(entity)) {
            return new TechniqueSelectionCandidate(
                select,
                "entity.jujutsucraft.eight_handled_sword_divergent_sila_divine_general_mahoraga",
                true,
                false,
                0.0D
            );
        }
        if (canUseMahoragaWheel(entity)) {
            return new TechniqueSelectionCandidate(select, "item.jujutsucraft.mahoraga_wheel_helmet", true, false, 1000.0D);
        }
        return new TechniqueSelectionCandidate(
            select,
            "entity.jujutsucraft.eight_handled_sword_divergent_sila_divine_general_mahoraga",
            false,
            false,
            1000.0D
        );
    }

    private static boolean canUsePiercingWater(Entity entity) {
        if (entity == null || !entity.isShiftKeyDown()) {
            return false;
        }
        return isCreative(entity)
            || (hasTamedTechnique(entity, 7, SKILL_MAX_ELEPHANT) && hasAdvancement(entity, SKILL_MAX_ELEPHANT_PIERCING_BLOOD));
    }

    private static boolean canUseMahoragaWheel(Entity entity) {
        if (entity == null || !entity.isShiftKeyDown()) {
            return false;
        }
        return isCreative(entity) || (hasTamedTechnique(entity, 14, SKILL_MAHORAGA) && hasAdvancement(entity, SKILL_MAHORAGA_WHEEL));
    }

    private static boolean hasTamedTechnique(Entity entity, int techniqueIndex, ResourceLocation advancementId) {
        return entity.getPersistentData().getDouble("TenShadowsTechnique" + techniqueIndex) > 0.0D
            || (entity.getPersistentData().getDouble("TenShadowsTechnique" + techniqueIndex) >= 0.0D && hasAdvancement(entity, advancementId));
    }

    private static boolean hasAdvancement(Entity entity, ResourceLocation advancementId) {
        return entity instanceof ServerPlayer player && JjaAdvancementHelper.has(player, advancementId);
    }

    private static boolean isCreative(Entity entity) {
        return entity instanceof Player player && player.getAbilities().instabuild;
    }

    private static boolean isWearingMahoragaWheel(Entity entity) {
        if (!(entity instanceof LivingEntity livingEntity)) {
            return false;
        }
        ItemStack headStack = CuriosEquipmentReadService.resolveEquipmentRead(
            livingEntity,
            EquipmentSlot.HEAD,
            livingEntity.getItemBySlot(EquipmentSlot.HEAD)
        );
        return headStack.getItem() == JujutsucraftModItems.MAHORAGA_WHEEL_HELMET.get();
    }

    private static ResourceLocation jujutsucraft(String path) {
        return ResourceLocation.fromNamespaceAndPath("jujutsucraft", path);
    }
}
