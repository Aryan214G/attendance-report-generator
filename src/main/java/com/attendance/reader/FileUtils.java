package com.attendance.reader;

import java.io.IOException;
import java.nio.file.*;
import java.util.Scanner;

public class FileUtils {
	Scanner sc = new Scanner(System.in);
	
    public static Path getDataFilePath(String fileName) {
    	 String userHome = System.getProperty("user.home");
         Path reportDir = Paths.get(userHome, "AppData", "Local", "MyAttendanceApp", "Data");
         try {
         	Files.createDirectories(reportDir);
         } catch (IOException e) {
         	System.out.println("Failed to create app data directory.");
             e.printStackTrace();
         }
         return reportDir.resolve(fileName);
    }
    public static Path getReportPathFile(String fileName)
    {
        String userHome = System.getProperty("user.home");
        Path reportDir = Paths.get(userHome, "AppData", "Local", "MyAttendanceApp", "Reports");
        try {
        	Files.createDirectories(reportDir);
        } catch (IOException e) {
        	System.out.println("Failed to create app data directory.");
            e.printStackTrace();
        }
        return reportDir.resolve(fileName);
    }
}
