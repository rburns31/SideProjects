import java.io.IOException;
import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/**
 * Holds the logic for scoring a bracket with known results and generating
 *     random formulas to attempt to fit a bracket with unknown results
 * @author Ryan Burns
 */
public class BracketBuster {
    /**
     * Defines the initial spread of each randomly generated coefficient in
     *     maxFind(), and also serves as the starting point for the
     *     diminishing spreads that maxTune() uses
     */
    private static final int SPREAD = 32;
    /**
     * Sets a lower limit score that a formula must receive in maxFind() to
     *     determine whether or not it is worth attempting to tune this
     *     specific random formula using maxTune()
     */
    private static final int LIMIT = 100;
    /**
     * The number of trials to be run in each iteration of a maxTune() call
     */
    private static final int TUNE_TRIALS = 100;

    /**
     * The number of trials to be run in this iteration
     */
    private final int trials;
    /**
     * Holds the current coefficients being evaluated
     */
    private final double[] constants;
    /**
     * Holds the current maximum score reached by a formula in this iteration
     */
    private int max;
    /**
     * The number of trials currently completed
     */
    private int progress;

    /**
     * Holds the coefficients that lead to the current maximum score reached
     *     by a formula in this iteration
     */
    public double[] best;
    /**
     * Array to hold the winners of each game of the tournament, but they are
     *     represented in terms of their column in the inputted data
     */
    public int[] winnerPos;



    public BracketBuster(int trials) {
        this.trials = trials;
        constants = new double[Driver.YEAR_TO_SIZE.get(Driver.YEAR)];
        best = new double[Driver.YEAR_TO_SIZE.get(Driver.YEAR)];
        winnerPos = new int[63];
    }

    /**
     * Each trial consists of:
     *     1. Randomly generating values for each coefficient
     *     2. Scoring this random formula against the known results
     *     3. Updating the instance variables if this score is the best yet
     *     4. If this score is over the predefined threshold, call maxTune()
     * Upon completion of maxFind(), the instance variable "best" will hold
     *     the coefficients that led to the maximum score, which is returned
     * @return The maximum score that this iteration was able to find
     */
    public int maxFind() {
        for (progress = 0; progress < trials; progress++) {
            for (int j = 0; j < constants.length - 1; j++) {
                constants[j] = (Math.random() * SPREAD) - (SPREAD / 2);
                int current = score(constants);
                if (current > max) {
                    max = current;
                    System.arraycopy(
                            constants, 0, best, 0, constants.length - 1);
                }
                if (current > LIMIT) {
                    for (int k = 0; k < 10; k++) {
                        maxTune(SPREAD / Math.pow(Math.sqrt(2), k + 1));
                    }
                }
            }
        }
        List trimmed = trimCoeff(max, best);
        max = (int) trimmed.get(0);
        best = (double[]) trimmed.get(1);
        return max;
    }

    /**
     * Essentially increases level of granularity in an attempt to make good
     *     formulas into even better ones by using the good formula as a base
     *     to be randomly generated around
     * Each trial consists of:
     *     1. Randomly generating smaller values to add to each coefficient
     *     2. Scoring this random formula against the known results
     *     3. Updating the instance variables if this score is the best yet
     * @param spread The current spread of the granularity being added
     */
    public void maxTune(double spread) {
        for (int i = 0; i < TUNE_TRIALS; i++) {
            for (int j = 0; j < constants.length - 1; j++) {
                constants[j] += (Math.random() * spread) - (spread / 2);
                int current = score(constants);
                if (current > max) {
                    max = current;
                    System.arraycopy(
                            constants, 0, best, 0, constants.length - 1);
                }
            }
        }
    }

