package com.arf8vhg7.jja.feature.player.state.model;

import com.arf8vhg7.jja.feature.player.state.JjaPlayerStateSchema;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;

public final class PlayerAddonStatsState {
    private double cursePowerPreservation;
    private boolean shikigamiEnhancementEnabled;
    private boolean hollowWickerBasketEnabled;
    private boolean techniqueSetupMigrated;
    private int antiDomainNormalOptionId = -1;
    private int antiDomainCrouchOptionId = -1;
    private int domainTypeNormalOptionId = -1;
    private int domainTypeCrouchOptionId = -1;
    private int seenAntiDomainAvailableMask;
    private int seenDomainTypeAvailableMask;
    private int activeAntiDomainPresentationId;
    private int bfRanded;
    private int deUsed;
    private int simpleDomainUsed;
    private int fbeUsed;
    private boolean pendingOpenBarrierMastery;
    private boolean observedDoubleJumpUnlock;
    private int ceColorOverride = -1;
    private int curtainRadius = 44;

    public double getCursePowerPreservation() {
        return this.cursePowerPreservation;
    }

    public void setCursePowerPreservation(double cursePowerPreservation) {
        this.cursePowerPreservation = cursePowerPreservation;
    }

    public boolean isShikigamiEnhancementEnabled() {
        return this.shikigamiEnhancementEnabled;
    }

    public void setShikigamiEnhancementEnabled(boolean shikigamiEnhancementEnabled) {
        this.shikigamiEnhancementEnabled = shikigamiEnhancementEnabled;
    }

    public boolean isHollowWickerBasketEnabled() {
        return this.hollowWickerBasketEnabled;
    }

    public void setHollowWickerBasketEnabled(boolean hollowWickerBasketEnabled) {
        this.hollowWickerBasketEnabled = hollowWickerBasketEnabled;
    }

    public boolean isTechniqueSetupMigrated() {
        return this.techniqueSetupMigrated;
    }

    public void setTechniqueSetupMigrated(boolean techniqueSetupMigrated) {
        this.techniqueSetupMigrated = techniqueSetupMigrated;
    }

    public int getAntiDomainNormalOptionId() {
        return this.antiDomainNormalOptionId;
    }

    public void setAntiDomainNormalOptionId(int antiDomainNormalOptionId) {
        this.antiDomainNormalOptionId = antiDomainNormalOptionId;
    }

    public int getAntiDomainCrouchOptionId() {
        return this.antiDomainCrouchOptionId;
    }

    public void setAntiDomainCrouchOptionId(int antiDomainCrouchOptionId) {
        this.antiDomainCrouchOptionId = antiDomainCrouchOptionId;
    }

    public int getDomainTypeNormalOptionId() {
        return this.domainTypeNormalOptionId;
    }

    public void setDomainTypeNormalOptionId(int domainTypeNormalOptionId) {
        this.domainTypeNormalOptionId = domainTypeNormalOptionId;
    }

    public int getDomainTypeCrouchOptionId() {
        return this.domainTypeCrouchOptionId;
    }

    public void setDomainTypeCrouchOptionId(int domainTypeCrouchOptionId) {
        this.domainTypeCrouchOptionId = domainTypeCrouchOptionId;
    }

    public int getSeenAntiDomainAvailableMask() {
        return this.seenAntiDomainAvailableMask;
    }

    public void setSeenAntiDomainAvailableMask(int seenAntiDomainAvailableMask) {
        this.seenAntiDomainAvailableMask = Math.max(0, seenAntiDomainAvailableMask);
    }

    public int getSeenDomainTypeAvailableMask() {
        return this.seenDomainTypeAvailableMask;
    }

    public void setSeenDomainTypeAvailableMask(int seenDomainTypeAvailableMask) {
        this.seenDomainTypeAvailableMask = Math.max(0, seenDomainTypeAvailableMask);
    }

    public int getActiveAntiDomainPresentationId() {
        return this.activeAntiDomainPresentationId;
    }

    public void setActiveAntiDomainPresentationId(int activeAntiDomainPresentationId) {
        this.activeAntiDomainPresentationId = activeAntiDomainPresentationId;
    }

    public int getBfRanded() {
        return this.bfRanded;
    }

    public void setBfRanded(int bfRanded) {
        this.bfRanded = Math.max(0, bfRanded);
    }

