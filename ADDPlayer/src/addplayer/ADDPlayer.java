package addplayer;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


/**
 * 
 * @author Ryan Burns
 */
public class ADDPlayer extends Application {
    /**
     * 
     */
    public static ArrayList<String> library = new ArrayList<>();
    public static Stage mainStage;
    public static int NUM_SONGS;
    public static int SONG_LENGTH;

    @Override
    public void start(Stage stage) throws Exception {
        mainStage = stage;
        Parent root = FXMLLoader.load(getClass().getResource("OpeningScreen.fxml"));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
        //extensionDiagnostics();
        System.exit(0);
    }

    /**
     * 
     */
    private static void extensionDiagnostics() {
        HashMap<String, Integer> extensions = new HashMap<>();
        for (int i = 0; i < library.size(); i++) {
            String song = library.get(i);
            String extension = song.substring(song.lastIndexOf("."));
            if (extensions.containsKey(extension)) {
                extensions.replace(extension, extensions.get(extension) + 1);
            } else {
                extensions.put(extension, 1);
            }
        }
        for (String ext : extensions.keySet()) {
            System.out.println(ext + " - " + extensions.get(ext));
        }
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
            } else if (!f.isHidden()) {
                //System.out.println(f.getAbsoluteFile().toString());
                library.add(f.getAbsoluteFile().toString());
            }
        }
    }
}