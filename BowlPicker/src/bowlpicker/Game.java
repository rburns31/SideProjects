package bowlpicker;

/**
 * A model class in BowlPicker that represents a single bowl game
 * @author Ryan Burns
 */
public class Game {
    private Team awayTeam;
    private Team homeTeam;
    private final String bowlName;

    public Game(Team awayTeam, Team homeTeam, String bowlName) {
        this.awayTeam = awayTeam;
        this.homeTeam = homeTeam;
        this.bowlName = bowlName;
    }

    public Team getAwayTeam() {
        return awayTeam;
    }

    public Team getHomeTeam() {
        return homeTeam;
    }

    public String getBowlName() {
        return bowlName;
    }

    public Team getOtherTeam(Team firstTeam) {
        if (firstTeam.equals(awayTeam)) {
            return homeTeam;
        }
        return awayTeam;
    }

    public Game setAwayTeam(Team newTeam) {
        this.awayTeam = newTeam;
        return this;
    }

    public Game setHomeTeam(Team newTeam) {
        this.homeTeam = newTeam;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Game)) {
            return false;
        }
        Game other = (Game)o;
        return bowlName.equals(other.getBowlName());
    }
}