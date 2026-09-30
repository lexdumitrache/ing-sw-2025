package View;


import View.States.MenuStates.ChooseActionState;
import View.States.MenuStates.MenuState;
import View.States.ViewNothingState;
import View.States.ViewState;

import java.util.List;
import java.util.Map;

/**
 * Abstract class representing a View in the application.
 * This class defines the basic structure and methods that any View implementation should have.
 */
public abstract class View implements Runnable {
    /**
     * The current state of the view.
     * This field is used to manage the different states of the view.
     */
    private ViewState state = new ViewNothingState();

    /**
     * The current menu state of the view.
     * This field is used to manage the menu interactions within the view.
     */
    private MenuState menuState = new ChooseActionState();

    /**
     * Sets the current state of the view.
     * This method allows changing the view's state to a new ViewState.
     *
     * @param state The new state to set for the view.
     */
    public void setState(ViewState state){
        this.state = state;
    }

    /**
     * Gets the current state of the view.
     * This method retrieves the current ViewState of the view.
     *
     * @return The current state of the view.
     */
    public ViewState getState(){
        return this.state;
    }

    /**
     * Sets the current menu state of the view.
     * This method allows changing the menu state to a new MenuState.
     *
     * @param menuState The new menu state to set for the view.
     */
    public void setMenuState(MenuState menuState){
        this.menuState = menuState;
    }

    /**
     * Gets the current menu state of the view.
     * This method retrieves the current MenuState of the view.
     *
     * @return The current menu state of the view.
     */
    public MenuState getMenuState(){
        return this.menuState;
    }

    /**
     * Abstract method to repaint the view.
     * This method should be implemented by subclasses to define how the view should be redrawn.
     */
    public abstract void repaint();

    /**
     * Abstract method to log messages in the view.
     * This method should be implemented by subclasses to define how messages are logged or displayed.
     *
     * @param message The message to log.
     */
    public abstract void log(String message);

    /**
     * Abstract method to show options in the view.
     * This method should be implemented by subclasses to display a list of options to the user.
     *
     * @param prompt   The prompt message to display before the options.
     * @param options  The list of options to show to the user.
     */
    public abstract void showOptions(String prompt, List<String> options);

    /**
     * Abstract method to show arguments in the view.
     * This method should be implemented by subclasses to display a list of arguments and their provided values.
     *
     * @param arguments          The list of argument names to display.
     * @param providedArguments  A map containing the provided values for each argument.
     */
    public abstract void showArguments(List<String> arguments, Map<String, String> providedArguments);
}
