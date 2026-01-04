package com.attendance.report;

import java.time.LocalTime;

public class IndexTimePair {
    private final int index;
    private final LocalTime time;

    public IndexTimePair(int index, LocalTime time) {
        this.index = index;
        this.time = time;
    }

    public int getIndex() { return index; }
    public LocalTime getTime() { return time; }
}

