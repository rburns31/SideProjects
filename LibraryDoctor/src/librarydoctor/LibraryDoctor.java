package librarydoctor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author Ryan Burns
 */
public class LibraryDoctor {
    public static ArrayList<String> LIBRARY = new ArrayList<>();

    /**
     * 
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        //walk("E:\\Users\\Ryan\\Music\\Library");
        //extensionDiagnostics();
        readInPlaylist();
    }

    /**
     * 
     */
    private static void readInPlaylist() {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                new FileInputStream("Music.txt"), "UTF-16"))) {
            String line = br.readLine();
            while (line != null) {
                LIBRARY.add(line);
                line = br.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        String[] details = LIBRARY.get(0).split("\t");
        String[] values = LIBRARY.get(1).split("\t");
        for (int i = 0; i < details.length; i++) {
            System.out.println(details[i] + " - " + values[i]);
        }
    }

    /**
     * 
     */
    private static void extensionDiagnostics() {
        HashMap<String, Integer> extensions = new HashMap<>();
        for (int i = 0; i < LIBRARY.size(); i++) {
            String song = LIBRARY.get(i);
            String extension = song.substring(song.lastIndexOf("."));
            if (extensions.containsKey(extension)) {
                extensions.replace(extension, extensions.get(extension) + 1);
            } else {
                extensions.put(extension, 1);
            }
        }
        int totalSongs = 0;
        for (String ext : extensions.keySet()) {
            System.out.println(ext + " - " + extensions.get(ext));
            totalSongs += extensions.get(ext);
        }
        System.out.println("Total - " + totalSongs);
    }

    /**
     * 
     * @param path 
     */
    private static void walk(String path) {
        File root = new File(path);
        File[] list = root.listFiles();
        if (list == null) {
            return;
        }
        for (File f : list) {
            if (f.isDirectory()) {
                walk(f.getAbsolutePath());
            //} else if (f.toString().substring(f.toString().length() - 3)
            //    .toLowerCase().equals("mp3")) {
            } else if (!f.isHidden()) {
                LIBRARY.add(f.getAbsoluteFile().toString());
            }
        }
    }
}