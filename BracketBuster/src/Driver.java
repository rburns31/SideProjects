import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Scanner;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
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
     * The year of the tournament currently selected
     */
    public static String YEAR;
    /**
     * 
     */
    public static String MODE;
    /**
     * 
     */
    public static String FORMULA;
    /**
     * The data set currently selected ('1', '2', or '3')
     */
    public static String DATA_SET;
    /**
     * 
     */
    public static HashMap<String, String[]> TEAM_NAMES = new HashMap<>();
    /**
     * 
     */
    public static HashMap<String, double[]> FORMULAS = new HashMap<>();
    /**
     * 
     */
    public static HashMap<String, String[]> VALID = new HashMap<>();
    /**
     * 
     */
    public static HashMap<String, String[]> STATS_HEADERS = new HashMap<>();
    /**
     * A map from the currently selected data set to the number of
     *   variables that it contains
     */
    public static HashMap<String, Integer> DATA_SET_TO_SIZE = new HashMap<>();

    @Override
    public void start(Stage stage) throws Exception {
        setUpConfigFiles();

        Parent root = FXMLLoader.load(getClass().getResource("GUIFXML.fxml"));
        Scene scene = new Scene(root, 1200, 900);
        stage.setScene(scene);
        stage.setResizable(false);
        try {
            stage.getIcons().add(new Image("file:bb_logo.png"));
        } catch (Exception e) {
            System.out.println("Failed to open the application icon.");
        }
        stage.setTitle("BracketBuster");
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
     * 
     */
    private static void setUpConfigFiles() {
        readInConfigFiles("config/teams.txt", TEAM_NAMES);
        readInConfigFiles("config/formulas.txt", FORMULAS);
        readInConfigFiles("config/valid.txt", VALID);
        readInConfigFiles("config/stats_headers.txt", STATS_HEADERS);

        // Initialize the YEAR_TO_SIZE map from the stats headers' lengths
        for (String key: STATS_HEADERS.keySet()) {
            DATA_SET_TO_SIZE.put(key, STATS_HEADERS.get(key).length + 1);
        }

        generateAverageFormulas();
    }

    /**
     * Generates all of the 'Average' formulas present in the VALID map
     */
    private static void generateAverageFormulas() {
        // Iterate through any valid formula which is an average
        for (String[] validFormulas: VALID.values()) {
            for (String validFormula: validFormulas) {
                if (validFormula.contains("Average")) {
                    String trimmedFormulaName = validFormula.substring(8);
                    
                    // This will hold the average formula as it is built up
                    String dataSet = Character.toString(
                            validFormula.charAt(validFormula.indexOf("(") + 1));
                    double[] avgCoeff = new double[DATA_SET_TO_SIZE.get(dataSet) - 1];
                    
                    // Decide which formulas to combine to form this average
                    String oldestYear = Collections.min(VALID.keySet()).substring(2);
                    String thisFormula = trimmedFormulaName;
                    String curYear = thisFormula.substring(2, 4);
                    while (curYear.compareTo(oldestYear) > 0) {
                        String olderYear = Integer.toString(Integer.parseInt(curYear) - 1);
                        thisFormula = thisFormula.replace(curYear, olderYear);
                        if (!thisFormula.equals(trimmedFormulaName)) {
                            if (FORMULAS.get(thisFormula) == null) {
                                // If this formula maps to null then append a "2.0"
                                thisFormula += " 2.0";
                            }
                            
                            // Scale the coefficients to be in the range [0, 1]
                            double[] thisCoeff = FORMULAS.get(thisFormula);
                            double max = findMaxInDoubleArray(thisCoeff);
                            double min = findMinInDoubleArray(thisCoeff);
                            for (int i = 0; i < thisCoeff.length; i++) {
                                avgCoeff[i] += (thisCoeff[i] - min) / (max - min);
                            }
                        }
                        
                        curYear = olderYear;
                    }

                    // Trim the average coefficients
                    YEAR = trimmedFormulaName.substring(0, 4);
                    DATA_SET = dataSet;
                    convertExcel();
                    
                    BracketBuster bb = new BracketBuster(0);
                    int score = bb.score(avgCoeff);
                    if (score != -1) {
                        avgCoeff = (double[]) bb.trimCoeff(score, avgCoeff).get(1);
                        bb.score(avgCoeff);
                    }
                    
                    FORMULAS.put(validFormula, avgCoeff);
                }
            }
        }
    }

    /**
     * Will get the current time and date in a predefined format whenever called
     * @return The current instance so that each formula is time stamped
     */
    public static String getTime() {
        StringBuilder timeStr = new StringBuilder();
        Calendar calendar = new GregorianCalendar();
        timeStr.append(String.format("%02d", calendar.get(Calendar.MONTH) + 1));
        timeStr.append("-");
        timeStr.append(String.format("%02d", calendar.get(Calendar.DAY_OF_MONTH)));
        timeStr.append("-");
        timeStr.append(Integer.toString(calendar.get(Calendar.YEAR)));
        timeStr.append(" ");
        timeStr.append(String.format("%02d", calendar.get(Calendar.HOUR_OF_DAY)));
        timeStr.append(":");
        timeStr.append(String.format("%02d", calendar.get(Calendar.MINUTE)));
        timeStr.append(":");
        timeStr.append(String.format("%02d", calendar.get(Calendar.SECOND)));
        return timeStr.toString();
    }

    /**
     * Converts Excel spreadsheet into a text file
     */
    public static void convertExcel() {
        String fileName = "stats/stats_" + YEAR + "(" + DATA_SET + ")_3";
        File outFile = new File(fileName + ".txt");
        outFile.deleteOnExit();
        try {
            PrintStream output = new PrintStream(outFile);
            InputStream input = new BufferedInputStream(new FileInputStream(
                    fileName + ".xls"));
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

    /**
     * Reads in any of the config data
     *   (formulas, teams, valid formulas, stats headers)
     *   from their text file and populates them into a passed-in hash map
     * @param fileName The name of the config file to be read in
     * @param map The structure to contain the config data
     */
    public static void readInConfigFiles(String fileName, HashMap map) {
        try {
            Scanner fileScanner = new Scanner(new File(fileName));
            while (fileScanner.hasNextLine()) {
                // Get the next line
                String line = fileScanner.nextLine();
                String[] parts = line.split(" = ");

                // Cut off the leading and trailing brackets
                String temp = parts[1].substring(1, parts[1].length() - 1);
                String[] data = temp.split(", ");

                // Handle special case where config file holds numbers
                if (fileName.equals("config/formulas.txt")) {
                    double[] coefficients = new double[data.length];
                    for (int i = 0; i < data.length; i++) {
                        coefficients[i] = Double.parseDouble(data[i]);
                    }
                    map.put(parts[0], coefficients);
                } else {
                    map.put(parts[0], data);
                }
            }
            fileScanner.close();
        } catch (IOException e) {
            System.out.println(fileName + " file does not exist.");
        }
    }

    /**
     * Finds the maximum element in the passed in double array
     * @param input A double array
     * @return The maximum element
     */
    private static double findMaxInDoubleArray(double[] input) {
        double max = Double.MIN_VALUE;
        for (double element: input) {
            if (element > max) {
                max = element;
            }
        }
        return max;
    }

    /**
     * Finds the minimum element in the passed in double array
     * @param input A double array
     * @return The minimum element
     */
    private static double findMinInDoubleArray(double[] input) {
        double min = Double.MAX_VALUE;
        for (double element: input) {
            if (element < min) {
                min = element;
            }
        }
        return min;
    }
}