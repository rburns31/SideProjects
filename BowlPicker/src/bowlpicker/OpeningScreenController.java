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
 * 
 * @author Ryan Burns
 */
public class OpeningScreenController implements Initializable {
    @FXML
    private TextField nameField;
    @FXML
    private TextField emailField;
    @FXML
    private Label errorField;
    
    private FadeTransition ft;

    public void confirmButtonAction(ActionEvent e) {
        if (!nameField.getText().matches("[A-Z][a-z]* [A-Z][a-z]*")) {
            errorField.setText("Your name is not valid!");
            if (ft != null) {
                ft.play();
            }
        } else if (!emailField.getText().matches("([a-z]|[A-Z])([a-z]|[A-Z]|\\d|\\_|\\-|\\.)*@"
                + "([a-z]|[A-Z])([a-z]|[A-Z]|\\d|\\_|\\-|\\.)*\\.(com|org|net|edu)")) {
            errorField.setText("Your email address is not valid!");
            if (ft != null) {
                ft.play();
            }
        } else {
            try {
                Pane myPane = (Pane)FXMLLoader.load(getClass().getResource("BowlPicker.fxml"));
                Scene myScene = new Scene(myPane);
                Driver.mainStage.setScene(myScene);
                Driver.mainStage.show();
            } catch (IOException ioe) {
                System.out.println(ioe.getMessage());
            }
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ft = new FadeTransition(Duration.millis(1000), errorField);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.setAutoReverse(true);
        ft.setCycleCount(2);
    }
}