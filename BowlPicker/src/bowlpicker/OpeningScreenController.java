package bowlpicker;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.util.Duration;

public class OpeningScreenController implements Initializable, ControlledScreen {
    @FXML
    private Label nameField;
    @FXML
    private Label emailField;
    @FXML
    private Label errorField;
    
    private ScreensController parentController;
    private FadeTransition ft;

    public void confirmButtonAction(ActionEvent e) {
        if (nameField.getText().matches("[A-Z][a-z]* [A-Z][a-z]*")
            && emailField.getText().matches("([a-z]|[A-Z])([a-z]|[A-Z]|\\d|\\_|\\-|\\.)*@"
                + "([a-z]|[A-Z])([a-z]|[A-Z]|\\d|\\_|\\-|\\.)*\\.(com|org|net|edu)")) {
            //parentController.setScreen("BowlPickerController");
        }
        errorField.setText("Your information is not valid!");
        if (ft != null) {
            ft.play();
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

    @Override
    public void setScreenParent(ScreensController parentController) {
        this.parentController = parentController;
    }
}