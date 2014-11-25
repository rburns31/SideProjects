package bowlpicker;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;

public class ButtonHandler<E extends ActionEvent> implements EventHandler<ActionEvent> {

    private SpecButton b;

    public ButtonHandler(SpecButton b) {
        this.b = b;
    }

    @Override
    public void handle(ActionEvent event) {
        int index = Initializer.buttons.indexOf(b);
        if (!b.isSelected) {
            b.setStyle("-fx-background-color: #00BFFF;");
            b.isSelected = true;
            if(index % 2 != 0) {
                SpecButton b2 = Initializer.buttons.get(index - 1);
                b2.setStyle("-fx-background-color: #FFFFFF;");
                b2.isSelected = false;
            }
            else {
                SpecButton b2 = Initializer.buttons.get(index + 1);
                b2.setStyle("-fx-background-color: #FFFFFF;");
                b2.isSelected = false;
            }
        } else {
            b.setStyle("-fx-background-color: #FFFFFF;");
            b.isSelected = false;
        }
    }
}