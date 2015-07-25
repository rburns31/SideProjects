package addplayer;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

/**
 *
 * @author Ryan Burns
 */
public class ResultsScreenController implements Initializable {
    @FXML
    private Label playerField;
    @FXML
    private Label pointsField;
    @FXML
    private Button playAgainButton;
    @FXML
    private Button mainMenuButton;
    @FXML
    private AnchorPane previewPane;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        playerField.setText(ADDPlayer.PLAYER);
        pointsField.setText(Integer.toString(ADDPlayer.POINTS));

        ColoredLabelClickHandler handler =
                new ColoredLabelClickHandler(pointsField);
        for (PreviewHBox previewBox : ADDPlayer.PREVIEW_BOXES) {
            previewBox.songColor.setOnMouseClicked(handler);
            previewBox.artistColor.setOnMouseClicked(handler);
            previewBox.albumColor.setOnMouseClicked(handler);
            previewPane.getChildren().add(previewBox);
        }
    }

    @FXML
    private void playAgainButtonAction(ActionEvent event) throws IOException {
        // TO DO
        Parent root = FXMLLoader.load(
                getClass().getResource("GameplayScreen.fxml"));
        Scene scene = new Scene(root);
        ADDPlayer.MAIN_STAGE.setScene(scene);
        ADDPlayer.MAIN_STAGE.show();
    }

    @FXML
    private void mainMenuButtonAction(ActionEvent event) throws IOException {
        // TO DO
        Parent root = FXMLLoader.load(
                getClass().getResource("OpeningScreen.fxml"));
        Scene scene = new Scene(root);
        ADDPlayer.MAIN_STAGE.setScene(scene);
        ADDPlayer.MAIN_STAGE.show();
    }
}