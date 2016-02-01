import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
    public static String FORMULA;
    /**
     *
     */
    private double[] lastCoefficients;
    /**
     * 
     */
    private int L1;
    /**
     * 
     */
    private int L2;

    /**
     * 
     */
    private final HashMap<String, double[]> formulas = new HashMap<>();
    /**
     * 
     */
    private final HashMap<String, String[]> allValid = new HashMap<>();
    /**
     * 
     */
    private final HashMap<String, String[]> statsHeaders = new HashMap<>();

    /**
     * Initializes the controller class
     * Sets up the values in the year drop-down list, and updates the global
     *     YEAR variable every time the selection is changed
     * Calls the method to update the coefficient rows whenever the YEAR
     *     variable user selection is changed
     * @param url Not used
     * @param rb Not used
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Instantiate the maps from their respective text files
        Driver.readInConfigFiles("config/formulas.txt", formulas);
        Driver.readInConfigFiles("config/valid.txt", allValid);
        Driver.readInConfigFiles("config/stats_headers.txt", statsHeaders);

        // Initialize the convenience length variables
        L1 = statsHeaders.get("1").length;
        L2 = statsHeaders.get("2").length;

        // Font needs to be monospaced or the bracket goes all wonky
        bracketField.setFont(Font.font(java.awt.Font.MONOSPACED, 13.5));

        // Instantiate the instance array to the maximum coefficient size
        lastCoefficients = new double[
                Collections.max(Driver.YEAR_TO_SIZE.values())];

        // Set up the dropdown menus with choices, defaults, and listeners
        ArrayList<String> years = new ArrayList(allValid.keySet());
        Collections.sort(years);
        yearDropdown.getItems().addAll(years);
        yearDropdown.setValue("2015(3)");
        yearDropdown.valueProperty().addListener((obs, old, newYear) -> {
            Driver.YEAR = newYear;
            update();
        });
        modeDropdown.getItems().addAll("High Seeds", "Manual Formula",
                "Select Formula", "Generate Formula", "Actual Results");
        modeDropdown.setValue("High Seeds");
        modeDropdown.valueProperty().addListener((obs, old, newMode) -> {
            MODE = newMode;
            update();
        });

        // Sets the relevant global variables to the current dropdown values
        Driver.YEAR = (String)yearDropdown.getValue();
        MODE = (String)modeDropdown.getValue();

        // Handles the dynamic GUI based on the default dropdown selections
        update();

        // Adds a listener to the trials field to verify it is always an int
        trialsField.textProperty().addListener((obs, oldInput, newInput) -> {
            try {
                Integer.parseInt(newInput);
                trialsField.setText(newInput.replaceAll("[d,f,i]", ""));
                update();
            } catch (Exception e) {
                trialsField.setText(oldInput);
            }
        });
        formulaDropdown.valueProperty().addListener((obs, old, newF) -> {
            bracketField.clear();
            FORMULA = newF;
        });
    }

    /**
     * Handles updating all parts of the form when any input is changed
     */
    private void update() {
        // Set the valid formulas to the appropriate array for the year
        String[] validFormulas = allValid.get(Driver.YEAR);
        if (validFormulas == null) {
            System.out.println("No valid formulas for that year. Exiting.");
            System.exit(0);
        }

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

        // If the program doesn't support the selected year, don't show anything
        if (!Driver.YEAR_TO_SIZE.containsKey(Driver.YEAR)) {
            return;
        }

        // Since the selected year is supported, show the basic options
        modeBox.setVisible(true);
        scoreBox.setVisible(true);
        goButton.setVisible(true);

        // If the program is in "High Seeds" mode, do not fill in the rows
        if (MODE.equals("High Seeds") || MODE.equals("Actual Results")) {
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
        setUpRow(0, statsHeaders.get("1"), coeffRow);

        // If the selected year supports additional stats, use the overflow row
        if (Driver.YEAR_TO_SIZE.get(Driver.YEAR) > L1) {
            setUpRow(L1, statsHeaders.get(Driver.YEAR.split("[\\(, \\)]")[1]),
                    overflowCoeffRow);
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
        return !str.contains(".") ? str
                : str.replaceAll("0*$", "").replaceAll("\\.$", "");
    }

    /**
     * 
     * @param event 
     */
    @FXML
    private void saveButtonHandler(ActionEvent event) {
        try {
            Scanner input = new Scanner(new File("bracket.txt"));
            PrintWriter output = new PrintWriter(
                    outputFileNameField.getText() + ".txt");
            while (input.hasNextLine()) {
                output.println(input.nextLine());
            }
            input.close();
            output.close();
        } catch (IOException e) {
            System.out.println("Some problem copying"
                    + " the bracket to the new file.");
        }
    }

    /**
     * 
     * @param event 
     */
    @FXML
    private void goButtonHandler(ActionEvent event) {
        bracketField.clear();
        Driver.convertExcel();
        if (MODE.equals("High Seeds")) {
            BracketBuster bb = new BracketBuster(0);
            int score = bb.highSeed();
            scoreField.setText(Integer.toString(score));
            new BracketVisual(bb.winnerPos, score, bb.best, -1);
        } else if (MODE.equals("Manual Formula")) {
            BracketBuster bb = new BracketBuster(0);
            storeCoeff();
            double[] inputCoeff = concat(Arrays.copyOfRange(
                    lastCoefficients, 0, Driver.YEAR_TO_SIZE.get(
                            Driver.YEAR) - 1), new double[]{0});

            int score = bb.score(inputCoeff);
            scoreField.setText(Integer.toString(score));
            new BracketVisual(bb.winnerPos, score, inputCoeff, -1);
        } else if (MODE.equals("Select Formula")) {
            BracketBuster bb = new BracketBuster(0);
            storeCoeff();

            // Pass in the specified formula from the map
            double[] inputCoeff = concat(Arrays.copyOfRange(formulas.get(
                    formulaDropdown.valueProperty().asString().getValue()), 0,
                    Driver.YEAR_TO_SIZE.get(Driver.YEAR) - 1), new double[]{0});

            int score = bb.score(inputCoeff);
            scoreField.setText(Integer.toString(score));

            // Fill in the coefficient rows with the selected formula
            Object[] objs = coeffRow.getChildren().toArray();
            VBox[] vboxes = new VBox[objs.length];
            for (int i = 0; i < objs.length; i++) {
                vboxes[i] = (VBox)objs[i];
                ((TextField)vboxes[i].getChildren().toArray()[1]).setText(
                        Double.toString(inputCoeff[i]));
            }
            if (!overflowCoeffRow.getChildren().isEmpty()) {
                Object[] objs2 = overflowCoeffRow.getChildren().toArray();
                VBox[] vboxes2 = new VBox[objs2.length];
                for (int i = 0; i < objs2.length; i++) {
                    vboxes2[i] = (VBox)objs2[i];
                    ((TextField)vboxes2[i].getChildren().toArray()[1]).setText(
                            Double.toString(inputCoeff[i + objs.length]));
                }
            }
            new BracketVisual(bb.winnerPos, score, inputCoeff, -1);
        } else if (MODE.equals("Generate Formula")) {
            BracketBuster bb = new BracketBuster(
                    Integer.parseInt(trialsField.getText()));
            int score = bb.maxFind();
            storeCoeff();
            progressBox.setVisible(true);
            Object[] objs = coeffRow.getChildren().toArray();
            VBox[] vboxes = new VBox[objs.length];
            for (int i = 0; i < objs.length; i++) {
                vboxes[i] = (VBox)objs[i];
                ((TextField)vboxes[i].getChildren().toArray()[1]).setText(
                        Double.toString(bb.best[i]));
            }
            if (!overflowCoeffRow.getChildren().isEmpty()) {
                Object[] objs2 = overflowCoeffRow.getChildren().toArray();
                VBox[] vboxes2 = new VBox[objs2.length];
                for (int i = 0; i < objs2.length; i++) {
                    vboxes2[i] = (VBox)objs2[i];
                    ((TextField)vboxes2[i].getChildren().toArray()[1]).setText(
                            Double.toString(bb.best[i + objs.length]));
                }
            }
            scoreField.setText(Integer.toString(score));
            new BracketVisual(bb.winnerPos, score, bb.best,
                    Integer.parseInt(trialsField.getText()));
        } else if (MODE.equals("Actual Results")) {
            BracketBuster bb = new BracketBuster(0);
            bb.actualResults();
            scoreField.setText("N/A");
            new BracketVisual(bb.winnerPos, -1, bb.best, -1);
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
                lastCoefficients[i] = Double.parseDouble(((TextField)
                        vboxes[i].getChildren().toArray()[1]).getText());
            }
        } else {
            for (int i = 0; i < L1; i++) {
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
                lastCoefficients[i + L1] = Double.parseDouble(((TextField)
                        vboxes[i].getChildren().toArray()[1]).getText());
            }
        } else {
            for (int i = L1; i < L2; i++) {
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
            BufferedReader reader = new BufferedReader(
                    new FileReader(dataFile));
            String line;
            boolean pastHeaders = false;
            while ((line = reader.readLine()) != null) {
                if (pastHeaders) {
                    bracketField.appendText(line);
                    bracketField.appendText("\n");
                }
                if (line.equals("")) {
                    pastHeaders = true;
                }
            }
            reader.close();
        } catch (IOException e) {
            System.out.println("Bracket file does not exist, exiting.");
            System.exit(0);
        }
    }
}