package bowlpicker;

import java.util.HashMap;
import java.util.Objects;
import javafx.scene.image.Image;

/**
 * A model class in BowlPicker that represents a single football team
 * @author Ryan Burns
 */
public class Team {
    private final String name;
    private Image image;
    private final String rank;
    private final Conference conference;
    private final String record;
    private final String espnID;
    public static HashMap<Conference, String> confStrings;
    /**
     * The list of valid conferences that a team can be in
     */
    public enum Conference {
        AAC, ACC, BIG10, BIG12, CUSA, INDEPENDENT, MWC, MAC, PAC12,
        SEC, SUNBELT, MEAC, SWAC, TBD
    }

    public Team(String name, String rank,
                    Conference conference, String record, String espnID) {
        this.name = name;
        StringBuilder imageName = new StringBuilder();
        imageName.append("cfbicons/");
        imageName.append(name.toLowerCase().replace(" ", ""));
        imageName.append("_96.png");
        try {
            this.image = new Image(imageName.toString());
        } catch (IllegalArgumentException iae) {
            this.image = new Image("cfbicons/NCAA.png");
        }
        this.rank = rank;
        this.conference = conference;
        this.record = record;
        this.espnID = espnID;
        confStrings = new HashMap<>();
        confStrings.put(Conference.AAC, "American");
        confStrings.put(Conference.BIG10, "Big 10");
        confStrings.put(Conference.BIG12, "Big 12");
        confStrings.put(Conference.CUSA, "Conference USA");
        confStrings.put(Conference.INDEPENDENT, "Independent");
        confStrings.put(Conference.MWC, "Mountain West");
        confStrings.put(Conference.PAC12, "Pac 12");
        confStrings.put(Conference.SUNBELT, "Sun Belt");
    }

    public String getName() {
        return name;
    }

    public Image getImage() {
        return image;
    }

    public String getRank() {
        return rank;
    }

    public Conference getConference() {
        return conference;
    }

    public String getConferenceString() {
        if (confStrings.containsKey(conference)) {
            return confStrings.get(conference);
        }
        return conference.toString();
    }

    public String getRecord() {
        return record;
    }

    public String getEspnID() {
        return espnID;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Team)) {
            return false;
        }
        Team other = (Team)o;
        return name.equals(other.getName());
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 43 * hash + Objects.hashCode(this.name);
        return hash;
    }
}