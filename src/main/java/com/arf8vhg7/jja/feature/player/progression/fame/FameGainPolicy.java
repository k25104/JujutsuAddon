package com.arf8vhg7.jja.feature.player.progression.fame;

import com.arf8vhg7.jja.compat.jujutsucraft.JjaJujutsucraftDataAccess;
import java.util.function.Function;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;

public final class FameGainPolicy {
    private static final int MAX_OWNER_DEPTH = 5;

    private FameGainPolicy() {
    }

    public static LivingEntity resolveFameTarget(Mob mob) {
        if (mob == null) {
            return null;
        }
        Entity resolvedTarget = resolvePreferredFameTarget(
            mob.getKillCredit(),
            resolveDamageSourceTarget(mob),
            mob.getTarget(),
            JjaJujutsucraftDataAccess::jjaGetOwnerUuid,
            ownerUuid -> JjaJujutsucraftDataAccess.jjaResolveOwnerByUuid(mob.level(), ownerUuid),
            LivingEntity.class::isInstance
        );
        return resolvedTarget instanceof LivingEntity livingEntity ? livingEntity : null;
    }

    public static boolean allowFameWhenUntargeted() {
        return true;
    }

    @Nullable
    static <T> T resolvePreferredFameTarget(
        @Nullable T killCredit,
        @Nullable T damageSourceTarget,
        @Nullable T mobTarget,
        Function<T, String> ownerUuidGetter,
        Function<String, T> ownerResolver,
        Predicate<T> livingPredicate
    ) {
        T resolvedKillCredit = resolveRootLivingOwnerOrSelf(killCredit, ownerUuidGetter, ownerResolver, livingPredicate);
        if (resolvedKillCredit != null) {
            return resolvedKillCredit;
        }

        T resolvedDamageSourceTarget = resolveRootLivingOwnerOrSelf(damageSourceTarget, ownerUuidGetter, ownerResolver, livingPredicate);
        if (resolvedDamageSourceTarget != null) {
            return resolvedDamageSourceTarget;
        }

        return resolveRootLivingOwnerOrSelf(mobTarget, ownerUuidGetter, ownerResolver, livingPredicate);
    }

    @Nullable
    static <T> T resolveRootLivingOwnerOrSelf(
        @Nullable T candidate,
        Function<T, String> ownerUuidGetter,
        Function<String, T> ownerResolver,
        Predicate<T> livingPredicate
    ) {
        if (candidate == null) {
            return null;
        }

        T resolved = candidate;
        String ownerUuid = normalizeOwnerUuid(ownerUuidGetter.apply(candidate));
        int safeCount = 0;
        while (!ownerUuid.isEmpty() && safeCount < MAX_OWNER_DEPTH) {
            T owner = ownerResolver.apply(ownerUuid);
            if (owner == null || !livingPredicate.test(owner)) {
                break;
            }
            resolved = owner;
            ownerUuid = normalizeOwnerUuid(ownerUuidGetter.apply(owner));
            safeCount++;
        }

        return livingPredicate.test(resolved) ? resolved : null;
    }

    @Nullable
    private static Entity resolveDamageSourceTarget(Mob mob) {
        DamageSource lastDamageSource = mob.getLastDamageSource();
        if (lastDamageSource == null) {
            return null;
        }

        return resolvePreferredFameTarget(
            lastDamageSource.getEntity(),
            lastDamageSource.getDirectEntity(),
            null,
            JjaJujutsucraftDataAccess::jjaGetOwnerUuid,
            ownerUuid -> JjaJujutsucraftDataAccess.jjaResolveOwnerByUuid(mob.level(), ownerUuid),
            LivingEntity.class::isInstance
        );
    }

    private static String normalizeOwnerUuid(@Nullable String ownerUuid) {
        return ownerUuid == null ? "" : ownerUuid;
    }
}
