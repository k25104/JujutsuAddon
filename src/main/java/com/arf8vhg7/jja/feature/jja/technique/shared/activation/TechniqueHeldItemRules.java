package com.arf8vhg7.jja.feature.jja.technique.shared.activation;

import com.arf8vhg7.jja.feature.jja.traits.twinnedbody.TwinnedBodyRuntimeStateAccess;
import com.arf8vhg7.jja.feature.jja.traits.twinnedbody.TwinnedBodyTechniqueAnimationStateAccess;
import net.mcreator.jujutsucraft.init.JujutsucraftModItems;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public final class TechniqueHeldItemRules {
    private static final int HIGURUMA_CT = 27;
    private static final int HIGURUMA_DE_SKILL_ID = 2720;
    private static final int DIVERGENT_FIST_SKILL_ID = 2105;

    private TechniqueHeldItemRules() {
    }

    public static boolean canUseSkill(Player player, int skillId) {
        if (player == null) {
            return false;
        }
        if (player.isCreative()) {
            return true;
        }

        boolean twinnedBodyExtraArmsUsed = TwinnedBodyRuntimeStateAccess.isTwinnedBodyMarked(player)
            && TwinnedBodyTechniqueAnimationStateAccess.isTechniqueAnimationActive(player);
        PlayerHandState handState = PlayerHandState.resolve(player, twinnedBodyExtraArmsUsed);
        if (TwinnedBodyRuntimeStateAccess.isTwinnedBodyMarked(player) && !twinnedBodyExtraArmsUsed) {
            return true;
        }

        if (skillId == HIGURUMA_DE_SKILL_ID && TechniqueSkillResolver.hasTechnique(player, HIGURUMA_CT)) {
            return true;
        }

        TechniqueHandRestriction restriction = TechniqueSkillPolicyCatalog.resolve(skillId).handRestriction();
        return satisfiesHandRestriction(restriction, handState.hasAnyMeaningfulPhysicalHand(), handState.hasBothMeaningfulPhysicalHands());
    }

    public static boolean canUseSelectedSkill(Player player) {
        return canUseSkill(player, TechniqueSkillResolver.resolveSelectedSkillId(player));
    }

    public static boolean shouldDisableHeldItemDamageBonus(Entity entity) {
        if (!(entity instanceof Player)) {
            return false;
        }

        int skillId = TechniqueSkillResolver.resolveCurrentSkillId(entity);
        if (skillId == 0) {
            return false;
        }
        if (skillId == DIVERGENT_FIST_SKILL_ID) {
            return entity.getPersistentData().getDouble("cnt7") > 0.0;
        }
        return TechniqueSkillPolicyCatalog.resolve(skillId).disableHeldItemDamageBonus();
    }

    public static boolean shouldDisableHeldItemDamageBonus(Entity entity, boolean extraArmAttack) {
        return extraArmAttack || shouldDisableHeldItemDamageBonus(entity);
    }

    public static HeldItemBfPenalty resolveBlackFlashPenalty(Entity entity) {
        if (!(entity instanceof Player player)) {
            return HeldItemBfPenalty.NONE;
        }

        if (!hasActiveHeldItemDamageBonus(player)) {
            return HeldItemBfPenalty.NONE;
        }

        PlayerHandState handState = PlayerHandState.resolve(player);
        HandItemState mainHandState = handState.mainHandState(player.getMainArm());
        return resolveBlackFlashPenalty(
            mainHandState,
            isGarudaWeapon(player.getMainHandItem()),
            false
        );
    }

    public static HeldItemBfPenalty resolveBlackFlashPenalty(Entity entity, boolean extraArmAttack) {
        return extraArmAttack ? HeldItemBfPenalty.NONE : resolveBlackFlashPenalty(entity);
    }

    static HeldItemBfPenalty resolveBlackFlashPenalty(
        HandItemState mainHandState,
        boolean garudaWeapon,
        boolean twinnedBodyExtraArmsUsed
    ) {
        if (twinnedBodyExtraArmsUsed) {
            return HeldItemBfPenalty.NONE;
        }

        if (mainHandState == null || !mainHandState.isMeaningfulHeldItem()) {
            return HeldItemBfPenalty.NONE;
        }

        if (garudaWeapon) {
            return HeldItemBfPenalty.BLUNT;
        }
        if (mainHandState.isSlashWeapon()) {
            return HeldItemBfPenalty.SLASH;
        }
        return HeldItemBfPenalty.BLUNT;
    }

    private static boolean isGarudaWeapon(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return false;
        }

        Item item = stack.getItem();
        return item == JujutsucraftModItems.GARUDA_ITEM.get() || item == JujutsucraftModItems.GARUDA_ITEM_BALL.get();
    }

    public static boolean isBareHandEquivalent(Player player, ItemStack stack) {
        return PlayerHandStateRules.isBareHandEquivalent(player, stack);
    }

    private static boolean hasActiveHeldItemDamageBonus(Entity entity) {
        if (!(entity instanceof Player player)) {
            return false;
        }
        if (shouldDisableHeldItemDamageBonus(entity)) {
            return false;
        }
        return isDamageBoostingHeldItem(player, player.getMainHandItem());
    }

    private static boolean isDamageBoostingHeldItem(Player player, ItemStack stack) {
        if (stack == null || stack.isEmpty() || PlayerHandStateRules.isBareHandEquivalent(player, stack)) {
            return false;
        }
        if (stack.getItem() == JujutsucraftModItems.PLAYFUL_CLOUD.get()) {
            return true;
        }
        if (!stack.hasTag()) {
            return false;
        }
        return stack.getOrCreateTag().getDouble("Power") > 0.0;
    }

    static boolean satisfiesHandRestriction(TechniqueHandRestriction restriction, boolean anyHandOccupied, boolean bothHandsOccupied) {
        return switch (restriction) {
            case NONE -> true;
            case FORBID_ANY_HELD_ITEM -> !anyHandOccupied;
            case FORBID_BOTH_HELD_ITEMS -> !bothHandsOccupied;
        };
    }

    public enum HeldItemBfPenalty {
        NONE,
        BLUNT,
        SLASH
    }
}
