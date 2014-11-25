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

public class FileConverter {

    public void convert() throws IOException {
        File outFile = new File("stats_" + Driver.YEAR + "_3" + ".txt");
        PrintStream output = new PrintStream(outFile);
        InputStream input = new BufferedInputStream(new FileInputStream("stats_" + Driver.YEAR + "_3" + ".xls"));
        POIFSFileSystem fs = new POIFSFileSystem(input);
        HSSFWorkbook wb = new HSSFWorkbook(fs);
        HSSFSheet sheet = wb.getSheetAt(0);
        Iterator<Row> rows = sheet.rowIterator();
        while(rows.hasNext()) {
            HSSFRow row = (HSSFRow) rows.next();
            Iterator<Cell> cells = row.cellIterator();
            while(cells.hasNext()) {
                HSSFCell cell = (HSSFCell) cells.next();
                if(HSSFCell.CELL_TYPE_NUMERIC == cell.getCellType()) {
                    output.printf("%.5f ", cell.getNumericCellValue());
                }
            }
            output.println(" ");
        }
        output.close();
    }
}