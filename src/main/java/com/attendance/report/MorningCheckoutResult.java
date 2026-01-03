package com.attendance.report;

import java.time.LocalTime;

public class MorningCheckoutResult {
    private final int index;
    private final LocalTime time;

    public MorningCheckoutResult(int index, LocalTime time) {
        this.index = index;
        this.time = time;
    }

    public int getIndex() { return index; }
    public LocalTime getTime() { return time; }
}
s
