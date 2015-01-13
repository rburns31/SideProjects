package bracketbuster;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Iterator;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

/**
 * 
 * @author Ryan Burns
 */
public class FileConverter {
    /**
     * The year of the stats spreadsheet to be imported
     */
    private final String year;

    public FileConverter(String year) {
        this.year = year;
    }

    /**
     * 
     * @throws IOException Will be thrown if the correct stats file does not
     *     exist, among potentially other things
     *     This is a fatal error intentionally
     */
    public void convert() throws IOException {
        File outFile = new File("stats_" + year + "_3" + ".txt");
        PrintStream output = new PrintStream(outFile);
        InputStream input = new BufferedInputStream(
                new FileInputStream("stats_" + year + "_3" + ".xls"));
        POIFSFileSystem fs = new POIFSFileSystem(input);
        HSSFWorkbook wb = new HSSFWorkbook(fs);
        HSSFSheet sheet = wb.getSheetAt(0);
        Iterator<Row> rows = sheet.rowIterator();
        while (rows.hasNext()) {
            HSSFRow row = (HSSFRow) rows.next();
            Iterator<Cell> cells = row.cellIterator();
            while (cells.hasNext()) {
                HSSFCell cell = (HSSFCell)cells.next();
                if (HSSFCell.CELL_TYPE_NUMERIC == cell.getCellType()) {
                    output.printf("%.5f ", cell.getNumericCellValue());
                }
            }
            output.println(" ");
        }
        output.close();
    }
}