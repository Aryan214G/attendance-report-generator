package com.attendance.reader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class MainApp {
	private static Scanner sc = new Scanner(System.in);
	private static AttendanceService service = new AttendanceService();

	public static void main(String[] args) {
		// System.out.print("Enter the Excel file path: ");
		// String filePath = sc.nextLine();
		// String filePath = "E:\\projects\\attendance project files\\DEC.xlsx";
		// try (FileInputStream fis = new FileInputStream(new File(filePath));
		// Workbook workbook = new XSSFWorkbook(fis)){
		// Sheet sheet = workbook.getSheetAt(0);
		// MainApp MainApp = new MainApp();
		Map<String, List<Double>> hoursWorked = service.hoursWorked(sheet);

		boolean running = true;
		while (running == true) {
			// System.out.println("Choose an option:\n 1. Employees list\n 2. Total work hours\n 3. Days worked\n 4. Employee specific\n 5. Add hours\n 6. Update password.\n 7. Exit");

			System.out.println("\n--- Attendance System Menu ---");
			System.out.println("1. Load attendance Excel file");
			System.out.println("2. Enter working hours per day");
			System.out.println("3. Enter working days in a month");
			System.out.println("4. Generate report");
			System.out.println("5. View report summary");
			System.out.println("6. Save report to CSV");
			System.out.println("7. Exit");

			int option = sc.nextInt();
			sc.nextLine();
			switch (option) {

				case 1:
					System.out.print("Enter Excel file path: ");
					String filePath = sc.nextLine();
					service.loadExcelFile(filePath);
					break;
				case 2:
					System.out.print("Enter working hours per day: ");
					double hours = sc.nextDouble();
					sc.nextLine();
					service.setHoursPerDay(hours);
					break;
				case 3:
					System.out.print("Enter total working days in a month: ");
					double days = sc.nextDouble();
					service.setWorkingDaysInMonth(days);
				case 4:
				ReportGenerator.generateReport(service.getSheet(), service);
				case 5:
					if (reportData != null) {
						reportData.forEach((name, stats) -> {
							System.out.println(name + ": " + stats.toString());
						});
					} else {
						System.out.println("No report generated yet.");
					}
					break;
				case 6:
					if (reportData == null) {
						System.out.println("Generate the report first (option 3).");
					} else {
						System.out.print("Enter path to save CSV file: ");
						String savePath = scanner.nextLine();
						CSVStorage.save(reportData, savePath);
						System.out.println("Report saved to " + savePath);
					}
					break;
				case 7:
					running = false;
					break;
				default:
					System.out.println("Invalid choice.");
			}
		}
		try {
		} catch (FileNotFoundException e) {
			System.out.println("Excel file not found at: " + filePath);
		} catch (NullPointerException e) {
			System.out.println("Sheet data is missing or malformed.");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
