import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

/**
 * Driver for the BracketBuster application, which opens the GUI and contains
 *   some convenience helper methods and variables used in the application
 * @author Ryan Burns
 */
public class Driver extends Application {
    /**
     * The year to be simulated
     */
    public static String YEAR;

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("GUIFXML.fxml"));
        Scene scene = new Scene(root, 1200, 900);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    /**
     * Launch the BracketBuster GUI
     * @param args Not used
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Will get the current time and date in a predefined format whenever called
     * @return The current instance so that each formula is time stamped
     */
    public static String getTime() {
        StringBuilder timeStr = new StringBuilder();
        Calendar calendar = new GregorianCalendar();
        timeStr.append(Integer.toString(calendar.get(Calendar.MONTH) + 1));
        timeStr.append("-");
        timeStr.append(Integer.toString(calendar.get(Calendar.DAY_OF_MONTH)));
        timeStr.append("-");
        timeStr.append(Integer.toString(calendar.get(Calendar.YEAR)));
        timeStr.append(" ");
        timeStr.append(Integer.toString(calendar.get(Calendar.HOUR_OF_DAY)));
        timeStr.append("'");
        timeStr.append(Integer.toString(calendar.get(Calendar.MINUTE)));
        timeStr.append("'");
        timeStr.append(Integer.toString(calendar.get(Calendar.SECOND)));
        return timeStr.toString();
    }

    /**
     * Converts Excel spreadsheet into a text file
     */
    public static void convert() {
        try {
            File outFile = new File("stats_" + Driver.YEAR + "_3" + ".txt");
            PrintStream output = new PrintStream(outFile);
            InputStream input = new BufferedInputStream(
                    new FileInputStream("stats_" + Driver.YEAR + "_3" + ".xls"));
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
        } catch (IOException e) {
            System.out.println("Some problem with converting the stats.");
        }
    }
}