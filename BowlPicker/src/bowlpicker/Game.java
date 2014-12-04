package bowlpicker;

/**
 * 
 * @author Ryan Burns
 */
public class Game {
    private final Team awayTeam;
    private final Team homeTeam;

    public Game(Team awayTeam, Team homeTeam) {
        this.awayTeam = awayTeam;
        this.homeTeam = homeTeam;
    }

    public Team getAwayTeam() {
        return awayTeam;
    }

    public Team getHomeTeam() {
        return homeTeam;
    }

    public Team getOtherTeam(Team firstTeam) {
        if (firstTeam.equals(awayTeam)) {
            return homeTeam;
        }
        return awayTeam;
    }

    @Override
    public String toString() {
        return awayTeam + " vs. " + homeTeam;
    }
}