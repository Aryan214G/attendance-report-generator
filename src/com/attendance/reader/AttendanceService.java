package com.attendance.reader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class AttendanceService {
	
	private EmployeeStats stats = new EmployeeStats();
	private double hoursPerDay;
	private double workingDaysInMonth;
	private Sheet sheet;
	public void setHoursPerDay(double hours)
	{
		this.hoursPerDay = hours;
	}
	public void setWorkingDaysInMonth(double days)
	{
		this.workingDaysInMonth = days;
	}
	public void setSheet(Sheet sheet)
	{
		this.sheet = sheet;
	}
	public double getHoursPerDay()
	{
		return hoursPerDay;
	}
	public double getWorkingDaysInMonth()
	{
		return workingDaysInMonth;
	}
	public Sheet getSheet()
	{
		return sheet;
	}


	
	public void loadExcelFile(String filePath) throws IOException
	{
		try(FileInputStream fis = new FileInputStream(new File(filePath));
				Workbook workbook = new XSSFWorkbook(fis)){
			this.sheet = workbook.getSheetAt(0);
		}
	}
	//addHours method
		public void addHours(Sheet sheet,Map<String, List<Double>> hoursWorked, Scanner sc, Map<String, Double> addedHours)
		{
			sc.nextLine();
			System.out.println("Enter password: ");
			 String pass = sc.nextLine();
			if(!PasswordManager.verifyPassword(pass))
			{
				System.out.println("Incorrect password");
				return;
			}
			
			System.out.println("Choose an option:\n1. Add to an Employee. \n2. Add to all.");
			
			int option = sc.nextInt();
			double add;
			switch(option)
			{
				case 1:
					sc.nextLine();
					System.out.println("Enter name of the Employee: ");
					String name = sc.nextLine();
					
					if(!getEmployeesList(this.sheet).contains(name))
					{
						System.out.println("Employee not found.");
						return;
					}

					System.out.println("Enter value of hours to add: ");
					add = sc.nextDouble();
					sc.nextLine();
					allEmployeesData(name, add, 1);
					System.out.println("Successfully added!");
					// update addedHours
					// addedHours.put(name, addedHours.getOrDefault(name, 0.0) + add);
					break;
				case 2:
					System.out.println("Enter value of hours to add: ");
					add = sc.nextDouble();
					sc.nextLine();
					allEmployeesData(null, add, 1);
					System.out.println("Successfully added to all employees!");
					break;
			}
			
			
		}
		
	//getEmployeesList method
		public List<String> getEmployeesList(Sheet sheet)
		{
			List<String> employees = new ArrayList<>();
			for(int i=4; i<= sheet.getLastRowNum();i++)
			{
				Row row = sheet.getRow(i);
				if (row != null) {
				    Cell cell = row.getCell(1);
				    if (cell != null && cell.getCellType() == CellType.STRING) {
				        employees.add(cell.getStringCellValue());
				    }
				}

			}
			return employees;
			
		}
		
	//hoursWorked method
		public Map<String, List<Double>> hoursWorked(Sheet sheet)
		{
			List<String> employees = getEmployeesList(sheet);
			Map<String, List<Double>> hoursWorked = new HashMap<>();
			int id = 0;
			for(int i=4;i<= sheet.getLastRowNum();i++)
			{
				double total=0;
				double incompleteDays = 0;
				Row row = sheet.getRow(i);
				for(int j=2;j<row.getLastCellNum();j++)
				{
					
					Cell cell = row.getCell(j);
					if(cell == null || cell.getCellType() == CellType.BLANK)
					{
						continue;
					}
					String timing = cell.getStringCellValue();
					String[] times = timing.split("\n");
					
					if(times.length >= 2)
					{
						double hrs = hoursWorkedHelper(times);
						total += hrs;
					}
					else if(times.length == 1)
					{
						incompleteDays++;
					}
					else
					{
						total += 0;
					}
					
				}
				// hoursWorked.put(employees.get(id), new ArrayList<>());
				// hoursWorked.get(employees.get(id)).add(total);
				// hoursWorked.get(employees.get(id)).add(incompleteDays);
				stats.setOriginalHours(total);
				stats.setSinglePunchCount(incompleteDays);
				allEmployeesData(employees.get(id), total, 0);
				allEmployeesData(employees.get(id), incompleteDays, 6);

				id++;
			}
			return hoursWorked;
			
		}
		
		public double hoursWorkedHelper(String[] times)
		{
				String start = times[0].trim();
				String end = "";
				if(times.length == 2)
				{
					end = times[1].trim();
				}
				else if(times.length >= 3)
				{
					end = times[times.length-1].trim();
				}
				else {
					return 0.0;
				}
				LocalTime s = LocalTime.parse(start);
				LocalTime e = LocalTime.parse(end);
				if(s.isBefore(e))
				{
					//dayshift
					Duration d = Duration.between(s, e);
					long mins = d.toMinutes();
					double hrs = mins/60.0; 
					return hrs;
				}
				else
				{
					//nightshift
					double minutesBeforeMidNight = Duration.between(s, LocalTime.MIDNIGHT).toMinutes();
					double minutesAfterMidNight = Duration.between(LocalTime.MIDNIGHT, e).toMinutes();
					double hrs = (minutesBeforeMidNight + minutesAfterMidNight) / 60.0;
					return hrs;
				}
				
		}
		
	//displayEmployeesList method
		public void displayEmployeesList(Sheet sheet)
		{
			List<String> employeesList = new ArrayList<>();
			employeesList = getEmployeesList(sheet);
			for(int i=0;i<employeesList.size();i++)
			{
				System.out.println(employeesList.get(i));
			}
		}
		
	//displayWorkHours method
		public void displayWorkHours(Sheet sheet, Map<String, List<Double>> hoursWorked)
		{	
			for (Map.Entry<String,List<Double>> i : hoursWorked.entrySet())
			{
				List<Double> info = i.getValue();
				System.out.println(i.getKey() + " : " + "\nHours worked: "+info.get(0)+"\nTotal number of single entries: "+info.get(1)+" days.\n");
			}
		}
		
	//daysWorked method
		public void daysWorked(Sheet sheet)
		{
			Map<String, List<Double>> hwMap = hoursWorked(sheet);
			double dayHours = this.getHoursPerDay();
			if(dayHours == 0)
			{
				System.out.println("Please set hours per day first");
				return;
			}
			
			for (Map.Entry<String,List<Double>> i : hwMap.entrySet())
			{
				List<Double> info = i.getValue();
				double days = (info.get(0)/dayHours);
				allEmployeesData(i.getKey(), days, 3);
			}
		}
		
	//displayEmployeeSpecificData method
		// public void displayEmployeeSpecificData(Sheet sheet, Map<String, List<Double>> hoursWorked, Scanner sc)
		// {
		// 	sc.nextLine();
		// 	System.out.println("Enter name: ");
		// 	String name = sc.nextLine();
		// 	if(!hoursWorked.containsKey(name))
		// 	{
		// 		System.out.println("Employee not found.");
		// 		return;
		// 	}
		// 	double HW = hoursWorked.get(name).get(0);
		// 	Map<String, Double> overtime = overtimeCalculator(sheet, hoursWorked);
		// 	System.out.println("Hours worked: " + HW);
		// 	System.out.println("Days worked: " + daysWorked);
		// 	System.out.println("Total number of single entries: "+hoursWorked.get(name).get(1)+" days.");
		// 	System.out.println("Overtime: "+overtime1+" hours.");
		// }
		public Map<String, Double> overtimeCalculator(Sheet sheet)
		{
			Map<String, Double> overtimeData = new HashMap<>();
			Map<String, List<Double>> hoursWorked = hoursWorked(sheet);
			
			// Validate configuration first
			if(this.getHoursPerDay() <= 0 || this.getWorkingDaysInMonth() <= 0)
			{
				System.out.println("Invalid configuration: hours per day or working days not set");
				return overtimeData; // Return empty map instead of null
			}
			
			for (Map.Entry<String,List<Double>> i : hoursWorked.entrySet())
			{
				String name = i.getKey();
				double dayHours = this.getHoursPerDay();
				double workingDays = this.getWorkingDaysInMonth();
				
				double totalHoursWorked = hoursWorked.get(name).get(0);
				double expectedHours = workingDays * dayHours;
				double overtime1 = 0;
				
				if(totalHoursWorked > expectedHours)
				{
					overtime1 = totalHoursWorked - expectedHours;
				}
				
				overtimeData.put(name, overtime1);
				allEmployeesData(name, overtime1, 5);
			}
			return overtimeData;
		}
		
		
				// Shared dataset storage
		private final Map<String, List<Double>> data = new HashMap<>();

		/**
		 * Builds and returns the full dataset for all employees.
		 * Initializes data and runs calculations.
		 */
		public Map<String, List<Double>> allEmployeesData() {
			// Ensure employee names are added
			List<String> names = getEmployeesList(getSheet());
			for (String empName : names) {
				data.putIfAbsent(empName, new ArrayList<>(Collections.nCopies(7, 0.0)));
			}

			// Run calculations
			hoursWorked(sheet);
			daysWorked(sheet);
			overtimeCalculator(sheet);

			// Set working days in the month for everyone
			for (Map.Entry<String, List<Double>> entry : data.entrySet()) {
				data.get(entry.getKey()).set(4, getWorkingDaysInMonth());
			}

			return data;
		}

		/**
		 * Updates specific employee data based on index and value.
		 */
		public Map<String, List<Double>> allEmployeesData(String name, double value, int index) {
			// Ensure initialization first
			allEmployeesData();

			switch (index) {
				case 0: // hours worked
					data.get(name).set(0, value);
					break;
				case 1: // hours added
					if (name == null) {
						for (String empName : data.keySet()) {
							double hoursAdded = data.get(empName).get(2) + value;
							data.get(empName).set(2, hoursAdded);
						}
					} else {
						double hoursAdded = data.get(name).get(2) + value;
						data.get(name).set(2, hoursAdded);
					}
					break;
				case 3: // days worked
					data.get(name).set(3, value);
					break;
				case 5: // overtime
					data.get(name).set(5, value);
					break;
				case 6: // single punches
					data.get(name).set(6, value);
					break;
			}

			// Recompute total hours (col 2 = worked + added)
			if (name != null && data.containsKey(name)) {
				double totalHoursWorked = data.get(name).get(0);
				double addedHours = data.get(name).get(2);
				double total = totalHoursWorked + addedHours;
				data.get(name).set(2, total);
			}

			return data;
		}

}
