package addplayer;

import java.util.ArrayList;

/**
 * 
 * @author Ryan Burns
 */
public class Player {
    /**
     * The name of this player
     */
    public String name;

    /**
     * 0 if from export iTunes play-list, 1 if from folder on hard drive
     */
    public int mode;

    /**
     * Will either hold the library as Strings of the file locations or as
     *     SongDetails objects of the song information depending on the
     *     method of library input selected by the user
     */
    public ArrayList library;

    /**
     * 
     */
    public ArrayList<Integer> scores;

    /**
     * 
     */
    public boolean isPlayerOne;

    public Player(boolean isPlayerOne) {
        this.isPlayerOne = isPlayerOne;
        scores = new ArrayList<>();
    }
}