    public void incrementBfRanded() {
        this.bfRanded++;
    }

    public int getDeUsed() {
        return this.deUsed;
    }

    public void setDeUsed(int deUsed) {
        this.deUsed = Math.max(0, deUsed);
    }

    public void incrementDeUsed() {
        this.deUsed++;
    }

    public int getSimpleDomainUsed() {
        return this.simpleDomainUsed;
    }

    public void setSimpleDomainUsed(int simpleDomainUsed) {
        this.simpleDomainUsed = Math.max(0, simpleDomainUsed);
    }

    public void incrementSimpleDomainUsed() {
        this.simpleDomainUsed++;
    }

    public int getFbeUsed() {
        return this.fbeUsed;
    }

    public void setFbeUsed(int fbeUsed) {
        this.fbeUsed = Math.max(0, fbeUsed);
    }

    public void incrementFbeUsed() {
        this.fbeUsed++;
    }

    public boolean isPendingOpenBarrierMastery() {
        return this.pendingOpenBarrierMastery;
    }

    public void setPendingOpenBarrierMastery(boolean pendingOpenBarrierMastery) {
        this.pendingOpenBarrierMastery = pendingOpenBarrierMastery;
    }

    public boolean isObservedDoubleJumpUnlock() {
        return this.observedDoubleJumpUnlock;
    }

    public void setObservedDoubleJumpUnlock(boolean observedDoubleJumpUnlock) {
        this.observedDoubleJumpUnlock = observedDoubleJumpUnlock;
    }

    public boolean hasCeColorOverride() {
        return this.ceColorOverride >= 1 && this.ceColorOverride <= 5;
    }

    public int getCeColorOverride() {
        return this.ceColorOverride;
    }

    public void setCeColorOverride(int ceColorOverride) {
        if (ceColorOverride < 1) {
            this.ceColorOverride = -1;
            return;
        }
        this.ceColorOverride = Math.min(5, ceColorOverride);
    }

    public void clearCeColorOverride() {
        this.ceColorOverride = -1;
    }

    public int getCurtainRadius() {
        return this.curtainRadius;
    }

    public void setCurtainRadius(int curtainRadius) {
        this.curtainRadius = Math.max(1, curtainRadius);
    }

    public void copyCommandStateFrom(PlayerAddonStatsState other) {
        this.bfRanded = other.bfRanded;
        this.deUsed = other.deUsed;
        this.simpleDomainUsed = other.simpleDomainUsed;
        this.fbeUsed = other.fbeUsed;
        this.ceColorOverride = other.ceColorOverride;
        this.curtainRadius = other.curtainRadius;
    }

    public void copyProgressionStateFrom(PlayerAddonStatsState other) {
        this.pendingOpenBarrierMastery = other.pendingOpenBarrierMastery;
        this.observedDoubleJumpUnlock = other.observedDoubleJumpUnlock;
    }

    public void copyShikigamiStateFrom(PlayerAddonStatsState other) {
        this.shikigamiEnhancementEnabled = other.shikigamiEnhancementEnabled;
    }

    public void copySdStateFrom(PlayerAddonStatsState other) {
        this.hollowWickerBasketEnabled = other.hollowWickerBasketEnabled;
        this.techniqueSetupMigrated = other.techniqueSetupMigrated;
        this.antiDomainNormalOptionId = other.antiDomainNormalOptionId;
        this.antiDomainCrouchOptionId = other.antiDomainCrouchOptionId;
        this.domainTypeNormalOptionId = other.domainTypeNormalOptionId;
        this.domainTypeCrouchOptionId = other.domainTypeCrouchOptionId;
        this.seenAntiDomainAvailableMask = other.seenAntiDomainAvailableMask;
        this.seenDomainTypeAvailableMask = other.seenDomainTypeAvailableMask;
        this.activeAntiDomainPresentationId = other.activeAntiDomainPresentationId;
    }

