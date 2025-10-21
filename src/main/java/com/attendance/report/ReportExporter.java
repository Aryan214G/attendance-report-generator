package com.attendance.report;

import com.attendance.model.ReportRow;

import java.io.File;
import java.io.PrintWriter;
import java.util.List;

public class ReportExporter {

    public void saveReportAsCsv(List<ReportRow> lastGeneratedReport, String filePath) {
        try {
            File file = new File(filePath);
            if (file.isDirectory()) {
                file = new File(file, "Attendance_Report.csv");
            }

            try (PrintWriter writer = new PrintWriter(file)) {
                writer.println("Employee Name,Hours Worked,Hours Added,Total Hours Worked,Days Worked,Working Days,O.T. (hours),Single Check-ins");
                for (ReportRow row : lastGeneratedReport) {
                    writer.printf("%s,%.2f,%.2f,%.2f,%d,%d,%.2f,%d%n",
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

            System.out.println("Report saved successfully at: " + file.getAbsolutePath());
        } catch (Exception e) {
            System.err.println("Error saving report: " + e.getMessage());
        }
    }

}
