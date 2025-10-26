package com.attendance.report;

import com.attendance.model.EmployeeAttendance;
import com.attendance.model.ReportRow;
import java.time.LocalTime;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ReportGenerator {

    public List<ReportRow> generateReport(List<EmployeeAttendance> employees, int workingDaysInMonth, double workingHoursPerDay) {
        List<ReportRow> report = new ArrayList<>();

        for (EmployeeAttendance emp : employees) {
            double totalWorked = 0;
            int daysWorked = 0;
            int singleCheckIns = 0;

            Map<Integer, List<String>> dailyCheckIns = emp.getDailyCheckIns();

            for (List<String> checkIns : dailyCheckIns.values()) {
                if (checkIns.isEmpty()) continue;
                daysWorked++;

                if (checkIns.size() == 1) singleCheckIns++;

                // Pair in/out and sum durations
//                for (int i = 0; i < checkIns.size() - 1; i += 2) {
                    LocalTime in = LocalTime.parse(checkIns.get(0));
                    LocalTime out = LocalTime.parse(checkIns.get(checkIns.size()-1));
                    totalWorked += Duration.between(in, out).toMinutes() / 60.0;
//                }
            }

            double expectedHours = workingDaysInMonth * workingHoursPerDay;
            double hoursAdded = 0;
            double totalHoursWorked = totalWorked + hoursAdded;
            double overtime = Math.max(0, totalHoursWorked - expectedHours);

            double totalWorkedRounded = Math.round(totalWorked * 10.0) / 10.0;
            double hoursAddedRounded = Math.round(hoursAdded * 10.0) / 10.0;
            double totalHoursWorkedRounded = Math.round(totalHoursWorked * 10.0) / 10.0;
            double overtimeRounded = Math.round(overtime * 10.0) / 10.0;

            ReportRow row = new ReportRow(
                    emp.getEmployeeName(),
                    totalWorkedRounded,
                    hoursAddedRounded,
                    totalHoursWorkedRounded,
                    daysWorked,
                    workingDaysInMonth,
                    overtimeRounded,
                    singleCheckIns
            );

            report.add(row);
        }

        return report;
    }
}
