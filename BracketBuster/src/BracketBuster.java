import java.io.IOException;
import java.io.File;
import java.util.Arrays;
import java.util.Scanner;

public class BracketBuster {

    private final int trials;
    private final int spread = 32;
    private double[] constants;
    protected double[] best;
    protected int max = 0;
    private int[] winnerPos = new int[63];
    private int size;
    private int typeChoice;
    private int limit;
    private int counter;
	
    public BracketBuster(int trials) {
        this.trials = trials;
        if (Driver.YEAR == "2010" || Driver.YEAR == "2011" || Driver.YEAR == "2012(1)" || Driver.YEAR == "2013" || Driver.YEAR == "2014(1)") {
            size = 13;
        } else if (Driver.YEAR == "2012(2)") {
            size = 21;
        } else if (Driver.YEAR == "2014(3)") {
            size = 20;
        }
        constants = new double[size];
        best = new double[size];
    }

    public BracketBuster(int trials, int typeChoice) {
        this(trials);
        this.typeChoice = typeChoice;
    }

    public int getTrials() {
        return trials;
    }

    public int[] getWinnerPos() {
        return winnerPos;
    }

    public int getCounter() {
        return counter;
    }
    
    public int maxFind() {
   	    for(int i = 1; i < trials; i++) {
   	        if (i % 500 == 0) {
   	            System.out.println(i);
   	        }
   	        for (int j = 0; j < constants.length - 1; j++) {
   	            constants[j] = (Math.random() * spread) - (spread / 2);
   	            int current = score(constants);		
                if(current > max) {
               	    max = current;
               	    for (int k = 0; k < constants.length - 1; k++) {
               	        best[k] = constants[k];
               	    }
                }
                limit = 140;
                if(current > limit) {
               	    for (int k = 0; k < 10; k++) {
               	        counter++;
               	        max = maxTune(spread / Math.pow(Math.sqrt(2), k + 1));
               	    }
                }
   	        }
        }
        return max;
    }

    public int maxTune(double spread) {
   	    for(int i = 1; i < trials; i++) {
   	        for (int j = 0; j < constants.length - 1; j++) {
   	            constants[j] += (Math.random() * spread) - (spread / 2);
   	            int current = score(constants);        		
                if(current > max) {
               	    max = current;
               	    for (int k = 0; k < constants.length - 1; k++) {
               	        best[k] = constants[k];
               	    }
                }
   	        }
        }
   	    for (int i = 0; i < best.length - 1; i++) {
   	        constants[i] = best[i];
   	    }
        return max;
    }

    public int score(double[] constants) {
        int counter = 0;
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
                    teams[m][i] = Double.parseDouble((line.split(" ",0))[i]);
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
            int value = (int) (Math.pow(2, round + 1));
            int[] compare = new int[value];
            double[] test1 = new double[value];
            double[] test2 = new double[value];
            int loopVar = (int) (Math.pow(2, 5 - round));
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