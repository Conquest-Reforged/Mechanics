package com.conquestreforged.mechanics.time.timer;

import net.minecraft.world.IWorld;

public class WorldTimer {

    private static final float MAX = 23999;

    private float rate = 1F;
    private float time = 0F;
    private long lastTimeTicks = 0L;

    public void tick(IWorld world) {
        long ticks = world.getWorldInfo().getDayTime();
        if (ticks != lastTimeTicks) {
            setTime(ticks);
        } else {
            setTime(getTime() + getRate());
        }
        world.getWorldInfo().setDayTime(lastTimeTicks - 1);
    }

    public float getRate() {
        return rate;
    }

    public float getTime() {
        return time;
    }

    public void setRate(float rate) {
        this.rate = rate;
    }

    private void setTime(float time) {
        while (time > MAX) {
            time -= MAX;
        }
        this.time = time;
        this.lastTimeTicks = (long) time;
    }
}
