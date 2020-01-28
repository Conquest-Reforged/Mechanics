package com.conquestreforged.mechanics.time.ticker;

import net.minecraft.world.IWorld;

public interface TimeTicker {

    TimeTicker NONE = w -> 1F;

    float getIncrement(IWorld world);
}
