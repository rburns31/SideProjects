package bowlpicker;

public class Game {
    private final Team awayTeam;
    private final Team homeTeam;
    private final int spread;

    public Game(Team awayTeam, Team homeTeam, int spread) {
        this.awayTeam = awayTeam;
        this.homeTeam = homeTeam;
        this.spread = spread;
    }

    public Team getAwayTeam() {
        return awayTeam;
    }

    public Team getHomeTeam() {
        return homeTeam;
    }
 
    public int getSpread() {
        return spread;
    }

    @Override
    public String toString() {
        return awayTeam + " vs. " + homeTeam;
    }
}