package com.attendance.reader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalTime;
import java.util.*;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.attendance.model.EmployeeAttendance;

public class AttendanceReader {

    public Map<String, EmployeeAttendance> readExcel(List<File> files) {
        Map<String, EmployeeAttendance> attendanceMap = new HashMap<>();

        for(File filePath : files)
        {
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
                        List<String> checkIns = new ArrayList<>(Arrays.asList(cellValue.split("\\n")));

                        dailyCheckIns.put(col - 1, checkIns); // day number = column index - 1
                    }
                    if (!attendanceMap.containsKey(name)) {
                        // First time seeing this employee
                        attendanceMap.put(name, new EmployeeAttendance(name, dailyCheckIns));
                    }
                    else {
                        // Employee already exists → merge
                        EmployeeAttendance existing = attendanceMap.get(name);
                        Map<Integer, List<String>> existingDays = existing.getDailyCheckIns();

                        for (Map.Entry<Integer, List<String>> entry : dailyCheckIns.entrySet()) {
                            int day = entry.getKey();
                            List<String> newCheckIns = entry.getValue();

                            // If day already exists, append check-ins
                            existingDays.merge(
                                    day,
                                    new ArrayList<>(newCheckIns),
                                    (oldList, incomingList) -> {
                                        oldList.addAll(incomingList);

                                        // sort after merge
                                        oldList.sort(Comparator.comparing(LocalTime::parse));

                                        return oldList;
                                    }
                            );
                        }
                    }
                }

            } catch (IOException e) {
                System.out.println("Error reading Excel file: " + e.getMessage());
            }
        }
        return attendanceMap;
    }
}
