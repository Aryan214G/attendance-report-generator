package com.attendance.reader;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import org.apache.poi.ss.usermodel.Sheet;


//import org.apache.poi.sl.draw.geom.Path;

public class ReportGenerator {

    public static void generateReport(Sheet sheet, Scanner sc) {
        System.out.print("Enter month and year of the attendance data(e.g., July_2025): ");
        String fileNameSuffix = sc.nextLine().trim().replaceAll("\\s+", "_");
        String fileName = "attendance_report_" + fileNameSuffix + ".csv";
        Path filePath = FileUtils.getReportPathFile(fileName);
        
        AttendanceService attendanceService = new AttendanceService();
        Map<String, List<Double>> hoursWorked = attendanceService.hoursWorked(sheet);
        
            try (BufferedWriter writer = Files.newBufferedWriter(filePath)) {
                writer.write("Employee Name,Hours Worked,Hours Added,Total Hours Worked,Days Worked,Number of Working Days In The Month,Over Time (hours),Number of Single Punches\n");
                for (Map.Entry<String, List<Double>> entry : hoursWorked.entrySet()) {
                    String name = entry.getKey();
                    List<Double> data = entry.getValue();
                    //  data: [workedHours, incompleteDays]
                    
                    
                    
                    writer.write(String.format("%s,%.2f,%.2f,%.2f,%.0f\n",
                        name, data.get(0), data.get(1), data.get(2), data.get(3)));
                }
                System.out.println("Report saved at: " + filePath.toString());
        } catch (IOException e) {
            System.out.println("Error saving report: " + e.getMessage());
        }
    }
}
