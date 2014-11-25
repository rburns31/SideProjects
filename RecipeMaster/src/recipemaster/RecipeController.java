package recipemaster;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TitledPane;

/**
 * FXML Controller class for Recipe
 * @author Ryan Burns
 */
public class RecipeController implements Initializable, ControlledScreen {
    @FXML private TitledPane recipePane;

    private ScreensController parentController;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    @Override
    public void setScreenParent(ScreensController parentController) {
        this.parentController = parentController;
    }
}