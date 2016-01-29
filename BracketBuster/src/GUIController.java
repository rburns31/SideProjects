import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.Scanner;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

/**
 * FXML Controller class
 * @author Ryan Burns
 */
public class GUIController implements Initializable {
    @FXML
    private Button goButton;
    @FXML
    private Button clearButton;
    @FXML
    private ChoiceBox<String> yearDropdown;
    @FXML
    private ChoiceBox<String> modeDropdown;
    @FXML
    private ChoiceBox<String> formulaDropdown;
    @FXML
    private HBox coeffRow;
    @FXML
    private HBox overflowCoeffRow;
    @FXML
    private HBox fileNameRow;
    @FXML
    private HBox modeBox;
    @FXML
    private HBox formulaBox;
    @FXML
    private HBox progressBox;
    @FXML
    private HBox trialsBox;
    @FXML
    private HBox scoreBox;
    @FXML
    private TextArea bracketField;
    @FXML
    private TextField scoreField;
    @FXML
    private TextField outputFileNameField;
    @FXML
    private TextField trialsField;

    /**
     * 
     */
    public static String MODE;
    /**
     *
     */
    private double[] lastCoefficients;
    /**
     * 
     */
    private final String[] statsOne = {"FG%", "3P%", "ORPG", "RPG", "FTMPG",
                    "ASTPG", "TOPG", "STPG", "BLKPG", "CF", "T20", "CT"};
    /**
     * 
     */
    private final String[] statsTwo = {"FG%", "3P%", "ORPG", "RPG", "FTMPG",
                    "ASTPG", "TOPG", "STPG", "BLKPG", "CF", "T20", "CT",
                    "FTAPG", "Opp FG%", "Opp TO", "Opp RPG", "Opp FTAPG",
                    "FT%", "AST%", "Opp AST%"};
    /**
     * 
     */
    private final String[] statsThree = {"FG%", "3P%", "ORPG", "RPG", "FTMPG",
                    "ASTPG", "TOPG", "STPG", "BLKPG", "CF", "T20", "CT",
                    "FT%", "FGMPG", "PPS", "ADJ FG%", "3PMPG", "AST/TO", "ST/TO"};
    /**
     * The formulas which are valid to be applied to the 2010 data
     */
    private final String[] valid2010 = {"2010 1.0", "2010 2.0", "Everything(1)"};
    /**
     * The formulas which are valid to be applied to the 2011 data
     */
    private final String[] valid2011 = {"2010 1.0", "2010 2.0", "2011 1.0",
                    "2011 2.0", "Everything(1)"};
    /**
     * The formulas which are valid to be applied to the 2012(1) data
     */
    private final String[] valid2012x1 = {"2010 1.0", "2010 2.0", "2011 1.0",
                    "2011 2.0", "2012(1) 1.0", "2012(1) 2.0",
                    "Average 2012 1.0", "Everything(1)"};
    /**
     * The formulas which are valid to be applied to the 2012(2) data
     */
    private final String[] valid2012x2 = {"2012(2) 2.0", "Average 2012 2.0",
                    "Everything(2)"};
    /**
     * The formulas which are valid to be applied to the 2013 data
     */
    private final String[] valid2013 = {"2010 1.0", "2010 2.0", "2011 1.0",
                    "2011 2.0", "2012(1) 1.0", "2012(1) 2.0", "2013 2.0",
                    "Average 2013 1.0", "Average 2013 2.0", "Everything(1)"};
    /**
     * The formulas which are valid to be applied to the 2014(1) data
     */
    private final String[] valid2014x1 = {"2010 1.0", "2010 2.0", "2011 1.0",
                    "2011 2.0", "2012(1) 1.0", "2012(1) 2.0", "2013 2.0",
                    "2014(1) 2.0", "Average 2014 2.0", "Everything(1)"};
    /**
     * The formulas which are valid to be applied to the 2014(3) data
     */
    private final String[] valid2014x3 = {"2014(3) 2.0", "Everything(3)"};
    /**
     * The formulas which are valid to be applied to the 2015(1) data
     */
    private final String[] valid2015x1 = {"2010 1.0", "2010 2.0", "2011 1.0",
                    "2011 2.0", "2012(1) 1.0", "2012(1) 2.0", "2013 2.0",
                    "2014(1) 2.0", "Everything(1)", "Average 2015 2.0"};
    /**
     * The formulas which are valid to be applied to the 2015(3) data
     */
    private final String[] valid2015x3 = {"2014(3) 2.0", "Everything(3)"};
    /**
     * 
     */
    private HashMap<String, double[]> formulas;

