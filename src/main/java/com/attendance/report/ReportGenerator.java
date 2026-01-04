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

            debug("Session: " + in + " → " + out + " = " + (Duration.between(in, out).toMinutes() / 60.0));
            hours += Duration.between(in, out).toMinutes() / 60.0;
        }

        return hours;
    }

    public List<ReportRow> generateReport(List<EmployeeAttendance> employees, int workingDaysInMonth, double workingHoursPerDay) {
        List<ReportRow> report = new ArrayList<>();

        for (EmployeeAttendance emp : employees) {
            double totalWorked = 0;
//            double daysWorked = 0;
            int singleCheckIns = 0;
            Map<Integer, List<String>> dailyCheckIns = emp.getDailyCheckIns();

            debug("========== " + emp.getEmployeeName() + " ==========");

            for (Map.Entry<Integer, List<String>> entry : dailyCheckIns.entrySet()) {
                int day = entry.getKey();
                List<String> checkIns = entry.getValue();

                debug("\nDay " + day + ": " + checkIns);

                // end of debug block
                //single checkins
                if (checkIns.size() == 1) {
                    singleCheckIns++;
                    debug("⚠ Single check-in detected!");
                }

                // ========== night shift case ==========
                // Night shifts are continuous by policy (no unpaid breaks)
                // Do NOT use paired logic here
                Double nightWorked = calculateNightShiftHours(checkIns, day, dailyCheckIns);
                if (nightWorked != null) {
                    totalWorked += nightWorked;
                    continue;
                } //nigh shift handled, skip to next day

                // ========== normal case ==========
                double worked = calculatePairedHours(checkIns);
                debug("🕘 Paired sessions total = " + worked);
                totalWorked += worked;
            }

            debug("\nTOTAL WORKED (raw): " + totalWorked);
            debug("============================================\n");

            reportHelper(workingDaysInMonth, workingHoursPerDay, totalWorked, emp, singleCheckIns, report);
        }
        return report;
    }

    private Double calculateNightShiftHours(List<String> checkIns, int day, Map<Integer, List<String>> dailyCheckIns) {
        int prevDay = day - 1;
        // Check if previous day has check-ins
        if (!dailyCheckIns.containsKey(prevDay)) {
            return null;
        }

        // Get last check-in of previous day
        List<String> prevDayCheckIns = dailyCheckIns.get(prevDay);
        LocalTime lastPrevDayTime = LocalTime.parse(prevDayCheckIns.get(prevDayCheckIns.size() - 1));

        // Check if current day's first check-in is between 00:00 and 01:00 or previous day's last check-in after 23:30
        LocalTime time = LocalTime.parse(checkIns.get(0));
        if (time.equals(LocalTime.parse("00:00"))
                || time.isAfter(LocalTime.parse("00:00")) && time.isBefore(LocalTime.parse("01:00"))
                || lastPrevDayTime.isAfter(LocalTime.parse("23:30")))
        {

            //Stepping back to find night shift start
            LocalTime nightStart = findNightShiftStart(checkIns, prevDayCheckIns);
            debug("🌙 Night shift start detected at: " + nightStart);

            // Case 1: Only 1 or 2 timestamps => NOT a dual shift
            if (checkIns.size() < 3) {
                double hours = nonDualShift(nightStart, checkIns);
                return hours;
            }

            debug("🌙 Night shift detected — entering dual-session handler");

            // Find the first time AFTER 1 AM
            int i = findFirstAfterOneAM(checkIns, nightStart);

            // Safety check — avoid out-of-bounds
            if (i >= checkIns.size() - 1) {
                // no proper second shift
                double hours = outOfBoundsHandler(nightStart, checkIns);
                return hours;
            }

            //choose the time closest to noon as morning checkout
            MorningCheckoutResult result = findMorningCheckout(i, checkIns);
            i = result.getIndex();
            LocalTime morningCheckout = result.getTime();

            // Calculate night session hours
            double nightHours = calculateHoursTillMidnight(nightStart, checkIns);

                // Calculate morning session hours
                double morningHours = 0;
                morningHours = Duration.between(
                        LocalTime.MIDNIGHT,
                        morningCheckout
                ).toMinutes() / 60.0;

                debug("Morning session: 00:00 → " + morningCheckout + " = " + morningHours);

                // If no night session exists after morning
                if (i + 1 >= checkIns.size()) {
                    debug("⚠ No night session after morning. Treating as single continuous shift.");
                    return morningHours + nightHours;
                }

                // Safe to calculate night session
                LocalTime nightIn = LocalTime.parse(checkIns.get(i + 1));
                LocalTime nightOut = LocalTime.parse(checkIns.get(checkIns.size() - 1));
                if (nightOut.isBefore(LocalTime.parse("23:30"))) {
                    debug("⚠ Night checkout before 23:30 — invalid night shift pattern. Treating as single continuous shift.");
                    return morningHours;
                }
                nightHours = Duration.between(nightIn, nightOut).toMinutes() / 60.0;
                debug("Night session: " + nightIn + " → " + nightOut + " = " + nightHours);
//            totalWorked += morningHours + nightHours;
                return morningHours + nightHours;
            }
            return null;
        }
    private double nonDualShift (LocalTime nightStart, List<String> checkIns) {
        double hours = calculateHoursTillMidnight(nightStart, checkIns);
        hours += Duration.between(
                LocalTime.MIDNIGHT,
                LocalTime.parse(checkIns.get(checkIns.size() - 1))
        ).toMinutes() / 60.0;

        debug("⚠ Only one session (no night return). Counting normally: \n" +
                "Morning session: " + nightStart + " → " + checkIns.get(checkIns.size() - 1) + " = " + hours);
        return hours;
    }

    private int findFirstAfterOneAM(List<String> checkIns, LocalTime nightStart) {
        int i = 1;
        while (i < checkIns.size()
                && !LocalTime.parse(checkIns.get(i)).isAfter(LocalTime.parse("01:00"))) {
            debug("Still before 1 AM: " + checkIns.get(i));
            i++;
        }
        return i;
    }

    private double calculateHoursTillMidnight(LocalTime nightStart, List<String> checkIns) {
        double hours = 0;
        if (nightStart.isBefore(LocalTime.MIDNIGHT)) {
            hours = Duration.between(
                    nightStart,
                    LocalTime.MAX.plusNanos(1) // to include midnight as 24:00 and not 00:00
            ).toMinutes() / 60.0;
        }
        return hours;
    }

    private LocalTime findNightShiftStart(List<String> checkIns, List<String> prevDayCheckIns) {
        LocalTime nightStart;
        int j = prevDayCheckIns.size() - 1;
        while (j > 0 && LocalTime.parse(prevDayCheckIns.get(j)).isAfter(LocalTime.parse("23:30"))) {
            debug("Stepping back to find night shift start: " + prevDayCheckIns.get(j));
            j--;
        }
        nightStart = LocalTime.parse(prevDayCheckIns.get(j));
        return nightStart;
    }

    private double outOfBoundsHandler(LocalTime nightStart, List<String> checkIns) {
        double hours = calculateHoursTillMidnight(nightStart, checkIns);

        // Add morning session
        hours += Duration.between(
                LocalTime.MIDNIGHT,
                LocalTime.parse(checkIns.get(checkIns.size() - 1))
        ).toMinutes() / 60.0;

        debug("⚠ Incomplete night shift pattern. Using full session: " + nightStart + " -> "
                + checkIns.get(checkIns.size() - 1) + " = "
                + hours);
        return hours;
    }

    private MorningCheckoutResult findMorningCheckout(int i, List<String> checkIns) {
        LocalTime morningCheckout = LocalTime.parse(checkIns.get(i));

        while (i < checkIns.size() && !morningCheckout.isAfter(LocalTime.parse("09:00"))) {
            debug("Still before noon: " + checkIns.get(i));
            i++;
            if (i < checkIns.size())
                morningCheckout = LocalTime.parse(checkIns.get(i));
        }
        i--; //step back to last before noon
        morningCheckout = LocalTime.parse(checkIns.get(i));

        return new MorningCheckoutResult(i, morningCheckout);
    }

    private void reportHelper(int workingDaysInMonth, double workingHoursPerDay, double totalWorked, EmployeeAttendance emp, int singleCheckIns, List<ReportRow> report) {
        double expectedHours = workingDaysInMonth * workingHoursPerDay;
        double hoursAdded = 0;
        double totalHoursWorked = totalWorked + hoursAdded;
        double daysWorked = totalHoursWorked / workingHoursPerDay;
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

}

