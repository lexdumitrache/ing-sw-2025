package Networking.Messages;

import Controller.Controller;
import Controller.Server.Server;
import Networking.Agent;
import Networking.Network;

/**
 * Represents a message sent by a controller to leave the game.
 * This message is handled by the controller agent to initiate the shutdown of the server handler.
 */
public class LeaveGameMessage implements Message {
    /**
     * Constructs a LeaveGameMessage.
     * This message does not contain any data or perform any action.
     */
    @Override
    public void handle(Agent agent, Network network) {
        try{
            final Controller _ = (Controller) agent;
        } catch(ClassCastException e){
            System.err.println(agent.getClass().getSimpleName() + " is not a Controller");
            return;
        }

        new Handler<>(Server.server, network).start();

        throw new RuntimeException("Killing ControllerHandler and starting ServerHandler");
    }
}
