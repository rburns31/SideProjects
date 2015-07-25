package addplayer;

import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.MouseEvent;

/**
 * 
 * @author Ryan Burns
 */
public class ColoredLabelClickHandler implements EventHandler<MouseEvent> {
    /**
     * 
     */
    private final Label pointsField;

    /**
     * 
     */
    private ToggleButton correctButton;

    /**
     * 
     * @param pointsField 
     */
    public ColoredLabelClickHandler(Label pointsField) {
        this.pointsField = pointsField;
    }

    /**
     * 
     * @param pointsField
     * @param correctButton 
     */
    public ColoredLabelClickHandler(Label pointsField,
            ToggleButton correctButton) {
        this(pointsField);
        this.correctButton = correctButton;
    }

    /**
     * 
     * @param t 
     */
    @Override
    public void handle(MouseEvent t) {
        Label clickedLabel = (Label) t.getSource();
        if (clickedLabel.getStyleClass().toString().equals(
                "label redlabel")) {

            clickedLabel.getStyleClass().remove("redlabel");
            clickedLabel.getStyleClass().add("greenlabel");
            pointsField.setText(Integer.toString(
                    Integer.parseInt(pointsField.getText()) + 1));
            if (correctButton != null) {
                correctButton.setSelected(true);
                ADDPlayer.CORR_BUTTONS.get(correctButton).setSelected(false);
            }
        } else if (clickedLabel.getStyleClass().toString().equals(
                "label greenlabel")) {

            clickedLabel.getStyleClass().remove("greenlabel");
            clickedLabel.getStyleClass().add("redlabel");
            pointsField.setText(Integer.toString(
                    Integer.parseInt(pointsField.getText()) - 1));
            if (correctButton != null) {
                correctButton.setSelected(false);
                ADDPlayer.CORR_BUTTONS.get(correctButton).setSelected(true);
            }
        }
    }
}