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
import javafx.scene.control.TextArea;
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
    private Label songPoints;
    @FXML
    private Label artistPoints;
    @FXML
    private Label albumPoints;
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
    private Label artistField;
    @FXML
    private Label albumField;
    @FXML
    private Slider volumeSlider;
    @FXML
    private AnchorPane previewPane;

    private MediaPlayer mediaPlayer;
    private int songLocation;
    //private boolean isPlaying = false;
    private int songsPlayed;
    private Random random;
    private HashMap<ToggleButton, ToggleButton> corrButtons;

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
        thisButton.setSelected(true);
        corrButtons.get(thisButton).setSelected(false);
        List<Node> children = thisButton.getParent().getChildrenUnmodifiable();
        Label thisLabel = null;
        for (Node node : children) {
            if (node instanceof Label
                    && ((Label) node).getText().equals("+1")) {
                thisLabel = (Label) node;
            }
        }
        if (thisLabel == null) {
            System.exit(1);
        }
        if (!thisLabel.isVisible()) {
            pointsField.setText(Integer.toString(
                    Integer.parseInt(pointsField.getText()) + 1));
            thisLabel.setVisible(true);
        }
    }

    @FXML
    private void incorrectButtonAction(ActionEvent event) {
        ToggleButton thisButton = (ToggleButton)event.getSource();
        thisButton.setSelected(true);
        corrButtons.get(thisButton).setSelected(false);
        List<Node> children = thisButton.getParent().getChildrenUnmodifiable();
        Label thisLabel = null;
        for (Node node : children) {
            if (node instanceof HBox) {
                for (Node child : ((HBox) node).getChildrenUnmodifiable()) {
                    if (child instanceof Label
                            && ((Label) child).getText().equals("+1")) {
                        thisLabel = (Label) child;
                    }
                }            
            }
        }
        if (thisLabel == null) {
            System.exit(1);
        }
        if (thisLabel.isVisible()) {
            pointsField.setText(Integer.toString(
                    Integer.parseInt(pointsField.getText()) - 1));
            thisLabel.setVisible(false);
        }
    }

    private void cycle() {
        // Stop the previous song (if this isn't the first song)
        if (mediaPlayer != null) {
            mediaPlayer.pause();
        }

        // Play the new song (until we hit the passed in number)
        if (songsPlayed < ADDPlayer.NUM_SONGS) {
            resetDisplay();
            SongDetails song = playNextSong();

            // Add the new song to the preview pane
            PreviewVBox songPreview = new PreviewVBox(song);
            songPreview.setLayoutY(800 / ADDPlayer.NUM_SONGS * songsPlayed);
            previewPane.getChildren().add(songPreview);

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
        songLocation = random.nextInt(ADDPlayer.LIBRARY.size() + 1);

        SongDetails song;
        if (ADDPlayer.MODE == 1) {
            // Create fake SongDetails objects from the metadata
            String location = (String) ADDPlayer.LIBRARY.get(songLocation);
            song = convertWithMetadata(location);
        } else {
            song = (SongDetails) ADDPlayer.LIBRARY.get(songLocation);
        }
        Media songFile = new Media(new File(song.location).toURI().toString());
        mediaPlayer = new MediaPlayer(songFile);
        mediaPlayer.play();
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
        songPoints.setVisible(false);
        artistPoints.setVisible(false);
        albumPoints.setVisible(false);
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
        setUpButtonMapping();
        songsPlayed = 0;
        random = new Random();
        librarySizeField.setText(Integer.toString(ADDPlayer.LIBRARY.size()));
        playerField.setText(ADDPlayer.PLAYER);
        cycle();
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.millis(ADDPlayer.SONG_LENGTH * 1000),
                        ae -> cycle()));
        timeline.setCycleCount(ADDPlayer.NUM_SONGS);
        timeline.play();
    }
}