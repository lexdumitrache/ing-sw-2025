package Networking;

import Networking.Messages.HandOffException;
import Networking.Messages.Handler;
import Networking.Messages.Message;
import org.junit.jupiter.api.Test;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import static org.junit.jupiter.api.Assertions.*;

public class HandlerTest {

    /** A network that serves a fixed list of messages, then reports it is done. */
    private static final class FakeNetwork implements Network {
        private final Queue<Message> messages;
        private boolean done = false;

        FakeNetwork(List<Message> messages) {
            this.messages = new ArrayDeque<>(messages);
        }

        @Override public boolean send(Message message) { return true; }

        @Override
        public Message read() {
            final Message message = messages.poll();
            if (messages.isEmpty()) {
                done = true;
            }
            return message;
        }

        @Override public void setDone() { done = true; }
        @Override public boolean isDone() { return done; }
        @Override public long getTimeout() { return 0; }
        @Override public void setTimeout(long timeout) {}
    }

    private static final Agent AGENT = () -> {};

    @Test
    public void testKeepsReadingAfterAFailingMessage() throws InterruptedException {
        final List<String> handled = new ArrayList<>();
        final List<Message> messages = List.of(
                new Message() { @Override public void handle(Agent a, Network n) { throw new IllegalStateException("bug"); } },
                new Message() { @Override public void handle(Agent a, Network n) { handled.add("second"); } });

        final Handler<Agent> handler = new Handler<>(AGENT, new FakeNetwork(messages));
        handler.start();
        handler.join(2000);

        assertEquals(List.of("second"), handled);
    }

    @Test
    public void testStopsReadingOnHandOff() throws InterruptedException {
        final List<String> handled = new ArrayList<>();
        final List<Message> messages = List.of(
                new Message() { @Override public void handle(Agent a, Network n) { throw new HandOffException("controller takes over"); } },
                new Message() { @Override public void handle(Agent a, Network n) { handled.add("must not be read"); } });

        final Handler<Agent> handler = new Handler<>(AGENT, new FakeNetwork(messages));
        handler.start();
        handler.join(2000);

        assertFalse(handler.isAlive());
        assertTrue(handled.isEmpty(), "the message after a hand-off belongs to the new handler");
    }
}
