package com.attendance.reader;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.attendance.model.EmployeeAttendance;

public class AttendanceReader {

    public List<EmployeeAttendance> readExcel(String filePath) {
        List<EmployeeAttendance> attendanceList = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {
                if (row.getRowNum() <= 3) continue; // skip header

                String name = row.getCell(1).getStringCellValue(); // 2nd column is name

                Map<Integer, List<String>> dailyCheckIns = new TreeMap<>();

                for (int col = 2; col < row.getLastCellNum(); col++) { // columns 3+ are days
                    Cell cell = row.getCell(col);
                    if (cell == null || cell.getCellType() == CellType.BLANK) continue;

                    String cellValue = cell.getStringCellValue();

                    // Split multiple check-ins by line breaks if multiple entries exist
                    List<String> checkIns = Arrays.asList(cellValue.split("\\n"));

                    dailyCheckIns.put(col - 1, checkIns); // day number = column index - 1
                }

                attendanceList.add(new EmployeeAttendance(name, dailyCheckIns));
            }

        } catch (IOException e) {
            System.out.println("Error reading Excel file: " + e.getMessage());
        }

        return attendanceList;
    }
}
