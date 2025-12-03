package com.attendance.report;

import com.attendance.model.EmployeeAttendance;
import com.attendance.model.ReportRow;
import java.time.LocalTime;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ReportGenerator {
    private static final boolean DEBUG = true; // turn off later if needed
    private void debug(String msg) {
        if (DEBUG) System.out.println(msg);
    }

    public List<ReportRow> generateReport(List<EmployeeAttendance> employees, int workingDaysInMonth, double workingHoursPerDay) {
        List<ReportRow> report = new ArrayList<>();

        for (EmployeeAttendance emp : employees) {
            double totalWorked = 0;
            double daysWorked = 0;
            int singleCheckIns = 0;

            debug("========== " + emp.getEmployeeName() + " ==========");

            Map<Integer, List<String>> dailyCheckIns = emp.getDailyCheckIns();

            for (List<String> checkIns : dailyCheckIns.values()) {

                // Start of debug block
                int day = dailyCheckIns.entrySet().stream()
                        .filter(entry -> entry.getValue().equals(checkIns))
                        .map(Map.Entry::getKey)
                        .findFirst()
                        .orElse(-1);
                debug("\nDay " + day + ": " + checkIns);
                // end of debug block
                if (checkIns.size() == 1)
                {
                    singleCheckIns++;
                    debug("⚠ Single check-in detected!");
                }

                //TODO: fix the bug where after day 5 its directly jumping to day 21
                //night shift case
                LocalTime time = LocalTime.parse(checkIns.get(0));
                if(time.equals(LocalTime.parse("00:00")) || time.isAfter(LocalTime.parse("00:00")) && time.isBefore(LocalTime.parse("01:00"))){
                    debug("🌙 Night shift detected — entering dual-session handler");
                    int i = 1;
                    while(!LocalTime.parse(checkIns.get(i)).isAfter(LocalTime.parse("01:00")))
                    {
                        debug("Still before 1 AM: " + checkIns.get(i));
                        i++;
                    }
                    LocalTime morningCheckout = LocalTime.parse(checkIns.get(i));
                    double morningHours = Duration.between(time, morningCheckout).toMinutes() / 60.0;
                    debug("Morning session: " + time + " → " + morningCheckout + " = " + morningHours);

                    LocalTime nightIn = LocalTime.parse(checkIns.get(i+1));
                    LocalTime nightOut = LocalTime.parse(checkIns.get(checkIns.size()-1));
                    double nightHours = Duration.between(nightIn, nightOut).toMinutes() / 60.0;
                    debug("Night session: " + nightIn + " → " + nightOut + " = " + nightHours);

                    totalWorked += morningHours + nightHours;
                    continue;
                }

                //normal case
                LocalTime in = LocalTime.parse(checkIns.get(0));
                LocalTime out = LocalTime.parse(checkIns.get(checkIns.size()-1));
                double worked = Duration.between(in, out).toMinutes() / 60.0;

                debug("🕘 Normal session: " + in + " → " + out + " = " + worked);

                totalWorked += worked;
            }
            debug("\nTOTAL WORKED (raw): " + totalWorked);
            debug("============================================\n");

            double expectedHours = workingDaysInMonth * workingHoursPerDay;
            double hoursAdded = 0;
            double totalHoursWorked = totalWorked + hoursAdded;
            daysWorked = totalHoursWorked/workingHoursPerDay;
            double overtime = Math.max(0, totalHoursWorked - expectedHours);

            double totalWorkedRounded = Math.round(totalWorked * 10.0) / 10.0;
            double hoursAddedRounded = Math.round(hoursAdded * 10.0) / 10.0;
            double totalHoursWorkedRounded = Math.round(totalHoursWorked * 10.0) / 10.0;
            double overtimeRounded = Math.round(overtime * 10.0) / 10.0;
            double daysWorkedRounded = Math.round(daysWorked * 10.0) / 10.0;

            ReportRow row = new ReportRow(
                    emp.getEmployeeName(),
                    totalWorkedRounded,
                    hoursAddedRounded,
                    totalHoursWorkedRounded,
                    daysWorkedRounded,
                    workingDaysInMonth,
                    overtimeRounded,
                    singleCheckIns
            );

            report.add(row);
        }

        return report;
    }
}
