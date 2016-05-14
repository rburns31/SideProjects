import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Scanner;
import javafx.collections.FXCollections;

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
    private ChoiceBox<String> dataSetDropdown;
    @FXML
    private HBox coeffRow;
    @FXML
    private HBox overflowCoeffRow;
    @FXML
    private HBox fileNameRow;
    @FXML
    private HBox formulaBox;
    @FXML
    private HBox progressBox;
    @FXML
    private HBox trialsBox;
    @FXML
    private HBox scoreBox;
    @FXML
    private HBox dataSetBox;
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
     * Initializes the controller class
     * 
     * @param url Not used
     * @param rb Not used
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Initialize the convenience length variables
        L1 = Driver.STATS_HEADERS.get("1").length;
        L2 = Driver.STATS_HEADERS.get("2").length;

        // Font needs to be monospaced or the bracket goes all wonky
        bracketField.setFont(Font.font(java.awt.Font.MONOSPACED, 13.5));

        // Instantiate the instance array to the maximum coefficient size
        lastCoefficients = new double[
                Collections.max(Driver.DATA_SET_TO_SIZE.values())];

        setUpDropdowns();

        // Sets the relevant global variables to the current dropdown values
        Driver.YEAR = (String) yearDropdown.getValue();
        Driver.MODE = (String) modeDropdown.getValue();
        Driver.DATA_SET = (String) dataSetDropdown.getValue();

        // Handles the dynamic GUI based on the default dropdown selections
        update(false);

        // Adds a listener to the trials field to verify it is always an int
        trialsField.setText("1");
        trialsField.textProperty().addListener((obs, oldInput, newInput) -> {
            try {
                Integer.parseInt(newInput);
                trialsField.setText(newInput.replaceAll("[d,f,i]", ""));
            } catch (Exception e) {
                trialsField.setText(oldInput);
            }
        });
    }

    /**
     * Set up the dropdown menus with choices, defaults, and listeners
     *   (year, mode, formula, data set)
     */
    private void setUpDropdowns() {
        ArrayList<String> years = new ArrayList(Driver.VALID.keySet());
        Collections.sort(years);
        yearDropdown.getItems().addAll(years);
        yearDropdown.setValue(years.get(years.size() - 1));
        yearDropdown.valueProperty().addListener((obs, old, newYear) -> {
            Driver.YEAR = newYear;
            if (newYear != null) {
                update(false);
            }
        });
        
        modeDropdown.getItems().addAll("High Seeds", "Manual Formula",
                "Select Formula", "Generate Formula",
                "Actual Results", "Run Formula Batch");
        modeDropdown.setValue("Select Formula");
        modeDropdown.valueProperty().addListener((obs, old, newMode) -> {
            Driver.MODE = newMode;
            if (newMode != null) {
                update(false);
            }
        });
        
        formulaDropdown.valueProperty().addListener((obs, old, newFormula) -> {
            Driver.FORMULA = newFormula;
            if (newFormula != null) {
                update(true);
            }
        });
        
        dataSetDropdown.getItems().addAll("1", "3");
        dataSetDropdown.setValue("3");
        dataSetDropdown.valueProperty().addListener((obs, old, newDataSet) -> {
            Driver.DATA_SET = newDataSet;
            if (newDataSet != null) {
                update(false);
            }
        });
    }

    /**
     * Handles updating all parts of the form when any input is changed
     */
    private void update(boolean formulaChanged) {
        // Set the valid formulas to the appropriate array for the year
        String[] validFormulas = Driver.VALID.get(Driver.YEAR);
        if (validFormulas == null) {
            System.out.println("No valid formulas for that year. Exiting.");
            System.exit(0);
        }

        // Store the current coefficients before clearing the rows out
        storeCoeff();

        // Clear out the rows before re-adding the correct children
        coeffRow.getChildren().clear();
        overflowCoeffRow.getChildren().clear();

        // Clear out the results from the last iteration
        bracketField.clear();
        scoreField.clear();
        trialsField.clear();

        // Nothing is shown until you tell it to be visible
        formulaBox.setVisible(false);
        dataSetBox.setVisible(false);
        trialsBox.setVisible(false);
        scoreBox.setVisible(false);
        goButton.setVisible(false);
        clearButton.setVisible(false);
        fileNameRow.setVisible(false);
        progressBox.setVisible(false);

        // If the program doesn't support the selected year, don't show anything
        if (!Driver.VALID.containsKey(Driver.YEAR)) {
            return;
        } else {
            // Since the selected year is supported, show the go button
            goButton.setVisible(true);
        }

        // Make sure the data set dropdown always has the correct options
        if (Driver.YEAR.equals("2012")
                && !dataSetDropdown.getItems().contains("2")) {

            dataSetDropdown.getItems().add(1, "2");
        } else if (!Driver.YEAR.equals("2012")
                && dataSetDropdown.getItems().contains("2")) {

            dataSetDropdown.setValue("3");
            dataSetDropdown.getItems().remove("2");

            // Because update() will get called twice unnecessarily
            coeffRow.getChildren().clear();
            overflowCoeffRow.getChildren().clear();
        }

        // Manipulate the UI elements based on the currently selected mode
        if (Driver.MODE.equals("Manual Formula")) {
            dataSetBox.setVisible(true);
            clearButton.setVisible(true);

            clearButtonHandler(null);

            // Add the correct children to the main coefficient row
            setUpRow(0, Driver.STATS_HEADERS.get("1"), coeffRow);

            // If the selected year supports additional stats, use the overflow row
            if (Driver.DATA_SET_TO_SIZE.get(Driver.DATA_SET) > L1) {
                setUpRow(L1, Driver.STATS_HEADERS.get(Driver.DATA_SET), overflowCoeffRow);
            }

        } else if (Driver.MODE.equals("Select Formula")) {            
            formulaBox.setVisible(true);

            // Handle updating the formulas list
            if (!formulaChanged) {
                String lastFormula = Driver.FORMULA;
                List<String> validList = Arrays.asList(validFormulas);
                formulaDropdown.setItems(FXCollections.observableList(validList));
                if (lastFormula != null && validList.contains(lastFormula)) {
                    formulaDropdown.setValue(lastFormula);
                } else {
                    formulaDropdown.setValue(validFormulas[0]);
                }
            }

            // Set the Driver's data set based on the selected formula
            String lastDataSet = Driver.DATA_SET;
            String thisFormula = formulaDropdown.valueProperty().asString().getValue();
            Driver.DATA_SET = Character.toString(
                    thisFormula.charAt(thisFormula.indexOf("(") + 1));
            
            // Because update() will get called thrice unnecessarily
            coeffRow.getChildren().clear();
            overflowCoeffRow.getChildren().clear();

            // Add the correct children to the main coefficient row
            setUpRow(0, Driver.STATS_HEADERS.get("1"), coeffRow);

            // If the selected year supports additional stats, use the overflow row
            if (Driver.DATA_SET_TO_SIZE.get(Driver.DATA_SET) > L1 + 1) {
                setUpRow(L1, Driver.STATS_HEADERS.get(Driver.DATA_SET), overflowCoeffRow);
            }

            // Pass in the specified formula from the map
            double[] inputCoeff = concat(Arrays.copyOfRange(Driver.FORMULAS.get(
                    formulaDropdown.valueProperty().asString().getValue()), 0,
                    Driver.DATA_SET_TO_SIZE.get(Driver.DATA_SET) - 1), new double[]{0});

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
            
            // Reset the data set to what it was set to before this change
            Driver.DATA_SET = lastDataSet;
            
        } else if (Driver.MODE.equals("Generate Formula")) {
            dataSetBox.setVisible(true);
            trialsBox.setVisible(true);

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
            
            if (Driver.MODE.equals("Select Formula")
                    || Driver.MODE.equals("Generate Formula")) {
                field.setText("0");
                field.setEditable(false);
            } else {
                field.setText(removeTrailingZeros(
                        Double.toString(lastCoefficients[i])));
            }
            
            field.textProperty().addListener((obs, oldInput, newInput) -> {
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
        
        // Keep track of the data set value when the 'Go' button was pressed
        String lastDataSet = Driver.DATA_SET;
        
        // Read in the stats files for all possible data sets
        for (String dataSet: Driver.DATA_SET_TO_SIZE.keySet()) {
            Driver.DATA_SET = dataSet;
            Driver.convertExcel();
        }
        
        // Reset the data set to what it was set to before this button selection
        Driver.DATA_SET = lastDataSet;

        if (Driver.MODE.equals("High Seeds")) {
            BracketBuster bb = new BracketBuster(0);
            int score = bb.highSeed();

            // score = -1 if the current year does not have worths input
            if (score == -1) {
                scoreField.setText("N/A");
                new BracketVisual(bb.winnerPos, -1, bb.best, -1);
            } else {
                scoreField.setText(Integer.toString(score));
                new BracketVisual(bb.winnerPos, score, bb.best, -1);
            }

            scoreBox.setVisible(true);
            fileToGUI("bracket.txt");

        } else if (Driver.MODE.equals("Manual Formula")) {
            BracketBuster bb = new BracketBuster(0);
            storeCoeff();
            double[] inputCoeff = concat(Arrays.copyOfRange(
                    lastCoefficients, 0, Driver.DATA_SET_TO_SIZE.get(
                            Driver.DATA_SET) - 1), new double[]{0});

            int score = bb.score(inputCoeff);
            if (score != -1) {
                inputCoeff = (double[]) bb.trimCoeff(score, inputCoeff).get(1);
                score = bb.score(inputCoeff);
            }

            // Fill in the coefficient rows with the trimmed manual formula
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

            // score = -1 if the current year does not have worths input
            if (score == -1) {
                scoreField.setText("N/A");
                new BracketVisual(bb.winnerPos, -1, inputCoeff, -1);
            } else {
                scoreField.setText(Integer.toString(score));
                new BracketVisual(bb.winnerPos, score, inputCoeff, -1);
            }
            
            scoreBox.setVisible(true);
            fileToGUI("bracket.txt");

        } else if (Driver.MODE.equals("Select Formula")) {
            // Verify that the correct data set is selected
            String thisFormula = formulaDropdown.valueProperty().asString().getValue();
            Driver.DATA_SET = Character.toString(
                    thisFormula.charAt(thisFormula.indexOf("(") + 1));
            System.out.println(Driver.DATA_SET);
            
            BracketBuster bb = new BracketBuster(0);
            storeCoeff();
            
            // Pass in the specified formula from the map
            double[] inputCoeff = concat(
                    Arrays.copyOfRange(Driver.FORMULAS.get(thisFormula), 0,
                            Driver.DATA_SET_TO_SIZE.get(Driver.DATA_SET) - 1),
                    new double[]{0});
            System.out.println(inputCoeff.length);

            int score = bb.score(inputCoeff);
            scoreField.setText(Integer.toString(score));
            scoreBox.setVisible(true);
            
            new BracketVisual(bb.winnerPos, score, inputCoeff, -1);
            
            fileToGUI("bracket.txt");
            
            // Reset the data set to what it was set to before this button selection
            Driver.DATA_SET = lastDataSet;
            
        } else if (Driver.MODE.equals("Generate Formula")) {
            update(false);
            
            progressBox.setVisible(true);
            
            BracketBuster bb = new BracketBuster(
                    Integer.parseInt(trialsField.getText()));
            int score = bb.maxFind();
            
            storeCoeff();
            
            // Because update() will get called thrice unnecessarily
            coeffRow.getChildren().clear();
            overflowCoeffRow.getChildren().clear();
            
            // Add the correct children to the main coefficient row
            setUpRow(0, Driver.STATS_HEADERS.get("1"), coeffRow);

            // If the selected year supports additional stats, use the overflow row
            if (Driver.DATA_SET_TO_SIZE.get(Driver.DATA_SET) > L1) {
                setUpRow(L1, Driver.STATS_HEADERS.get(Driver.DATA_SET), overflowCoeffRow);
            }
            
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
            
            scoreBox.setVisible(true);
            
            fileToGUI("bracket.txt");
            
            progressBox.setVisible(false);
            
        } else if (Driver.MODE.equals("Actual Results")) {
            BracketBuster bb = new BracketBuster(0);
            bb.actualResults();
            new BracketVisual(bb.winnerPos, -1, bb.best, -1);

            fileToGUI("bracket.txt");
        } else if (Driver.MODE.equals("Run Formula Batch")) {
            progressBox.setVisible(true);

            // Create a folder to house all of the formula results in this batch
            new File("Applied to " + Driver.YEAR).mkdir();

            // Iterate through all of the valid formulas for the selected year
            Driver.MODE = "Select Formula";
            String[] validFormulas = Driver.VALID.get(Driver.YEAR);
            for (String thisFormulaName: validFormulas) {

                // Pass in the specified formula from the map
                double[] thisFormula = Driver.FORMULAS.get(thisFormulaName);
                double[] inputCoeff = concat(thisFormula, new double[]{0});

                // Update the global variables for this formula run
                Driver.DATA_SET = (String) (
                        thisFormulaName.split("\\(")[1]).substring(0, 1);
                Driver.FORMULA = thisFormulaName;

                // Score this formula and output the results to file
                BracketBuster bb = new BracketBuster(0);
                int score = bb.score(inputCoeff);
                System.out.println(thisFormulaName + " - " + score);
                new BracketVisual(bb.winnerPos, score, inputCoeff, -1);

                // Save this formula's results in the batch's folder
                outputFileNameField.setText(
                        "Applied to " + Driver.YEAR + "\\" + thisFormulaName);
                saveButtonHandler(null);
            }
            
            // Output a bracket for the actual results
            Driver.MODE = "Actual Results";
            BracketBuster bb = new BracketBuster(0);
            bb.actualResults();
            new BracketVisual(bb.winnerPos, -1, bb.best, -1);
            outputFileNameField.setText("Applied to " + Driver.YEAR + "\\Actual Results");
            saveButtonHandler(null);
            
            // Output a bracket for the high seeds
            Driver.MODE = "High Seeds";
            BracketBuster bb2 = new BracketBuster(0);
            int score = bb2.highSeed();

            // score = -1 if the current year does not have worths input
            if (score == -1) {
                new BracketVisual(bb2.winnerPos, -1, bb2.best, -1);
            } else {
                new BracketVisual(bb2.winnerPos, score, bb2.best, -1);
            }
            outputFileNameField.setText(
                        "Applied to " + Driver.YEAR + "\\High Seeds");
            saveButtonHandler(null);
            
            // Reset Driver's mode and hide progress box when completed
            Driver.MODE = "Run Formula Batch";
            progressBox.setVisible(false);
        }

        // All modes but 'Run Formula Batch' utilize the file name row
        if (!Driver.MODE.equals("Run Formula Batch")) {
            fileNameRow.setVisible(true);
        }
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