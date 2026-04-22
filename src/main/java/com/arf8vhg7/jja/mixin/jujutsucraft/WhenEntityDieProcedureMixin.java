package com.arf8vhg7.jja.mixin.jujutsucraft;

import com.arf8vhg7.jja.hook.jujutsucraft.WhenEntityDieProcedureHook;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalDoubleRef;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import java.util.List;
import java.util.function.Predicate;
import net.mcreator.jujutsucraft.procedures.WhenEntityDieProcedure;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = WhenEntityDieProcedure.class, remap = false)
public abstract class WhenEntityDieProcedureMixin {
    @WrapOperation(
        method = "execute",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/Mob;m_5448_()Lnet/minecraft/world/entity/LivingEntity;",
            ordinal = 0
        ),
        remap = false
    ,
        require = 1
    )
    private static LivingEntity jja$resolveFameTarget(Mob mob, Operation<LivingEntity> original) {
        return WhenEntityDieProcedureHook.resolveFameTarget(mob);
    }

    @ModifyExpressionValue(
        method = "execute",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/LivingEntity;m_21023_(Lnet/minecraft/world/effect/MobEffect;)Z",
            ordinal = 1
        ),
        remap = false,
        require = 1
    )
    private static boolean jja$blockSukunaFameWhenDisabled(boolean original) {
        return WhenEntityDieProcedureHook.shouldBlockSukunaFame(original);
    }

    @ModifyConstant(method = "execute", constant = @Constant(doubleValue = 40.0), remap = false, require = 1)
    private static double jja$expandPNameSearchRadius(double original) {
        return 1024.0;
    }

    @Definition(id = "getPersistentData", method = "Lnet/minecraft/world/entity/Entity;getPersistentData()Lnet/minecraft/nbt/CompoundTag;")
    @Definition(id = "getDouble", method = "Lnet/minecraft/nbt/CompoundTag;m_128459_(Ljava/lang/String;)D")
    @Expression("?.getPersistentData().getDouble('cnt_target') > 3.0")
    @ModifyExpressionValue(method = "execute", at = @At("MIXINEXTRAS:EXPRESSION"), require = 1)
    private static boolean jja$allowFameWhenUntargeted(boolean original) {
        return WhenEntityDieProcedureHook.allowFameWhenUntargeted();
    }

    @WrapOperation(
        method = "execute",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/LevelAccessor;m_6443_(Ljava/lang/Class;Lnet/minecraft/world/phys/AABB;Ljava/util/function/Predicate;)Ljava/util/List;",
            ordinal = 1
        ),
        remap = false,
        require = 1
    )
    private static List<Entity> jja$appendResolvedMvpRecipient(
        LevelAccessor world,
        Class<Entity> entityClass,
        AABB bounds,
        Predicate<? super Entity> predicate,
        Operation<List<Entity>> original,
        @Local(argsOnly = true) Entity entity
    ) {
        return WhenEntityDieProcedureHook.appendResolvedFameTargetRecipient(entity, original.call(world, entityClass, bounds, predicate));
    }

    @WrapOperation(
        method = "execute",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/player/Player;m_5661_(Lnet/minecraft/network/chat/Component;Z)V"
        ),
        remap = false,
        require = 1
    )
    private static void jja$sendRawFameKeyToClient(
        Player player,
        Component component,
        boolean actionBar,
        Operation<Void> original,
        @Local(argsOnly = true) Entity entity,
        @Local(index = 8) LocalDoubleRef fame
    ) {
        if (WhenEntityDieProcedureHook.sendFameGainChat(player, entity, fame.get())) {
            return;
        }
        original.call(player, component, actionBar);
    }

    @Inject(method = "execute", at = @At("TAIL"), remap = false, require = 1)
    private static void jja$syncSpecialGrades(LevelAccessor world, double x, double y, double z, Entity entity, CallbackInfo ci) {
        WhenEntityDieProcedureHook.syncSpecialGrades(world);
    }
}
