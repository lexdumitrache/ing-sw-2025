package View.Client.Actions;

import View.Client.ClientState;

import java.util.List;
import java.util.Map;

/**
 * Represents an action to connect to a server with a specified hostname(IP address) and port.
 * This action is executed by the client to establish a connection to the server.
 */
public class ConnectAction implements Action {

    /**
     * The hostname(IP address) of the server to connect to.
     */
    private final String hostname;

    /**
     * The port number of the server to connect to.
     */
    private final Integer port;

    public ConnectAction(String hostname, Integer port) {
        this.hostname = hostname;
        this.port = port;
    }

    /**
     * Executes the connect action on the given client state.
     * This method attempts to connect to the server using the specified hostname and port.
     *
     * @param state The current client state.
     * @return The updated client state after attempting to connect.
     */
    @Override
    public ClientState execute(ClientState state) {
        return state.connect(hostname, port);
    }

    /**
     * Returns a string representation of the connect action.
     * This method is used for debugging and logging purposes.
     *
     * @return A string describing the connect action.
     */
    static public ActionConstructor getConstructor() {
        return new ActionConstructor() {

            @Override
            public ConnectAction create(Map<String, String> arguments) throws IllegalArgumentException {
                try {
                    final String hostname = arguments.get("hostname");
                    final int port = Integer.parseInt(arguments.get("port"));

                    return new ConnectAction(hostname, port);
                } catch (Exception e) {
                    throw new IllegalArgumentException(e);
                }
            }

            @Override
            public List<String> getArguments() {
                return List.of("hostname", "port");
            }
        };
    }
}
