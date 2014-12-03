package bowlpicker;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;

public class EndingScreenController  implements Initializable, ControlledScreen {
    private ScreensController parentController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        
    }

    @Override
    public void setScreenParent(ScreensController parentController) {
        this.parentController = parentController;
    } 
}