package Networking.Messages;

import Networking.Network;

/**
 * Represents a ping message that is sent to check the connectivity of the network.
 * When handled, it sends an empty message back to indicate that the network is still alive.
 */
public class PingMessage implements Message {
    /**
     * Constructs a PingMessage.
     * This message is used to check the connectivity of the network.
     */
    @Override
    public void handle(Network network) {
        if(!network.send(new EmptyMessage())){
            System.err.println("PingCommand: could not send pong command. Killing network");
            network.setDone();
        }
    }
}
