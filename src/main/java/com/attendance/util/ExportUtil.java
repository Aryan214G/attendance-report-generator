package com.attendance.util;

import java.io.FileWriter;
import java.io.IOException;

public class ExportUtil {

    public static void exportAttendance(String data) {
        try (FileWriter writer = new FileWriter("attendance_report.txt")) {
            writer.write(data);
            System.out.println("Export successful!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
