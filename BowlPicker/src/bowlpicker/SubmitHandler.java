package bowlpicker;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class SubmitHandler<E extends ActionEvent> implements EventHandler<ActionEvent> {

    @Override
    public void handle(ActionEvent event) {
        Stage dialog = new Stage();
        dialog.setResizable(false);
        dialog.initStyle(StageStyle.UTILITY);
        Text msg = null;
        if (allGamesPicked()) {
            msg = new Text("Thank you! Email 'picks.txt' to Ryan.");
        } else {
            msg = new Text("Please pick all games before submitting!");
        }
        msg.setWrappingWidth(300);
        msg.setFont(new Font(15));
        HBox hbox = new HBox();
        hbox.setPrefSize(dialog.getMaxWidth(), dialog.getMaxHeight());
        hbox.setPadding(new Insets(5, 12, 5, 12));
        hbox.getChildren().add(msg);
        Scene scene = new Scene(hbox, msg.getWrappingWidth(), 30);
        dialog.setScene(scene);
        dialog.show();
    }

    private boolean allGamesPicked() {
        for (int i = 0; i < Initializer.buttons.size(); i += 2) {
            if (!(Initializer.buttons.get(i).isSelected || Initializer.buttons.get(i + 1).isSelected)) {
                return false;
            }
        }
        return true;
    }
}