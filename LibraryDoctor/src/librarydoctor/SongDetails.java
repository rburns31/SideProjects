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
    protected final String length;
    protected final String trackNumber;
    protected final String year;
    protected final String dateAdded;
    protected final String bitRate;
    protected final String fileType;
    protected final String plays;
    protected final String skips;
    protected final String location;

    public SongDetails(String name, String artist, String album, String genre,
            String size, String length, String trackNumber, String year,
            String dateAdded, String bitRate, String fileType, String plays,
            String skips, String location) {
        this.name = name;
        this.artist = artist;
        this.album = album;
        this.genre = genre;
        this.size = size;
        this.length = length;
        this.trackNumber = trackNumber;
        this.year = year;
        this.dateAdded = dateAdded;
        this.bitRate = bitRate;
        this.fileType = fileType;
        this.plays = plays;
        this.skips = skips;
        this.location = location;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Name: ").append(name).append("\n");
        sb.append("Artist: ").append(artist).append("\n");
        sb.append("Album: ").append(album).append("\n");
        sb.append("Genre: ").append(genre).append("\n");
        sb.append("Size: ").append(size).append("\n");
        sb.append("Length: ").append(length).append("\n");
        sb.append("Track Number: ").append(trackNumber).append("\n");
        sb.append("Year: ").append(year).append("\n");
        sb.append("Date Added: ").append(dateAdded).append("\n");
        sb.append("Bit Rate: ").append(bitRate).append("\n");
        sb.append("File Type: ").append(fileType).append("\n");
        sb.append("Plays: ").append(plays).append("\n");
        sb.append("Skips: ").append(skips).append("\n");
        sb.append("Location: ").append(location).append("\n");
        sb.append("----------");
        return sb.toString();
    }
}