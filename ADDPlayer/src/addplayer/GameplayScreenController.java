package addplayer;

import java.io.File;
import java.io.IOException;
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
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

/**
 * 
 * @author Ryan Burns
 */
public class GameplayScreenController implements Initializable {
    @FXML
    private Label fileNameField;
    @FXML
    private Label pointsField;

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
        Label thisLabel = getThisLabel(thisButton);
        if (thisLabel == null) {
            System.exit(1);
        }
        if (!thisLabel.isVisible()) {
            pointsField.setText(Integer.toString(Integer.parseInt(pointsField.getText()) + 1));
            thisLabel.setVisible(true);
        }
    }

    @FXML
    private void incorrectButtonAction(ActionEvent event) {
        ToggleButton thisButton = (ToggleButton)event.getSource();
        toggle(thisButton);
        Label thisLabel = getThisLabel(thisButton);
        if (thisLabel == null) {
            System.exit(1);
        }
        if (thisLabel.isVisible()) {
            pointsField.setText(Integer.toString(Integer.parseInt(pointsField.getText()) - 1));
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

    private Label getThisLabel(ToggleButton button) {
        List<Node> children = button.getParent().getChildrenUnmodifiable();
        for (Node node : children) {
            if (node instanceof Label && ((Label)node).getText().equals("+1")) {
                return (Label)node;
            }
        }
        return null;
    }

    private void cycle() {
        // Stop the previous song (if this isn't the first song)
        if (currentSong != null) {
            currentSong.pause();
        }
        if (songsPlayed < ADDPlayer.NUM_SONGS) {
            // Play the new song (until we hit the passed in number)
            songLocation = random.nextInt(ADDPlayer.library.size() + 1);
            String songName = ADDPlayer.library.get(songLocation);
            Media test = new Media(new File(songName).toURI().toString());
            currentSong = new MediaPlayer(test);
            currentSong.play();
            fileNameField.setText(songName);
        } else {
            // Game is over, show end screen
            try {
                Parent root = FXMLLoader.load(getClass().getResource("ResultsScreen.fxml"));
                Scene scene = new Scene(root);
                ADDPlayer.mainStage.setScene(scene);
                ADDPlayer.mainStage.show();
            } catch (IOException e) {
                System.exit(2);
            }
        }
        songsPlayed++;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        songsPlayed = 0;
        random = new Random();
        cycle();
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.millis(ADDPlayer.SONG_LENGTH * 1000), ae -> cycle()));
        timeline.setCycleCount(ADDPlayer.NUM_SONGS);
        timeline.play();
    }
}