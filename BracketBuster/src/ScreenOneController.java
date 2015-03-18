import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.util.Arrays;
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
public class ScreenOneController implements Initializable {
    @FXML
    private ChoiceBox<String> yearDropdown;
    @FXML
    private ChoiceBox<String> modeDropdown;
    @FXML
    private HBox coeffRow;
    @FXML
    private HBox overflowCoeffRow;
    @FXML
    private HBox fileNameRow;
    @FXML
    private TextField trialsField;
    @FXML
    private Button goButton;
    @FXML
    private Button clearButton;
    @FXML
    private Button saveButton;
    @FXML
    private TextArea bracketField;
    @FXML
    private TextField scoreField;
    @FXML
    private TextField outputFileNameField;

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
        bracketField.setFont(Font.font(java.awt.Font.MONOSPACED, 13.5));
        yearDropdown.getItems().addAll("2010", "2011", "2012(1)", "2012(2)",
                    "2013", "2014(1)", "2014(3)", "2015(1)", "2015(3)");
        yearDropdown.setValue("2015(3)");
        modeDropdown.getItems().addAll("High Seeds", "Manual Formula");
        modeDropdown.setValue("High Seeds");
        Driver.YEAR = (String)yearDropdown.getValue();
        MODE = (String)modeDropdown.getValue();
        lastCoefficients = new double[20];
        yearUpdate();
        modeUpdate();
        yearDropdown.valueProperty().addListener((observable, old, newYear) -> {
            Driver.YEAR = newYear;
            yearUpdate();
        });
        modeDropdown.valueProperty().addListener((observable, old, newMode) -> {
            MODE = newMode;
            modeUpdate();
        });
        trialsField.textProperty().addListener((observable, oldInput, newInput) -> {
            try {
                if (!trialsField.getText().equals("N/A")) {
                    Integer.parseInt(newInput);
                    trialsField.setText(newInput.replaceAll("[d,f,i]", ""));
                }
            } catch (Exception e) {
                trialsField.setText(oldInput);
            }
        });
    }

    /**
     * Handles updating all parts of the form when the year selection is changed
     */
    private void yearUpdate() {
        fileNameRow.setVisible(false);
        storeCoeff();
        // Clear out the rows before re-adding the correct children
        coeffRow.getChildren().clear();
        overflowCoeffRow.getChildren().clear();
        bracketField.clear();
        scoreField.clear();
        clearButton.setVisible(false);
        // If the program does not support the selected year, don't show anything
        if (!BracketBuster.YEAR_TO_SIZE.containsKey(Driver.YEAR)) {
            System.out.println("No support for the selected year!");
            goButton.setVisible(false);
            return;
        }
        // Since the selected year is supported, show the buttons
        goButton.setVisible(true);
        // If the program is in "High Seeds" mode, do not fill in the rows
        if (MODE.equals("High Seeds")) {
            return;
        }
        clearButton.setVisible(true);
        // Add the correct children to the main coefficient row
        for (int i = 0; i < statsOne.length; i++) {
            VBox entry = new VBox();
            Label label = new Label(statsOne[i]);
            String s = Double.toString(lastCoefficients[i]);
            s = s.indexOf(".") < 0 ? s : s.replaceAll("0*$", "").replaceAll("\\.$", "");
            TextField field = new TextField(s);
            field.textProperty().addListener((observable, oldInput, newInput) -> {
                try {
                    Double.parseDouble(newInput);
                    field.setText(newInput.replaceAll("[d,f]", ""));
                } catch (Exception e) {
                    field.setText(oldInput);
                }
            });
            entry.getChildren().addAll(label, field);
            coeffRow.getChildren().add(entry);
        }
        // If the selected year supports additional statistics, fill in the overflow row
        if (BracketBuster.YEAR_TO_SIZE.get(Driver.YEAR) == 21) {
            for (int i = statsOne.length; i < statsTwo.length; i++) {
                VBox entry = new VBox();
                entry.setPrefWidth(82);
                Label label = new Label(statsTwo[i]);
                String s = Double.toString(lastCoefficients[i]);
                s = s.indexOf(".") < 0 ? s : s.replaceAll("0*$", "").replaceAll("\\.$", "");
                TextField field = new TextField(s);
                field.textProperty().addListener((observable, oldInput, newInput) -> {
                    try {
                        Double.parseDouble(newInput);
                        field.setText(newInput.replaceAll("[d,f]", ""));
                    } catch (Exception e) {
                        field.setText(oldInput);
                    }
                });
                entry.getChildren().addAll(label, field);
                overflowCoeffRow.getChildren().add(entry);
            }
        } else if (BracketBuster.YEAR_TO_SIZE.get(Driver.YEAR) == 20) {
            for (int i = statsOne.length; i < statsThree.length; i++) {
                VBox entry = new VBox();
                entry.setPrefWidth(82);
                Label label = new Label(statsThree[i]);
                String s = Double.toString(lastCoefficients[i]);
                s = s.indexOf(".") < 0 ? s : s.replaceAll("0*$", "").replaceAll("\\.$", "");
                TextField field = new TextField(s);
                field.textProperty().addListener((observable, oldInput, newInput) -> {
                    try {
                        Double.parseDouble(newInput);
                        field.setText(newInput.replaceAll("[d,f]", ""));
                    } catch (Exception e) {
                        field.setText(oldInput);
                    }
                });
                entry.getChildren().addAll(label, field);
                overflowCoeffRow.getChildren().add(entry);
            }
        }
    }

    /**
     * Handles updating all parts of the form when the mode selection is changed
     */
    private void modeUpdate() {
        fileNameRow.setVisible(false);
        storeCoeff();
        yearUpdate();
        bracketField.clear();
        scoreField.clear();
        if (MODE.equals("High Seeds")) {
            trialsField.setText("N/A");
            trialsField.setEditable(false);
        } else if (MODE.equals("Manual Formula")) {
            trialsField.setText("1");
            trialsField.setEditable(false);
        }
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
        if (MODE.equals("High Seeds")) {
            BracketBuster bb = new BracketBuster(0);
            FileConverter fc1 = new FileConverter();
            fc1.convert();
            int score = bb.highSeed();
            scoreField.setText(Integer.toString(score));
            BracketVisual bv = new BracketVisual(
                    bb.getWinnerPos(), Driver.getTime(), false);
            fileToGUI("bracket.txt");
        } else if (MODE.equals("Manual Formula")) {
            BracketBuster bb = new BracketBuster(0);
            FileConverter fc1 = new FileConverter();
            fc1.convert();
            storeCoeff();
            double[] zeroArr = {0};
            double[] inputCoeff = concat(
                    Arrays.copyOfRange(lastCoefficients, 0, BracketBuster.YEAR_TO_SIZE.get(Driver.YEAR) - 1), zeroArr);
            int score = bb.score(inputCoeff);
            scoreField.setText(Integer.toString(score));
            BracketVisual bv = new BracketVisual(
                    bb.getWinnerPos(), Driver.getTime(), false);
            fileToGUI("bracket.txt");
        }
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
                if (i > 2) {
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