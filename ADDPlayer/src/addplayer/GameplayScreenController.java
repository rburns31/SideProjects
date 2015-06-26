package addplayer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
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
import javafx.scene.control.ToggleButton;
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
    private Label playerField;
    @FXML
    private Label librarySizeField;
    @FXML
    private Label progressField;
    @FXML
    private Label pointsField;
    @FXML
    private Label songField;
    @FXML
    private Label artistField;
    @FXML
    private Label albumField;

    private MediaPlayer currentSong;
    private int songLocation;
    //private boolean isPlaying = false;
    private int songsPlayed;
    private Random random;

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
        toggle(thisButton);
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
        toggle(thisButton);
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

    private void toggle(ToggleButton button) {
        List<Node> children = button.getParent().getChildrenUnmodifiable();
        for (Node node : children) {
            if (node instanceof ToggleButton && !node.equals(button)) {
                ((ToggleButton)node).setSelected(false);
            }
        }
    }

    private void cycle() {
        // Stop the previous song (if this isn't the first song)
        if (currentSong != null) {
            currentSong.pause();
        }
        // Play the new song (until we hit the passed in number)
        if (songsPlayed < ADDPlayer.NUM_SONGS) {
            songLocation = random.nextInt(ADDPlayer.library.size() + 1);
            String songName = ADDPlayer.library.get(songLocation);
            Media test = new Media(new File(songName).toURI().toString());
            currentSong = new MediaPlayer(test);
            currentSong.play();
            displayMetadata(songName);
        } else {
            // Game is over, show end screen
            try {
                Parent root = FXMLLoader.load(
                        getClass().getResource("ResultsScreen.fxml"));
                Scene scene = new Scene(root);
                ADDPlayer.MAIN_STAGE.setScene(scene);
                ADDPlayer.MAIN_STAGE.show();
            } catch (IOException e) {
                System.exit(2);
            }
        }
        songsPlayed++;
    }

    private void displayMetadata(String song) {
        try {
            InputStream input = new FileInputStream(new File(song));
            Metadata metadata = new Metadata();
            new Mp3Parser().parse(
                    input, new DefaultHandler(), metadata, new ParseContext());
            input.close();

            // List all metadata
            //String[] metadataNames = metadata.names();
            //for (String name : metadataNames) {
            //    System.out.println(name + ": " + metadata.get(name));
            //}

            songField.setText(metadata.get("title"));
            artistField.setText(metadata.get("xmpDM:artist"));
            albumField.setText(metadata.get("xmpDM:album"));
        } catch (IOException | SAXException | TikaException e) {
            e.printStackTrace();
        }
    }
        
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        songsPlayed = 0;
        random = new Random();
        cycle();
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.millis(ADDPlayer.SONG_LENGTH * 1000),
                        ae -> cycle()));
        timeline.setCycleCount(ADDPlayer.NUM_SONGS);
        timeline.play();
    }
}