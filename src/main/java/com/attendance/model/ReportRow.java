package com.attendance.model;

public class ReportRow {

    private String employeeName;
    private double hoursWorked;
    private double hoursAdded;
    private double totalHoursWorked;
    private int daysWorked;
    private int workingDaysInMonth;
    private double overtimeHours;
    private int singleCheckInDays;

    // Constructor
    public ReportRow(String employeeName, double hoursWorked, double hoursAdded, double totalHoursWorked,
                     int daysWorked, int workingDaysInMonth, double overtimeHours, int singleCheckInDays) {
        this.employeeName = employeeName;
        this.hoursWorked = hoursWorked;
        this.hoursAdded = hoursAdded;
        this.totalHoursWorked = totalHoursWorked;
        this.daysWorked = daysWorked;
        this.workingDaysInMonth = workingDaysInMonth;
        this.overtimeHours = overtimeHours;
        this.singleCheckInDays = singleCheckInDays;
    }

    // Getters and setters
    public String getEmployeeName() { return employeeName; }
    public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }

    public double getHoursWorked() { return hoursWorked; }
    public void setHoursWorked(double hoursWorked) { this.hoursWorked = hoursWorked; }

    public double getHoursAdded() { return hoursAdded; }
    public void setHoursAdded(double hoursAdded) { this.hoursAdded = hoursAdded; }

    public double getTotalHoursWorked() { return totalHoursWorked; }
    public void setTotalHoursWorked(double totalHoursWorked) { this.totalHoursWorked = totalHoursWorked; }

    public int getDaysWorked() { return daysWorked; }
    public void setDaysWorked(int daysWorked) { this.daysWorked = daysWorked; }

    public int getWorkingDaysInMonth() { return workingDaysInMonth; }
    public void setWorkingDaysInMonth(int workingDaysInMonth) { this.workingDaysInMonth = workingDaysInMonth; }

    public double getOvertimeHours() { return overtimeHours; }
    public void setOvertimeHours(double overtimeHours) { this.overtimeHours = overtimeHours; }

    public int getSingleCheckInDays() { return singleCheckInDays; }
    public void setSingleCheckInDays(int singleCheckInDays) { this.singleCheckInDays = singleCheckInDays; }
}