    public void writeTo(CompoundTag nbt) {
        nbt.putDouble(JjaPlayerStateSchema.NBT_KEY_PRESERVATION, this.cursePowerPreservation);
        nbt.putBoolean(JjaPlayerStateSchema.NBT_KEY_JJA_SHIKIGAMI_ENHANCEMENT_ENABLED, this.shikigamiEnhancementEnabled);
        nbt.putBoolean(JjaPlayerStateSchema.NBT_KEY_JJA_HOLLOW_WICKER_BASKET_ENABLED, this.hollowWickerBasketEnabled);
        nbt.putBoolean(JjaPlayerStateSchema.NBT_KEY_JJA_TECHNIQUE_SETUP_MIGRATED, this.techniqueSetupMigrated);
        nbt.putInt(JjaPlayerStateSchema.NBT_KEY_JJA_ANTI_DOMAIN_NORMAL, this.antiDomainNormalOptionId);
        nbt.putInt(JjaPlayerStateSchema.NBT_KEY_JJA_ANTI_DOMAIN_CROUCH, this.antiDomainCrouchOptionId);
        nbt.putInt(JjaPlayerStateSchema.NBT_KEY_JJA_DOMAIN_TYPE_NORMAL, this.domainTypeNormalOptionId);
        nbt.putInt(JjaPlayerStateSchema.NBT_KEY_JJA_DOMAIN_TYPE_CROUCH, this.domainTypeCrouchOptionId);
        nbt.putInt(JjaPlayerStateSchema.NBT_KEY_JJA_SEEN_ANTI_DOMAIN_AVAILABLE_MASK, this.seenAntiDomainAvailableMask);
        nbt.putInt(JjaPlayerStateSchema.NBT_KEY_JJA_SEEN_DOMAIN_TYPE_AVAILABLE_MASK, this.seenDomainTypeAvailableMask);
        nbt.putInt(JjaPlayerStateSchema.NBT_KEY_JJA_ACTIVE_ANTI_DOMAIN_PRESENTATION, this.activeAntiDomainPresentationId);
        nbt.putInt(JjaPlayerStateSchema.NBT_KEY_JJA_BF_RANDED, this.bfRanded);
        nbt.putInt(JjaPlayerStateSchema.NBT_KEY_JJA_DE_USED, this.deUsed);
        nbt.putInt(JjaPlayerStateSchema.NBT_KEY_JJA_SIMPLE_DOMAIN_USED, this.simpleDomainUsed);
        nbt.putInt(JjaPlayerStateSchema.NBT_KEY_JJA_FBE_USED, this.fbeUsed);
        nbt.putBoolean(JjaPlayerStateSchema.NBT_KEY_JJA_PENDING_OPEN_BARRIER_MASTERY, this.pendingOpenBarrierMastery);
        nbt.putBoolean(JjaPlayerStateSchema.NBT_KEY_JJA_OBSERVED_DOUBLE_JUMP_UNLOCK, this.observedDoubleJumpUnlock);
        nbt.putInt(JjaPlayerStateSchema.NBT_KEY_JJA_CE_COLOR_OVERRIDE, this.ceColorOverride);
        nbt.putInt(JjaPlayerStateSchema.NBT_KEY_JJA_CURTAIN_RADIUS, this.curtainRadius);
    }

