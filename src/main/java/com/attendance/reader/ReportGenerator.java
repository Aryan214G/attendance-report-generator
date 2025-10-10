package com.attendance.reader;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import org.apache.poi.ss.usermodel.Sheet;


//import org.apache.poi.sl.draw.geom.Path;

public class ReportGenerator {
    
    public static Path getReportPathFile() {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter file name for the report:");
        String fileNameSuffix = sc.nextLine().trim(); 
        if (fileNameSuffix.isEmpty())
        {
            fileNameSuffix = "default";
        }   

        String fileName = "attendance_report_" + fileNameSuffix + "_" 
                   + ".csv";

        Path filePath = FileUtils.getDataFilePath(fileName);
        return filePath;
        
    }
    public static void generateReport(Sheet sheet, AttendanceService service) {
        
        Path filePath = getReportPathFile();
        Map<String, List<Double>> dataMap = service.allEmployeesData();
        
            try (BufferedWriter writer = Files.newBufferedWriter(filePath)) {
                writer.write("Employee Name,Hours Worked,Hours Added,Total Hours Worked,Days Worked,Number of Working Days In The Month,Over Time (hours),Number of Single Punches\n");
                for (Map.Entry<String, List<Double>> entry : dataMap.entrySet()) {
                    String name = entry.getKey();
                    List<Double> data = entry.getValue();
                    
                    
                    
                    writer.write(String.format("%s,%.2f,%.2f,%.2f,%.2f,%.2f,%.2f,%.2f\n",
                        name, data.get(0), data.get(1), data.get(2), data.get(3), data.get(4), data.get(5), data.get(6)));
                }
                System.out.println("Report saved at: " + filePath.toString());
        } catch (IOException e) {
            System.out.println("Error saving report: " + e.getMessage());
        }
    }
}
