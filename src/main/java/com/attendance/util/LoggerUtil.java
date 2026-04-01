package com.attendance.util;

public class LoggerUtil {
    private static StringBuilder attendanceLog = new StringBuilder();

    public static void log(String text) {
        System.out.println(text); // still show in console
        attendanceLog.append(text).append("\n");
    }

    public static String getLog() {
        return attendanceLog.toString();
    }
}
