import java.io.IOException;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class Driver {

    protected static String YEAR = "2014(3)";
    public static final double[] f2010x1 = {1, 0, 1, 1, 1.5, 0, 4, 0, 0, 1, 1, 4, 0};
    public static final double[] f2010x2 = {-1.337083, -12.297309, 1.608043, 10.634143, 12.31531,
                                            7.048258, 13.381866, 6.853372, -9.505144, 15.456194,
                                            -12.141937, 13.525092, 0};
    public static final double[] f2011x1 = {1, 0, 5, 2, 1, 0, 6, 1, 1, 1, 3, 10, 0};
    public static final double[] f2011x2 = {-0.81792337, 1.87528926, 3.87770963, 5.74087101,
                                            -3.29130357, -1.84229468, 4.55649673, 1.07559803,
                                            -2.07042864, 0.02727832, 3.86377006, 2.79739375, 0};
    public static final double[] f2012x1x1 = {4.25, 0, 1, 2, 1, 0, 0, 0, 2, 3, 0, 0, 0};
    public static final double[] f2012x1x2 = {-1.4355873, 1.174553, -0.3186505, -2.0694545,
                                            -0.8081462, 5.1496393, -8.0884127, 3.1297044, 6.1520308,
                                            10.0600618, 3.5383397, -7.1743825, 0};
    public static final double[] f2012x2x2 = {7.281393, -6.980683, -7.353928, 4.016225, -4.194983,
                                            11.409783, 1.917827, 8.168176, 11.830549, 37.225538, 8.098729,
                                            -2.473676, 16.917126, 33.229607, 22.993276, 15.950146,
                                            -1.283997, -6.580479, -16.465808, 6.676674, 0};
    public static final double[] f2013x2 = {1.6018698, -1.4856902, -0.0436828, -1.5707792, 2.4437282,
                                            2.2706905, 5.9098004, 3.0189493, 1.2125967, 10.9239804,
                                            0.9288362, 1.0214649, 0};
    public static final double[] e1 = {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0};
    public static final double[] e2 = {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0};
    public static final double[] e3 = {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0};
    public static final double[] a2012x1 = {1, 0, 3, 1.5, 1.25, 0, 5, .5, .5, 1, 2, 7, 0};
    public static final double[] a2012x2 = {-1.077503185, -5.21100987, 2.742876315, 8.187507005,
                                            4.512003215, 2.60298166, 8.969181365, 3.964485015,
                                            -5.78778632, 7.74173616, -4.13908347, 8.161242875, 0};
    public static final double[] a2013x1 = {25f / 12, 0, 7f / 3, 5f / 3, 7f / 6, 0, 10f / 3,
                                            1f / 3, 1, 5f / 3, 4f / 3, 14f / 3, 0};
    public static final double[] a2013x2 = {-1.19686456, -3.08248891, 1.72236738, 4.76851984, 2.73862008,
                                            3.45186754, 3.28331668, 3.68622481, -1.80784728,
                                            8.51451137, -1.57994241, 3.04936775, 0};
    public static final double[] a2014x2 = {-0.4971809675, -2.683289235, 1.2808548325, 3.1836950775,
                                            2.6648971075, 3.15657328, 3.9399376075, 3.5194059325,
                                            -1.052736285, 9.11687863, -0.95274776, 2.5423920375, 0};

    public static void main(String[] args) throws IOException {
        //Optional second parameter: 0 for normal, 1 for high seed, 2 for actual results. (Not yet functional)
        BracketBuster bb1 = new BracketBuster(1);
//        BracketBusterDebugging bb1 = new BracketBusterDebugging(5);
        FileConverter fc1 = new FileConverter();
        fc1.convert();
        int max = bb1.maxFind();
        PrintWriter toFile = new PrintWriter(new FileWriter("log.txt", true));
        toFile.println("Generated: " + getTime());
        toFile.println("Year: " + YEAR + " with trials: " + bb1.getTrials());
        for (int k = 0; k < bb1.best.length - 1; k++) {
            System.out.printf("%f ", bb1.best[k]);
            toFile.printf("%f ", bb1.best[k]);
        }
        toFile.println();
        toFile.println("Maximum: " + max);
        toFile.println();
        System.out.println("\n" + "Maximum: " + max + "\n" + "All done!");
        System.out.println(bb1.getCounter());
        toFile.close();

        int score = bb1.score(e3);
//        int score = bb1.score(bb1.best);
        System.out.println(score);
        BracketVisual bv = new BracketVisual(bb1.getWinnerPos(), getTime(), true);
        bv.show();
    }

    private static String getTime() {
        Calendar calendar = new GregorianCalendar();
        String year = Integer.toString(calendar.get(Calendar.YEAR));
        String month = Integer.toString(calendar.get(Calendar.MONTH) + 1);
        String dayOfMonth = Integer.toString(calendar.get(Calendar.DAY_OF_MONTH));
        String hourOfDay = Integer.toString(calendar.get(Calendar.HOUR_OF_DAY));
        String minute = Integer.toString(calendar.get(Calendar.MINUTE));
        String sec = Integer.toString(calendar.get(Calendar.SECOND));
        String timeStr = month + "-" + dayOfMonth + "-" + year + " " + hourOfDay + "'" + minute + "'" + sec;
        return timeStr;
    }
}