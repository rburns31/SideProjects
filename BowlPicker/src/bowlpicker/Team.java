package bowlpicker;

import javafx.scene.image.Image;

/**
 * 
 * @author Ryan Burns
 */
public class Team {
    private final String name;
    private Image image;
    private final boolean isAwayTeam;
    private final String rank;
    private final Conference conference;
    private final String record;
    
    public enum Conference {
        AAC, ACC, BIG10, BIG12, CUSA, INDEPENDENT, MWC, MAC, PAC12, SEC, SUNBELT
    }

    public Team(String name, boolean isAwayTeam, String rank,
                    Conference conference, String record) {
        this.name = name;
        StringBuilder imageName = new StringBuilder();
        imageName.append("cfbicons/");
        imageName.append(name.toLowerCase().replace(" ", ""));
        imageName.append("_96.png");
        try {
            this.image = new Image(imageName.toString());
        } catch (IllegalArgumentException iae) {
            this.image = new Image("cfbicons/ncaa.png");
        }
        this.isAwayTeam = isAwayTeam;
        this.rank = rank;
        this.conference = conference;
        this.record = record;
    }

    public String getName() {
        return name;
    }


    public Image getImage() {
        return image;
    }

    public boolean getIsAwayTeam() {
        return isAwayTeam;
    }

    public String getRank() {
        return rank;
    }

    public Conference getConference() {
        return conference;
    }

    public String getRecord() {
        return record;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Team)) {
            return false;
        }
        Team other = (Team)o;
        return name.equals(other.getName());
    }
}