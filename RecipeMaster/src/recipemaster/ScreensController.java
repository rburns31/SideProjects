package recipemaster;

import java.io.IOException;
import java.util.HashMap;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

/**
 * @author Ryan Burns
 * @version 1.0
 */
public class ScreensController extends StackPane {

    // Holds the screens to be displayed
    private HashMap<String, Node> screens;

    public ScreensController() {
        super();
        screens = new HashMap<>();
    }

    // Adds the screen to the collection
    public void addScreen(String name, Node screen) {
        screens.put(name, screen);
    }

    // Returns the Node with the appropriate name
    public Node getScreen(String name) {
        return screens.get(name);
    }

    /**
     * Loads the FXML file, adds the screen to the screens collection and
     *   finally injects the screenPane to the controller
     * @param name The name of the screen being loaded
     * @param resource The FXML file for the screen to be loaded
     * @return Whether or not the screen was successfully loaded
    */
    public boolean loadScreen(String name, String resource) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(resource));
            Parent loadScreen = (Parent) loader.load();
            ControlledScreen controller = ((ControlledScreen) loader.getController());
            controller.setScreenParent(this);
            addScreen(name, loadScreen);
            return true;
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    /**
     * This method tries to display the screen with a predefined name
     *   First it makes sure the screen has been already loaded
     *   If there is more than one screen, the current screen
     *     is removed, and then the new screen is added
     *   If there isn't any screen being displayed, the new screen is just added
     * @param name The name of the screen to be displayed
     * @return Whether the screen was successfully added or not
    */
    public boolean setScreen(final String name) {       
        if (screens.get(name) != null) { // Screen is already loaded
            final DoubleProperty opacity = opacityProperty();
            if (!getChildren().isEmpty()) { // If there is more than one screen
                Timeline fade = new Timeline(
                        new KeyFrame(Duration.ZERO, new KeyValue(opacity, 1.0)),
                        new KeyFrame(new Duration(1000), (ActionEvent t) -> {
                            getChildren().remove(0); // Remove the displayed screen
                            getChildren().add(0, screens.get(name)); // Add the screen
                            Timeline fadeIn = new Timeline(
                                    new KeyFrame(Duration.ZERO, new KeyValue(opacity, 0.0)),
                                    new KeyFrame(new Duration(800), new KeyValue(opacity, 1.0)));
                            fadeIn.play();
                }, new KeyValue(opacity, 0.0)));
                fade.play();
            } else {
                setOpacity(0.0);
                getChildren().add(screens.get(name)); // No one else been displayed, then just show
                Timeline fadeIn = new Timeline(
                        new KeyFrame(Duration.ZERO, new KeyValue(opacity, 0.0)),
                        new KeyFrame(new Duration(2500), new KeyValue(opacity, 1.0)));
                fadeIn.play();
            }
            return true;
        } else {
            System.out.println("Screen has not been loaded.\n");
            return false;
        }
    }

    // This method will remove the screen with the given name from the collection of screens
    public boolean unloadScreen(String name) {
        if (screens.remove(name) == null) {
            System.out.println("Screen does not exist.\n");
            return false;
        } else {
            return true;
        }
    }
}