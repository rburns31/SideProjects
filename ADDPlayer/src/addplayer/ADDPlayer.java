package addplayer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ToggleButton;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
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
public class ADDPlayer extends Application {
    /**
     * 
     */
    public static Stage MAIN_STAGE;

    /**
     * 
     */
    //public static Scene RESULTS_SCENE;

    /**
     * 
     */
    public static int NUM_SONGS;

    /**
     * 
     */
    public static int SONG_LENGTH;

    /**
     * 
     */
    public static int POINTS;

    /**
     * Holds the Player object who is not guessing in this round
     */
    public static Player OTHER_PLAYER;

    /**
     * Holds the Player object who is guessing in this round
     */
    public static Player CUR_PLAYER;

    /**
     * 
     */
    public static PreviewHBox[] PREVIEW_BOXES;

    /**
     * 
     */
    public static HashMap<ToggleButton, ToggleButton> CORR_BUTTONS;

    /**
     * The indices of the songs in the library that will be played this round
     */
    public static int[] SONGS_IN_ROUND;

    /**
     * 
     * @param stage
     * @throws Exception 
     */
    @Override
    public void start(Stage stage) throws Exception {
        MAIN_STAGE = stage;
        Parent root = FXMLLoader.load(getClass().getResource(
                "OpeningScreen.fxml"));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * 
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        CUR_PLAYER = new Player(true);
        OTHER_PLAYER = new Player(false);

        launch(args);
        System.exit(0);
    }

    /**
     * Recursively pulls all music files from a directory on file 
     * @param path 
     * @param player 
     */
    public static void walk(String path, Player player) {
        File root = new File(path);
        File[] list = root.listFiles();
        if (list == null) {
            return;
        }
        for (File f : list) {
            if (f.isDirectory()) {
                walk(f.getAbsolutePath(), player);
            } else if (f.toString().substring(f.toString().length() - 3)
                .toLowerCase().equals("mp3")) {
                player.library.add(f.getAbsoluteFile().toString());
            }
        }
    }

    /**
     * Reads in an exported play-list from iTunes and stores all of the songs
     *     in the play-list into the user's library
     * @param file The exported iTunes play-list as a text file
     * @param player 
     */
    public static void readInPlaylist(String file, Player player) {
        player.library = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                new FileInputStream(file), "UTF-16"))) {
            br.readLine();
            String line;
            while ((line = br.readLine()) != null) {
                storeSong(line, player);
            }
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    /**
     * Parses a single line from an exported iTunes play-list and adds it to
     *     the user's library
     * @param line The line which contains all of the song's data from iTunes
     * @param player 
     */
    private static void storeSong(String line, Player player) {
        String[] info = line.split("\t");
        if (info.length != 27) {
            // iTunes doesn't know where this file is so don't add it
            for (int i = 0; i < info.length; i++) {
                System.out.println(i + " - " + info[i]);
            }
        } else {
            player.library.add(new SongDetails(info[0], info[1], info[3],
                    info[7], info[18], info[26]));
        }
    }

    /**
     * This method handles standardizing the format of the songs regardless
     *     of whether the library came from on file or from an iTunes play-list
     * @param songIndex The index of this song into the library data structure
     * @param player 
     * @return The song packaged into a SongDetails object
     */
    public static SongDetails packageIntoSongDetails(
            int songIndex, Player player) {

        SongDetails song;
        if (player.mode == 1) {
            // Create fake SongDetails objects from the metadata
            String location = (String) player.library.get(songIndex);
            song = convertWithMetadata(location);
        } else {
            song = (SongDetails) player.library.get(songIndex);
        }
        return song;
    }

    /**
     * Uses a MP3 parser to pull the relevant meta-data (song, artist and album)
     *     out of the song file at the passed in location on disk
     * @param location The location of the song file
     * @return The song packaged into a SongDetails object
     */
    private static SongDetails convertWithMetadata(String location) {
        try (InputStream input = new FileInputStream(new File(location))) {
            Metadata metadata = new Metadata();
            new Mp3Parser().parse(
                    input, new DefaultHandler(), metadata, new ParseContext());
            input.close();

            return new SongDetails(metadata.get("title"),
                    metadata.get("xmpDM:artist"), metadata.get("xmpDM:album"), 
                    null, null, location);
        } catch (IOException | SAXException | TikaException e) {
            System.out.println(e);
        }
        return null;
    }

    /**
     * Plays the next song
     * @param song The song to be played
     * @return The media player which is now playing this song
     */
    public static MediaPlayer playNextSong(SongDetails song) {
        Media songFile = new Media(new File(song.location).toURI().toString());
        MediaPlayer mediaPlayer = new MediaPlayer(songFile);
        mediaPlayer.play();
        return mediaPlayer;
    }
}