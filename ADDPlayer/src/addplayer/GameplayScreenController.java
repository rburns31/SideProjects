package addplayer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.mp3.Mp3Parser;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * 
 * @author Ryan Burns
 */
public class GameplayScreenController implements Initializable {
    @FXML
    private ToggleButton songCorrect;
    @FXML
    private ToggleButton artistCorrect;
    @FXML
    private ToggleButton albumCorrect;
    @FXML
    private ToggleButton songIncorrect;
    @FXML
    private ToggleButton artistIncorrect;
    @FXML
    private ToggleButton albumIncorrect;
    @FXML
    private Label playerField;
    @FXML
    private Label librarySizeField;
    @FXML
    private Label progressField;
    @FXML
    private Label pointsField;
    @FXML
    private TextField songField;
    @FXML
    private TextField artistField;
    @FXML
    private TextField albumField;
    @FXML
    private Slider volumeSlider;
    @FXML
    public AnchorPane previewPane;

    private MediaPlayer mediaPlayer;
    private boolean isPlaying = false;
    private int songsPlayed;
    private HashMap<ToggleButton, ToggleButton> corrButtons;
    private int[] songsInRound;
    private PreviewHBox[] previewBoxes;

    @FXML
    private void playPauseButtonAction(ActionEvent event) {
        /**System.out.println(currentSong.currentTimeProperty());
        if (!isPlaying) {
            currentSong.play();
            isPlaying = true;
        } else {
            currentSong.pause();
            isPlaying = false;
        }*/
    }

    @FXML
    private void correctButtonAction(ActionEvent event) {
        ToggleButton thisButton = (ToggleButton)event.getSource();
        if (thisButton.isSelected()) {
            pointsField.setText(Integer.toString(
                    Integer.parseInt(pointsField.getText()) + 1));

            // Update the correct color in the preview pane
            if (thisButton.getId().equals("songCorrect")) {
               previewBoxes[songsPlayed - 1].songColor.getStyleClass()
                       .add("greenlabel");
            } else if (thisButton.getId().equals("artistCorrect")) {
                previewBoxes[songsPlayed - 1].artistColor.getStyleClass()
                        .add("greenlabel");
            } else if (thisButton.getId().equals("albumCorrect")) {
                previewBoxes[songsPlayed - 1].albumColor.getStyleClass()
                        .add("greenlabel");
            }
        }
        thisButton.setSelected(true);
        corrButtons.get(thisButton).setSelected(false);
    }

    @FXML
    private void incorrectButtonAction(ActionEvent event) {
        ToggleButton thisButton = (ToggleButton)event.getSource();
        if (thisButton.isSelected()) {
            pointsField.setText(Integer.toString(
                    Integer.parseInt(pointsField.getText()) - 1));

            // Update the correct color in the preview pane
            if (thisButton.getId().equals("songIncorrect")) {
               previewBoxes[songsPlayed - 1].songColor.getStyleClass()
                       .remove("greenlabel");
            } else if (thisButton.getId().equals("artistIncorrect")) {
                previewBoxes[songsPlayed - 1].artistColor.getStyleClass()
                        .remove("greenlabel");
            } else if (thisButton.getId().equals("albumIncorrect")) {
                previewBoxes[songsPlayed - 1].albumColor.getStyleClass()
                        .remove("greenlabel");
            }
        }
        thisButton.setSelected(true);
        corrButtons.get(thisButton).setSelected(false);
    }

    private void cycle() {
        // Stop the previous song (if this isn't the first song)
        if (mediaPlayer != null) {
            mediaPlayer.pause();
        }

        // Play the new song (until we hit the passed in number)
        if (songsPlayed < songsInRound.length) {
            resetDisplay();
            SongDetails song = playNextSong();

            // Set the text fields on this screen with the new song info
            songField.setText(song.name);
            artistField.setText(song.artist);
            albumField.setText(song.album);
        } else {
            gameOver();
        }
        songsPlayed++;
        progressField.setText(Integer.toString(songsPlayed) + "/"
                + Integer.toString(ADDPlayer.NUM_SONGS));
    }

