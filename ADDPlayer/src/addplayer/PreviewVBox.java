package addplayer;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

/**
 *
 * @author Ryan Burns
 */
public class PreviewVBox extends VBox {
    @FXML
    private final Label previewSong;
    @FXML
    private final Label previewArtist;
    @FXML
    private final Label previewAlbum;

    public PreviewVBox(SongDetails song) {
        super();
        previewSong = new Label(song.name);
        previewArtist = new Label(song.artist);
        previewAlbum = new Label(song.album);
        //this.setAlignment(Pos.CENTER);
        this.getChildren().add(previewSong);
        this.getChildren().add(previewArtist);
        this.getChildren().add(previewAlbum);
    }
}