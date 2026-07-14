package com.startup.cobranza.cartera.controller;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;

@RestController
@RequestMapping("/cartera")
public class PlantillaExcelController {

    @GetMapping("/descargar-plantilla")
    public ResponseEntity<byte[]> descargarPlantilla() throws Exception {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Cartera");

            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            String[] headers = {
                "NOMBRE", "DNI", "NUMERO_CUENTA", "NUMERO_OPERACION",
                "DEUDA_CAPITAL", "DEUDA_TOTAL", "TELEFONO", "DIRECCION", "ESTADO"
            };

            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            Row sampleRow = sheet.createRow(1);
            String[] sampleData = {
                "Juan Perez", "12345678", "001234", "OP001",
                "5000.00", "6500.00", "999888777", "Av. Lima 123", "Sin Gestionar"
            };
            for (int i = 0; i < sampleData.length; i++) {
                Cell cell = sampleRow.createCell(i);
                if (i == 4 || i == 5) {
                    cell.setCellValue(Double.parseDouble(sampleData[i]));
                } else {
                    cell.setCellValue(sampleData[i]);
                }
            }

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            workbook.write(baos);

            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=plantilla_cartera.xlsx")
                    .header("Content-Type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                    .body(baos.toByteArray());
        }
    }
}
