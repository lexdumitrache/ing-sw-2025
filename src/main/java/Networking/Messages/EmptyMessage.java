package Networking.Messages;

/**
 * Represents an empty message that does not perform any action when handled.
 * This can be used as a placeholder or to indicate no operation.
 */
public class EmptyMessage implements Message {

    /**
     * Constructs an EmptyMessage.
     * This message does not contain any data or perform any action.
     */
    @Override
    public void handle() {
    }
}
