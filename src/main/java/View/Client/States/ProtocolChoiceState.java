package View.Client.States;

import View.Client.ClientState;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a state in which the client can choose between different protocols (RMI or TCP).
 * This state allows the user to select the protocol for communication with the server.
 */
public final class ProtocolChoiceState implements ClientState {

    /**
     * Constructs a ProtocolChoiceState.
     * This state is used to prompt the user to choose a communication protocol.
     */
    @Override
    public ClientState chooseProtocol(String choice){
        if(choice.equalsIgnoreCase("RMI")){
            return new ConnectingState(true);
        }else if(choice.equalsIgnoreCase("TCP")){
            return new ConnectingState(false);
        }

        return this;
    }

    /**
     * Returns a string representation of the ProtocolChoiceState.
     * This method is used for debugging or logging purposes.
     *
     * @return A string indicating the protocol choice state.
     */
    @Override
    public List<String> getAvailableCommands() {
        List<String> commands = new ArrayList<>();

        commands.add("RMI");
        commands.add("TCP");
        commands.addAll(ClientState.super.getAvailableCommands());

        return commands;
    }
}
