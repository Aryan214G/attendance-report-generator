package com.attendance.util;

import com.attendance.service.AttendanceService;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;



public class LogExportUtil {

    public static void exportAttendanceLog(String data) {
        AttendanceService service = new AttendanceService();
        service.fileDirectoryHelper();

        String rootDirectory = service.getRootDirectory();
        String logFolder = rootDirectory + File.separator + "logs";
        String filePath = logFolder + File.separator +  "attendance_log.txt";

        try {
            // create folder if it doesn't exist
            File folder = new File(logFolder);
            if (!folder.exists()) {
                folder.mkdirs();
            }

            FileWriter writer = new FileWriter(filePath);
            writer.write(data);
            writer.close();

            System.out.println("Export successful! File saved at: " + filePath);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}