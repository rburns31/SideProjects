package bowlpicker;

import java.util.Objects;

/**
 * A model class in BowlPicker that represents a single bowl game
 * @author Ryan Burns
 */
public class Game {
    private Team awayTeam;
    private Team homeTeam;
    private final String bowlName;
    private final String date;
    private final String time;

    public Game(Team awayTeam, Team homeTeam, String bowlName,
            String date, String time) {
        this.awayTeam = awayTeam;
        this.homeTeam = homeTeam;
        this.bowlName = bowlName;
        this.date = date;
        this.time = time;
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

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
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

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.bowlName);
        return hash;
    }
}