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
    public static ArrayList<SongDetails> LIBRARYFROMITUNES = new ArrayList<>();
    public static ArrayList<String> LIBRARYFROMFILE = new ArrayList<>();

    /**
     * 
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        walk("E:\\Users\\Ryan\\Music\\Library");
        readInPlaylist("Music.txt");

        extensionDiagnostics();
        findHidingSongs();
    }

    /**
     * Checks for any songs which are in the library folder on the disk, but
     *     are not in the iTunes library, or vice versa
     */
    private static void findHidingSongs() {
        for (int i = 0; i < LIBRARYFROMITUNES.size(); i++) {
            String location = LIBRARYFROMITUNES.get(i).location;
            if (LIBRARYFROMFILE.contains(location)) {
                LIBRARYFROMFILE.remove(LIBRARYFROMFILE.indexOf(location));
                LIBRARYFROMITUNES.remove(i);
                i--;
            }
        }
        // Print out any songs that are in the iTunes library only
        for (int i = 0; i < LIBRARYFROMITUNES.size(); i++) {
            System.out.println(LIBRARYFROMITUNES.get(i));
        }
        // Print out any songs that are on file only
        for (int i = 0; i < LIBRARYFROMFILE.size(); i++) {
            System.out.println(LIBRARYFROMFILE.get(i));
        }
        System.out.println("In iTunes, not on file: " + LIBRARYFROMITUNES.size());
        System.out.println("On file, not in iTunes: " + LIBRARYFROMFILE.size());
        System.out.println("----------");
    }

    /**
     * 
     * @param file 
     */
    private static void readInPlaylist(String file) {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                new FileInputStream(file), "UTF-16"))) {
            String line = br.readLine();
            while ((line = br.readLine()) != null) {
                storeSong(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
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
            LIBRARYFROMITUNES.add(new SongDetails(info[0], info[1], info[3],
                    info[5], info[6], info[7], info[10], info[12], info[14],
                    info[15], info[18], info[21], info[23], info[26]));
        }
    }

    /**
     * 
     */
    private static void extensionDiagnostics() {
        HashMap<String, Integer> extensions = new HashMap<>();
        for (int i = 0; i < LIBRARYFROMFILE.size(); i++) {
            String song = LIBRARYFROMFILE.get(i);
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
        System.out.println("----------");
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
                LIBRARYFROMFILE.add(f.getAbsoluteFile().toString());
            }
        }
    }
}