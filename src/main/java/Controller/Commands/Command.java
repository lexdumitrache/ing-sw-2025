package Controller.Commands;

import Controller.Controller;
import Controller.Exceptions.InvalidCommand;
import Controller.Exceptions.InvalidContextualAction;
import Controller.Exceptions.InvalidParameters;
import Model.Exceptions.InvalidMethodParameters;

import java.io.Serializable;

public abstract class Command implements Serializable {
    private final String PlayerName;

    public Command(String playerName) {
        PlayerName = playerName;
    }

    public final String getPlayerName() {
        return PlayerName;
    }

    public abstract void execute(Controller controller) throws InvalidCommand, InvalidParameters, InvalidMethodParameters, InvalidContextualAction;

}
