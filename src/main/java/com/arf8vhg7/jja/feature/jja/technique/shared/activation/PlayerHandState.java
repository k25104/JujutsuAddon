package com.arf8vhg7.jja.feature.jja.technique.shared.activation;

import javax.annotation.Nullable;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;

public record PlayerHandState(HandItemState rightHandState, HandItemState leftHandState, boolean extraArmsUsed) {
    private static final PlayerHandState EMPTY = new PlayerHandState(HandItemState.EMPTY, HandItemState.EMPTY, false);

    public static PlayerHandState resolve(@Nullable Player player) {
        return resolve(player, false);
    }

    public static PlayerHandState resolve(@Nullable Player player, boolean extraArmsUsed) {
        if (player == null) {
            return extraArmsUsed ? new PlayerHandState(HandItemState.EMPTY, HandItemState.EMPTY, true) : EMPTY;
        }

        HandItemState mainHandState = PlayerHandStateRules.classifyHandItem(player, player.getMainHandItem());
        HandItemState offHandState = PlayerHandStateRules.classifyHandItem(player, player.getOffhandItem());
        return resolve(player.getMainArm(), mainHandState, offHandState, extraArmsUsed);
    }

    static PlayerHandState resolve(HumanoidArm mainArm, HandItemState mainHandState, HandItemState offHandState, boolean extraArmsUsed) {
        HandItemState rightHandState = mainArm == HumanoidArm.RIGHT ? mainHandState : offHandState;
        HandItemState leftHandState = mainArm == HumanoidArm.RIGHT ? offHandState : mainHandState;
        return new PlayerHandState(rightHandState, leftHandState, extraArmsUsed);
    }

    public HandItemState mainHandState(HumanoidArm mainArm) {
        return mainArm == HumanoidArm.RIGHT ? this.rightHandState : this.leftHandState;
    }

    public HandItemState offHandState(HumanoidArm mainArm) {
        return mainArm == HumanoidArm.RIGHT ? this.leftHandState : this.rightHandState;
    }

    public boolean isMainHandMeaningful(HumanoidArm mainArm) {
        return this.mainHandState(mainArm).isMeaningfulHeldItem();
    }

    public boolean isOffHandMeaningful(HumanoidArm mainArm) {
        return this.offHandState(mainArm).isMeaningfulHeldItem();
    }

    public boolean isMainHandSlashWeapon(HumanoidArm mainArm) {
        return this.mainHandState(mainArm).isSlashWeapon();
    }

    public boolean isMainHandTaijutsuWeapon(HumanoidArm mainArm) {
        return this.mainHandState(mainArm).isTaijutsuWeapon();
    }

    public boolean isRightHandFree() {
        return !this.rightHandState.isMeaningfulHeldItem();
    }

    public boolean isLeftHandFree() {
        return !this.leftHandState.isMeaningfulHeldItem();
    }

    public boolean hasAnyMeaningfulPhysicalHand() {
        return this.rightHandState.isMeaningfulHeldItem() || this.leftHandState.isMeaningfulHeldItem();
    }

    public boolean hasBothMeaningfulPhysicalHands() {
        return this.rightHandState.isMeaningfulHeldItem() && this.leftHandState.isMeaningfulHeldItem();
    }

    public boolean hasAnyFreePhysicalHand() {
        return this.isRightHandFree() || this.isLeftHandFree();
    }

    public boolean isExtraArmsUsed() {
        return this.extraArmsUsed;
    }

    public boolean isExtraArmsFree() {
        return !this.extraArmsUsed;
    }
}
