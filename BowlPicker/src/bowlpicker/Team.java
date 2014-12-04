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
    private final int rank;
    private final Conference conference;
    private final String record;
    
    public enum Conference {
        ACC, BIG10, BIG12, BIGEAST, CUSA, INDEPENDENT, MWC, MAC, PAC12, SEC, SUNBELT, WAC
    }

    public Team(String name, boolean isAwayTeam, int rank,
                    Conference conference, String record) {
        this.name = name;
        StringBuilder imageName = new StringBuilder();
        imageName.append(name.toLowerCase().replace(" ", ""));
        imageName.append("_96.png");
        try {
            this.image = new Image(imageName.toString());
        } catch (Exception e) {
            this.image = new Image("NCAA.png");
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

    public int getRank() {
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