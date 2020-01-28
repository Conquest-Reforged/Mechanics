package com.conquestreforged.mechanics.time.timer;

import net.minecraft.world.IWorld;
import net.minecraft.world.dimension.DimensionType;

public class WorldTimer {

    private static final float MAX = 23999;

    private float time = -1F;
    private float rate = 1F;
    private final DimensionType type;

    public WorldTimer(DimensionType type) {
        this.type = type;
    }

    public DimensionType getDimension() {
        return type;
    }

    public void tick(IWorld world) {
        updatePartialTime(world.getWorldInfo().getDayTime());

        setTime(time + getIncrement(world));

        world.getWorldInfo().setDayTime(getTimeTicks() - 1);
    }

    public void setRate(float rate) {
        this.rate = rate;
    }

    protected float getIncrement(IWorld world) {
        return rate;
    }

    protected float getTime() {
        return time;
    }

    protected long getTimeTicks() {
        return (long) getTime();
    }

    private void updatePartialTime(long ticks) {
        // if the delta is greater than one then someone else has changed the time
        if (Math.abs(ticks - getTimeTicks()) > 1) {
            setTime(ticks);
        }
    }

    private void setTime(float time) {
        // wrap the time if it overflows into the next day
        while (time > MAX) {
            time -= MAX;
        }
        this.time = time;
    }
}
