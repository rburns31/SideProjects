package bowlpicker;

import java.io.IOException;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 * The driver for BowlPicker
 * Sets up the basics of the window, and then loads the OpeningScreen
 * Credit to Jason Nessa for the cool team icons
 *     (http://icons.jasonnessa.com/collegefootball/)
 * @author Ryan Burns
 */
public class Driver extends Application {
    public static final int SCREEN_WIDTH = 660;
    public static final int SCREEN_HEIGHT = 720;

    public static Stage mainStage;
    public static String playerName;

    @Override
    public void start(Stage primaryStage) {
        mainStage = primaryStage;
        primaryStage.setTitle("BowlPicker");
        primaryStage.setResizable(false);
        try {
            primaryStage.getIcons().add(
                    new Image("cfbicons/applicationIcon.png"));
        } catch (Exception e) {
            System.out.println("Couldn't find the window icon image.");
        }
        try {
            Pane myPane = (Pane)FXMLLoader.load(
                    getClass().getResource("OpeningScreen.fxml"));
            Scene myScene = new Scene(myPane);
            primaryStage.setScene(myScene);
            primaryStage.show();
        } catch (IOException ioe) {
            System.out.println("Couldn't load the opening screen.");
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}