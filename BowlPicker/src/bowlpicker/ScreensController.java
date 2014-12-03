package bowlpicker;

import java.io.IOException;
import java.util.HashMap;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

public final class ScreensController extends StackPane {
    private static final HashMap<String, Node> SCREENS = new HashMap<>();
    private static final HashMap<String, ControlledScreen> CONTROLLERS = new HashMap<>();

    private static final int TRANSITION_DURATION = 300;
    private static final int TRANSITION_DURATION_GAME_START = 2500;

    public ScreensController() {
        super();
    }

    /**
     * Adds a screen to the ScreensController, so that it can be viewed
     * @param name The name of the scene
     * @param screen The view
     * @param controller The controller that controls the view
     */
    public void addScreen(String name, Node screen, ControlledScreen controller) {
        SCREENS.put(name, screen);
        CONTROLLERS.put(name, controller);
    }

    /**
     * Returns the requested screen, as in a view to be loaded
     * @param name The name of the screen corresponding to the view
     * @return The requested view
     */
    public static Node getScreen(String name) {
        return SCREENS.get(name);
    }

    /**
     * Returns the Node with the appropriate name
     * @param name The name of the Node to be returned
     * @return The Node with the appropriate name
     */
    public static ControlledScreen getController(String name) {
        return CONTROLLERS.get(name);
    }

    /**
     * Loads the FXML file, adds the screen to the screens collection
     *     and finally injects the screenPane to the controller
     * @param name The name of the screen being loaded
     * @param resource The FXML file for the screen to be loaded
     * @return Whether or not the screen was successfully loaded
     */
    public boolean loadScreen(String name, String resource) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(resource));
            Parent loadScreen = (Parent)loader.load();
            ControlledScreen controller = (ControlledScreen)loader.getController();
            controller.setScreenParent(this);
            addScreen(name, loadScreen, controller);
            return true;
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    /**
     * This method tries to display the screen with a predefined name
     * First it makes sure the screen has been already loaded
     * If there is more than one screen, the current screen is removed, and
     *     then the new screen is added
     * If there isn't any screen being displayed, the new screen is just added
     * @param name The name of the screen to be displayed
     * @return Whether the screen was successfully added or not
     */
    public boolean setScreen(final String name) {
        Node screen = SCREENS.get(name);
        if (screen != null) { // Screen is already loaded
            final DoubleProperty opacity = opacityProperty();
            if (!getChildren().isEmpty()) { // If there is more than one screen
                Timeline fade = new Timeline(
                        new KeyFrame(Duration.ZERO, new KeyValue(opacity, 1.0)),
                        new KeyFrame(new Duration(1000), new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent event) {
                            getChildren().remove(0); // Remove the displayed
                            getChildren().add(0, screen); // Add the screen
                            Timeline fadeIn = new Timeline(
                                    new KeyFrame(Duration.ZERO,
                                            new KeyValue(opacity, 0.0)),
                                    new KeyFrame(new Duration(
                                            TRANSITION_DURATION),
                                            new KeyValue(opacity, 1.0)));
                            fadeIn.play();
                        }
                        }, new KeyValue(opacity, 0.0)));
                fade.play();
            } else {
                setOpacity(0.0);
                getChildren().add(screen); // Nothing displayed, then just show
                Timeline fadeIn = new Timeline(new KeyFrame(Duration.ZERO, new KeyValue(opacity, 0.0)),
                        new KeyFrame(new Duration(TRANSITION_DURATION_GAME_START), new KeyValue(opacity, 1.0)));
                fadeIn.play();
            }
            return true;
        } else {
            System.out.println("Screen has not been loaded.\n");
            return false;
        }
    }

    /**
     * Remove the screen with the given name from the collection of screens
     * @param name The name of the screen to be removed
     * @return Whether or not the screen being removed existed initially
     */
    public boolean unloadScreen(final String name) {
        if (SCREENS.remove(name) == null) {
            System.out.println("Screen does not exist.\n");
            return false;
        } else {
            return true;
        }
    }

    @Override
    public String toString() {
        String str = "";
        for (String s : SCREENS.keySet()) {
            str += s + '\n';
        }
        return str;
    }
}