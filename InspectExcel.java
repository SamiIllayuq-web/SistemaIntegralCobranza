import org.apache.poi.ss.usermodel.*;
import org.apache.poi.util.*;
import org.apache.poi.xssf.usermodel.*;
import java.io.*;

public class InspectExcel {
    public static void main(String[] args) throws Exception {
        String path = "/mnt/d/dev/gato/SistemaIntegralCobranza/plantillas/05 - MAYO MC.xlsx";
        try (XSSFWorkbook workbook = new XSSFWorkbook(new FileInputStream(path))) {
            Sheet sheet = workbook.getSheetAt(0);

            System.out.println("=== HEADER ROW (Row 0) ===");
            Row headerRow = sheet.getRow(0);
            if (headerRow != null) {
                for (Cell cell : headerRow) {
                    String colLetter = CellReference.convertNumToColString(cell.getColumnIndex());
                    String value = getCellStringValue(cell);
                    System.out.println("  " + colLetter + ": " + value);
                }
            }

            System.out.println("\n=== FIRST 3 DATA ROWS (Rows 1-3) ===");
            for (int rowNum = 1; rowNum <= 3; rowNum++) {
                Row row = sheet.getRow(rowNum);
                if (row == null) {
                    System.out.println("  Row " + rowNum + ": (empty)");
                    continue;
                }
                System.out.println("  --- Row " + rowNum + " ---");
                for (Cell cell : row) {
                    String colLetter = CellReference.convertNumToColString(cell.getColumnIndex());
                    String value = getCellStringValue(cell);
                    System.out.println("    " + colLetter + ": " + value);
                }
            }

            // Sheet name
            System.out.println("\n=== SHEET INFO ===");
            System.out.println("  Sheet name: " + sheet.getSheetName());
            System.out.println("  Total rows (physical): " + sheet.getPhysicalNumberOfRows());
            System.out.println("  Total rows (last row num): " + sheet.getLastRowNum());
        }
    }

    private static String getCellStringValue(Cell cell) {
        if (cell == null) return "(null)";
        switch (cell.getCellType()) {
            case STRING: return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getLocalDateTimeCellValue().toString();
                }
                double val = cell.getNumericCellValue();
                if (val == Math.floor(val) && !Double.isInfinite(val)) {
                    return String.valueOf((long) val);
                }
                return String.valueOf(val);
            case BOOLEAN: return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                try { return cell.getStringCellValue(); } catch (Exception e) { return cell.getFormulaCachedValueType().toString(); }
            case BLANK: return "(blank)";
            default: return "(unknown)";
        }
    }
}