    /**
     * Initializes the controller class
     * Sets up the values in the year drop-down list, and updates the global
     *     YEAR variable every time the selection is changed
     * Calls the method to update the coefficient rows whenever the YEAR
     *     variable user selection is changed
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Instantiate the formulas map from the text file
        readInFormulas();
        // Font needs to be monospaced or the bracket goes all wonky
        bracketField.setFont(Font.font(java.awt.Font.MONOSPACED, 13.5));
        // Instantiate the instance array to the maximum coefficient size
        lastCoefficients = new double[statsTwo.length];
        // Set up the dropdown menus with choices, defaults, and listeners
        yearDropdown.getItems().addAll("2010", "2011", "2012(1)", "2012(2)",
                    "2013", "2014(1)", "2014(3)", "2015(1)", "2015(3)");
        yearDropdown.setValue("2015(3)");
        yearDropdown.valueProperty().addListener((observable, old, newYear) -> {
            Driver.YEAR = newYear;
            update();
        });
        modeDropdown.getItems().addAll("High Seeds", "Manual Formula",
                "Select Formula", "Generate Formula");
        modeDropdown.setValue("High Seeds");
        modeDropdown.valueProperty().addListener((observable, old, newMode) -> {
            MODE = newMode;
            update();
        });
        // Sets the relevant global variables to the current dropdown values
        Driver.YEAR = (String)yearDropdown.getValue();
        MODE = (String)modeDropdown.getValue();
        // Handles the dynamic GUI based on the default dropdown selections
        update();
        // Adds a listener to the trials field to verify it is always an int
        trialsField.textProperty().addListener((observable, oldInput, newInput) -> {
            try {
                Integer.parseInt(newInput);
                trialsField.setText(newInput.replaceAll("[d,f,i]", ""));
                update();
            } catch (Exception e) {
                trialsField.setText(oldInput);
            }
        });
        formulaDropdown.valueProperty().addListener((observable, old, newFormula) -> {
            bracketField.clear();
        });
    }

    /**
     * 
     */
    private void readInFormulas() {
        try {
            formulas = new HashMap<>();
            Scanner fileScanner = new Scanner(new File("formulas.txt"));
            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine();
                String[] parts = line.split("=");
                String temp = parts[1].substring(2, parts[1].length() - 1);
                String[] coeffStr = temp.split(",");
                double[] coefficients = new double[coeffStr.length];
                for (int i = 0; i < coeffStr.length; i++) {
                    coefficients[i] = Double.parseDouble(coeffStr[i]);
                }
                formulas.put(parts[0].trim(), coefficients);
            }
            fileScanner.close();
        } catch (IOException e) {
            System.out.println("Formulas file does not exist.");
        }
    }

    /**
     * 
     */
    private String[] validFormulaFinder() {
        String[] validFormulas = null;
        if (Driver.YEAR.equals("2010")) {
            validFormulas = valid2010;
        } else if (Driver.YEAR.equals("2011")) {
            validFormulas = valid2011;
        } else if (Driver.YEAR.equals("2012(1)")) {
            validFormulas = valid2012x1;
        } else if (Driver.YEAR.equals("2012(2)")) {
            validFormulas = valid2012x2;
        } else if (Driver.YEAR.equals("2013")) {
            validFormulas = valid2013;
        } else if (Driver.YEAR.equals("2014(1)")) {
            validFormulas = valid2014x1;
        } else if (Driver.YEAR.equals("2014(3)")) {
            validFormulas = valid2014x3;
        } else if (Driver.YEAR.equals("2015(1)")) {
            validFormulas = valid2015x1;
        } else if (Driver.YEAR.equals("2015(3)")) {
            validFormulas = valid2015x3;
        }
        return validFormulas;
    }

    /**
     * Handles updating all parts of the form when any input is changed
     */
    private void update() {
        // Set the valid formulas variable to the appropriate array for the year
        String[] validFormulas = validFormulaFinder();
        // Store the current coefficients before clearing the rows out
        storeCoeff();
        // Clear out the rows before re-adding the correct children
        coeffRow.getChildren().clear();
        overflowCoeffRow.getChildren().clear();
        formulaDropdown.getItems().clear();
        // Clear out the results from the last iteration
        bracketField.clear();
        scoreField.clear();
        // Nothing is shown until you tell it to be visible
        modeBox.setVisible(false);
        formulaBox.setVisible(false);
        trialsBox.setVisible(false);
        scoreBox.setVisible(false);
        goButton.setVisible(false);
        clearButton.setVisible(false);
        fileNameRow.setVisible(false);
        progressBox.setVisible(false);
        // If the program does not support the selected year, don't show anything
        if (!BracketBuster.YEAR_TO_SIZE.containsKey(Driver.YEAR)) {
            return;
        }
        // Since the selected year is supported, show the basic options
        modeBox.setVisible(true);
        scoreBox.setVisible(true);
        goButton.setVisible(true);
        // If the program is in "High Seeds" mode, do not fill in the rows
        if (MODE.equals("High Seeds")) {
            return;
        } else if (MODE.equals("Select Formula")) {
            formulaBox.setVisible(true);
            for (String s2: validFormulas) {
                for (String s: formulas.keySet()) {
                    if (s.equals(s2)) {
                        if (formulaDropdown.getItems().isEmpty()) {
                            formulaDropdown.setValue(s);
                        }
                        formulaDropdown.getItems().add(s);
                    }
                }
            }
        } else if (MODE.equals("Manual Formula")) {
            clearButton.setVisible(true);
        } else if (MODE.equals("Generate Formula")) {
            trialsBox.setVisible(true);
        }
        // Add the correct children to the main coefficient row
        setUpRow(0, statsOne, coeffRow);
        // If the selected year supports additional statistics, fill in the overflow row
        if (BracketBuster.YEAR_TO_SIZE.get(Driver.YEAR) == 21) {
            setUpRow(statsOne.length, statsTwo, overflowCoeffRow);
        } else if (BracketBuster.YEAR_TO_SIZE.get(Driver.YEAR) == 20) {
            setUpRow(statsOne.length, statsThree, overflowCoeffRow);
        }
    }

    /**
     * 
     */
    private void setUpRow(int num, String[] arr, HBox row) {
        for (int i = num; i < arr.length; i++) {
            VBox entry = new VBox();
            entry.setPrefWidth(82);
            Label label = new Label(arr[i]);
            TextField field = new TextField();
            if (MODE.equals("Select Formula")
                    || MODE.equals("Generate Formula")) {
                field.setText("0");
                field.setEditable(false);
            } else {
                field.setText(removeTrailingZeros(
                        Double.toString(lastCoefficients[i])));
            }
            field.textProperty().addListener(
                    (observable, oldInput, newInput) -> {
                try {
                    String str = Double.toString(Double.parseDouble(
                            newInput.replaceAll("[d,f]", "")));
                    field.setText(removeTrailingZeros(str));
                } catch (Exception e) {
                    field.setText(oldInput);
                }
            });
            entry.getChildren().addAll(label, field);
            row.getChildren().add(entry);
        }
    }

    /**
     * Removes trailing zeros from a passed in String
     */
    private String removeTrailingZeros(String str) {
        return !str.contains(".") ? str : str.replaceAll("0*$", "").replaceAll("\\.$", "");
    }

    /**
     * 
     * @param event 
     */
    @FXML
    private void saveButtonHandler(ActionEvent event) {
        try {
            Scanner input = new Scanner(new File("bracket.txt"));
            PrintWriter output = new PrintWriter(outputFileNameField.getText() + ".txt");
            while (input.hasNextLine()) {
                output.println(input.nextLine());
            }
            input.close();
            output.close();
        } catch (IOException e) {
            System.out.println("Some problem copying the bracket to the new file.");
        }
    }

    /**
     * 
     * @param event 
     */
    @FXML
    private void goButtonHandler(ActionEvent event) {
        bracketField.clear();
        Driver.convert();
        if (MODE.equals("High Seeds")) {
            BracketBuster bb = new BracketBuster(0);
            int score = bb.highSeed();
            scoreField.setText(Integer.toString(score));
            BracketVisual bv = new BracketVisual(bb.getWinnerPos(), score,
                    bb.getBest());
        } else if (MODE.equals("Manual Formula")) {
            BracketBuster bb = new BracketBuster(0);
            storeCoeff();
            double[] inputCoeff = concat(Arrays.copyOfRange(
                    lastCoefficients, 0, BracketBuster.YEAR_TO_SIZE.get(Driver.YEAR) - 1), new double[]{0});

            int score = bb.score(inputCoeff);
            scoreField.setText(Integer.toString(score));
            BracketVisual bv = new BracketVisual(bb.getWinnerPos(), score,
                    inputCoeff);
        } else if (MODE.equals("Select Formula")) {
            BracketBuster bb = new BracketBuster(0);
            storeCoeff();
            // Pass in the specified formula from the map
            double[] inputCoeff = concat(Arrays.copyOfRange(formulas.get(formulaDropdown.valueProperty().asString().getValue()),
                            0, BracketBuster.YEAR_TO_SIZE.get(Driver.YEAR) - 1), new double[]{0});

            int score = bb.score(inputCoeff);
            scoreField.setText(Integer.toString(score));
            // Fill in the coefficient rows with the selected formula
            Object[] objs = coeffRow.getChildren().toArray();
            VBox[] vboxes = new VBox[objs.length];
            for (int i = 0; i < objs.length; i++) {
                vboxes[i] = (VBox)objs[i];
                ((TextField)vboxes[i].getChildren().toArray()[1]).setText(Double.toString(inputCoeff[i]));
            }
            if (!overflowCoeffRow.getChildren().isEmpty()) {
                Object[] objs2 = overflowCoeffRow.getChildren().toArray();
                VBox[] vboxes2 = new VBox[objs2.length];
                for (int i = 0; i < objs2.length; i++) {
                    vboxes2[i] = (VBox)objs2[i];
                    ((TextField)vboxes2[i].getChildren().toArray()[1]).setText(Double.toString(inputCoeff[i + objs.length]));
                }
            }
            BracketVisual bv = new BracketVisual(bb.getWinnerPos(), score,
                    inputCoeff);
        } else if (MODE.equals("Generate Formula")) {
            BracketBuster bb = new BracketBuster(Integer.parseInt(trialsField.getText()));
            storeCoeff();
            progressBox.setVisible(true);
            int max = bb.maxFind();
            int score = bb.score(bb.getBest());
            Object[] objs = coeffRow.getChildren().toArray();
            VBox[] vboxes = new VBox[objs.length];
            for (int i = 0; i < objs.length; i++) {
                vboxes[i] = (VBox)objs[i];
                ((TextField)vboxes[i].getChildren().toArray()[1]).setText(Double.toString(bb.getBest()[i]));
            }
            if (!overflowCoeffRow.getChildren().isEmpty()) {
                Object[] objs2 = overflowCoeffRow.getChildren().toArray();
                VBox[] vboxes2 = new VBox[objs2.length];
                for (int i = 0; i < objs2.length; i++) {
                    vboxes2[i] = (VBox)objs2[i];
                    ((TextField)vboxes2[i].getChildren().toArray()[1]).setText(Double.toString(bb.getBest()[i + objs.length]));
                }
            }
            scoreField.setText(Integer.toString(score));
            BracketVisual bv = new BracketVisual(bb.getWinnerPos(), score,
                    bb.getBest());
        }
        fileToGUI("bracket.txt");
        fileNameRow.setVisible(true);
    }

    /**
     * Simple helper method to concatenate 2 double arrays into 1
     * @param a The first double array
     * @param b The second double array
     * @return The two input double arrays, concatenated together
     */
    private double[] concat(double[] a, double[] b) {
        double[] ans = new double[a.length + b.length];
        System.arraycopy(a, 0, ans, 0, a.length);
        System.arraycopy(b, 0, ans, a.length, b.length);
        return ans;
    }

    /**
     * 
     */
    private void storeCoeff() {
        if (!coeffRow.getChildren().isEmpty()) {
            Object[] objs = coeffRow.getChildren().toArray();
            VBox[] vboxes = new VBox[objs.length];
            for (int i = 0; i < objs.length; i++) {
                vboxes[i] = (VBox)objs[i];
            }
            for (int i = 0; i < objs.length; i++) {
                lastCoefficients[i] = Double.parseDouble(
                        ((TextField)vboxes[i].getChildren().toArray()[1]).getText());
            }
        } else {
            for (int i = 0; i < statsOne.length; i++) {
                lastCoefficients[i] = 0;
            }
        }
        if (!overflowCoeffRow.getChildren().isEmpty()) {
            Object[] objs = overflowCoeffRow.getChildren().toArray();
            VBox[] vboxes = new VBox[objs.length];
            for (int i = 0; i < objs.length; i++) {
                vboxes[i] = (VBox)objs[i];
            }
            for (int i = 0; i < objs.length; i++) {
                lastCoefficients[i + statsOne.length] = Double.parseDouble(
                        ((TextField)vboxes[i].getChildren().toArray()[1]).getText());
            }
        } else {
            for (int i = statsOne.length; i < statsTwo.length; i++) {
                lastCoefficients[i] = 0;
            }
        }
    }

    /**
     * 
     * @param event 
     */
    @FXML
    private void clearButtonHandler(ActionEvent event) {
        if (!coeffRow.getChildren().isEmpty()) {
            Object[] objs = coeffRow.getChildren().toArray();
            VBox[] vboxes = new VBox[objs.length];
            for (int i = 0; i < objs.length; i++) {
                vboxes[i] = (VBox)objs[i];
                ((TextField)vboxes[i].getChildren().toArray()[1]).setText("0");
            }
        }
        if (!overflowCoeffRow.getChildren().isEmpty()) {
            Object[] objs = overflowCoeffRow.getChildren().toArray();
            VBox[] vboxes = new VBox[objs.length];
            for (int i = 0; i < objs.length; i++) {
                vboxes[i] = (VBox)objs[i];
                ((TextField)vboxes[i].getChildren().toArray()[1]).setText("0");
            }
        }
        bracketField.clear();
        scoreField.clear();
        fileNameRow.setVisible(false);
        storeCoeff();
    }

    /**
     * Reads in information from the file that was just created by BracketVisual
     *     and places that information in the GUI field which displays bracket
     *     results to the user after their selections have been made
     * @param fileName The file to be read in from
     */
    private void fileToGUI(String fileName) {
        try {
            File dataFile = new File(fileName);
            BufferedReader reader = new BufferedReader(new FileReader(dataFile));
            String line;
            int i = 0;
            while ((line = reader.readLine()) != null) {
                if (i > 4) {
                    bracketField.appendText(line);
                    bracketField.appendText("\n");
                }
                i++;
            }
            reader.close();
        } catch (IOException e) {
            System.out.println("Bracket file does not exist, exiting.");
            System.exit(0);
        }
    }
}