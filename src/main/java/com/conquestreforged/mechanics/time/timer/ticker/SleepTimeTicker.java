package com.conquestreforged.mechanics.time.timer.ticker;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.IWorld;

public class SleepTimeTicker implements TimeTicker {

    private final float increment;

    public SleepTimeTicker(float increment) {
        this.increment = increment;
    }

    @Override
    public float getIncrement(IWorld world) {
        if (world.getPlayers().isEmpty()) {
            return 0F;
        }

        float sleeping = 0;
        for (PlayerEntity player : world.getPlayers()) {
            if (player.isSleeping()) {
                sleeping++;
            }
        }

        float modifier = sleeping / world.getPlayers().size();

        return 1F + (modifier * increment);
    }
}
