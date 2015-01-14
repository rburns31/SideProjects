package bracketbuster;

import java.io.IOException;
import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;

/**
 *
 * @author Ryan Burns
 */
public class BracketBuster {
    /**
     * 
     */
    private static final int SPREAD = 32;
    /**
     * 
     */
    private static final int LIMIT = 140;

    /**
     * The number of trials to be run in this iteration
     */
    private final int trials;
    /**
     * 
     */
    private final double[] constants;
    /**
     * 
     */
    private final double[] best;
    /**
     * 
     */
    private final int[] winnerPos;
    /**
     * 
     */
    private int max;
    /**
     * Currently has no effect, ideally it would correspond to 0 for a normal
     *     formula generation, 1 for high seed, and 2 for actual results
     */
    private int typeChoice;
    /**
     * 
     */
    private int counter;

    /**
     * 
     */
    public static final HashMap<String, Integer> YEAR_TO_SIZE;
    static {
        YEAR_TO_SIZE = new HashMap<>();
        YEAR_TO_SIZE.put("2010", 13);
        YEAR_TO_SIZE.put("2011", 13);
        YEAR_TO_SIZE.put("2012(1)", 13);
        YEAR_TO_SIZE.put("2013", 13);
        YEAR_TO_SIZE.put("2014(1)", 13);
        YEAR_TO_SIZE.put("2012(2)", 21);
        YEAR_TO_SIZE.put("2014(3)", 20);
    }

    public BracketBuster(int trials) {
        this.trials = trials;
        constants = new double[YEAR_TO_SIZE.get(Driver.YEAR)];
        best = new double[YEAR_TO_SIZE.get(Driver.YEAR)];
        winnerPos = new int[63];
    }

    public BracketBuster(int trials, int typeChoice) {
        this(trials);
        this.typeChoice = typeChoice;
    }

    public int getTrials() {
        return trials;
    }

    public double[] getBest() {
        return best;
    }

    public int getMax() {
        return max;
    }

    public int[] getWinnerPos() {
        return winnerPos;
    }

    public int getCounter() {
        return counter;
    }

    /**
     * 
     * @return 
     */
    public int maxFind() {
        for (int i = 0; i < trials; i++) {
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
                        counter++;
                        max = maxTune(SPREAD / Math.pow(Math.sqrt(2), k + 1));
                    }
                }
            }
        }
        return max;
    }

    /**
     * 
     * @param spread
     * @return 
     */
    public int maxTune(double spread) {
        for (int i = 1; i < trials; i++) {
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
        System.arraycopy(best, 0, constants, 0, best.length - 1);
        return max;
    }

    /**
     * 
     * @param constants The coefficients specific to the formula being scored
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
            File dataFile = new File("stats_" + Driver.YEAR + "_3" + ".txt");
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
         * Sets up each team's composite z score and actual worth
         */
        for (int i = 0; i < scores.length; i++) {
            worths[i] = teams[i][constants.length - 1];
            for (int j = 0; j < constants.length; j++) {
                scores[i] += constants[j] * teams[i][j];
            }
        }

        for (int round = 0; round < 6; round++) {
            int value = (int)(Math.pow(2, round + 1));
            int[] compare = new int[value];
            double[] test1 = new double[value];
            double[] test2 = new double[value];
            int loopVar = (int)(Math.pow(2, 5 - round));
            for (int i = 0; i < loopVar; i++) {
                for (int j = 0; j < value; j++) {
                    compare[j] = (value * i) + j;
                    test1[j] = scores[compare[j]];
                    test2[j] = worths[compare[j]];
                }
                double max1 = test1[0];
                double max2 = test2[0];
                for (int j = 0; j < value; j++) {
                    if (test1[j] > max1) {
                        max1 = test1[j];
                    }
                    if (test2[j] > max2) {
                        max2 = test2[j];
                    }
                }
                Arrays.sort(test1);
                Arrays.sort(test2);
                int found = 0;
                for (int j = 0; j < scores.length; j++) {
                    if (max1 == scores[j]) {
                        found = j;
                        if (typeChoice == 0) {
                            if (counter < 63) {
                                winnerPos[counter] = j;
                            }
                            counter++;
                        }
                    }
                    if (typeChoice == 2) {
                        if (max2 == worths[j]) {
                            if (counter < 63) {
                                winnerPos[counter] = j;
                            }
                        }
                        counter++;
                    }
                }
                if (worths[found] == test2[value - 1]) {
                    pointsEarned += value / 2;
                }
            }
        }
        return pointsEarned;
    }
}