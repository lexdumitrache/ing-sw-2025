package Networking.TCP;

import Networking.Messages.Message;
import Networking.Messages.MessageQueue;

import java.util.LinkedList;
import java.util.Queue;

public class TCPMessageQueue implements MessageQueue {
    private final Queue<Message> queue = new LinkedList<>();

    @Override
    public synchronized boolean enqueue(Message message) {
        this.notifyAll();
        return queue.add(message);
    }

    @Override
    public synchronized Message dequeue() {
        if (queue.isEmpty()) {
            try {
                this.wait();
            } catch (InterruptedException e) {
                System.err.println("Could not wait on dequeue? Why?");
                throw new RuntimeException(e);
            }
            return null;
        }
        return queue.poll();
    }
}
