package Networking.Messages;

import Networking.Agent;
import Networking.Network;

import java.io.Serializable;


/**
 * Represents a message that can be sent over the network.
 * Messages can be handled in different contexts, such as by a network or an agent.
 */
public interface Message extends Serializable {
    default void handle() throws UnsupportedOperationException {
        throw new UnsupportedOperationException(this.getClass().getSimpleName() + " cannot be invoked in this context.");
    }

    /**
     * Handles the message in the context of a network.
     * This method can be overridden to provide specific handling logic.
     *
     * @param network The network over which the message is sent.
     * @throws UnsupportedOperationException if the message cannot be handled in this context.
     */
    default void handle(Network network) throws UnsupportedOperationException{
        handle();
    }

    /**
     * Handles the message in the context of an agent.
     * This method can be overridden to provide specific handling logic.
     *
     * @param agent The agent that is handling the message.
     * @param network The network over which the message is sent.
     * @throws UnsupportedOperationException if the message cannot be handled in this context.
     */
    default void handle(Agent agent, Network network) throws UnsupportedOperationException {
        handle(network);
    }
}
