package addplayer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ToggleButton;
import javafx.stage.Stage;

/**
 * 
 * @author Ryan Burns
 */
public class ADDPlayer extends Application {
    /**
     * Will either hold the library as Strings of the file locations or as
     *     SongDetails objects of the song information depending on the
     *     method of library input selected by the user
     */
    public static ArrayList LIBRARY;
    /**
     * 0 if from export iTunes play-list, 1 if from folder on hard drive
     */
    public static int MODE;
    public static Stage MAIN_STAGE;
    public static int NUM_SONGS;
    public static int SONG_LENGTH;
    public static int POINTS;
    public static String PLAYER;
    public static PreviewHBox[] PREVIEW_BOXES;
    public static HashMap<ToggleButton, ToggleButton> CORR_BUTTONS;

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
        launch(args);
        System.exit(0);
    }

    /**
     * 
     * @param path 
     */
    public static void walk(String path) {
        File root = new File(path);
        File[] list = root.listFiles();
        if (list == null) {
            return;
        }
        for (File f : list) {
            if (f.isDirectory()) {
                walk(f.getAbsolutePath());
            } else if (f.toString().substring(f.toString().length() - 3)
                .toLowerCase().equals("mp3")) {
                LIBRARY.add(f.getAbsoluteFile().toString());
            }
        }
    }

    /**
     * 
     * @param file 
     */
    public static void readInPlaylist(String file) {
        LIBRARY = new ArrayList<SongDetails>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                new FileInputStream(file), "UTF-16"))) {
            String line = br.readLine();
            while ((line = br.readLine()) != null) {
                storeSong(line);
            }
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    /**
     * 
     * @param line 
     */
    private static void storeSong(String line) {
        String[] info = line.split("\t");
        if (info.length != 27) {
            // iTunes doesn't know where this file is so don't add it
            for (int i = 0; i < info.length; i++) {
                System.out.println(i + " - " + info[i]);
            }
        } else {
            LIBRARY.add(new SongDetails(info[0], info[1], info[3], info[7],
                    info[18], info[26]));
        }
    }
}