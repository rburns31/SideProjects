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
    private static final int SPREAD = 32;
    private static final int LIMIT = 140;

    private final int trials;
    private final double[] constants;
    private final double[] best;
    private final int[] winnerPos;
    private int max;
    private int typeChoice;
    private int counter;

    private static final HashMap<String, Integer> YEAR_TO_SIZE;
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
        for (int i = 1; i < trials; i++) {
            if (i % 500 == 0) {
                System.out.println(i);
            }
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
     * @param constants
     * @return 
     */
    public int score(double[] constants) {
        double[][] teams = new double[64][constants.length];
        double[] worths = new double[64];
        double[] scores = new double[64];
        int points = 0;
        File dataFile = new File("stats_" + Driver.YEAR + "_3" + ".txt");
        int m = 0;
        try {
            Scanner fileScanner = new Scanner(dataFile);
            while (fileScanner.hasNext()) {
                String line = fileScanner.nextLine();
                for (int i = 0; i < constants.length; i++) {
                    teams[m][i] = Double.parseDouble((line.split(" ", 0))[i]);
                }
                m++;
            }
            fileScanner.close();
        } catch (IOException e) {
            System.out.println("Data file does not exist, exiting.");
            System.exit(0);
        }

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
                    points += value / 2;
                }
            }
        }
        return points;
    }
}