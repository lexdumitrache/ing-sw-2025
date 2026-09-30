package Networking.Messages;

import Networking.Agent;
import Networking.Network;

/**
 * Represents a handler for processing messages in a network.
 * This class extends Thread to allow message handling in a separate thread.
 *
 * @param <T> The type of Agent that this handler will process messages for.
 */
public class Handler<T extends Agent> extends Thread {

    /**
     * The Agent that will handle the messages.
     */
    protected final T reference;

    /**
     * The Network over which messages will be processed.
     */
    protected final Network network;

    /**
     * Constructs a Handler for the specified Agent and Network.
     *
     * @param reference The Agent that will handle the messages.
     * @param network   The Network over which messages will be processed.
     */
    public Handler(T reference, Network network) {
        super(reference.getClass().getSimpleName() + "Handler");
        this.reference = reference;
        this.network = network;
    }

    /**
     * Runs the message handling loop.
     * This method continuously reads messages from the network and processes them
     * until the network is done.
     */
    @Override
    public final void run() {
        while (!this.network.isDone()) {
            final Message message = this.network.read();

            if (message != null) {
                this.network.setTimeout(System.currentTimeMillis() + Network.TIMEOUT);
                try {
                    message.handle(this.reference, this.network);
                } catch (RuntimeException e) {
                    e.printStackTrace(System.err);
                    return;
                }
            }
        }
    }
}
