package recipemaster;

/**
 * @author Ryan Burns
 * @version 1.0
 */
public interface ControlledScreen {

    //This method will allow the injection of the Parent ScreenPane
    public void setScreenParent(ScreensController parentControllers);
}