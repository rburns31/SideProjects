package bowlpicker;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

/**
 * The FXML controller for the opening screen of BowlPicker
 * Allows the user to enter their name, and then choose between the two screens
 * @author Ryan Burns
 */
public class OpeningScreenController implements Initializable {
    @FXML
    private TextField nameField;
    @FXML
    private Label errorField;

    private FadeTransition ft;

    /**
     * Sets up the fade transition and the header image
     * @param url not used
     * @param rb not used
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        ft = new FadeTransition(Duration.millis(1000), errorField);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.setAutoReverse(true);
        ft.setCycleCount(2);
    }

    /**
     * Does not allow the user to enter either screen without a valid name
     * @return Whether or not the currently entered name is valid
     */
    private boolean validateName() {
        if (!nameField.getText().matches("[A-Z][a-z]+ [A-Z][a-z]+")) {
            errorField.setText("Your name is not valid! Please use the "
                    + "format 'Firstname Lastname'");
            if (ft != null) {
                ft.play();
            }
            return false;
        }
        return true;
    }

    /**
     * If the name is valid then store it for future use and open the screen
     *     where the user can make their picks
     * @param e The clicking of the 'Make Picks' button
     */
    public void picksButtonAction(ActionEvent e) {
        if (validateName()) {
            Driver.playerName = nameField.getText();
            try {
                Pane myPane = (Pane)FXMLLoader.load(getClass().getResource(
                        "BowlPicker.fxml"));
                Scene myScene = new Scene(myPane);
                Driver.mainStage.setScene(myScene);
                Driver.mainStage.show();
            } catch (IOException ioe) {
                System.out.println("Couldn't load the bowl picking screen.");
            }
        }
    }
}