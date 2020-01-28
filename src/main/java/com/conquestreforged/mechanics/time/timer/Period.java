package com.conquestreforged.mechanics.time.timer;

public enum Period {
    MORNING(23501, 6000),
    AFTERNOON(6001, 13000),
    NIGHT(13001, 23500),
    ;

    private final long from;
    private final long to;

    Period(long from, long to) {
        this.from = from;
        this.to = to;
    }

    public boolean isWithin(long ticks) {
        if (from > to) {
            return ticks >= from;
        } else {
            return ticks >= from && ticks <= to;
        }
    }

    public static Period getPeriod(long ticks) {
        for (Period period : Period.values()) {
            if (period.isWithin(ticks)) {
                return period;
            }
        }
        return Period.MORNING;
    }
}
