import java.util.Calendar;
import java.util.GregorianCalendar;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Driver for the BracketBuster application, allows for manual entry of both
 *     the year and number of trials, as well as supporting the scoring of
 *     predefined formulas
 * NOTE: All formulas must have a zero as their last index, for that
 *     column represents actual placement in the spreadsheets (called worth)
 * @author Ryan Burns
 */
public class Driver extends Application {
    /**
     * The year to be simulated
     */
    public static String YEAR;
    /**
     * The predefined formula to be evaluated
     */
    public static double[] FORMULA;
    /**
     * Original holistic formula generated based on 2010 results, created
     *     prior to the 2013 tournament
     */
    public static final double[] f2010x1 =
            {1, 0, 1, 1, 1.5, 0, 4, 0, 0, 1, 1, 4, 0};
    /**
     * Formula derived from simulation, based on 2010 results, created
     *     prior to the 2014 tournament
     */
    public static final double[] f2010x2 =
            {-1.337083, -12.297309, 1.608043, 10.634143, 12.31531, 7.048258,
                13.381866, 6.853372, -9.505144, 15.456194, -12.141937,
                13.525092, 0};
    /**
     * Original holistic formula generated based on 2011 results, created
     *     prior to the 2013 tournament
     */
    public static final double[] f2011x1 =
            {1, 0, 5, 2, 1, 0, 6, 1, 1, 1, 3, 10, 0};
    /**
     * Formula derived from simulation, based on 2011 results, created
     *     prior to the 2014 tournament
     */
    public static final double[] f2011x2 =
            {-0.81792337, 1.87528926, 3.87770963, 5.74087101, -3.29130357,
                -1.84229468, 4.55649673, 1.07559803, -2.07042864, 0.02727832,
                3.86377006, 2.79739375, 0};
    /**
     * Original holistic formula generated based on 2012 results, created
     *     prior to the 2013 tournament
     */
    public static final double[] f2012x1x1 =
            {4.25, 0, 1, 2, 1, 0, 0, 0, 2, 3, 0, 0, 0};
    /**
     * Formula derived from simulation, based on 2012 results, created
     *     prior to the 2014 tournament
     */
    public static final double[] f2012x1x2 =
            {-1.4355873, 1.174553, -0.3186505, -2.0694545, -0.8081462,
                5.1496393, -8.0884127, 3.1297044, 6.1520308, 10.0600618,
                3.5383397, -7.1743825, 0};
    /**
     * Formula derived from simulation, based on 2012 results, created
     *     prior to the 2014 tournament, using statistics given at GHP
     *     (eg. opp fg%. opp rpg, ast%, etc.)
     */
    public static final double[] f2012x2x2 =
            {7.281393, -6.980683, -7.353928, 4.016225, -4.194983, 11.409783,
                1.917827, 8.168176, 11.830549, 37.225538, 8.098729, -2.473676,
                16.917126, 33.229607, 22.993276, 15.950146, -1.283997,
                -6.580479, -16.465808, 6.676674, 0};
    /**
     * Formula derived from simulation, based on 2013 results, created
     *     prior to the 2014 tournament
     */
    public static final double[] f2013x2 =
            {1.6018698, -1.4856902, -0.0436828, -1.5707792, 2.4437282,
                2.2706905, 5.9098004, 3.0189493, 1.2125967, 10.9239804,
                0.9288362, 1.0214649, 0};
    /**
     * Everything formula, all stats weighted equally, compatible with all
     *     stats that utilize the (1) suffix or do not have a suffix
     */
    public static final double[] e1 =
            {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0};
    /**
     * Everything formula, all stats weighted equally, compatible with all
     *     stats that utilize the (2) suffix
     */
    public static final double[] e2 =
            {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0};
    /**
     * Everything formula, all stats weighted equally, compatible with all
     *     stats that utilize the (3) suffix
     */
    public static final double[] e3 =
            {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0};
    /**
     * Formula generated from averaging 2010 1.0 and 2011 1.0
     */
    public static final double[] a2012x1 =
            {1, 0, 3, 1.5, 1.25, 0, 5, .5, .5, 1, 2, 7, 0};
    /**
     * Formula generated from averaging 2010 2.0 and 2011 2.0
     */
    public static final double[] a2012x2 =
            {-1.077503185, -5.21100987, 2.742876315, 8.187507005, 4.512003215,
                2.60298166, 8.969181365, 3.964485015, -5.78778632, 7.74173616,
                -4.13908347, 8.161242875, 0};
    /**
     * Formula generated from averaging 2010 1.0, 2011 1.0, and 2012 1.0
     */
    public static final double[] a2013x1 =
            {25f / 12, 0, 7f / 3, 5f / 3, 7f / 6, 0, 10f / 3, 1f / 3, 1, 5f / 3,
                4f / 3, 14f / 3, 0};
    /**
     * Formula generated from averaging 2010 2.0, 2011 2.0, and 2012(1) 2.0
     */
    public static final double[] a2013x2 =
            {-1.19686456, -3.08248891, 1.72236738, 4.76851984, 2.73862008,
                3.45186754, 3.28331668, 3.68622481, -1.80784728, 8.51451137,
                -1.57994241, 3.04936775, 0};
    /**
     * Formula generated from averaging
     *     2010 2.0, 2011 2.0, 2012(1) 2.0, and 2013 2.0
     */
    public static final double[] a2014x2 =
            {-0.4971809675, -2.683289235, 1.2808548325, 3.1836950775,
                2.6648971075, 3.15657328, 3.9399376075, 3.5194059325,
               -1.052736285, 9.11687863, -0.95274776, 2.5423920375, 0};

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("ScreenOneFXML.fxml"));
        Scene scene = new Scene(root, 1200, 900);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    /**
     * To score a predefined formula, set the year and formula fields
     * To generate a new formula, use maxFind() and then score()
     * Has the option to print to file and/or to console
     * Has the option to save a copy of the bracket as a text file
     * TODO: Optional second parameter to BracketBuster:
     *     0 for normal, 1 for high seed, 2 for actual results
     * @param args
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Will get the current time and date in a predefined format whenever called
     * @return The current instance so that each formula is time stamped
     */
    public static String getTime() {
        StringBuilder timeStr = new StringBuilder();
        Calendar calendar = new GregorianCalendar();
        timeStr.append(Integer.toString(calendar.get(Calendar.MONTH) + 1));
        timeStr.append("-");
        timeStr.append(Integer.toString(calendar.get(Calendar.DAY_OF_MONTH)));
        timeStr.append("-");
        timeStr.append(Integer.toString(calendar.get(Calendar.YEAR)));
        timeStr.append(" ");
        timeStr.append(Integer.toString(calendar.get(Calendar.HOUR_OF_DAY)));
        timeStr.append("'");
        timeStr.append(Integer.toString(calendar.get(Calendar.MINUTE)));
        timeStr.append("'");
        timeStr.append(Integer.toString(calendar.get(Calendar.SECOND)));
        return timeStr.toString();
    }
}