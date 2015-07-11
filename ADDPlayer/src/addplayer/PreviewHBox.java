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

    public PreviewHBox(SongDetails song) {
        super();
        previewSong = new Label(song.name);
        previewArtist = new Label(song.artist);
        previewAlbum = new Label(song.album);
        this.getChildren().add(previewSong);
    }
}