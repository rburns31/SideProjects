public class Team implements Comparable {

    private final String name;
    public double[] stats;
    public double worth;

    public Team(String name, double[] stats, double worth) {
        this.name = name;
        this.stats = stats;
        this.worth = worth;
    }

    public String getName() {
        return name;
    }

    @Override
    public int compareTo(Object other) {
        if (other instanceof Team) {
            Team otherTeam = (Team) other;
            return worth.compareTo(otherTeam.worth);
        }
        else {
            return 1;
        }
    }


}