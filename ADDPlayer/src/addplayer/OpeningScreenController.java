package addplayer;

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
 * The opening screen of ADDPlayer
 * @author Ryan Burns
 */
public class OpeningScreenController implements Initializable {
    @FXML
    private ChoiceBox numSongsChoice;
    @FXML
    private ChoiceBox songLengthChoice;
    @FXML
    private ChoiceBox libraryInputChoice1;
    @FXML
    private ChoiceBox libraryInputChoice2;
    //@FXML
    //private TextField pathField1;
    //@FXML
    //private TextField pathField2;
    @FXML
    private TextField player1NameField;
    @FXML
    private TextField player2NameField;

    /**
     * Sets the options and default values for the three drop-down choices
     *     that the user has on this screen
     * @param url Not used
     * @param rb Not used
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        numSongsChoice.setItems(FXCollections.observableArrayList(
                "1", "5", "10", "15", "20"));
        numSongsChoice.setValue("1");

        /**
         * TODO: Remove 1 as an option here, it's just for debugging
         */
        songLengthChoice.setItems(FXCollections.observableArrayList(
                "1", "5", "10", "15", "20", "30"));
        songLengthChoice.setValue("1");

        libraryInputChoice1.setItems(FXCollections.observableArrayList(
                "Exported iTunes playlist", "Folder on hard drive"));
        libraryInputChoice1.setValue("Exported iTunes playlist");

        libraryInputChoice2.setItems(FXCollections.observableArrayList(
                "Exported iTunes playlist", "Folder on hard drive"));
        libraryInputChoice2.setValue("Exported iTunes playlist");
    }

    /**
     * 
     * @param libraryChoice
     * @param player 
     */
    private void populateLibrary(ChoiceBox libraryChoice, Player player) {
        if (libraryChoice.getValue().toString().equals(
                "Exported iTunes playlist")) {

            player.mode = 0;
            ADDPlayer.readInPlaylist("Mutual.txt", player);
        } else {
            player.mode = 1;
            player.library = new ArrayList<>();
            ADDPlayer.walk("E:\\Users\\Ryan\\Music\\Library", player);
        }
    }

    /**
     * 
     * @param event Not used
     * @throws Exception 
     */
    @FXML
    private void startButtonAction(ActionEvent event) throws Exception {
        if (!player1NameField.getText().equals("")
                && !player2NameField.getText().equals("")) {

            // If the inputs are valid, populate the selected values
            ADDPlayer.CUR_PLAYER.name = player1NameField.getText();
            ADDPlayer.OTHER_PLAYER.name = player2NameField.getText();
            ADDPlayer.NUM_SONGS = Integer.parseInt(
                    numSongsChoice.getValue().toString());
            ADDPlayer.SONG_LENGTH = Integer.parseInt(
                    songLengthChoice.getValue().toString());

            // Populate the libraries according to the library input choice
            populateLibrary(libraryInputChoice1, ADDPlayer.CUR_PLAYER);
            populateLibrary(libraryInputChoice2, ADDPlayer.OTHER_PLAYER);

            // Launch the gameplay screen
            Parent root = FXMLLoader.load(
                    getClass().getResource("GameplayScreen.fxml"));
            Scene scene = new Scene(root);
            ADDPlayer.MAIN_STAGE.setScene(scene);
            ADDPlayer.MAIN_STAGE.show();
        } else {
            // Wait for the user to input valid player names
            
        }
    }

    /**
     * 
     * @param event Not used
     */
    @FXML
    private void browseButtonAction(ActionEvent event) {
        /**try {
            Runtime.getRuntime().exec("explorer.exe /select,C:/");
        } catch (IOException e) {
            System.out.println("Error attempting to open Windows Explorer.");
        }*/
    }
}