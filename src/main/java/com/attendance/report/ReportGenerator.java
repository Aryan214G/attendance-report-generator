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

    //for breaks during day
    private double calculatePairedHours(List<String> checkIns) {
        double hours = 0;

        for (int i = 0; i + 1 < checkIns.size(); i += 2) {
            LocalTime in = LocalTime.parse(checkIns.get(i));
            LocalTime out = LocalTime.parse(checkIns.get(i + 1));

            if (out.isBefore(in)) {
                // defensive check
                continue;
            }

            hours += Duration.between(in, out).toMinutes() / 60.0;
        }

        return hours;
    }

    public List<ReportRow> generateReport(List<EmployeeAttendance> employees, int workingDaysInMonth, double workingHoursPerDay) {
        List<ReportRow> report = new ArrayList<>();

        for (EmployeeAttendance emp : employees) {
            double totalWorked = 0;
            double daysWorked = 0;
            int singleCheckIns = 0;

            debug("========== " + emp.getEmployeeName() + " ==========");

            Map<Integer, List<String>> dailyCheckIns = emp.getDailyCheckIns();

            for (Map.Entry<Integer, List<String>> entry : dailyCheckIns.entrySet()) {

                int day = entry.getKey();
                List<String> checkIns = entry.getValue();

                debug("\nDay " + day + ": " + checkIns);


                // end of debug block
                if (checkIns.size() == 1)
                {
                    singleCheckIns++;
                    debug("⚠ Single check-in detected!");
                }


                // ========== night shift case ==========
                // Night shifts are continuous by policy (no unpaid breaks)
                // Do NOT use paired logic here

                LocalTime time = LocalTime.parse(checkIns.get(0));
                if(time.equals(LocalTime.parse("00:00")) || time.isAfter(LocalTime.parse("00:00")) && time.isBefore(LocalTime.parse("01:00"))){
                    // Case 1: Only 1 or 2 timestamps => NOT a dual shift
                    if (checkIns.size() < 3) {
                        double hours = Duration.between(
                                LocalTime.parse(checkIns.get(0)),
                                LocalTime.parse(checkIns.get(checkIns.size() - 1))
                        ).toMinutes() / 60.0;

                        debug("⚠ Only one session (no night return). Counting normally: " + hours);
                        totalWorked += hours;
                        continue;
                    }
                    debug("🌙 Night shift detected — entering dual-session handler");

                    int i = 1;
                    while(i < checkIns.size() && !LocalTime.parse(checkIns.get(i)).isAfter(LocalTime.parse("01:00")))
                    {
                        debug("Still before 1 AM: " + checkIns.get(i));
                        i++;
                    }

                    // Safety check — avoid out-of-bounds
                    if (i >= checkIns.size() - 1) {
                        // no proper second shift
                        double hours = Duration.between(
                                LocalTime.parse(checkIns.get(0)),
                                LocalTime.parse(checkIns.get(checkIns.size() - 1))
                        ).toMinutes() / 60.0;

                        debug("⚠ Incomplete night shift pattern. Using full session: " + hours);
                        totalWorked += hours;
                        continue;
                    }

                    //choose the time closest to noon as morning checkout
                    LocalTime morningTime = LocalTime.parse(checkIns.get(i));
                    while(i < checkIns.size() && !morningTime.isAfter(LocalTime.parse("12:00")))
                    {
                        debug("Still before noon: " + checkIns.get(i));
                        i++;
                        if (i < checkIns.size())
                            morningTime = LocalTime.parse(checkIns.get(i));
                    }
                    i--; //step back to last before noon
                    LocalTime morningCheckout = LocalTime.parse(checkIns.get(i));
                    double morningHours = Duration.between(time, morningCheckout).toMinutes() / 60.0;
                    debug("Morning session: " + time + " → " + morningCheckout + " = " + morningHours);

                    // If no night session exists after morning
                    if (i + 1 >= checkIns.size()) {
                        debug("⚠ No night session after morning. Treating as single continuous shift.");
                        totalWorked += morningHours;
                        continue;
                    }

                    // Safe to calculate night session
                    LocalTime nightIn = LocalTime.parse(checkIns.get(i + 1));
                    LocalTime nightOut = LocalTime.parse(checkIns.get(checkIns.size() - 1));
                    double nightHours = Duration.between(nightIn, nightOut).toMinutes() / 60.0;

                    debug("Night session: " + nightIn + " → " + nightOut + " = " + nightHours);
                    totalWorked += morningHours + nightHours;
                    continue;

                }

                // ========== normal case ==========
                double worked = calculatePairedHours(checkIns);
                debug("🕘 Paired sessions total = " + worked);
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
