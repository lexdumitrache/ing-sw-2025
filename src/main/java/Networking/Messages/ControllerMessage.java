package Networking.Messages;

import Controller.Commands.Command;
import Controller.Controller;
import Networking.Agent;
import Networking.Network;

/**
 * Represents a message sent by a controller containing a command to be executed.
 * This message is handled by the controller agent to enqueue the specified command.
 */
public class ControllerMessage implements Message {
    private final Command command;

    /**
     * Constructs a ControllerMessage with the specified command.
     * @param command
     */
    public ControllerMessage(Command command) {
        this.command = command;
    }

    /**
     * Gets the command contained in this message.
     * @return The command to be executed by the controller.
     */
    @Override
    public void handle(Agent agent, Network network) {
        final Controller controller;

        try{
            controller = (Controller) agent;
        } catch (ClassCastException e) {
            System.err.println("\"controller\" is not a controller");
            return;
        }

        controller.enqueueCommand(this.command);
    }

}