    /**
     * Scores an inputted formula based on the given year's actual results
     * @param constants The coefficients of the formula being scored
     * @return The number of points scored by this formula
     */
    public int score(double[] constants) {
        /**
         * Sets up all of the arrays which will be used to score:
         *     teams houses all of the z scores for each team in the tournament
         *     worths houses the number of games that team actually won
         *         in that year's tournament (based on ESPN standard scoring)
         *     scores houses each team's composite z score based on whatever
         *         formula is currently being applied
         */
        double[][] teams = new double[64][constants.length];
        double[] worths = new double[64];
        double[] scores = new double[64];
        int pointsEarned = 0;

        /**
         * Reads in the z scores from file and fills in the teams array
         */
        try {
            File dataFile = new File(
                    "stats/stats_" + Driver.YEAR + "_3" + ".txt");
            Scanner fileScanner = new Scanner(dataFile);
            int j = 0;
            while (fileScanner.hasNext() && j < 64) {
                String line = fileScanner.nextLine();
                for (int i = 0; i < constants.length; i++) {
                    teams[j][i] = Double.parseDouble((line.split(" ", 0))[i]);
                }
                j++;
            }
            fileScanner.close();
        } catch (IOException e) {
            System.out.println("Data file does not exist, exiting.");
            System.exit(0);
        }

        /**
         * Reads in the worths from file and fills in the worths array
         * Calculates the composite z scores by multiplying the current
         *     formula's coefficients by the z scores in the teams array
         */
        for (int i = 0; i < scores.length; i++) {
            worths[i] = teams[i][constants.length - 1];
            for (int j = 0; j < constants.length; j++) {
                scores[i] += constants[j] * teams[i][j];
            }
        }

        // Hold on to your hat, this is where it gets a tad convoluted

        // counter keeps track of what index of winnerPos we fill in next
        int counter = 0;
        /**
         * Go through the bracket one round at a time
         */
        for (int round = 0; round < 6; round++) {
            /**
             * (value / 2) is how much each game in this round is worth
             * value is also how many teams are vying for those points
             *     (i.e. in round 1 only 2 teams, in round 6 all 64 teams)
             */
            int value = (int)(Math.pow(2, round + 1));
            // compare houses the index of each team in the "match-up"
            int[] compare = new int[value];
            // For each "match-up" in this round
            for (int i = 0; i < (64 / value); i++) {
                int maxScoreIndex = value * i;
                int maxWorthIndex = value * i;
                // For each team in this "match-up"
                for (int j = 0; j < value; j++) {
                    // Find the index of the team
                    compare[j] = (value * i) + j;
                    /**
                     * If the team's score is the biggest in the "match-up"
                     *     thus far, save its index in maxScoreIndex
                     */
                    if (scores[compare[j]] > scores[maxScoreIndex]) {
                        maxScoreIndex = compare[j];
                    }
                    /**
                     * If the team's worth is the biggest in the "match-up"
                     *     thus far, save it in maxWorth
                     */
                    if (worths[compare[j]] > worths[maxWorthIndex]) {
                        maxWorthIndex = compare[j];
                    }
                }

                /**
                 * Put the index of the team with the biggest score into winnerPos
                 * If that team also had the biggest worth, then increase the pointsEarned
                 * There are only 63 games, so there are only 63 winners
                 */
                if (counter < winnerPos.length) {
                    winnerPos[counter] = maxScoreIndex;
                }
                counter++;
                if (maxScoreIndex == maxWorthIndex) {
                    pointsEarned += value / 2;
                }
            }
        }
        return pointsEarned;
    }

