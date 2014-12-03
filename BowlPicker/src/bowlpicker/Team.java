package bowlpicker;

import javafx.scene.image.Image;

public class Team {
    private final String name;
    private final Image image;
    private final boolean isAwayTeam;

    public Team(String name, boolean isAwayTeam) {
        this.name = name;
        this.image = new Image("NoImageFound.jpg");
        this.isAwayTeam = isAwayTeam;
    }

    public Team(String name, Image image, boolean isAwayTeam) {
        this.name = name;
        this.image = image;
        this.isAwayTeam = isAwayTeam;
    }

    public String getName() {
        return name;
    }

    public Image getImage() {
        return image;
    }
}