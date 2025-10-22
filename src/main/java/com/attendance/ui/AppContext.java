package com.attendance.ui;

import com.attendance.model.ReportRow;
import com.attendance.service.AttendanceService;

import java.io.File;
import java.util.List;

public class AppContext {
    private static final AttendanceService attendanceService = new AttendanceService();
    private static File selectedExcelFile;
    private static int month;
    private static int year;
    private static int workingDays;
    private static double workingHoursPerDay;
    private static List<ReportRow> reportData;

    // === Getters and Setters ===
    public static AttendanceService getAttendanceService() {
        return attendanceService;
    }

    public static File getSelectedExcelFile() {
        return selectedExcelFile;
    }

    public static void setSelectedExcelFile(File file) {
        selectedExcelFile = file;
    }

    public static int getMonth() {
        return month;
    }

    public static void setMonth(int m) {
        month = m;
    }

    public static int getYear() {
        return year;
    }

    public static void setYear(int y) {
        year = y;
    }

    public static int getWorkingDays() {
        return workingDays;
    }

    public static void setWorkingDays(int d) {
        workingDays = d;
    }

    public static double getWorkingHoursPerDay() {
        return workingHoursPerDay;
    }

    public static void setWorkingHoursPerDay(double h) {
        workingHoursPerDay = h;
    }

    public static List<ReportRow> getReportData() {
        return reportData;
    }

    public static void setReportData(List<ReportRow> report) {
        reportData = report;
    }

    public static void reset() {
        selectedExcelFile = null;
        month = 0;
        year = 0;
        workingDays = 0;
        workingHoursPerDay = 0;
        reportData = null;
    }

}


