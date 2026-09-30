package Networking.Messages;

/**
 * Represents a message that prints a command to the console when handled.
 * This message is used for debugging or informational purposes.
 */
public class PrintMessage implements Message {
    /**
     * The command to be printed when this message is handled.
     */
    private final String command;

    /**
     * Constructs a PrintMessage with the specified command.
     * @param command The command to be printed when this message is handled.
     */
    public PrintMessage(String command) {
        this.command = command;
    }

    /**
     * Handles the print message by printing the command to the console.
     * This method does not require an agent or network context.
     */
    @Override
    public void handle() {
        System.out.println(command);
    }
}
