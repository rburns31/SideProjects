package bowlpicker;

public interface ControlledScreen {

    /**
     * This method will allow the injection of the parent ScreenPane
     * @param parentController The parentController of this screen
     */
    void setScreenParent(ScreensController parentController);
}