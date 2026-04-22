package com.arf8vhg7.jja.mixin.minecraft;

import com.arf8vhg7.jja.hook.minecraft.FoodDataHook;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(FoodData.class)
public abstract class FoodDataMixin {
    @WrapOperation(
        method = "tick(Lnet/minecraft/world/entity/player/Player;)V",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;heal(F)V")
    ,
        require = 1
    )
    private void jja$allowNaturalHeal(Player player, float amount, Operation<Void> original) {
        if (FoodDataHook.shouldAllowNaturalHeal(player)) {
            original.call(player, amount);
        }
    }
}
