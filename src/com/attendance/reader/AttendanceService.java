package com.attendance.reader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
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
			sheet = workbook.getSheetAt(0);
			setSheet(sheet);
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
			Double add;
			switch(option)
			{
				case 1:
					sc.nextLine();
					System.out.println("Enter name of the Employee: ");
					String name = sc.nextLine();
					if(!hoursWorked.containsKey(name))
					{
						System.out.println("Employee information not available.");
						return;
					}
					Double hours = hoursWorked.get(name).get(0);
					System.out.println("Enter value of hours to add: ");
					add = sc.nextDouble();
					sc.nextLine();
					hours += add;
					hoursWorked.get(name).set(0, hours);
					System.out.println("Successfully added!");
					// update addedHours
					addedHours.put(name, addedHours.getOrDefault(name, 0.0) + add);
					break;
				case 2:
					for (Map.Entry<String,List<Double>> i : hoursWorked.entrySet())
					{
						System.out.println("Enter value of hours to add: ");
						add = sc.nextDouble();
						List<Double> info = i.getValue();
						hours = info.get(0);
						hours += add;
						hoursWorked.get(i.getKey()).set(0, hours);
						System.out.println("Successfully added!");
						
						//track added hours
						String empName = i.getKey();
						addedHours.put(empName, addedHours.getOrDefault(empName, 0.0) + add);
					}
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
				hoursWorked.put(employees.get(id), new ArrayList<>());
				hoursWorked.get(employees.get(id)).add(total);
				hoursWorked.get(employees.get(id)).add(incompleteDays);
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
		
	//displayDaysWorked method
		public void displayDaysWorked(Sheet sheet, Map<String, List<Double>> hoursWorked, Scanner sc, Map<String, Double> daysMap)
		{
			double dayHours = this.getHoursPerDay();
			if(dayHours == 0)
			{
				System.out.println("Please set hours per day first");
				return;
			}
			
			for (Map.Entry<String,List<Double>> i : hoursWorked.entrySet())
			{
				List<Double> info = i.getValue();
				Double days = (info.get(0)/dayHours);
				System.out.println(i.getKey() + " : " + days + "\nTotal number of single entries: "+info.get(1)+" days.");
				daysMap.put(i.getKey(), days);
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
		public Map<String, Double> overtimeCalculator(Sheet sheet, Map<String, List<Double>> hoursWorked)
		{
			Map<String, Double> overtimeData = new HashMap<>();
			
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
			}
			return overtimeData;
		}
		
		//returns complete list of employees data
		public Map<String, EmployeeStats> allEmployeesData(Sheet sheet, Scanner sc, double hoursPerDay, double workingDays)
		{
			Map<String, EmployeeStats> data = new HashMap<>();
			
			AttendanceService service = new AttendanceService();
			service.setHoursPerDay(hoursPerDay);
			service.setWorkingDaysInMonth(workingDays);
			List<String> names = service.getEmployeesList(sheet);
			Map<String, List<Double>> hoursWorked = service.hoursWorked(sheet);
			Map<String, Double> addedHours = new HashMap<>();
			Map<String, Double> daysMap = new HashMap<>();
			Map<String, Double> overtime = service.overtimeCalculator(sheet, hoursWorked);
			
			// Call addHours with addedHours map
			service.addHours(sheet, hoursWorked, sc, addedHours);
			//Call displayDaysWorked with days map
			service.displayDaysWorked(sheet, hoursWorked, sc, daysMap);
			
			for(String name : names)
			{
			    double hours = hoursWorked.get(name).get(0);
			    double added = addedHours.getOrDefault(name, 0.0);
			    double total = hours;
			    double daysWorked = daysMap.get(name);
			    double ot = overtime.get(name);
			    double workingDays = service.getWorkingDaysInMonth();
			    
			    EmployeeStats stats = new EmployeeStats(hours - added, added, total, daysWorked, workingDays, ot, hoursWorked.get(name).get(1));
			    data.put(name, stats);
			}
			
			return data;
		}
}
