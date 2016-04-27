import java.io.PrintWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Outputs a visual representation of a set of picks on a hard-coded bracket
 *     text file
 * @author Ryan Burns
 */
public class BracketVisual {
    /**
     * The number of points corresponding to this bracket
     */
    private final int max;
    /**
     * The coefficients which lead to this bracket
     */
    private final double[] best;
    /**
     * The number of trials which lead to this formula (only for the
     *   'Generate Formula' mode
     */
    private final int trials;
    /**
     * The file name that BracketVisual is creating, defaults to bracket.txt
     */
    private final String outputTxt;
    /**
     * The winners of each game in the tournament
     */
    private final String[] winners;
    /**
     * The names of all of the teams in the specified year's tournament
     */
    private final String[] teams;

    public BracketVisual(int[] winnerPos, int max, double[] best, int trials) {
        this.max = max;
        this.best = best;
        this.trials = trials;
        this.outputTxt = "bracket.txt";

        teams = Driver.TEAM_NAMES.get(Driver.YEAR);
        if (teams == null) {
            System.out.println("Cannot make a bracket for that year. Exiting.");
            System.exit(0);
        }

        // Make sure that each team name in that array is valid to be printed
        validate(teams);

        // Extract the team names of the winners from their passed in indices
        winners = new String[63];
        for (int i = 0; i < winners.length; i++) {
            winners[i] = teams[winnerPos[i]];
        }

        show();
    }

    /**
     * Due to the nature of how brackets are visualized in the produced text
     *     file, each team name must be limited to 9 characters to fit on
     *     the hard-coded lines
     * @param teamNames The team names to check for length
     */
    private void validate(String[] teamNames) {
        ArrayList<String> badOnes = new ArrayList<>();
        for (String teamName : teamNames) {
            if (teamName.length() > 9) {
                badOnes.add(teamName);
            }
        }
        if (!badOnes.isEmpty()) {
            System.out.println("The following team names"
                    + " are too long, please revise:");
            for (String badOne : badOnes) {
                System.out.println(badOne);
            }
            System.out.println("Exiting.");
            System.exit(0);
        }
    }

    /**
     * Handles printing all of the applicable diagnostic headers to the file
     * @param outFile The file for these headers to be printed into
     */
    private void printHeaders(PrintWriter outFile) {
        String header1 = "Tournament bracket for year: " + Driver.YEAR;

        String header2 = "Generated at: " + Driver.getTime();

        String header3 = "Scored: " + max + " points";
        if (max == -1) {
            header3 = "Scored: N/A";
        }

        String header4 = "Mode: " + Driver.MODE;
        if (Driver.MODE.equals("Run Formula Batch")) {
            header4 = "Mode: Select Formula";
        }

        String header5 = "Formula: " + Driver.FORMULA;

        String header6 = "Trials: " + trials;

        StringBuilder header7 = new StringBuilder("Coefficients: ");
        for (int i = 0; i < best.length - 1; i++) {
            if (i != 0) {
                header7.append(", ");
            }
            String str = Double.toString(best[i]);
            header7.append(!str.contains(".") ? str
                    : str.replaceAll("0*$", "").replaceAll("\\.$", ""));
        }

        outFile.println(header1);
        outFile.println(header2);
        outFile.println(header3);
        outFile.println(header4);
        if (Driver.MODE.equals("Select Formula") || Driver.MODE.equals("Run Formula Batch")) {
            outFile.println(header5);
            outFile.println(header7);
        } else if (Driver.MODE.equals("Manual Formula")) {
            outFile.println(header7);
        } else if (Driver.MODE.equals("Generate Formula")) {
            outFile.println(header6);
            outFile.println(header7);
        }
        outFile.println();
    }

