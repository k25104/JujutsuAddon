package com.arf8vhg7.jja.feature.jja.technique.shared.menu;

import com.arf8vhg7.jja.feature.jja.technique.shared.registration.TechniqueSetupRegistrationCandidate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import net.minecraft.network.FriendlyByteBuf;

public record TechniqueSetupViewState(
    int visibleCategoriesMask,
    int antiDomainAvailableMask,
    int domainTypeAvailableMask,
    int antiDomainNormalOptionId,
    int antiDomainCrouchOptionId,
    int domainTypeNormalOptionId,
    int domainTypeCrouchOptionId,
    List<TechniqueSetupRegistrationCandidate> registrationCandidates
) {
    public TechniqueSetupViewState {
        registrationCandidates = List.copyOf(Objects.requireNonNull(registrationCandidates, "registrationCandidates"));
    }

    public void write(FriendlyByteBuf buffer) {
        buffer.writeVarInt(this.visibleCategoriesMask);
        buffer.writeVarInt(this.antiDomainAvailableMask);
        buffer.writeVarInt(this.domainTypeAvailableMask);
        buffer.writeVarInt(this.antiDomainNormalOptionId);
        buffer.writeVarInt(this.antiDomainCrouchOptionId);
        buffer.writeVarInt(this.domainTypeNormalOptionId);
        buffer.writeVarInt(this.domainTypeCrouchOptionId);
        buffer.writeVarInt(this.registrationCandidates.size());
        for (TechniqueSetupRegistrationCandidate candidate : this.registrationCandidates) {
            String displayName = Objects.requireNonNull(candidate.displayName(), "displayName");
            String canonicalName = Objects.requireNonNull(candidate.canonicalName(), "canonicalName");
            buffer.writeUtf(displayName);
            buffer.writeVarInt(candidate.selectTechniqueId());
            buffer.writeUtf(canonicalName);
        }
    }

    public static TechniqueSetupViewState read(FriendlyByteBuf buffer) {
        int visibleCategoriesMask = buffer.readVarInt();
        int antiDomainAvailableMask = buffer.readVarInt();
        int domainTypeAvailableMask = buffer.readVarInt();
        int antiDomainNormalOptionId = buffer.readVarInt();
        int antiDomainCrouchOptionId = buffer.readVarInt();
        int domainTypeNormalOptionId = buffer.readVarInt();
        int domainTypeCrouchOptionId = buffer.readVarInt();
        List<TechniqueSetupRegistrationCandidate> registrationCandidates = readRegistrationCandidates(buffer);
        return new TechniqueSetupViewState(
            visibleCategoriesMask,
            antiDomainAvailableMask,
            domainTypeAvailableMask,
            antiDomainNormalOptionId,
            antiDomainCrouchOptionId,
            domainTypeNormalOptionId,
            domainTypeCrouchOptionId,
            registrationCandidates
        );
    }

    private static List<TechniqueSetupRegistrationCandidate> readRegistrationCandidates(FriendlyByteBuf buffer) {
        int registrationCandidateCount = buffer.readVarInt();
        List<TechniqueSetupRegistrationCandidate> registrationCandidates = new ArrayList<>(registrationCandidateCount);
        for (int i = 0; i < registrationCandidateCount; i++) {
            String displayName = buffer.readUtf();
            int selectTechniqueId = buffer.readVarInt();
            String canonicalName = buffer.readUtf();
            registrationCandidates.add(new TechniqueSetupRegistrationCandidate(displayName, selectTechniqueId, canonicalName));
        }
        return registrationCandidates;
    }

    public boolean isVisible(TechniqueSetupCategory category) {
        return category != null && (this.visibleCategoriesMask & category.mask()) != 0;
    }

    public int visibleCategoryCount() {
        int count = 0;
        for (TechniqueSetupCategory category : TechniqueSetupCategory.values()) {
            if (isVisible(category)) {
                count++;
            }
        }
        return count;
    }

    public TechniqueSetupCategory firstVisibleCategory() {
        for (TechniqueSetupCategory category : TechniqueSetupCategory.values()) {
            if (isVisible(category)) {
                return category;
            }
        }
        return TechniqueSetupCategory.ANTI_DOMAIN;
    }

    public int availableMask(TechniqueSetupCategory category) {
        return category == TechniqueSetupCategory.DOMAIN_TYPE ? this.domainTypeAvailableMask : this.antiDomainAvailableMask;
    }

    public int selectionId(TechniqueSetupCategory category, TechniqueSetupInputSlot slot) {
        if (category == TechniqueSetupCategory.DOMAIN_TYPE) {
            return slot == TechniqueSetupInputSlot.CROUCH ? this.domainTypeCrouchOptionId : this.domainTypeNormalOptionId;
        }
        return slot == TechniqueSetupInputSlot.CROUCH ? this.antiDomainCrouchOptionId : this.antiDomainNormalOptionId;
    }
}
