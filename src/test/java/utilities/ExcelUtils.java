package utilities;

import java.io.InputStream;
import java.io.IOException;

import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

/**
 * Apache POI helper used for data-driven (parameterised) tests.
 */
public class ExcelUtils {

    private static final DataFormatter FORMATTER = new DataFormatter();

    private ExcelUtils() {
    }

    /** Reads every row below the header row of a sheet into a TestNG-style Object[][]. */
    public static Object[][] getSheetData(String filePath, String sheetName) {
        try (InputStream fis = ConfigReader.openResource(filePath);
             Workbook workbook = WorkbookFactory.create(fis)) {

            Sheet sheet = workbook.getSheet(sheetName);
            if (sheet == null) {
                throw new IllegalArgumentException("Sheet not found: " + sheetName);
            }
            int rows = sheet.getLastRowNum();
            int cols = sheet.getRow(0).getLastCellNum();
            Object[][] data = new Object[rows][cols];

            for (int r = 1; r <= rows; r++) {
                Row row = sheet.getRow(r);
                for (int c = 0; c < cols; c++) {
                    data[r - 1][c] = row == null ? "" : FORMATTER.formatCellValue(row.getCell(c));
                }
            }
            return data;

        } catch (IOException e) {
            throw new IllegalStateException("Unable to read test data from " + filePath, e);
        }
    }

    public static String getCellData(String filePath, String sheetName, int rowNum, int colNum) {
        try (InputStream fis = ConfigReader.openResource(filePath);
             Workbook workbook = WorkbookFactory.create(fis)) {
            return FORMATTER.formatCellValue(workbook.getSheet(sheetName).getRow(rowNum).getCell(colNum));
        } catch (IOException e) {
            throw new IllegalStateException("Unable to read test data from " + filePath, e);
        }
    }
}
