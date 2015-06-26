package addplayer;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;

/**
 * 
 * @author Ryan Burns
 */
public class OpeningScreenController implements Initializable {
    @FXML
    private ChoiceBox numSongsChoice;
    @FXML
    private ChoiceBox songLengthChoice;
    @FXML
    private TextField pathField;
    @FXML
    private TextField playerNameField;

    @FXML
    private void startButtonAction(ActionEvent event) throws Exception {
        if (isPathValid() && !playerNameField.getText().equals("")) {
            ADDPlayer.PLAYER = playerNameField.getText();
            ADDPlayer.walk(pathField.getText());
            ADDPlayer.NUM_SONGS = Integer.parseInt(
                    numSongsChoice.getValue().toString());
            ADDPlayer.SONG_LENGTH = Integer.parseInt(
                    songLengthChoice.getValue().toString());
            Parent root = FXMLLoader.load(
                    getClass().getResource("GameplayScreen.fxml"));
            Scene scene = new Scene(root);
            ADDPlayer.MAIN_STAGE.setScene(scene);
            ADDPlayer.MAIN_STAGE.show();
        } else {
            // Wait for the user to input a valid path and player name
            
        }
    }

    private boolean isPathValid() {
        
        return true;
    }

    @FXML
    private void browseButtonAction(ActionEvent event) {
        try {
            Runtime.getRuntime().exec("explorer.exe /select,C:/");
        } catch (IOException e) {
            System.out.println("Error attrempting to open Windows Explorer.");
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        numSongsChoice.setItems(FXCollections.observableArrayList(
                "1", "5", "10", "15", "20"));
        numSongsChoice.setValue("5");
        songLengthChoice.setItems(FXCollections.observableArrayList(
                "5", "10", "15", "20", "30"));
        songLengthChoice.setValue("5");
    }
}