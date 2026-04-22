package com.arf8vhg7.jja.feature.player.state;

import net.minecraft.core.Direction;
import net.minecraft.nbt.Tag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

public final class JjaPlayerStateProvider implements ICapabilitySerializable<Tag> {
    private final JjaPlayerState data = new JjaPlayerState();
    private final LazyOptional<JjaPlayerState> instance = LazyOptional.of(() -> this.data);

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
        if (cap == JjaPlayerCapability.CAPABILITY) {
            return this.instance.cast();
        }
        return LazyOptional.empty();
    }

    @Override
    public Tag serializeNBT() {
        return this.data.writeNBT();
    }

    @Override
    public void deserializeNBT(Tag nbt) {
        this.data.readNBT(nbt);
    }
}
