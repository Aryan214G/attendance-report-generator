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
import org.apache.pdfbox.pdmodel.common.PDRectangle;
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

        // Create landscape A4 page
        PDPage page = new PDPage(PDRectangle.A4);
        document.addPage(page);

        PDPageContentStream content = new PDPageContentStream(document, page);

        // Title
        content.beginText();
        content.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 14); // kept as requested
        content.setLeading(20f);
        content.newLineAtOffset(50, page.getMediaBox().getHeight() - 50);
        content.showText("Attendance Report - " + month + "/" + year);
        content.endText();

        // Table settings
        float margin = 50;
        float pageWidth = page.getMediaBox().getHeight(); // landscape width
        float pageHeight = page.getMediaBox().getWidth(); // landscape height
        float yStart = pageHeight - 100; // start below title
        float rowHeight = 20;
        float tableBottomY = 50;

        // Columns
        float[] colWidths = {150, 90, 90, 100, 80, 90, 90, 110};
        String[] headers = {"Employee Name", "Hours Worked", "Hours Added", "Total Hours",
                "Days Worked", "Working Days", "Overtime", "Single Check-ins"};

        int rowsPerPage = (int)((yStart - tableBottomY) / rowHeight);
        int rowCount = 0;
        float nextY = yStart;

        content.setLineWidth(1f);

        for (int i = 0; i <= lastGeneratedReport.size(); i++) {
            // Page break
            if (rowCount == rowsPerPage || i == 0) {
                if (i != 0) {
                    content.close();
                    page = new PDPage(PDRectangle.A4);
                    document.addPage(page);
                    content = new PDPageContentStream(document, page);
                    nextY = yStart;
                    rowCount = 0;
                }

                // Draw header row
                drawRow(content, margin, nextY, colWidths, rowHeight, headers, true);
                nextY -= rowHeight;
            }

            if (i == lastGeneratedReport.size()) break;

            ReportRow row = lastGeneratedReport.get(i);

            // Prepare row data with truncation
            String[] rowData = {
                    truncate(row.getEmployeeName(), 20),
                    String.format("%.2f", row.getHoursWorked()),
                    String.format("%.2f", row.getHoursAdded()),
                    String.format("%.2f", row.getTotalHoursWorked()),
                    String.valueOf(row.getDaysWorked()),
                    String.valueOf(row.getWorkingDaysInMonth()),
                    String.format("%.2f", row.getOvertimeHours()),
                    String.valueOf(row.getSingleCheckInDays())
            };

            drawRow(content, margin, nextY, colWidths, rowHeight, rowData, false);
            nextY -= rowHeight;
            rowCount++;
        }

        content.close();
        document.save(file);
        document.close();

        System.out.println("Report saved successfully as PDF at: " + file.getAbsolutePath());
    }

    // Draws a single row (header or data)
    private void drawRow(PDPageContentStream content, float margin, float y, float[] colWidths, float rowHeight, String[] data, boolean isHeader) throws IOException {
        float tableWidth = 0;
        for (float w : colWidths) tableWidth += w;

        // Draw row rectangle
        content.addRect(margin, y - rowHeight, tableWidth, rowHeight);
        content.stroke();

        // Draw vertical lines
        float nextX = margin;
        for (float w : colWidths) {
            content.moveTo(nextX, y);
            content.lineTo(nextX, y - rowHeight);
            content.stroke();
            nextX += w;
        }
        content.moveTo(nextX, y);
        content.lineTo(nextX, y - rowHeight);
        content.stroke();

        // Write text
        content.beginText();
        content.setFont(isHeader ? new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD)
                : new PDType1Font(Standard14Fonts.FontName.HELVETICA), 12);
        content.newLineAtOffset(margin + 2, y - 15);

        for (int i = 0; i < data.length; i++) {
            content.showText(data[i]);
            content.newLineAtOffset(colWidths[i], 0);
        }

        content.endText();
    }

    // Simple truncation for long text
    private String truncate(String text, int maxLength) {
        if (text.length() <= maxLength) return text;
        return text.substring(0, maxLength - 3) + "...";
    }
}

