package recipemaster;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * 
 * @author Ryan Burns
 */
public class Driver extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        ScreensController mainContainer = new ScreensController();
        mainContainer.loadScreen("RecipeBook", "RecipeBook.fxml");
        mainContainer.loadScreen("Recipe", "Recipe.fxml");
        mainContainer.setScreen("RecipeBook");

        Group root = new Group();
        root.getChildren().addAll(mainContainer);
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}