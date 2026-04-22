package com.arf8vhg7.jja.mixin.jujutsucraft;

import com.arf8vhg7.jja.hook.jujutsucraft.NueEntityHook;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.mcreator.jujutsucraft.entity.NueEntity;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = NueEntity.class, remap = false)
public abstract class NueEntityMixin {
    @WrapOperation(
        method = "m_6075_",
        at = @At(
            value = "INVOKE",
            target = "Lnet/mcreator/jujutsucraft/procedures/AINueProcedure;execute(Lnet/minecraft/world/level/LevelAccessor;DDDLnet/minecraft/world/entity/Entity;)V"
        ),
        remap = false,
        require = 1
    )
    private void jja$replaceAiWithRideControl(
        LevelAccessor world,
        double x,
        double y,
        double z,
        Entity entity,
        Operation<Void> original
    ) {
        NueEntityHook.executeAiOrRideControl(world, x, y, z, entity, original);
    }

    @ModifyExpressionValue(
        method = "m_7023_",
        at = @At(value = "INVOKE", target = "Lnet/mcreator/jujutsucraft/entity/NueEntity;m_20160_()Z"),
        remap = false,
        require = 1
    )
    private boolean jja$useDefaultTravelWhileMounted(boolean original) {
        return NueEntityHook.shouldUseDefaultTravel((Entity) (Object) this, original);
    }
}
