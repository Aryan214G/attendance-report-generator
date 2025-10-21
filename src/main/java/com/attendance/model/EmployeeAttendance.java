package com.attendance.model;

import java.util.List;
import java.util.Map;

public class EmployeeAttendance {

    private String employeeName;

    // Map of day -> list of check-ins
    // Key: day number (1-31)
    // Value: List of check-in/check-out times as strings (or LocalTime)
    private Map<Integer, List<String>> dailyCheckIns;

    public EmployeeAttendance(String employeeName, Map<Integer, List<String>> dailyCheckIns) {
        this.employeeName = employeeName;
        this.dailyCheckIns = dailyCheckIns;
    }

    // Getters and setters
    public String getEmployeeName() { return employeeName; }
    public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }

    public Map<Integer, List<String>> getDailyCheckIns() { return dailyCheckIns; }
    public void setDailyCheckIns(Map<Integer, List<String>> dailyCheckIns) { this.dailyCheckIns = dailyCheckIns; }

}
