package bowlpicker;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.text.Font;

/**
 * The FXML controller for the closing screen of BowlPicker
 * Outputs the picks and instructs the user how to submit them
 * @author Ryan Burns
 */
public class ClosingScreenController implements Initializable {
    @FXML
    private Label finalMsg;
    @FXML
    private TextField outputField;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        finalMsg.setText("To submit your picks you will need to copy and "
                + "paste *exactly* the string below this message into the "
                + "Groupme chat. Thanks and good luck!");
        outputField.setText("!submit " + Driver.output);
    }
}