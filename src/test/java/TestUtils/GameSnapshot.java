package TestUtils;

import Controller.Controller;
import Model.Game;

/**
 * Represents a saved game state
 */
public class GameSnapshot {
    public final Controller controller;
    public final Game model;
    public final String description;

    public GameSnapshot(Controller controller, String description) {
        this.controller = controller;
        this.model = controller.getModel();
        this.description = description;
    }

    public Controller getController() {
        return controller;
    }
    public Game getModel() {
        return model;
    }
    public String getDescription() {
        return description;
    }
}
