package com.grepp.spring.app.model.schedule.service;

import com.grepp.spring.app.model.schedule.entity.Line;
import com.grepp.spring.app.model.schedule.entity.Metro;
import com.grepp.spring.app.model.schedule.repository.MetroQueryRepository;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ExcelUploadService {

    private final MetroQueryRepository metroQueryRepository;

    @Transactional
    public void importFromExcel(MultipartFile file) throws IOException {

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);

            for (int i = 1; i <= sheet.getLastRowNum(); i++) { // header skip
                Row row = sheet.getRow(i);
                if (row == null) continue;

                String stationName = getStringCell(row, 0);
                String lineName = getStringCell(row, 1);
                String color = getStringCell(row, 2);
                String latStr = getStringCell(row, 3);
                String lonStr = getStringCell(row, 4);
                String address = getStringCell(row, 5);

                if (stationName == null || lineName == null || color == null) continue;

                Double lat = latStr == null || latStr.isEmpty() ? null : Double.parseDouble(latStr);
                Double lon = lonStr == null || lonStr.isEmpty() ? null : Double.parseDouble(lonStr);

                Metro metro = metroQueryRepository.findByName(stationName).orElseGet(() -> {
                    Metro s = new Metro();
                    s.setName(stationName);
                    s.setAddress(address);
                    s.setLatitude(lat);
                    s.setLongitude(lon);
                    return s;
                });

                Line line = new Line();
                line.setLineName(lineName);
                line.setColor(color);

                metro.addLine(line);
                metroQueryRepository.save(metro);
            }
        }
    }

    private String getStringCell(Row row, int idx) {
        Cell cell = row.getCell(idx);
        return (cell == null) ? null : cell.toString().trim();
    }
}
