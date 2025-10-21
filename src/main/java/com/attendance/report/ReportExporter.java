package com.attendance.report;

import com.attendance.model.ReportRow;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;


public class ReportExporter {

    public void saveAs(int option, int month, int year, List<ReportRow> lastGeneratedReport, File file) throws IOException {
        switch (option) {
            case 1:
                System.out.println("Save as CSV selected.");
                saveReportAsCsv(lastGeneratedReport, file);
                break;
            case 2:
                System.out.println("Save as PDF selected.");
                saveReportAsPDF(lastGeneratedReport,file ,month, year);
                break;
            default:
                System.out.println("Invalid option.");
        }
    }
    public void saveReportAsCsv(List<ReportRow> lastGeneratedReport, File file) throws FileNotFoundException {

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
        }

    public void saveReportAsPDF(List<ReportRow> lastGeneratedReport, File file, int month, int year) throws IOException {
        if (lastGeneratedReport == null || lastGeneratedReport.isEmpty()) {
            System.out.println("Nothing to save.");
            return;
        }

            PDDocument document = new PDDocument();
            PDPage page = new PDPage();
            document.addPage(page);

            PDPageContentStream content = new PDPageContentStream(document, page);

            // Title
            content.beginText();
            content.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 14);
            content.setLeading(20f);
            content.newLineAtOffset(50, 750);
            content.showText("Attendance Report - " + month + "/" + year);
            content.newLine();
            content.newLine();

            // Table header
            content.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 12);
            String header = String.format("%-20s %-12s %-12s %-15s %-10s %-10s %-10s %-15s",
                    "Employee Name", "Hours Worked", "Hours Added", "Total Hours",
                    "Days Worked", "Working Days", "Overtime", "Single Check-ins");
            content.showText(header);
            content.newLine();

            // Table rows
            content.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 12);
            for (ReportRow row : lastGeneratedReport) {
                String line = String.format("%-20s %-12.2f %-12.2f %-15.2f %-10d %-10d %-10.2f %-15d",
                        row.getEmployeeName(),
                        row.getHoursWorked(),
                        row.getHoursAdded(),
                        row.getTotalHoursWorked(),
                        row.getDaysWorked(),
                        row.getWorkingDaysInMonth(),
                        row.getOvertimeHours(),
                        row.getSingleCheckInDays());
                content.showText(line);
                content.newLine();
            }

            content.endText();
            content.close();

            document.save(file);
            document.close();

            System.out.println("Report saved successfully as PDF at: " + file.getAbsolutePath());
    }

}