    /**
     * Scores a bracket of all high seeds based on the given year's actual results
     * @return The number of points scored by picking all of the high seeds
     */
    public int highSeed() {
        /**
         * Sets up all of the arrays which will be used to score high-seed:
         *     worths houses the number of games that team actually won
         *         in that year's tournament (based on ESPN standard scoring)
         *     seeds houses each team's seed (with the #1 overall seed getting
         *         assigned a -1 and the #2 overall getting assigned a 0
         */
        double[] worths = new double[64];
        double[] seeds =
                    {-1, 16, 8, 9, 5, 12, 4, 13, 6, 11, 3, 14, 7, 10, 2, 15,
                    1, 16, 8, 9, 5, 12, 4, 13, 6, 11, 3, 14, 7, 10, 2, 15,
                    0, 16, 8, 9, 5, 12, 4, 13, 6, 11, 3, 14, 7, 10, 2, 15,
                    1, 16, 8, 9, 5, 12, 4, 13, 6, 11, 3, 14, 7, 10, 2, 15};
        int pointsEarned = 0;

        /**
         * Reads in the worths from file and fills in the worths array
         */
        try {
            File dataFile = new File(
                    "stats/stats_" + Driver.YEAR + "_3" + ".txt");
            Scanner fileScanner = new Scanner(dataFile);
            int i = 0;
            while (fileScanner.hasNext() && i < 64) {
                String line = fileScanner.nextLine();
                worths[i] = Double.parseDouble((line.split(" "))
                        [Driver.YEAR_TO_SIZE.get(Driver.YEAR) - 1]);
                i++;
            }
            fileScanner.close();
        } catch (IOException e) {
            System.out.println("Data file does not exist, exiting.");
            System.exit(0);
        }

        // Hold on to your hat, this is where it gets a tad convoluted

        // counter keeps track of what index of winnerPos we fill in next
        int counter = 0;
        /**
         * Go through the bracket one round at a time
         */
        for (int round = 0; round < 6; round++) {
            /**
             * (value / 2) is how much each game in this round is worth
             * value is also how many teams are vying for those points
             *     (i.e. in round 1 only 2 teams, in round 6 all 64 teams)
             */
            int value = (int)(Math.pow(2, round + 1));
            // compare houses the index of each team in the "match-up"
            int[] compare = new int[value];
            // For each "match-up" in this round
            for (int i = 0; i < (64 / value); i++) {
                int minSeedIndex = value * i;
                int maxWorthIndex = value * i;
                // For each team in this "match-up"
                for (int j = 0; j < value; j++) {
                    // Find the index of the team
                    compare[j] = (value * i) + j;
                    /**
                     * If the team's seed is the smallest in the "match-up"
                     *     thus far, save its index in minSeedIndex
                     */
                    if (seeds[compare[j]] < seeds[minSeedIndex]) {
                        minSeedIndex = compare[j];
                    }
                    /**
                     * If the team's worth is the biggest in the "match-up"
                     *     thus far, save it in maxWorth
                     */
                    if (worths[compare[j]] > worths[maxWorthIndex]) {
                        maxWorthIndex = compare[j];
                    }
                }

                /**
                 * Put the index of the team with the smallest seed into winnerPos
                 * If that team also had the biggest worth, then increase the pointsEarned
                 * There are only 63 games, so there are only 63 winners
                 */
                if (counter < winnerPos.length) {
                    winnerPos[counter] = minSeedIndex;
                }
                counter++;
                if (minSeedIndex == maxWorthIndex) {
                    pointsEarned += value / 2;
                }
            }
        }
        return pointsEarned;
    }

    /**
     * 
     * @param origScore
     * @param origCoeff
     * @return 
     */
    public List trimCoeff(int origScore, double[] origCoeff) {
        double[] trimmed = Arrays.copyOf(origCoeff, origCoeff.length);

        int j = Double.toString(Math.abs(origCoeff[0])).length()
                - Double.toString(Math.abs(origCoeff[0])).indexOf('.') - 1;
        while (origScore <= score(trimmed) && j > 0) {
            origCoeff = Arrays.copyOf(trimmed, trimmed.length);
            for (int i = 0; i < trimmed.length; i++) {
                String temp = Double.toString(Math.abs(trimmed[i]));
                int decimals = temp.length() - temp.indexOf('.') - 1;
                trimmed[i] = new BigDecimal(trimmed[i]).setScale(decimals - 1,
                        RoundingMode.HALF_UP).doubleValue();
            }
            j--;
        }
        return Arrays.asList(score(origCoeff), origCoeff);
    }
}