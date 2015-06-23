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
import javafx.scene.control.TextField;

/**
 * 
 * @author Ryan Burns
 */
public class OpeningScreenController implements Initializable {
    @FXML
    private TextField numSongsField;
    @FXML
    private TextField songLengthField;

    private String path;

    @FXML
    private void startButtonAction(ActionEvent event) throws Exception {
        if (isFieldValid(numSongsField) && isFieldValid(songLengthField) && isPathValid()) {
            ADDPlayer.walk(path);
            ADDPlayer.NUM_SONGS = Integer.parseInt(numSongsField.getText());
            ADDPlayer.SONG_LENGTH = Integer.parseInt(songLengthField.getText());
            Parent root = FXMLLoader.load(getClass().getResource("GameplayScreen.fxml"));
            Scene scene = new Scene(root);
            ADDPlayer.mainStage.setScene(scene);
            ADDPlayer.mainStage.show();
        } else {
            // Wait for the user to input both fields and a valid path
            
        }
    }

    private boolean isFieldValid(TextField thisField) {
        return thisField.getText().matches("^\\d+$")
                && !thisField.getText().equals("");
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
        
    }
}