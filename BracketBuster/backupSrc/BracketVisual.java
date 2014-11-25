import java.io.PrintWriter;
import java.io.IOException;
import java.util.ArrayList;

public class BracketVisual {
    
    private final int[] winnerPos;
    private String outputTxt = "bracket.txt";
    private String timeStr;
    
    public BracketVisual(int[] winnerPos, String timeStr) {
        this.winnerPos = winnerPos;
        this.timeStr = timeStr;
    }
    
    public BracketVisual(int[] winnerPos, String timeStr, boolean keepOnFile) {
        this(winnerPos, timeStr);
        if (keepOnFile) {
            this.outputTxt = timeStr + ".txt";
        }
    }

    private static void validate(String[] inputs) {
        ArrayList<String> badOnes = new ArrayList<String>();
        for (int i = 0; i < inputs.length; i++) {
            if (inputs[i].length() > 9) {
                badOnes.add(inputs[i]);
            }
        }
        if (!badOnes.isEmpty()) {
            System.out.println("The following team names are too long, please revise:");
            for (int i = 0; i < badOnes.size(); i++) {
                System.out.println(badOnes.get(i));
            }
            System.out.println("Exiting.");
            System.exit(0);
        }
    }

    protected void show() throws IOException {
        String[] names2010 = {"Kansas", "Lehigh", "UNLV", "N Iowa", "Mich St", "NM St", "Maryland", "Houston",
                                "Tenn", "San D St", "G'town", "Ohio", "Okla St", "Ga Tech", "Ohio St", "UCSB",
                                "Syracuse", "Vermont", "Gonzaga", "Fla St", "Butler", "UTEP", "Vandy", "Murray St",
                                "Xavier", "Minnesota", "Pitt", "Oakland", "BYU", "Florida", "Kansas St", "N Texas",
                                "Kentucky", "E Tenn St", "Texas", "Wake For", "Temple", "Cornell", "Wisconsin", "Wofford",
                                "Marquette", "Washingt", "New Mexic", "Montana", "Clemson", "Missouri", "WVU", "Morgan St",
                                "Duke", "Ark PB", "Cal", "L'ville", "Texas A&M", "Utah St", "Purdue", "Siena",
                                "ND", "Old Domin", "Baylor", "Sam H St", "Richmond", "St Marys", "Villanova", "Robert M"};
        String[] names2011 = {"Ohio St", "UTSA", "G Mason", "Villanova", "WVU", "Clemson", "Kentucky", "Princeton",
                                "Xavier", "Marquette", "Syracuse", "Ind St", "Washingt", "Georgia", "UNC", "LIU Bkn",
                                "Duke", "Hampton", "Michigan", "Tenn", "Arizona", "Memphis", "Texas", "Oakland",
                                "Cincy", "Missouri", "UConn", "Bucknell", "Temple", "Penn St", "San D St", "N Col",
                                "Kansas", "Boston U", "UNLV", "Illinois", "Vandy", "Richmond", "L'ville", "M'head St",
                                "G'town", "VCU", "Purdue", "St Peters", "Texas A&M", "Fla St", "ND", "Akron",
                                "Pitt", "UNC Ash", "Butler", "Old Domin", "Kansas St", "Utah St", "Wisconsin", "Belmont",
                                "St Johns", "Gonzaga", "BYU", "Wofford", "UCLA", "Mich St", "Florida", "UCSB"};
        String[] names2012 = {"Kentucky", "W KY", "Iowa St", "UConn", "Wich St", "VCU", "Indiana", "NM St",
                                "UNLV", "Colorado", "Baylor", "S Dak St", "ND", "Xavier", "Duke", "Lehigh",
                                "Mich St", "LIU Bkn", "Memphis", "St Louis", "New Mexic", "LB St", "L'ville", "Davidson",
                                "Murray St", "Col St", "Marquette", "BYU", "Florida", "Virginia", "Missouri", "Norf St",
                                "Syracuse", "UNC Ash", "Kansas St", "So Miss", "Vandy", "Harvard", "Wisconsin", "Montana",
                                "Cincy", "Texas", "Fla St", "St Bonnys", "Gonzaga", "WVU", "Ohio St", "Loyola MD",
                                "UNC", "Vermont", "Creighton", "Alabama", "Temple", "South Fla", "Michigan", "Ohio",
                                "San D St", "NC St", "G'town", "Belmont", "St Marys", "Purdue", "Kansas", "Detroit"};
        String[] names2013 = {"L'ville", "NC A&T", "Col St", "Missouri", "Okla St", "Oregon", "St Louis", "NM St",
                                "Memphis", "St Marys", "Mich St", "Valpo", "Creighton", "Cincy", "Duke", "Albany",
                                "Gonzaga", "Southern", "Pitt", "Wich St", "Wisconsin", "Ole Miss", "Kansas St", "La Salle",
                                "Arizona", "Belmont", "New Mexic", "Harvard", "ND", "Iowa St", "Ohio St", "Iona",
                                "Kansas", "W KY", "UNC", "Villanova", "VCU", "Akron", "Michigan", "S Dak St",
                                "UCLA", "Minnesota", "Florida", "NW St", "San D St", "Oklahoma", "G'town", "Fla GC",
                                "Indiana", "James Mad", "NC St", "Temple", "UNLV", "Cal", "Syracuse", "Montana",
                                "Butler", "Bucknell", "Marquette", "Davidson", "Illinois", "Colorado", "Miami", "Pacific"};
        String[] names2014 = {"Florida", "Albany", "Colorado", "Pitt", "VCU", "SF Austin", "UCLA", "Tulsa",
                                "Ohio St", "Dayton", "Syracuse", "W Mich", "New Mexic", "Stanford", "Kansas", "E KY",
                                "Virginia", "Coast Car", "Memphis", "G Wash", "Cincy", "Harvard", "Mich St", "Delaware",
                                "UNC", "Providenc", "Iowa St", "NC Cen", "UConn", "St Joes", "Villanova", "Milwaukee",
                                "Arizona", "Weber St", "Gonzaga", "Okla St", "Oklahoma", "N Dak St", "San D St", "NM St",
                                "Baylor", "Nebraska", "Creighton", "LA Lafay", "Oregon", "BYU", "Wisconsin", "American",
                                "Wich St", "Cal Poly", "Kentucky", "Kansas St", "St Louis", "NC St", "L'ville", "Manhattan",
                                "UMass", "Tenn", "Duke", "Mercer", "Texas", "ASU", "Michigan", "Wofford"};
        PrintWriter outFile = new PrintWriter(outputTxt);
        String topLine = "_________";
        String lBottomLine = "_________|";
        String rBottomLine = "|_________";
        String divider = "|";
        String empty = "";
        String fine = "____________";
        String header = "Displaying bracket for year: " + Driver.YEAR;
        String header2 = "Generated: " + timeStr;
        String[] names = new String[64];
        String[] winners = new String[63];
        if (Driver.YEAR == "2010") {
            names = names2010;
        } else if (Driver.YEAR == "2011") {
            names = names2011;
        } else if (Driver.YEAR == "2012(1)" || Driver.YEAR == "2012(2)") {
            names = names2012;
        } else if (Driver.YEAR == "2013") {
            names = names2013;
        } else if (Driver.YEAR == "2014(1)" || Driver.YEAR == "2014(3)") {
            names = names2014;
        } else {
            System.out.println("I cannot yet make a bracket for that year. Exiting.");
            System.exit(0);
        }
        for (int i = 0; i < winners.length; i++) {
            winners[i] = names[winnerPos[i]];
        }
        BracketVisual.validate(names);

        outFile.println(header);
        outFile.println(header2);
        outFile.println();
        outFile.printf("%-10s%110s%10s%n", names[0], empty, names[32]);
        outFile.printf("%-10s%110s%10s%n", topLine, empty, topLine);
        outFile.printf("%10s%-10s%90s%10s%-10s%n", divider, winners[0], empty, winners[16], divider);
        outFile.printf("%-9s%s%-10s%90s%10s%s%9s%n", names[1], divider, topLine, empty, topLine, divider, names[33]);
        outFile.printf("%-10s%10s%90s%-10s%10s%n", lBottomLine, divider, empty, divider, rBottomLine);
        outFile.printf("%20s%-10s%70s%10s%-20s%n", divider, winners[32], empty, winners[40], divider);
        outFile.printf("%-9s%11s%-10s%70s%10s%-11s%9s%n", names[2], divider, topLine, empty, topLine, divider, names[34]);
        outFile.printf("%-10s%10s%10s%70s%-10s%-10s%10s%n", topLine, divider, divider, empty, divider, divider, topLine);
        outFile.printf("%10s%-9s%s%10s%70s%-10s%s%9s%-10s%n", divider, winners[1], divider, divider, empty, divider, divider, winners[17], divider);
        outFile.printf("%-9s%s%-10s%10s%70s%-10s%10s%s%9s%n", names[3], divider, lBottomLine, divider, empty, divider, rBottomLine, divider, names[35]);
        outFile.printf("%-10s%20s%70s%-20s%10s%n", lBottomLine, divider, empty, divider, rBottomLine);
        outFile.printf("%30s%-10s%50s%10s%-30s%n", divider, winners[48], empty, winners[52], divider);
        outFile.printf("%-10s%20s%-10s%50s%10s%-20s%10s%n", names[4], divider, topLine, empty, topLine, divider, names[36]);
        outFile.printf("%-10s%20s%10s%50s%-10s%-20s%10s%n", topLine, divider, divider, empty, divider, divider, topLine);
        outFile.printf("%10s%-10s%10s%10s%50s%-10s%-10s%10s%-10s%n", divider, winners[2], divider, divider, empty, divider, divider, winners[18], divider);
        outFile.printf("%-9s%s%-10s%10s%10s%50s%-10s%-10s%10s%s%9s%n", names[5], divider, topLine, divider, divider, empty, divider, divider, topLine, divider, names[37]);
        outFile.printf("%-10s%10s%10s%10s%50s%-10s%-10s%-10s%10s%n", lBottomLine, divider, divider, divider, empty, divider, divider, divider, rBottomLine);
        outFile.printf("%20s%-9s%s%10s%50s%-10s%s%9s%-20s%n", divider, winners[33], divider, divider, empty, divider, divider, winners[41], divider);
        outFile.printf("%-9s%11s%-10s%10s%50s%-10s%10s%-11s%9s%n", names[6], divider, lBottomLine, divider, empty, divider, rBottomLine, divider, names[38]);
        outFile.printf("%-10s%10s%20s%50s%-20s%-10s%10s%n", topLine, divider, divider, empty, divider, divider, topLine);
        outFile.printf("%10s%-9s%-11s%10s%50s%-10s%11s%9s%-10s%n", divider, winners[3], divider, divider, empty, divider, divider, winners[19], divider);
        outFile.printf("%-9s%s%-10s%20s%50s%-20s%10s%s%9s%n", names[7], divider, lBottomLine, divider, empty, divider, rBottomLine, divider, names[39]);
        outFile.printf("%-10s%30s%50s%-30s%10s%n", lBottomLine, divider, empty, divider, rBottomLine);

        outFile.printf("%40s%-10s%30s%10s%-40s%n", divider, winners[56], empty, winners[58], divider);
        outFile.printf("%-10s%30s%-10s%30s%10s%-30s%10s%n", names[8], divider, topLine, empty, topLine, divider, names[40]);
        outFile.printf("%-10s%30s%10s%30s%-10s%-30s%10s%n", topLine, divider, divider, empty, divider, divider, topLine);
        outFile.printf("%10s%-10s%20s%10s%30s%-10s%-20s%10s%-10s%n", divider, winners[4], divider, divider, empty, divider, divider, winners[20], divider);
        outFile.printf("%-9s%s%-10s%20s%10s%30s%-10s%-20s%10s%s%9s%n", names[9], divider, topLine, divider, divider, empty, divider, divider, topLine, divider, names[41]);
        outFile.printf("%-10s%10s%20s%10s%30s%-10s%-20s%-10s%10s%n", lBottomLine, divider, divider, divider, empty, divider, divider, divider, rBottomLine);
        outFile.printf("%20s%-10s%10s%10s%30s%-10s%-10s%10s%-20s%n", divider, winners[34], divider, divider, empty, divider, divider, winners[42], divider);
        outFile.printf("%-9s%11s%-10s%10s%10s%30s%-10s%-10s%10s%-11s%9s%n", names[10], divider, topLine, divider, divider, empty, divider, divider, topLine, divider, names[42]);
        outFile.printf("%-10s%10s%10s%10s%10s%30s%-10s%-10s%-10s%-10s%10s%n", topLine, divider, divider, divider, divider, empty, divider, divider, divider, divider, topLine);
        outFile.printf("%10s%-9s%s%10s%10s%10s%30s%-10s%-10s%-10s%s%9s%-10s%n",
                        divider, winners[5], divider, divider, divider, divider, empty, divider, divider, divider, divider, winners[21], divider);
        outFile.printf("%-9s%s%-10s%10s%10s%10s%30s%-10s%-10s%-10s%10s%s%9s%n",
                        names[11], divider, lBottomLine, divider, divider, divider, empty, divider, divider, divider, rBottomLine, divider, names[43]);
        outFile.printf("%-10s%20s%10s%10s%30s%-10s%-10s%-20s%10s%n", lBottomLine, divider, divider, divider, empty, divider, divider, divider, rBottomLine);
        outFile.printf("%30s%-9s%s%10s%30s%-10s%s%9s%-30s%n", divider, winners[49], divider, divider, empty, divider, divider, winners[53], divider);
        outFile.printf("%-10s%20s%10s%10s%30s%-10s%10s%-20s%10s%n", names[12], divider, lBottomLine, divider, empty, divider, rBottomLine, divider, names[44]);
        outFile.printf("%-10s%20s%20s%30s%-20s%-20s%10s%n", topLine, divider, divider, empty, divider, divider, topLine);
        outFile.printf("%10s%-10s%10s%20s%30s%-20s%-10s%10s%-10s%n", divider, winners[6], divider, divider, empty, divider, divider, winners[22], divider);
        outFile.printf("%-9s%s%-10s%10s%20s%30s%-20s%-10s%10s%s%9s%n", names[13], divider, topLine, divider, divider, empty, divider, divider, topLine, divider, names[45]);
        outFile.printf("%-10s%10s%10s%20s%30s%-20s%-10s%-10s%10s%n", lBottomLine, divider, divider, divider, empty, divider, divider, divider, rBottomLine);
        outFile.printf("%20s%-9s%-11s%10s%10s%-10s%10s%-10s%11s%9s%-20s%n", divider, winners[35], divider, divider, empty, winners[62], empty, divider, divider, winners[43], divider);
        outFile.printf("%-9s%11s%-10s%20s%9s%12s%9s%-20s%10s%-11s%9s%n", names[14], divider, lBottomLine, divider, empty, fine, empty, divider, rBottomLine, divider, names[46]);
        outFile.printf("%-10s%10s%30s%30s%-30s%-10s%10s%n", topLine, divider, divider, empty, divider, divider, topLine);
        outFile.printf("%10s%-9s%-11s%20s%30s%-20s%11s%9s%-10s%n", divider, winners[7], divider, divider, empty, divider, divider, winners[23], divider);
        outFile.printf("%-9s%s%-10s%30s%30s%-30s%10s%s%9s%n", names[15], divider, lBottomLine, divider, empty, divider, rBottomLine, divider, names[47]);
        outFile.printf("%-10s%40s%30s%-40s%10s%n", lBottomLine, divider, empty, divider, rBottomLine);

        outFile.printf("%50s%-10s%10s%10s%-50s%n", divider, winners[60], empty, winners[61], divider);
        outFile.printf("%-10s%40s%-10s%10s%10s%-40s%10s%n", names[16], divider, topLine, empty, topLine, divider, names[48]);
        outFile.printf("%-10s%40s%30s%-40s%10s%n", topLine, divider, empty, divider, topLine);
        outFile.printf("%10s%-10s%30s%30s%-30s%10s%-10s%n", divider, winners[8], divider, empty, divider, winners[24], divider);
        outFile.printf("%-9s%s%-10s%30s%30s%-30s%10s%s%9s%n", names[17], divider, topLine, divider, empty, divider, topLine, divider, names[49]);
        outFile.printf("%-10s%10s%30s%30s%-30s%-10s%10s%n", lBottomLine, divider, divider, empty, divider, divider, rBottomLine);
        outFile.printf("%20s%-10s%20s%30s%-20s%10s%-20s%n", divider, winners[36], divider, empty, divider, winners[44], divider);
        outFile.printf("%-9s%11s%-10s%20s%30s%-20s%10s%-11s%9s%n", names[18], divider, topLine, divider, empty, divider, topLine, divider, names[50]);
        outFile.printf("%-10s%10s%10s%20s%30s%-20s%-10s%-10s%10s%n", topLine, divider, divider, divider, empty, divider, divider, divider, topLine);
        outFile.printf("%10s%-9s%s%10s%20s%30s%-20s%-10s%s%9s%-10s%n", divider, winners[9], divider, divider, divider, empty, divider, divider, divider, winners[25], divider);
        outFile.printf("%-9s%s%-10s%10s%20s%30s%-20s%-10s%10s%s%9s%n", names[19], divider, lBottomLine, divider, divider, empty, divider, divider, rBottomLine, divider, names[51]);
        outFile.printf("%-10s%20s%20s%30s%-20s%-20s%10s%n", lBottomLine, divider, divider, empty, divider, divider, rBottomLine);
        outFile.printf("%30s%-10s%10s%30s%-10s%10s%-30s%n", divider, winners[50], divider, empty, divider, winners[54], divider);
        outFile.printf("%-10s%20s%-10s%10s%30s%-10s%10s%-20s%10s%n", names[20], divider, topLine, divider, empty, divider, topLine, divider, names[52]);
        outFile.printf("%-10s%20s%10s%10s%30s%-10s%-10s%-20s%10s%n", topLine, divider, divider, divider, empty, divider, divider, divider, topLine);
        outFile.printf("%10s%-10s%10s%10s%10s%30s%-10s%-10s%-10s%10s%-10s%n", divider, winners[10], divider, divider, divider, empty, divider, divider, divider, winners[26], divider);
        outFile.printf("%-9s%s%-10s%10s%10s%10s%30s%-10s%-10s%-10s%10s%s%9s%n",
                        names[21], divider, topLine, divider, divider, divider, empty, divider, divider, divider, topLine, divider, names[53]);
        outFile.printf("%-10s%10s%10s%10s%10s%30s%-10s%-10s%-10s%-10s%10s%n", lBottomLine, divider, divider, divider, divider, empty, divider, divider, divider, divider, rBottomLine);
        outFile.printf("%20s%-9s%s%10s%10s%30s%-10s%-10s%s%9s%-20s%n", divider, winners[37], divider, divider, divider, empty, divider, divider, divider, winners[45], divider);
        outFile.printf("%-9s%11s%-10s%10s%10s%30s%-10s%-10s%10s%-11s%9s%n", names[22], divider, lBottomLine, divider, divider, empty, divider, divider, rBottomLine, divider, names[54]);
        outFile.printf("%-10s%10s%20s%10s%30s%-10s%-20s%-10s%10s%n", topLine, divider, divider, divider, empty, divider, divider, divider, topLine);
        outFile.printf("%10s%-9s%-11s%10s%10s%30s%-10s%-10s%11s%9s%-10s%n", divider, winners[11], divider, divider, divider, empty, divider, divider, divider, winners[27], divider);
        outFile.printf("%-9s%s%-10s%20s%10s%30s%-10s%-20s%10s%s%9s%n", names[23], divider, lBottomLine, divider, divider, empty, divider, divider, rBottomLine, divider, names[55]);
        outFile.printf("%-10s%30s%10s%30s%-10s%-30s%10s%n", lBottomLine, divider, divider, empty, divider, divider, rBottomLine);

        outFile.printf("%40s%-9s%s%30s%s%9s%-40s%n", divider, winners[57], divider, empty, divider, winners[59], divider);
        outFile.printf("%-10s%30s%-10s%30s%10s%-30s%10s%n", names[24], divider, lBottomLine, empty, rBottomLine, divider, names[56]);
        outFile.printf("%-10s%30s%50s%-30s%10s%n", topLine, divider, empty, divider, topLine);
        outFile.printf("%10s%-10s%20s%50s%-20s%10s%-10s%n", divider, winners[12], divider, empty, divider, winners[28], divider);
        outFile.printf("%-9s%s%-10s%20s%50s%-20s%10s%s%9s%n", names[25], divider, topLine, divider, empty, divider, topLine, divider, names[57]);
        outFile.printf("%-10s%10s%20s%50s%-20s%-10s%10s%n", lBottomLine, divider, divider, empty, divider, divider, rBottomLine);
        outFile.printf("%20s%-10s%10s%50s%-10s%10s%-20s%n", divider, winners[38], divider, empty, divider, winners[46], divider);
        outFile.printf("%-9s%11s%-10s%10s%50s%-10s%10s%-11s%9s%n", names[26], divider, topLine, divider, empty, divider, topLine, divider, names[58]);
        outFile.printf("%-10s%10s%10s%10s%50s%-10s%-10s%-10s%10s%n", topLine, divider, divider, divider, empty, divider, divider, divider, topLine);
        outFile.printf("%10s%-9s%s%10s%10s%50s%-10s%-10s%s%9s%-10s%n", divider, winners[13], divider, divider, divider, empty, divider, divider, divider, winners[29], divider);
        outFile.printf("%-9s%s%-10s%10s%10s%50s%-10s%-10s%10s%s%9s%n", names[27], divider, lBottomLine, divider, divider, empty, divider, divider, rBottomLine, divider, names[59]);
        outFile.printf("%-10s%20s%10s%50s%-10s%-20s%10s%n", lBottomLine, divider, divider, empty, divider, divider, rBottomLine);
        outFile.printf("%30s%-9s%s%50s%s%9s%-30s%n", divider, winners[51], divider, empty, divider, winners[55], divider);
        outFile.printf("%-10s%20s%10s%50s%10s%-20s%10s%n", names[28], divider, lBottomLine, empty, rBottomLine, divider, names[60]);
        outFile.printf("%-10s%20s%70s%-20s%10s%n", topLine, divider, empty, divider, topLine);
        outFile.printf("%10s%-10s%10s%70s%-10s%10s%-10s%n", divider, winners[14], divider, empty, divider, winners[30], divider);
        outFile.printf("%-9s%s%-10s%10s%70s%-10s%10s%s%9s%n", names[29], divider, topLine, divider, empty, divider, topLine, divider, names[61]);
        outFile.printf("%-10s%10s%10s%70s%-10s%-10s%10s%n", lBottomLine, divider, divider, empty, divider, divider, rBottomLine);
        outFile.printf("%20s%-9s%-11s%50s%11s%9s%-20s%n", divider, winners[39], divider, empty, divider, winners[47], divider);
        outFile.printf("%-9s%11s%-10s%70s%10s%-11s%9s%n", names[30], divider, lBottomLine, empty, rBottomLine, divider, names[62]);
        outFile.printf("%-10s%10s%90s%-10s%10s%n", topLine, divider, empty, divider, topLine);
        outFile.printf("%10s%-9s%-11s%70s%11s%9s%-10s%n", divider, winners[15], divider, empty, divider, winners[31], divider);
        outFile.printf("%-9s%s%-10s%100s%s%9s%n", names[31], divider, lBottomLine, rBottomLine, divider, names[63]);
        outFile.printf("%-10s%110s%10s%n", lBottomLine, empty, rBottomLine);

        outFile.close();
    }    
}