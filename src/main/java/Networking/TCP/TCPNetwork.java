package Networking.TCP;

import Networking.Messages.Message;
import Networking.Messages.QuitMessage;
import Networking.TimeoutThread;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

public final class TCPNetwork implements Networking.Network {
    private final TCPMessageQueue inQueue;
    private boolean done = false;

    private long timeout = System.currentTimeMillis() + TIMEOUT;

    private final Socket socket;
    private final ObjectOutputStream out;

    public TCPNetwork(Socket socket) throws IOException {
        this.socket = socket;
        this.out = new ObjectOutputStream(TCPNetwork.this.socket.getOutputStream());

        this.inQueue = new TCPMessageQueue();

        new TimeoutThread(this).start();
        new Receiver(this, this.inQueue).start();
    }

    public TCPNetwork(String hostname, Integer port) throws IOException {
        this(new Socket(hostname, port != null ? port : 1234));
    }

    @Override
    public Message read(){
        return this.inQueue.dequeue();
    }

    @Override
    public boolean send(Message message) {
        try {
            this.out.writeObject(message);
            this.out.flush();;
        } catch (IOException e) {
            if(!this.isDone()){
                System.err.println("TCPNetwork.send(): Could not enqueue " +
                        message.getClass().getSimpleName() +
                        ". Are you sure it's serializable?");
                e.printStackTrace(System.err);
            }
            return false;
        }
        return true;
    }

    @Override
    public synchronized void setDone() {
        if(this.done) return;
        send(new QuitMessage());
        this.done = true;

        try {
            this.socket.close();
        } catch (IOException e) {
            System.err.println("TCPNetwork.setDone(): Error occurred while closing socket.");
        }

        synchronized (this.inQueue) {
            this.inQueue.notifyAll();
        }
    }

    @Override
    public synchronized boolean isDone() {
        return this.done;
    }

    @Override
    public synchronized long getTimeout() {
        return this.timeout;
    }

    @Override
    public synchronized void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public Socket getSocket() {
        return this.socket;
    }
}
