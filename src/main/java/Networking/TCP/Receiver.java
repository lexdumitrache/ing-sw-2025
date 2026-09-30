package Networking.TCP;

import Networking.Messages.Message;

import java.io.IOException;
import java.io.ObjectInputStream;

public class Receiver extends Thread {
    private final ObjectInputStream in;
    private final TCPNetwork network;

    private final TCPMessageQueue inQueue;

    public Receiver(TCPNetwork network, TCPMessageQueue inQueue) throws IOException {
        super("Receiver");
        this.in = new ObjectInputStream(network.getSocket().getInputStream());

        this.network = network;

        this.inQueue = inQueue;
    }

    @Override
    public void run() {
        while (!this.network.isDone()) {
            Message message = null;
            try {
                message = (Message) this.in.readObject();
            } catch (IOException e) {
                if (this.network.isDone()) {
                    return;
                }
            } catch (ClassNotFoundException e) {
                System.err.println("Receiver.run(): received something that is not a Serialized Object. Panic!");
                throw new RuntimeException(e);
            }

            if(message != null) {
                this.inQueue.enqueue(message);
            }
        }
    }
}
