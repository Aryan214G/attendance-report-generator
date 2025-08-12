package com.attendance.reader;

import java.io.*;
import java.nio.file.Files;
import java.util.Map;
import java.util.Scanner;

import org.apache.poi.ss.usermodel.Sheet;

import java.nio.file.Path;
//import org.apache.poi.sl.draw.geom.Path;

import java.util.List;

public class CSVStorage {

    public static void saveToCSV(Sheet sheet, Scanner sc) {
    	System.out.print("Enter month and year of the attendance data(e.g., July_2025): ");
        String fileNameSuffix = sc.nextLine().trim().replaceAll("\\s+", "_");
        String fileName = "attendance_" + fileNameSuffix + ".csv";
        Path filePath = FileUtils.getDataFilePath(fileName);
        
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

            System.out.println("Data saved to CSV at: " + filePath);
        } catch (IOException e) {
            System.out.println("Error writing to CSV.");
            e.printStackTrace();
        }
    }
}