    public void readFrom(CompoundTag nbt) {
        this.cursePowerPreservation = nbt.getDouble(JjaPlayerStateSchema.NBT_KEY_PRESERVATION);
        this.shikigamiEnhancementEnabled = nbt.getBoolean(JjaPlayerStateSchema.NBT_KEY_JJA_SHIKIGAMI_ENHANCEMENT_ENABLED);
        this.hollowWickerBasketEnabled = nbt.getBoolean(JjaPlayerStateSchema.NBT_KEY_JJA_HOLLOW_WICKER_BASKET_ENABLED);
        this.techniqueSetupMigrated = nbt.getBoolean(JjaPlayerStateSchema.NBT_KEY_JJA_TECHNIQUE_SETUP_MIGRATED);
        this.antiDomainNormalOptionId = nbt.contains(JjaPlayerStateSchema.NBT_KEY_JJA_ANTI_DOMAIN_NORMAL, Tag.TAG_INT)
            ? nbt.getInt(JjaPlayerStateSchema.NBT_KEY_JJA_ANTI_DOMAIN_NORMAL)
            : -1;
        this.antiDomainCrouchOptionId = nbt.contains(JjaPlayerStateSchema.NBT_KEY_JJA_ANTI_DOMAIN_CROUCH, Tag.TAG_INT)
            ? nbt.getInt(JjaPlayerStateSchema.NBT_KEY_JJA_ANTI_DOMAIN_CROUCH)
            : -1;
        this.domainTypeNormalOptionId = nbt.contains(JjaPlayerStateSchema.NBT_KEY_JJA_DOMAIN_TYPE_NORMAL, Tag.TAG_INT)
            ? nbt.getInt(JjaPlayerStateSchema.NBT_KEY_JJA_DOMAIN_TYPE_NORMAL)
            : -1;
        this.domainTypeCrouchOptionId = nbt.contains(JjaPlayerStateSchema.NBT_KEY_JJA_DOMAIN_TYPE_CROUCH, Tag.TAG_INT)
            ? nbt.getInt(JjaPlayerStateSchema.NBT_KEY_JJA_DOMAIN_TYPE_CROUCH)
            : -1;
        this.seenAntiDomainAvailableMask = nbt.contains(JjaPlayerStateSchema.NBT_KEY_JJA_SEEN_ANTI_DOMAIN_AVAILABLE_MASK, Tag.TAG_INT)
            ? nbt.getInt(JjaPlayerStateSchema.NBT_KEY_JJA_SEEN_ANTI_DOMAIN_AVAILABLE_MASK)
            : 0;
        this.seenDomainTypeAvailableMask = nbt.contains(JjaPlayerStateSchema.NBT_KEY_JJA_SEEN_DOMAIN_TYPE_AVAILABLE_MASK, Tag.TAG_INT)
            ? nbt.getInt(JjaPlayerStateSchema.NBT_KEY_JJA_SEEN_DOMAIN_TYPE_AVAILABLE_MASK)
            : 0;
        this.activeAntiDomainPresentationId = nbt.contains(JjaPlayerStateSchema.NBT_KEY_JJA_ACTIVE_ANTI_DOMAIN_PRESENTATION, Tag.TAG_INT)
            ? nbt.getInt(JjaPlayerStateSchema.NBT_KEY_JJA_ACTIVE_ANTI_DOMAIN_PRESENTATION)
            : 0;
        this.bfRanded = nbt.getInt(JjaPlayerStateSchema.NBT_KEY_JJA_BF_RANDED);
        this.deUsed = nbt.getInt(JjaPlayerStateSchema.NBT_KEY_JJA_DE_USED);
        this.simpleDomainUsed = nbt.getInt(JjaPlayerStateSchema.NBT_KEY_JJA_SIMPLE_DOMAIN_USED);
        this.fbeUsed = nbt.getInt(JjaPlayerStateSchema.NBT_KEY_JJA_FBE_USED);
        this.pendingOpenBarrierMastery = nbt.getBoolean(JjaPlayerStateSchema.NBT_KEY_JJA_PENDING_OPEN_BARRIER_MASTERY);
        this.observedDoubleJumpUnlock = nbt.getBoolean(JjaPlayerStateSchema.NBT_KEY_JJA_OBSERVED_DOUBLE_JUMP_UNLOCK);
        this.ceColorOverride = nbt.contains(JjaPlayerStateSchema.NBT_KEY_JJA_CE_COLOR_OVERRIDE, Tag.TAG_INT)
            ? nbt.getInt(JjaPlayerStateSchema.NBT_KEY_JJA_CE_COLOR_OVERRIDE)
            : -1;
        this.curtainRadius = nbt.contains(JjaPlayerStateSchema.NBT_KEY_JJA_CURTAIN_RADIUS, Tag.TAG_INT)
            ? Math.max(1, nbt.getInt(JjaPlayerStateSchema.NBT_KEY_JJA_CURTAIN_RADIUS))
            : 44;
    }

    public void reset() {
        this.cursePowerPreservation = 0.0;
        this.shikigamiEnhancementEnabled = false;
        this.hollowWickerBasketEnabled = false;
        this.techniqueSetupMigrated = false;
        this.antiDomainNormalOptionId = -1;
        this.antiDomainCrouchOptionId = -1;
        this.domainTypeNormalOptionId = -1;
        this.domainTypeCrouchOptionId = -1;
        this.seenAntiDomainAvailableMask = 0;
        this.seenDomainTypeAvailableMask = 0;
        this.activeAntiDomainPresentationId = 0;
        this.bfRanded = 0;
        this.deUsed = 0;
        this.simpleDomainUsed = 0;
        this.fbeUsed = 0;
        this.pendingOpenBarrierMastery = false;
        this.observedDoubleJumpUnlock = false;
        this.ceColorOverride = -1;
        this.curtainRadius = 44;
    }
}
