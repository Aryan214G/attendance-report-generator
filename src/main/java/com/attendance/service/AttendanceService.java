package com.attendance.service;

import com.attendance.reader.AttendanceReader;
import com.attendance.model.EmployeeAttendance;
import com.attendance.model.ReportRow;
import com.attendance.report.ReportGenerator;
import com.attendance.report.ReportExporter;

import java.io.File;
import java.util.List;

public class AttendanceService {

    private List<EmployeeAttendance> attendanceList;
    private List<ReportRow> lastGeneratedReport; // store last report

    private double workingHoursPerDay;
    private int workingDaysInMonth;

    private final ReportGenerator reportGenerator = new ReportGenerator();
    private final ReportExporter reportExporter = new ReportExporter();
    private String rootDirectory;
    // 1. Load Excel
    public void loadExcelFile(String path) {
        AttendanceReader reader = new AttendanceReader();
        attendanceList = reader.readExcel(path);
        System.out.println("Loaded " + attendanceList.size() + " employee records.");
    }

    // 2. Set working hours per day
    public void setWorkingHours(double hours) {
        this.workingHoursPerDay = hours;
    }

    // 3. Set number of working days in month
    public void setWorkingDays(int days) {
        this.workingDaysInMonth = days;
    }

    public List<ReportRow> getLastGeneratedReport() {
        return lastGeneratedReport;
    }

    public void setRootDirectory(String rootDirectory) {
        this.rootDirectory = rootDirectory;
    }

    public String getRootDirectory() {
        return rootDirectory;
    }


    // 4. Generate report
    public List<ReportRow> generateReport() {
        if (attendanceList == null || attendanceList.isEmpty()) {
            System.out.println("No attendance data loaded.");
            return null;
        }
        lastGeneratedReport = reportGenerator.generateReport(attendanceList, workingDaysInMonth, workingHoursPerDay);
        return lastGeneratedReport;
    }

    // 5. Display report
    public void displayReport() {
        if (lastGeneratedReport == null || lastGeneratedReport.isEmpty()) {
            System.out.println("No report generated yet.");
            return;
        }

        System.out.printf("%-20s %-12s %-12s %-15s %-10s %-10s %-10s %-15s%n",
                "Employee Name", "Hours Worked", "Hours Added", "Total Hours Worked",
                "Days Worked", "Working Days", "Overtime", "Single Check-ins");

        for (ReportRow row : lastGeneratedReport) {
            System.out.printf("%-20s %-12.2f %-12.2f %-15.2f %-10d %-10d %-10.2f %-15d%n",
                    row.getEmployeeName(),
                    row.getHoursWorked(),
                    row.getHoursAdded(),
                    row.getTotalHoursWorked(),
                    row.getDaysWorked(),
                    row.getWorkingDaysInMonth(),
                    row.getOvertimeHours(),
                    row.getSingleCheckInDays());
        }
    }

    // 6. Add manual hours
    public void addHours(String employeeName, double extraHours) {
        if (lastGeneratedReport == null) {
            System.out.println("Generate a report first.");
            return;
        }

        for (ReportRow row : lastGeneratedReport) {
            if (row.getEmployeeName().equalsIgnoreCase(employeeName)) {
                double newAdded = row.getHoursAdded() + extraHours;
                row.setHoursAdded(newAdded);
                row.setTotalHoursWorked(row.getHoursWorked() + newAdded);
                System.out.println("Added " + extraHours + " hours to " + employeeName);
                return;
            }
        }
        System.out.println("Employee not found in report.");
    }


    // 7. Save report to CSV
    public void saveReport(int option, String month, int year) {
        if (lastGeneratedReport == null || lastGeneratedReport.isEmpty()) {
            System.out.println("Nothing to save.");
            return;
        }
        try {
            String rootDir = getRootDirectory();
            String csvDir = rootDir + File.separator + "CSVReports";
            String pdfDir = rootDir + File.separator + "PDFReports";
            File csv = new File(csvDir);
            File pdf = new File(pdfDir);
            File dir = (option == 1) ? csv : pdf;
            if (!dir.exists()) {
                dir.mkdirs();
            }

            if(option == 1)
            {
                String fileName = String.format("Attendance_Report_%s-%d.csv", month, year);
                File file = new File(dir, fileName);
                reportExporter.saveAs(option,month, year, lastGeneratedReport, file);
            } else if (option == 2) {
                String fileName = String.format("Attendance_Report_%s-%d.pdf", month, year);
                File file = new File(dir, fileName);
                reportExporter.saveAs(option,month, year, lastGeneratedReport, file);
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public void fileDirectoryHelper() {
        String userHome = System.getProperty("user.home");
        String documentsDir = userHome + File.separator + "Documents";
        String rootDir = documentsDir + File.separator + "AttendanceReports";
        setRootDirectory(rootDir);
    }
}
