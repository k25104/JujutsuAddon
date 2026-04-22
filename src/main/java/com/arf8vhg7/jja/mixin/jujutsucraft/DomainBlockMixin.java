package com.arf8vhg7.jja.mixin.jujutsucraft;

import com.arf8vhg7.jja.hook.jujutsucraft.DomainBlockHook;
import net.mcreator.jujutsucraft.block.DomainBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(value = DomainBlock.class, remap = false)
public abstract class DomainBlockMixin {
    @ModifyConstant(method = "m_6807_", constant = @Constant(intValue = 80), remap = false, require = 1)
    private int jja$extendDhruvTrailLifetime(int originalDelay) {
        return DomainBlockHook.resolveScheduledTickDelay(originalDelay);
    }
}
