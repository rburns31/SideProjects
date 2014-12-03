package bowlpicker;

import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Driver extends Application {
    /**
     * The width of the screen
     */
    public static final int SCREEN_WIDTH = 460;
    /**
     * The height of the screen
     */
    public static final int SCREEN_HEIGHT = 720;

    @Override
    public void start(Stage stage) {
        ScreensController mainContainer = new ScreensController();
        mainContainer.loadScreen("OpeningScreen", "OpeningScreen.fxml");
        System.out.println(mainContainer);
        mainContainer.unloadScreen("OpeningScreen");
        //mainContainer.loadScreen("BowlPicker", "BowlPicker.fxml");
        //mainContainer.loadScreen("EndingScreen", "EndingScreen.fxml");
        mainContainer.setScreen("OpeningScreen");

        Group root = new Group();
        root.getChildren().addAll(mainContainer);

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}