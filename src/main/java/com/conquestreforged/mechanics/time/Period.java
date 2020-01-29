package com.conquestreforged.mechanics.time;

public enum Period {
    MORNING,
    AFTERNOON,
    NIGHT,
    ;

    private static final long SUNRISE = 23500;
    private static final long MIDDAY = 6000;
    private static final long SUNSET = 13000;

    public static Period getPeriod(long ticks) {
        if (ticks < Period.MIDDAY) {
            return Period.MORNING;
        }
        if (ticks < Period.SUNSET) {
            return Period.AFTERNOON;
        }
        if (ticks < Period.SUNRISE) {
            return Period.NIGHT;
        }
        return Period.MORNING;
    }
}
