package addplayer;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

/**
 *
 * @author Ryan Burns
 */
public class PreviewHBox extends HBox {
    @FXML
    private Label previewSong;
    @FXML
    private Label previewArtist;
    @FXML
    private Label previewAlbum;
    @FXML
    private Label songColor;
    @FXML
    private Label artistColor;
    @FXML
    private Label albumColor;

    /**
     * 
     */
    public Label[] colorBoxes;

    /**
     * 
     * @param song
     * @param height 
     */
    public PreviewHBox(SongDetails song, int height) {
        super();

        this.setPrefSize(300, height);
        this.getStyleClass().add("hbox");
        this.getStylesheets().add("CSS_Sheets/PreviewPaneElement.css");
        this.getChildren().addAll(createInfoBox(song), createColorsBox());
    }

    /**
     * 
     * @param infoBox
     * @param song 
     * @return 
     */
    private VBox createInfoBox(SongDetails song) {
        VBox infoBox = new VBox();
        infoBox.setAlignment(Pos.CENTER_LEFT);
        infoBox.setPadding(new Insets(0, 0, 0, 10));

        if (ADDPlayer.NUM_SONGS <= 5) {
            infoBox.setSpacing(10);
        } else {
            infoBox.setSpacing(5);
        }
        infoBox.setPrefWidth(265);

        previewSong = new Label("Song: " + song.name);
        previewSong.setFont(Font.font(14));

        previewArtist = new Label("Artist: " + song.artist);
        previewArtist.setFont(Font.font(14));

        previewAlbum = new Label("Album: " + song.album);
        previewAlbum.setFont(Font.font(14));

        infoBox.getChildren().addAll(previewSong, previewArtist, previewAlbum);
        return infoBox;
    }

    /**
     * 
     * @return 
     */
    private VBox createColorsBox() {
        songColor = new Label();
        songColor.setPrefSize(15, 53);
        songColor.getStyleClass().add("graylabel");        

        artistColor = new Label();
        artistColor.setPrefSize(15, 53);
        artistColor.getStyleClass().add("graylabel");
        
        albumColor = new Label();
        albumColor.setPrefSize(15, 54);
        albumColor.getStyleClass().add("graylabel");

        colorBoxes = new Label[] {songColor, artistColor, albumColor};

        VBox colorsBox = new VBox();
        colorsBox.getChildren().addAll(colorBoxes);
        return colorsBox;
    }
}