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
    public Label songColor;
    @FXML
    public Label artistColor;
    @FXML
    public Label albumColor;

    public PreviewHBox(SongDetails song, int height) {
        super();
        
        this.setPrefWidth(300);
        this.setPrefHeight(height);
        
        this.getStyleClass().add("hbox");
        this.getStylesheets().add("CSS_Sheets/PreviewPaneElement.css");

        VBox infoBox = new VBox();
        setupInfoBox(infoBox, song);
        VBox colorsBox = new VBox();
        setupColorsBox(colorsBox);
        this.getChildren().addAll(infoBox, colorsBox);
    }

    private void setupInfoBox(VBox infoBox, SongDetails song) {
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
    }

    private void setupColorsBox(VBox colorsBox) {
        songColor = new Label();
        songColor.setPrefWidth(15);
        songColor.setPrefHeight(53);
        songColor.getStyleClass().add("redlabel");
        

        artistColor = new Label();
        artistColor.setPrefWidth(15);
        artistColor.setPrefHeight(53);
        artistColor.getStyleClass().add("redlabel");
        

        albumColor = new Label();
        albumColor.setPrefWidth(15);
        albumColor.setPrefHeight(54);
        albumColor.getStyleClass().add("redlabel");
        

        colorsBox.getChildren().addAll(songColor, artistColor, albumColor);
    }
}