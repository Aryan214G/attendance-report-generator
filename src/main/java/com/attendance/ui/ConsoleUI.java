package com.attendance.ui;

import com.attendance.service.AttendanceService;

import java.util.Scanner;

public class ConsoleUI {

    private final AttendanceService attendanceService = new AttendanceService();
    private final Scanner sc = new Scanner(System.in);

    public void start() {
        int choice;
        do {
            System.out.println(System.getProperty("java.version"));
            System.out.println("\n=== Attendance Report Generator ===");
            System.out.println("1. Load attendance Excel file");
            System.out.println("2. Enter working hours per day");
            System.out.println("3. Enter working days in a month");
            System.out.println("4. Generate report");
            System.out.println("5. View report");
            System.out.println("6. Add hours");
            System.out.println("7. Save report");
            System.out.println("8. Exit");
            System.out.print("Enter choice: ");
            choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1 -> {
                    System.out.print("Enter Excel file path: ");
                    String path = sc.nextLine();
                    attendanceService.loadExcelFile(path);
                }
                case 2 -> {
                    System.out.print("Enter working hours per day: ");
                    double hours = sc.nextDouble();
                    attendanceService.setWorkingHours(hours);
                }
                case 3 -> {
                    System.out.print("Enter number of working days in month: ");
                    int days = sc.nextInt();
                    attendanceService.setWorkingDays(days);
                }
                case 4 -> {
                    attendanceService.generateReport();
                    System.out.println("Report generated successfully.");
                }
                case 5 -> attendanceService.displayReport();
                case 6 -> {
                    System.out.print("Enter employee name to add hours: ");
                    String name = sc.nextLine();
                    System.out.print("Enter number of hours to add: ");
                    double extra = sc.nextDouble();
                    attendanceService.addHours(name, extra);
                }
                case 7 -> {
                    System.out.print("Enter month (e.g., January): ");
                    String month = sc.nextLine();
                    System.out.print("Enter year (e.g., 2025): ");
                    int year = sc.nextInt();
                    System.out.println("1. Export as CSV\n2. Export as PDF");
                    int option = sc.nextInt();
                    sc.nextLine();
                    attendanceService.saveReport(option, month, year);
                }
                case 8 -> System.out.println("Exiting...");
                default -> System.out.println("Invalid choice!");
            }
        } while (choice != 8);
    }

    public static void main(String[] args) {
        new ConsoleUI().start();
    }
}
