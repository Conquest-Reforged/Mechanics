package com.conquestreforged.mechanics.time.timer;

import net.minecraft.world.GameRules;
import net.minecraft.world.IWorld;

public class WorldTimer {

    private static final float MAX = 23999;

    private float rate = 1F;
    private float time = 0F;
    private long lastTimeTicks = 0L;

    public void tick(IWorld world) {
        long ticks = world.getWorldInfo().getDayTime();
        if (Math.abs(ticks - lastTimeTicks) > 1L) {
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

    public boolean canTick(IWorld world) {
        return world.getWorldInfo().getGameRulesInstance().get(GameRules.DO_DAYLIGHT_CYCLE).get();
    }

    private void setTime(float time) {
        while (time > MAX) {
            time -= MAX;
        }
        this.time = time;
        this.lastTimeTicks = (long) time;
    }
}