    /**
     * Creates a file, and fills it with a filled-in bracket based on the input
     *     year and winners
     * Not ideal setup to have this hard-coded like it is, try to fix this
     */
    public final void show() {
        PrintWriter outFile = null;
        try {
            outFile = new PrintWriter(outputTxt);
        } catch (IOException e) {
            System.out.println(
                    "Some problem with writing to the bracket visual.");
        }

        printHeaders(outFile);

        // Parts of the bracket to be printed (for convenience)
        String topLine = "_________";
        String lBottomLine = "_________|";
        String rBottomLine = "|_________";
        String divider = "|";
        String empty = "";
        String champLine = "____________";

        assert(outFile != null);

        // Print the first octets of each side of the bracket
        outFile.printf("%-10s%110s%10s%n", teams[0], empty, teams[32]);
        outFile.printf("%-10s%110s%10s%n", topLine, empty, topLine);
        outFile.printf("%10s%-10s%90s%10s%-10s%n", divider, winners[0], empty, winners[16], divider);
        outFile.printf("%-9s%s%-10s%90s%10s%s%9s%n", teams[1], divider, topLine, empty, topLine, divider, teams[33]);
        outFile.printf("%-10s%10s%90s%-10s%10s%n", lBottomLine, divider, empty, divider, rBottomLine);
        outFile.printf("%20s%-10s%70s%10s%-20s%n", divider, winners[32], empty, winners[40], divider);
        outFile.printf("%-9s%11s%-10s%70s%10s%-11s%9s%n", teams[2], divider, topLine, empty, topLine, divider, teams[34]);
        outFile.printf("%-10s%10s%10s%70s%-10s%-10s%10s%n", topLine, divider, divider, empty, divider, divider, topLine);
        outFile.printf("%10s%-9s%s%10s%70s%-10s%s%9s%-10s%n", divider, winners[1], divider, divider, empty, divider, divider, winners[17], divider);
        outFile.printf("%-9s%s%-10s%10s%70s%-10s%10s%s%9s%n", teams[3], divider, lBottomLine, divider, empty, divider, rBottomLine, divider, teams[35]);
        outFile.printf("%-10s%20s%70s%-20s%10s%n", lBottomLine, divider, empty, divider, rBottomLine);
        outFile.printf("%30s%-10s%50s%10s%-30s%n", divider, winners[48], empty, winners[52], divider);
        outFile.printf("%-10s%20s%-10s%50s%10s%-20s%10s%n", teams[4], divider, topLine, empty, topLine, divider, teams[36]);
        outFile.printf("%-10s%20s%10s%50s%-10s%-20s%10s%n", topLine, divider, divider, empty, divider, divider, topLine);
        outFile.printf("%10s%-10s%10s%10s%50s%-10s%-10s%10s%-10s%n", divider, winners[2], divider, divider, empty, divider, divider, winners[18], divider);
        outFile.printf("%-9s%s%-10s%10s%10s%50s%-10s%-10s%10s%s%9s%n", teams[5], divider, topLine, divider, divider, empty, divider, divider, topLine, divider, teams[37]);
        outFile.printf("%-10s%10s%10s%10s%50s%-10s%-10s%-10s%10s%n", lBottomLine, divider, divider, divider, empty, divider, divider, divider, rBottomLine);
        outFile.printf("%20s%-9s%s%10s%50s%-10s%s%9s%-20s%n", divider, winners[33], divider, divider, empty, divider, divider, winners[41], divider);
        outFile.printf("%-9s%11s%-10s%10s%50s%-10s%10s%-11s%9s%n", teams[6], divider, lBottomLine, divider, empty, divider, rBottomLine, divider, teams[38]);
        outFile.printf("%-10s%10s%20s%50s%-20s%-10s%10s%n", topLine, divider, divider, empty, divider, divider, topLine);
        outFile.printf("%10s%-9s%-11s%10s%50s%-10s%11s%9s%-10s%n", divider, winners[3], divider, divider, empty, divider, divider, winners[19], divider);
        outFile.printf("%-9s%s%-10s%20s%50s%-20s%10s%s%9s%n", teams[7], divider, lBottomLine, divider, empty, divider, rBottomLine, divider, teams[39]);
        outFile.printf("%-10s%30s%50s%-30s%10s%n", lBottomLine, divider, empty, divider, rBottomLine);

        // Print the second octets of each side of the bracket (and the winner)
        outFile.printf("%40s%-10s%30s%10s%-40s%n", divider, winners[56], empty, winners[58], divider);
        outFile.printf("%-10s%30s%-10s%30s%10s%-30s%10s%n", teams[8], divider, topLine, empty, topLine, divider, teams[40]);
        outFile.printf("%-10s%30s%10s%30s%-10s%-30s%10s%n", topLine, divider, divider, empty, divider, divider, topLine);
        outFile.printf("%10s%-10s%20s%10s%30s%-10s%-20s%10s%-10s%n", divider, winners[4], divider, divider, empty, divider, divider, winners[20], divider);
        outFile.printf("%-9s%s%-10s%20s%10s%30s%-10s%-20s%10s%s%9s%n", teams[9], divider, topLine, divider, divider, empty, divider, divider, topLine, divider, teams[41]);
        outFile.printf("%-10s%10s%20s%10s%30s%-10s%-20s%-10s%10s%n", lBottomLine, divider, divider, divider, empty, divider, divider, divider, rBottomLine);
        outFile.printf("%20s%-10s%10s%10s%30s%-10s%-10s%10s%-20s%n", divider, winners[34], divider, divider, empty, divider, divider, winners[42], divider);
        outFile.printf("%-9s%11s%-10s%10s%10s%30s%-10s%-10s%10s%-11s%9s%n", teams[10], divider, topLine, divider, divider, empty, divider, divider, topLine, divider, teams[42]);
        outFile.printf("%-10s%10s%10s%10s%10s%30s%-10s%-10s%-10s%-10s%10s%n", topLine, divider, divider, divider, divider, empty, divider, divider, divider, divider, topLine);
        outFile.printf("%10s%-9s%s%10s%10s%10s%30s%-10s%-10s%-10s%s%9s%-10s%n",
                        divider, winners[5], divider, divider, divider, divider, empty, divider, divider, divider, divider, winners[21], divider);
        outFile.printf("%-9s%s%-10s%10s%10s%10s%30s%-10s%-10s%-10s%10s%s%9s%n",
                        teams[11], divider, lBottomLine, divider, divider, divider, empty, divider, divider, divider, rBottomLine, divider, teams[43]);
        outFile.printf("%-10s%20s%10s%10s%30s%-10s%-10s%-20s%10s%n", lBottomLine, divider, divider, divider, empty, divider, divider, divider, rBottomLine);
        outFile.printf("%30s%-9s%s%10s%30s%-10s%s%9s%-30s%n", divider, winners[49], divider, divider, empty, divider, divider, winners[53], divider);
        outFile.printf("%-10s%20s%10s%10s%30s%-10s%10s%-20s%10s%n", teams[12], divider, lBottomLine, divider, empty, divider, rBottomLine, divider, teams[44]);
        outFile.printf("%-10s%20s%20s%30s%-20s%-20s%10s%n", topLine, divider, divider, empty, divider, divider, topLine);
        outFile.printf("%10s%-10s%10s%20s%30s%-20s%-10s%10s%-10s%n", divider, winners[6], divider, divider, empty, divider, divider, winners[22], divider);
        outFile.printf("%-9s%s%-10s%10s%20s%30s%-20s%-10s%10s%s%9s%n", teams[13], divider, topLine, divider, divider, empty, divider, divider, topLine, divider, teams[45]);
        outFile.printf("%-10s%10s%10s%20s%30s%-20s%-10s%-10s%10s%n", lBottomLine, divider, divider, divider, empty, divider, divider, divider, rBottomLine);
        outFile.printf("%20s%-9s%-11s%10s%10s%-10s%10s%-10s%11s%9s%-20s%n", divider, winners[35], divider, divider, empty, winners[62], empty, divider, divider, winners[43], divider);
        outFile.printf("%-9s%11s%-10s%20s%9s%12s%9s%-20s%10s%-11s%9s%n", teams[14], divider, lBottomLine, divider, empty, champLine, empty, divider, rBottomLine, divider, teams[46]);
        outFile.printf("%-10s%10s%30s%30s%-30s%-10s%10s%n", topLine, divider, divider, empty, divider, divider, topLine);
        outFile.printf("%10s%-9s%-11s%20s%30s%-20s%11s%9s%-10s%n", divider, winners[7], divider, divider, empty, divider, divider, winners[23], divider);
        outFile.printf("%-9s%s%-10s%30s%30s%-30s%10s%s%9s%n", teams[15], divider, lBottomLine, divider, empty, divider, rBottomLine, divider, teams[47]);
        outFile.printf("%-10s%40s%30s%-40s%10s%n", lBottomLine, divider, empty, divider, rBottomLine);

        // Print the third octets of each side of the bracket (and the semis)
        outFile.printf("%50s%-10s%10s%10s%-50s%n", divider, winners[60], empty, winners[61], divider);
        outFile.printf("%-10s%40s%-10s%10s%10s%-40s%10s%n", teams[16], divider, topLine, empty, topLine, divider, teams[48]);
        outFile.printf("%-10s%40s%30s%-40s%10s%n", topLine, divider, empty, divider, topLine);
        outFile.printf("%10s%-10s%30s%30s%-30s%10s%-10s%n", divider, winners[8], divider, empty, divider, winners[24], divider);
        outFile.printf("%-9s%s%-10s%30s%30s%-30s%10s%s%9s%n", teams[17], divider, topLine, divider, empty, divider, topLine, divider, teams[49]);
        outFile.printf("%-10s%10s%30s%30s%-30s%-10s%10s%n", lBottomLine, divider, divider, empty, divider, divider, rBottomLine);
        outFile.printf("%20s%-10s%20s%30s%-20s%10s%-20s%n", divider, winners[36], divider, empty, divider, winners[44], divider);
        outFile.printf("%-9s%11s%-10s%20s%30s%-20s%10s%-11s%9s%n", teams[18], divider, topLine, divider, empty, divider, topLine, divider, teams[50]);
        outFile.printf("%-10s%10s%10s%20s%30s%-20s%-10s%-10s%10s%n", topLine, divider, divider, divider, empty, divider, divider, divider, topLine);
        outFile.printf("%10s%-9s%s%10s%20s%30s%-20s%-10s%s%9s%-10s%n", divider, winners[9], divider, divider, divider, empty, divider, divider, divider, winners[25], divider);
        outFile.printf("%-9s%s%-10s%10s%20s%30s%-20s%-10s%10s%s%9s%n", teams[19], divider, lBottomLine, divider, divider, empty, divider, divider, rBottomLine, divider, teams[51]);
        outFile.printf("%-10s%20s%20s%30s%-20s%-20s%10s%n", lBottomLine, divider, divider, empty, divider, divider, rBottomLine);
        outFile.printf("%30s%-10s%10s%30s%-10s%10s%-30s%n", divider, winners[50], divider, empty, divider, winners[54], divider);
        outFile.printf("%-10s%20s%-10s%10s%30s%-10s%10s%-20s%10s%n", teams[20], divider, topLine, divider, empty, divider, topLine, divider, teams[52]);
        outFile.printf("%-10s%20s%10s%10s%30s%-10s%-10s%-20s%10s%n", topLine, divider, divider, divider, empty, divider, divider, divider, topLine);
        outFile.printf("%10s%-10s%10s%10s%10s%30s%-10s%-10s%-10s%10s%-10s%n", divider, winners[10], divider, divider, divider, empty, divider, divider, divider, winners[26], divider);
        outFile.printf("%-9s%s%-10s%10s%10s%10s%30s%-10s%-10s%-10s%10s%s%9s%n",
                        teams[21], divider, topLine, divider, divider, divider, empty, divider, divider, divider, topLine, divider, teams[53]);
        outFile.printf("%-10s%10s%10s%10s%10s%30s%-10s%-10s%-10s%-10s%10s%n", lBottomLine, divider, divider, divider, divider, empty, divider, divider, divider, divider, rBottomLine);
        outFile.printf("%20s%-9s%s%10s%10s%30s%-10s%-10s%s%9s%-20s%n", divider, winners[37], divider, divider, divider, empty, divider, divider, divider, winners[45], divider);
        outFile.printf("%-9s%11s%-10s%10s%10s%30s%-10s%-10s%10s%-11s%9s%n", teams[22], divider, lBottomLine, divider, divider, empty, divider, divider, rBottomLine, divider, teams[54]);
        outFile.printf("%-10s%10s%20s%10s%30s%-10s%-20s%-10s%10s%n", topLine, divider, divider, divider, empty, divider, divider, divider, topLine);
        outFile.printf("%10s%-9s%-11s%10s%10s%30s%-10s%-10s%11s%9s%-10s%n", divider, winners[11], divider, divider, divider, empty, divider, divider, divider, winners[27], divider);
        outFile.printf("%-9s%s%-10s%20s%10s%30s%-10s%-20s%10s%s%9s%n", teams[23], divider, lBottomLine, divider, divider, empty, divider, divider, rBottomLine, divider, teams[55]);
        outFile.printf("%-10s%30s%10s%30s%-10s%-30s%10s%n", lBottomLine, divider, divider, empty, divider, divider, rBottomLine);

        // Print the fourth octets of each side of the bracket
        outFile.printf("%40s%-9s%s%30s%s%9s%-40s%n", divider, winners[57], divider, empty, divider, winners[59], divider);
        outFile.printf("%-10s%30s%-10s%30s%10s%-30s%10s%n", teams[24], divider, lBottomLine, empty, rBottomLine, divider, teams[56]);
        outFile.printf("%-10s%30s%50s%-30s%10s%n", topLine, divider, empty, divider, topLine);
        outFile.printf("%10s%-10s%20s%50s%-20s%10s%-10s%n", divider, winners[12], divider, empty, divider, winners[28], divider);
        outFile.printf("%-9s%s%-10s%20s%50s%-20s%10s%s%9s%n", teams[25], divider, topLine, divider, empty, divider, topLine, divider, teams[57]);
        outFile.printf("%-10s%10s%20s%50s%-20s%-10s%10s%n", lBottomLine, divider, divider, empty, divider, divider, rBottomLine);
        outFile.printf("%20s%-10s%10s%50s%-10s%10s%-20s%n", divider, winners[38], divider, empty, divider, winners[46], divider);
        outFile.printf("%-9s%11s%-10s%10s%50s%-10s%10s%-11s%9s%n", teams[26], divider, topLine, divider, empty, divider, topLine, divider, teams[58]);
        outFile.printf("%-10s%10s%10s%10s%50s%-10s%-10s%-10s%10s%n", topLine, divider, divider, divider, empty, divider, divider, divider, topLine);
        outFile.printf("%10s%-9s%s%10s%10s%50s%-10s%-10s%s%9s%-10s%n", divider, winners[13], divider, divider, divider, empty, divider, divider, divider, winners[29], divider);
        outFile.printf("%-9s%s%-10s%10s%10s%50s%-10s%-10s%10s%s%9s%n", teams[27], divider, lBottomLine, divider, divider, empty, divider, divider, rBottomLine, divider, teams[59]);
        outFile.printf("%-10s%20s%10s%50s%-10s%-20s%10s%n", lBottomLine, divider, divider, empty, divider, divider, rBottomLine);
        outFile.printf("%30s%-9s%s%50s%s%9s%-30s%n", divider, winners[51], divider, empty, divider, winners[55], divider);
        outFile.printf("%-10s%20s%10s%50s%10s%-20s%10s%n", teams[28], divider, lBottomLine, empty, rBottomLine, divider, teams[60]);
        outFile.printf("%-10s%20s%70s%-20s%10s%n", topLine, divider, empty, divider, topLine);
        outFile.printf("%10s%-10s%10s%70s%-10s%10s%-10s%n", divider, winners[14], divider, empty, divider, winners[30], divider);
        outFile.printf("%-9s%s%-10s%10s%70s%-10s%10s%s%9s%n", teams[29], divider, topLine, divider, empty, divider, topLine, divider, teams[61]);
        outFile.printf("%-10s%10s%10s%70s%-10s%-10s%10s%n", lBottomLine, divider, divider, empty, divider, divider, rBottomLine);
        outFile.printf("%20s%-9s%-11s%50s%11s%9s%-20s%n", divider, winners[39], divider, empty, divider, winners[47], divider);
        outFile.printf("%-9s%11s%-10s%70s%10s%-11s%9s%n", teams[30], divider, lBottomLine, empty, rBottomLine, divider, teams[62]);
        outFile.printf("%-10s%10s%90s%-10s%10s%n", topLine, divider, empty, divider, topLine);
        outFile.printf("%10s%-9s%-11s%70s%11s%9s%-10s%n", divider, winners[15], divider, empty, divider, winners[31], divider);
        outFile.printf("%-9s%s%-10s%100s%s%9s%n", teams[31], divider, lBottomLine, rBottomLine, divider, teams[63]);
        outFile.printf("%-10s%110s%10s%n", lBottomLine, empty, rBottomLine);

        outFile.close();
    }    
}