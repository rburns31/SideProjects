package addplayer;

import static addplayer.ADDPlayer.LIBRARY;
import java.net.URL;
import java.util.ArrayList;
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
    private ChoiceBox libraryInputChoice;
    //@FXML
    //private TextField pathField;
    @FXML
    private TextField playerNameField;

    /**
     * 
     * @param event
     * @throws Exception 
     */
    @FXML
    private void startButtonAction(ActionEvent event) throws Exception {
        if (!playerNameField.getText().equals("")) {
            // If the inputs are valid, populate the selected values
            ADDPlayer.PLAYER = playerNameField.getText();
            ADDPlayer.NUM_SONGS = Integer.parseInt(
                    numSongsChoice.getValue().toString());
            ADDPlayer.SONG_LENGTH = Integer.parseInt(
                    songLengthChoice.getValue().toString());

            // Populate the library according to the library input choice
            if (libraryInputChoice.getValue().toString().equals(
                    "Exported iTunes playlist")) {
                ADDPlayer.MODE = 0;
                //ADDPlayer.readInPlaylist("Music.txt");
                ADDPlayer.readInPlaylist("Mutual.txt");
            } else {
                ADDPlayer.MODE = 1;
                LIBRARY = new ArrayList<String>();
                ADDPlayer.walk("E:\\Users\\Ryan\\Music\\Library");
            }

            // Launch the gameplay screen
            Parent root = FXMLLoader.load(
                    getClass().getResource("GameplayScreen.fxml"));
            Scene scene = new Scene(root);
            ADDPlayer.MAIN_STAGE.setScene(scene);
            ADDPlayer.MAIN_STAGE.show();
        } else {
            // Wait for the user to input a valid player name
            
        }
    }

    /**
     * 
     * @param event 
     */
    @FXML
    private void browseButtonAction(ActionEvent event) {
        /**try {
            Runtime.getRuntime().exec("explorer.exe /select,C:/");
        } catch (IOException e) {
            System.out.println("Error attempting to open Windows Explorer.");
        }*/
    }

    /**
     * 
     * @param url
     * @param rb 
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        numSongsChoice.setItems(FXCollections.observableArrayList(
                "1", "5", "10", "15", "20"));
        numSongsChoice.setValue("5");
        songLengthChoice.setItems(FXCollections.observableArrayList(
                "5", "10", "15", "20", "30"));
        songLengthChoice.setValue("5");
        libraryInputChoice.setItems(FXCollections.observableArrayList(
                "Exported iTunes playlist", "Folder on hard drive"));
        libraryInputChoice.setValue("Exported iTunes playlist");
    }
}