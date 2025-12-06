package com.attendance.ui;

import com.attendance.model.ReportRow;
import com.attendance.service.AttendanceService;

import java.io.File;
import java.util.List;

public class AppContext {
    private static final AttendanceService attendanceService = new AttendanceService();
    private static List<File> selectedExcelFile;
    private static String month;
    private static int year;
    private static int workingDays;
    private static double workingHoursPerDay;
    private static List<ReportRow> reportData;

    // === Getters and Setters ===
    public static AttendanceService getAttendanceService() {
        return attendanceService;
    }

    public static List<File> getSelectedExcelFiles() {
        return selectedExcelFile;
    }

    public static void setSelectedExcelFiles(List<File> files) {
        selectedExcelFile = files;
    }

    public static String getMonth() {
        return month;
    }

    public static void setMonth(String m) {
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
        month = "";
        year = 0;
        workingDays = 0;
        workingHoursPerDay = 0;
        reportData = null;
    }

}


