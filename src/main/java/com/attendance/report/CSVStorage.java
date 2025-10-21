//package com.attendance.report;
//
//import java.io.*;
//import java.nio.file.Files;
//import java.util.Map;
//import java.util.Scanner;
//
//import com.attendance.model.EmployeeStats;
//import com.attendance.service.AttendanceService;
//import com.attendance.util.FileUtils;
//import org.apache.poi.ss.usermodel.Sheet;
//
//import java.nio.file.Path;
////import org.apache.poi.sl.draw.geom.Path;
//
//import java.util.List;
//
//public class CSVStorage {
//    public static void save(Map<String, EmployeeStats> data, String filePath) throws IOException {
//        try (FileWriter writer = new FileWriter(filePath)) {
//            // Write header
//            writer.write("Name,Original Hours,Added Hours,Final Hours,Days Worked,Working Days,Overtime,Single Punch\n");
//
//            // Write data
//            for (Map.Entry<String, EmployeeStats> entry : data.entrySet()) {
//                String name = entry.getKey();
//                EmployeeStats stats = entry.getValue();
//                writer.write(String.format("%s,%.2f,%.2f,%.2f,%.2f,%.2f,%.2f,%.2f\n",
//                    name,
//                    stats.getOriginalHours(),
//                    stats.getAddedHours(),
//                    stats.getFinalHours(),
//                    stats.getDaysWorked(),
//                    stats.getWorkingDays(),
//                    stats.getOvertimeHours(),
//                    stats.getSinglePunchCount()
//                ));
//            }
//        }
//    }
//
//    public static void saveToCSV(Sheet sheet, Scanner sc) {
//    	System.out.print("Enter month and year of the attendance data(e.g., July_2025): ");
//        String fileNameSuffix = sc.nextLine().trim().replaceAll("\\s+", "_");
//        String fileName = "attendance_" + fileNameSuffix + ".csv";
//        Path filePath = FileUtils.getDataFilePath(fileName);
//
//        AttendanceService attendanceService = new AttendanceService();
//        Map<String, List<Double>> hoursWorked = attendanceService.hoursWorked(sheet);
//
//            try (BufferedWriter writer = Files.newBufferedWriter(filePath)) {
//                writer.write("Employee Name,Hours Worked,Hours Added,Total Hours Worked,Days Worked,Number of Working Days In The Month,Over Time (hours),Number of Single Punches\n");
//                for (Map.Entry<String, List<Double>> entry : hoursWorked.entrySet()) {
//                    String name = entry.getKey();
//                    List<Double> data = entry.getValue();
//                    //  data: [workedHours, incompleteDays]
//
//
//
//                    writer.write(String.format("%s,%.2f,%.2f,%.2f,%.0f\n",
//                        name, data.get(0), data.get(1), data.get(2), data.get(3)));
//                }
//
//            System.out.println("Data saved to CSV at: " + filePath);
//        } catch (IOException e) {
//            System.out.println("Error writing to CSV.");
//            e.printStackTrace();
//        }
//    }
//}
