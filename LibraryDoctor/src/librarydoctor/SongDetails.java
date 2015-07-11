package librarydoctor;

/**
 *
 * @author Ryan Burns
 */
public class SongDetails {
    protected final String name;
    protected final String artist;
    protected final String album;
    protected final String genre;
    protected final String size;
    protected final String time;
    protected final String trackNumber;
    protected final String year;
    protected final String dateAdded;
    protected final String bitRate;
    protected final String fileType;
    protected final String plays;
    protected final String skips;
    protected final String location;

    public SongDetails(String name, String artist, String album, String genre,
            String size, String time, String trackNumber, String year,
            String dateAdded, String bitRate, String fileType, String plays,
            String skips, String location) {
        this.name = name;
        this.artist = artist;
        this.album = album;
        this.genre = genre;
        this.size = size;
        this.time = time;
        this.trackNumber = trackNumber;
        this.year = year;
        this.dateAdded = dateAdded;
        this.bitRate = bitRate;
        this.fileType = fileType;
        this.plays = plays;
        this.skips = skips;
        this.location = location;
    }
}