package com.attendance.reader;

public class EmployeeStats {
    private double originalHours;
    private double addedHours;
    private double finalHours;
    private double daysWorked;
    private double workingDays;
    private double overtimeHours;
    private double singlePunchCount;

    public EmployeeStats(double originalHours, double addedHours, double finalHours, double daysWorked, double workingDays, double overtimeHours, double singlePunchCount) {
        this.originalHours = originalHours;
        this.addedHours = addedHours;
        this.finalHours = finalHours;
        this.daysWorked = daysWorked;
        this.workingDays = workingDays;
        this.overtimeHours = overtimeHours;
        this.singlePunchCount = singlePunchCount;
    }

    public double getOriginalHours() {
        return originalHours;
    }

    public double getAddedHours() {
        return addedHours;
    }

    public double getFinalHours() {
        return finalHours;
    }

    public double getDaysWorked() {
        return daysWorked;
    }

    public double getWorkingDays() {
		return workingDays;
	}

	public double getOvertimeHours() {
		return overtimeHours;
	}

	public double getSinglePunchCount() {
		return singlePunchCount;
	}
	public void setOriginalHours(double originalHours) {
	    this.originalHours = originalHours;
	}

	public void setAddedHours(double addedHours) {
	    this.addedHours = addedHours;
	}

	public void setFinalHours(double finalHours) {
	    this.finalHours = finalHours;
	}

	public void setDaysWorked(double daysWorked) {
	    this.daysWorked = daysWorked;
	}

	public void setWorkingDays(double workingDays) {
	    this.workingDays = workingDays;
	}

	public void setOvertimeHours(double overtimeHours) {
	    this.overtimeHours = overtimeHours;
	}

	public void setSinglePunchCount(double singlePunchCount) {
	    this.singlePunchCount = singlePunchCount;
	}


	@Override
	public String toString() {
	    return "Original: " + originalHours +
	           ", Added: " + addedHours +
	           ", Final: " + finalHours +
	           ", Days Worked: " + daysWorked +
	           ", Working Days: " + workingDays +
	           ", Overtime: " + overtimeHours +
	           ", Single Punches: " + singlePunchCount;
	}

}

