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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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
    @FXML
    private ImageView headerImageView;

    private FadeTransition ft;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ft = new FadeTransition(Duration.millis(1000), errorField);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.setAutoReverse(true);
        ft.setCycleCount(2);
        try {
            headerImageView.setImage(new Image("cfbicons/openingHeader.png"));
        } catch (Exception e) {
            System.out.println("Couldn't find the header image.");
        }
    }

    private boolean validateEntries() {
        if (!nameField.getText().matches("[A-Z][a-z]+ [A-Z][a-z]+")) {
            errorField.setText("Your name is not valid!");
            if (ft != null) {
                ft.play();
            }
            return false;
        } else if (!emailField.getText().matches("([a-z]|[A-Z])"
                + "([a-z]|[A-Z]|\\d|\\_|\\-|\\.)*@([a-z]|[A-Z])"
                + "([a-z]|[A-Z]|\\d|\\_|\\-|\\.)*\\.(com|org|net|edu)")) {
            errorField.setText("Your email address is not valid!");
            if (ft != null) {
                ft.play();
            }
            return false;
        }
        return true;
    }

    public void picksButtonAction(ActionEvent e) {
        if (validateEntries()) {
            try {
                Pane myPane = (Pane)FXMLLoader.load(getClass().getResource(
                        "BowlPicker.fxml"));
                Scene myScene = new Scene(myPane);
                Driver.mainStage.setScene(myScene);
                Driver.mainStage.show();
            } catch (IOException ioe) {
                System.out.println("Couldn't load the bowl picking screen.");
                ioe.printStackTrace();
            }
        }
    }

    public void standingsButtonAction(ActionEvent e) {
        if (validateEntries()) {
            // Change screen to the track standings page
            errorField.setText("Not currently supported.");
            if (ft != null) {
                ft.play();
            }
        }
    }
}