package addplayer;

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
    public Label previewSong;
    public Label previewArtist;
    public Label previewAlbum;
    private Label songColor;
    private Label artistColor;
    private Label albumColor;

    /**
     * 
     */
    public Label[] colorBoxes;

    private final SongDetails song;

    /**
     * 
     * @param song 
     */
    public PreviewHBox(SongDetails song) {
        super();
        this.song = song;

        this.setPrefSize(300, 160);
        this.getStyleClass().add("hbox");
        this.getStylesheets().add("CSS_Sheets/PreviewPaneElement.css");
        this.getChildren().addAll(createInfoBox(), createColorsBox());
    }

    /**
     * 
     * @return 
     */
    private VBox createInfoBox() {
        VBox infoBox = new VBox();
        infoBox.setAlignment(Pos.CENTER_LEFT);
        infoBox.setPadding(new Insets(0, 0, 0, 10));

        infoBox.setSpacing(10);
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