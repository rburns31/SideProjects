package addplayer;

/**
 *
 * @author Ryan Burns
 */
public class SongDetails {
    protected final String name;
    protected final String artist;
    protected final String album;
    // This is given in seconds
    protected final String length;
    // MPEG audio file = mp3
    protected final String fileType;
    protected final String location;

    public SongDetails(String name, String artist, String album, String length,
            String fileType, String location) {
        this.name = name;
        this.artist = artist;
        this.album = album;
        this.length = length;
        this.fileType = fileType;
        this.location = location;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Name: ").append(name).append("\n");
        sb.append("Artist: ").append(artist).append("\n");
        sb.append("Album: ").append(album).append("\n");
        sb.append("Length: ").append(length).append("\n");
        sb.append("File Type: ").append(fileType).append("\n");
        sb.append("Location: ").append(location).append("\n");
        sb.append("----------");
        return sb.toString();
    }
}