    private SongDetails playNextSong() {
        SongDetails song = packageIntoSongDetails();

        Media songFile = new Media(new File(song.location).toURI().toString());
        mediaPlayer = new MediaPlayer(songFile);
        mediaPlayer.play();
        return song;
    }

    private SongDetails packageIntoSongDetails() {
        SongDetails song;
        if (ADDPlayer.MODE == 1) {
            // Create fake SongDetails objects from the metadata
            String location = (String) ADDPlayer.LIBRARY.get(
                    songsInRound[songsPlayed]);
            song = convertWithMetadata(location);
        } else {
            song = (SongDetails) ADDPlayer.LIBRARY.get(
                    songsInRound[songsPlayed]);
        }
        return song;
    }

    /**
     * Game is over, set the final points value and show the end screen
     */
    private void gameOver() {
        ADDPlayer.POINTS = Integer.parseInt(pointsField.getText());
        try {
            Parent root = FXMLLoader.load(
                    getClass().getResource("ResultsScreen.fxml"));
            Scene scene = new Scene(root);
            ADDPlayer.MAIN_STAGE.setScene(scene);
            ADDPlayer.MAIN_STAGE.show();
        } catch (IOException e) {
            System.exit(1);
        }
    }

    private void resetDisplay() {
        if (songCorrect.isSelected()) {
            songCorrect.setSelected(false);
            songIncorrect.setSelected(true);
        }
        if (artistCorrect.isSelected()) {
            artistCorrect.setSelected(false);
            artistIncorrect.setSelected(true);
        }
        if (albumCorrect.isSelected()) {
            albumCorrect.setSelected(false);
            albumIncorrect.setSelected(true);
        }
    }

    private SongDetails convertWithMetadata(String location) {
        try {
            InputStream input = new FileInputStream(new File(location));
            Metadata metadata = new Metadata();
            new Mp3Parser().parse(
                    input, new DefaultHandler(), metadata, new ParseContext());
            input.close();

            return new SongDetails(metadata.get("title"),
                    metadata.get("xmpDM:artist"), metadata.get("xmpDM:album"), 
                    null, null, location);
        } catch (IOException | SAXException | TikaException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void setUpButtonMapping() {
        corrButtons = new HashMap<>();
        corrButtons.put(songCorrect, songIncorrect);
        corrButtons.put(songIncorrect, songCorrect);
        corrButtons.put(artistCorrect, artistIncorrect);
        corrButtons.put(artistIncorrect, artistCorrect);
        corrButtons.put(albumCorrect, albumIncorrect);
        corrButtons.put(albumIncorrect, albumCorrect);
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //songField.setStyle("-fx-focus-color: transparent;");
        setUpButtonMapping();
        songsPlayed = 0;
        Random random = new Random();
        librarySizeField.setText(Integer.toString(ADDPlayer.LIBRARY.size()));
        playerField.setText(ADDPlayer.PLAYER);
        songsInRound = new int[ADDPlayer.NUM_SONGS];
        previewBoxes = new PreviewHBox[ADDPlayer.NUM_SONGS];
        for (int i = 0; i < songsInRound.length; i++) {
            songsInRound[i] = random.nextInt(ADDPlayer.LIBRARY.size() + 1);

            // Add the next song to the preview pane
            SongDetails song = packageIntoSongDetails();
            PreviewHBox songPreview = new PreviewHBox(
                    song, 800 / ADDPlayer.NUM_SONGS);
            previewBoxes[i] = songPreview;
            songPreview.setLayoutY(800 / ADDPlayer.NUM_SONGS * i);
            previewPane.getChildren().add(songPreview);
            songsPlayed++;
        }
        songsPlayed = 0;
        cycle();
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.millis(ADDPlayer.SONG_LENGTH * 1000),
                        ae -> cycle()));
        timeline.setCycleCount(ADDPlayer.NUM_SONGS);
        timeline.play();
    }
}