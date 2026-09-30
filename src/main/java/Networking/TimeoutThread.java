package Networking;

import Networking.Messages.PingMessage;

/**
 * TimeoutThread is responsible for monitoring the network's timeout status.
 * It periodically sends a PingMessage to check if the network is still responsive.
 * If the network does not respond within the specified timeout, it will set the network as done.
 */
public class TimeoutThread extends Thread {
    /**
     * The network to monitor for timeouts.
     */
    private final Network network;

    /**
     * Constructs a TimeoutThread for the specified network.
     *
     * @param network The network to monitor for timeouts.
     */
    public TimeoutThread(Network network) {
        super("TimeoutThread");
        this.network = network;
    }

    /**
     * Runs the timeout monitoring loop.
     * This method continuously checks the network's timeout status and sends a PingMessage
     * if the timeout is approaching. If the network does not respond within the timeout period,
     * it will set the network as done.
     */
    @Override
    public void run() {
        while (!this.network.isDone()) {
            try {
                if (System.currentTimeMillis() < this.network.getTimeout() - (Network.TIMEOUT / 2)) {
                    Thread.sleep(Network.TIMEOUT / 10);
                } else if (System.currentTimeMillis() < this.network.getTimeout()) {
                    if (!this.network.send(new PingMessage()) && !this.network.isDone()) {
                        System.err.println("TimeoutThread: Could not send ping but network isn't done. What?");
                    }
                    Thread.sleep(Network.TIMEOUT / 20);
                } else {
                    System.err.println("TimeoutThread: Killing network due to timeout");
                    this.network.setDone();
                }
            } catch (InterruptedException e) {
                System.err.println("TimeoutThread: Could not sleep. exiting...");
                return;
            }
        }
    }
}