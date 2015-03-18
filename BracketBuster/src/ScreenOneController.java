import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Scanner;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;


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
    private TextField trialsField;
    @FXML
    private Button goButton;
    @FXML
    private Button clearButton;
    @FXML
    private TextField bracketField;
    @FXML
    private TextField scoreField;

    /**
     * 
     */
    public static String MODE;
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
        yearDropdown.getItems().addAll("2010", "2011", "2012(1)", "2012(2)",
                    "2013", "2014(1)", "2014(3)", "2015(1)", "2015(3)");
        yearDropdown.setValue("2015(3)");
        modeDropdown.getItems().addAll("High Seeds", "Manual Formula");
        modeDropdown.setValue("High Seeds");
        Driver.YEAR = (String)yearDropdown.getValue();
        MODE = (String)modeDropdown.getValue();
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
    }

    /**
     * Handles updating all parts of the form when the year selection is changed
     */
    private void yearUpdate() {
        // Clear out the rows before re-adding the correct children
        coeffRow.getChildren().clear();
        overflowCoeffRow.getChildren().clear();
        bracketField.clear();
        scoreField.clear();
        // If the program does not support the selected year, don't show anything
        if (!BracketBuster.YEAR_TO_SIZE.containsKey(Driver.YEAR)) {
            System.out.println("No support for the selected year!");
            goButton.setVisible(false);
            return;
        }
        // Since the selected year is supported, show the "Go!" button
        goButton.setVisible(true);
        // If the program is in "High Seeds" mode, do not fill in the rows
        if (MODE.equals("High Seeds")) {
            return;
        }
        // Add the correct children to the main coefficient row
        for (int i = 0; i < statsOne.length; i++) {
            VBox entry = new VBox();
            Label label = new Label(statsOne[i]);
            TextField field = new TextField("0");
            entry.getChildren().addAll(label, field);
            coeffRow.getChildren().add(entry);
        }
        // If the selected year supports additional statistics, fill in the overflow row
        if (BracketBuster.YEAR_TO_SIZE.get(Driver.YEAR) == 21) {
            for (int i = statsOne.length; i < statsTwo.length; i++) {
                VBox entry = new VBox();
                entry.setPrefWidth(82);
                Label label = new Label(statsTwo[i]);
                TextField field = new TextField("0");
                entry.getChildren().addAll(label, field);
                overflowCoeffRow.getChildren().add(entry);
            }
        } else if (BracketBuster.YEAR_TO_SIZE.get(Driver.YEAR) == 20) {
            for (int i = statsOne.length; i < statsThree.length; i++) {
                VBox entry = new VBox();
                entry.setPrefWidth(82);
                Label label = new Label(statsThree[i]);
                TextField field = new TextField("0");
                entry.getChildren().addAll(label, field);
                overflowCoeffRow.getChildren().add(entry);
            }
        }
    }

    /**
     * Handles updating all parts of the form when the mode selection is changed
     */
    private void modeUpdate() {
        yearUpdate();
        bracketField.clear();
        scoreField.clear();
        if (MODE.equals("High Seeds")) {
            trialsField.setText("N/A");
            trialsField.setEditable(false);
        } else if (MODE.equals("Manual Formula")) {
            trialsField.setText("0");
            trialsField.setEditable(true);
        }
    }

    @FXML
    private void goButtonHandler(ActionEvent event) {
        if (MODE.equals("High Seeds")) {
            BracketBuster bb = new BracketBuster(0);
            int score = bb.highSeed();
            scoreField.setText(Integer.toString(score));
            BracketVisual bv = new BracketVisual(
                    bb.getWinnerPos(), Driver.getTime(), false);
            fileToGUI("bracket.txt");
        }
    }

    @FXML
    private void clearButtonHandler(ActionEvent event) {
        Object[] objs = coeffRow.getChildren().toArray();
        VBox[] vboxes = new VBox[objs.length];
        String[] coefficients = new String[vboxes.length];
        for (int i = 0; i < objs.length; i++) {
            vboxes[i] = (VBox)objs[i];
        }
        for (int i = 0; i < objs.length; i++) {
            coefficients[i] = ((TextField)vboxes[i].getChildren().toArray()[1]).getText();
        }
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
            Scanner fileScanner = new Scanner(dataFile);
            while (fileScanner.hasNext()) {
                bracketField.appendText(fileScanner.next());
            }
            fileScanner.close();
        } catch (IOException e) {
            System.out.println("Bracket file does not exist, exiting.");
            System.exit(0);
        }
    }
}