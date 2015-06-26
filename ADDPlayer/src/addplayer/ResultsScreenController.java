package addplayer;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

/**
 *
 * @author Ryan Burns
 */
public class ResultsScreenController implements Initializable {
    @FXML
    private Label playerField;
    @FXML
    private Label scoreField;
    @FXML
    private Button playAgainButton;
    @FXML
    private Button mainMenuButton;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        playerField.setText(ADDPlayer.PLAYER);
        scoreField.setText(Integer.toString(ADDPlayer.POINTS));
    }

    @FXML
    private void playAgainButtonAction(ActionEvent event) {
        // TO DO
    }

    @FXML
    private void mainMenuButtonAction(ActionEvent event) {
        // TO DO
    }
}