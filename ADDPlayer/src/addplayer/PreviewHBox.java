package addplayer;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

/**
 *
 * @author Ryan Burns
 */
public class PreviewHBox extends HBox {
    @FXML
    private final Label previewSong;
    @FXML
    private final Label previewArtist;
    @FXML
    private final Label previewAlbum;

    public PreviewHBox() {
        super();
        previewSong = new Label("Song Name Here");
        previewArtist = new Label("Artist Name Here");
        previewAlbum = new Label("Album Name Here");
        this.getChildren().add(previewSong);
    }
}