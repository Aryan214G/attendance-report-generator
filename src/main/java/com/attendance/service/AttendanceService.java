package com.attendance.service;

import com.attendance.reader.AttendanceReader;
import com.attendance.model.EmployeeAttendance;
import com.attendance.model.ReportRow;
import com.attendance.report.ReportGenerator;
import com.attendance.report.ReportExporter;

import java.util.List;

public class AttendanceService {

    private List<EmployeeAttendance> attendanceList; // Loaded data
    private double workingHoursPerDay;
    private int workingDaysInMonth;

    private ReportGenerator reportGenerator = new ReportGenerator();
    private ReportExporter reportExporter = new ReportExporter();

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

    // 4. Generate report
    public List<ReportRow> generateReport() {
        if (attendanceList == null || attendanceList.isEmpty()) {
            System.out.println("No attendance data loaded.");
            return null;
        }
        return reportGenerator.generateReport(attendanceList, workingDaysInMonth, workingHoursPerDay);
    }

    // 5. Display report
    public void displayReport(List<ReportRow> report) {
        if (report == null || report.isEmpty()) {
            System.out.println("Report is empty.");
            return;
        }
        System.out.printf("%-20s %-12s %-12s %-15s %-10s %-10s %-10s %-15s%n",
                "Employee Name", "Hours Worked", "Hours Added", "Total Hours Worked",
                "Days Worked", "Working Days", "Overtime", "Single Check-ins");
        for (ReportRow row : report) {
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

    // 6. Save report to CSV
    public void saveReport(List<ReportRow> report, String filePath) {
        if (report == null || report.isEmpty()) {
            System.out.println("Nothing to save.");
            return;
        }
        reportExporter.saveReportAsCSV(report, filePath);
    }
}
