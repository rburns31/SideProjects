package addplayer;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.media.MediaPlayer;

/**
 * The results screen of ADDPlayer
 * @author Ryan Burns
 */
public class ResultsScreenController implements Initializable {
    @FXML
    private Label playerField;
    @FXML
    private Label pointsField;
    @FXML
    private AnchorPane previewPane;

    /**
     * 
     */
    private int indexPlaying;

    /**
     * 
     */
    private Button[] playPauseButtons;

    /**
     * Actually plays/pauses the songs
     */
    private MediaPlayer mediaPlayer;

    /**
     * Sets up the results scene by:
     *  - setting the player name and points text fields
     *  - applying new handlers to all of the colored boxes so they will now
     *        affect the points field on this screen rather than the last one
     * @param url Not used
     * @param rb Not used
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        indexPlaying = -1;
        playPauseButtons = new Button[ADDPlayer.NUM_SONGS];

        playerField.setText(ADDPlayer.PLAYER);
        pointsField.setText(Integer.toString(ADDPlayer.POINTS));

        ColoredLabelClickHandler handler =
                new ColoredLabelClickHandler(pointsField);

        for (int i = 0; i < ADDPlayer.NUM_SONGS; i++) {
            PreviewHBox previewBox = ADDPlayer.PREVIEW_BOXES[i];

            for (Label colorBox : previewBox.colorBoxes) {
                colorBox.setOnMouseClicked(handler);
            }

            previewBox.setPrefWidth(350);

            ImageView playImgView = new ImageView(new Image("/icons/play.png"));
            playImgView.setFitWidth(14);
            playImgView.setFitHeight(14);

            int boxHeight = (int) previewBox.getPrefHeight();
            HBox playPauseHolder = new HBox();
            playPauseHolder.setPadding(new Insets
                    ((boxHeight - 50) / 2, 10, (boxHeight - 50) / 2, 10));

            Button playPause = new Button();
            playPause.setGraphic(playImgView);
            playPause.setPrefSize(30, 30);

            int temp = i;
            playPause.setOnAction((ActionEvent e) -> {
                playPauseButtonAction(temp);
            });

            playPauseButtons[i] = playPause;

            playPauseHolder.getChildren().add(playPause);

            previewBox.getChildren().add(1, playPauseHolder);

            previewPane.getChildren().add(previewBox);
        }
    }

    /**
     * 
     * @param index 
     */
    private void playPauseButtonAction(int index) {
        ImageView playImgView = new ImageView(new Image("/icons/play.png"));
        playImgView.setFitWidth(14);
        playImgView.setFitHeight(14);

        ImageView pauseImgView = new ImageView(new Image("/icons/pause.png"));
        pauseImgView.setFitWidth(14);
        pauseImgView.setFitHeight(14);

        boolean isPlayImage = isPlayImage(playImgView, playPauseButtons[index]);

        // Play the selected song
        if (isPlayImage) {
            // Stop the last song if there was one
            if (indexPlaying != -1) {
                playPauseButtons[indexPlaying].setGraphic(playImgView);
                mediaPlayer.pause();
            }

            playPauseButtons[index].setGraphic(pauseImgView);
            SongDetails song = ADDPlayer.packageIntoSongDetails(
                    ADDPlayer.SONGS_IN_ROUND[index]);
            mediaPlayer = ADDPlayer.playNextSong(song);
            indexPlaying = index;
        // Stop playing the selected song
        } else {
            playPauseButtons[indexPlaying].setGraphic(playImgView);
            mediaPlayer.pause();
            indexPlaying = -1;
        }
    }

    /**
     * 
     * @param playImgView
     * @param playPause
     * @return 
     */
    private boolean isPlayImage(ImageView playImgView, Button playPause) {
        Image currentImg = ((ImageView) playPause.getGraphic()).getImage();
        boolean isPlayImage = true;
        for (int i = 0; i < currentImg.getWidth(); i++) {
            for (int j = 0; j < currentImg.getHeight(); j++) {
                if (isPlayImage && currentImg.getPixelReader().getArgb(i, j) !=
                        playImgView.getImage().getPixelReader().getArgb(i, j)) {

                    isPlayImage = false;
                }
            }
        }
        return isPlayImage;
    }

    /**
     * Button handlers below here
     */

    /**
     * 
     * @param event Not used
     */
    @FXML
    private void playAgainButtonAction(ActionEvent event) {
        if (mediaPlayer != null) {
            mediaPlayer.pause();
        }
        try {
            Parent root = FXMLLoader.load(
                    getClass().getResource("GameplayScreen.fxml"));
            Scene scene = new Scene(root);
            ADDPlayer.MAIN_STAGE.setScene(scene);
            ADDPlayer.MAIN_STAGE.show();
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    /**
     * 
     * @param event Not used
     */
    @FXML
    private void mainMenuButtonAction(ActionEvent event) {
        if (mediaPlayer != null) {
            mediaPlayer.pause();
        }
        try {
            Parent root = FXMLLoader.load(
                    getClass().getResource("OpeningScreen.fxml"));
            Scene scene = new Scene(root);
            ADDPlayer.MAIN_STAGE.setScene(scene);
            ADDPlayer.MAIN_STAGE.show();
        } catch (IOException e) {
            System.out.println(e);
        }
    }
}