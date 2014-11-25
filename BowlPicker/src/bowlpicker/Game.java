package bowlpicker;

public class Game {

    protected Team awayTeam;
    protected Team homeTeam;
    protected int spread;

    public Game(Team awayTeam, Team homeTeam, int spread) {
        this.awayTeam = awayTeam;
        this.homeTeam = homeTeam;
        this.spread = spread;
    }

    @Override
    public String toString() {
        return awayTeam + " vs. " + homeTeam;
    }
}