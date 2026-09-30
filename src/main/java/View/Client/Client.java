package View.Client;

import Networking.Agent;
import Networking.Utils;
import View.Client.Actions.Action;
import View.Client.Actions.ActionConstructor;
import View.Client.Actions.ExceptionAction;
import View.Client.States.ConnectingState;
import View.Client.States.ProtocolChoiceState;
import View.States.MenuStates.ChooseActionState;
import View.States.MenuStates.ActionCreationState;
import View.States.MenuStates.StopState;
import View.States.ViewNothingState;
import View.TUI;
import View.View;

import java.util.HashMap;
import java.util.Map;

/**
 * The Client class represents the main entry point for the client application.
 * It initializes the client state and manages the execution of actions based on user input.
 * The client can connect to a server using either RMI or TCP protocols.
 */
public class Client implements Agent {
    /**
     * The client instance that is used to interact with the server.
     */
    public static Client client;

    /**
     * The view used to display the client interface and handle user interactions.
     */
    public static View view;

    /**
     * The current state of the client, which determines the actions that can be performed.
     */
    private ClientState state;

    /**
     * The main method serves as the entry point for the client application.
     * It parses command-line arguments to determine the connection protocol (RMI or TCP)
     * and initializes the client state accordingly.
     *
     * @param args Command-line arguments passed to the application.
     */
    public static void main(String[] args) {
        final Boolean useRMI = (Utils.getPosition("--useRMI", args) != -1) ? Boolean.TRUE : (Utils.getPosition("--useTCP", args) != -1) ? Boolean.FALSE : null;

        final boolean localhost = Utils.getPosition("--localhost", args) != -1;


        view = new TUI();
        client = new Client();

        if (useRMI == null) {
            client.state = new ProtocolChoiceState();
        } else {
            client.state = new ConnectingState(useRMI);
            if(localhost) {
                client.state = client.state.connect("localhost", null);
            }
        }

        client.run();
    }

    /**
     * Constructs a new Client instance.
     * This constructor initializes the client state to a default state.
     */
    @Override
    public void run() {
        view.setState(new ViewNothingState());
        view.setMenuState(new ChooseActionState());
        view.run();
        view.repaint();
    }

    /**
     * Returns the current state of the client.
     * This method provides access to the current client state, which can be used to determine
     * the available actions and the current context of the client.
     *
     * @return The current ClientState instance representing the client's state.
     */
    public ClientState getState() {
        return state;
    }

    /**
     * Executes the specified action on the current client state.
     * This method synchronizes access to the client state to ensure thread safety
     * when executing actions that may modify the state.
     *
     * @param action The action to be executed, which may modify the client state.
     */
    public synchronized void execute(Action action) {
        try {
            this.state = action.execute(this.state);
        } catch (UnsupportedOperationException e) {
            view.log(e.getMessage());
        }

        if(this.state.isDone()){
            view.setMenuState(new StopState());
        } else {
            view.setMenuState(new ChooseActionState());
            view.repaint();
        }
    }

    public void createAction(String action, String[] args){
        ActionConstructor actionConstructor = ActionConstructor.getActionConstructor(action);

        //the command is not part of the available commands
        if(actionConstructor == null) {
            return;
        }

        //the command is part of the available commands but it doesn't require any arguments
        if(actionConstructor.getArguments() == null || actionConstructor.getArguments().isEmpty()) {
            this.execute(actionConstructor.create(null));
            return;
        }

        final Map<String, String> providedArguments = new HashMap<>();

        for(int i = 0; i < Integer.min(args.length, actionConstructor.getArguments().size()); i++){
            providedArguments.put(actionConstructor.getArguments().get(i), args[i]);
        }

        //the arguments provided are enough
        if(args.length >= actionConstructor.getArguments().size()){
            Action _action;
            try{
                _action = actionConstructor.create(providedArguments);
            } catch(Exception e){
                _action = new ExceptionAction(e);
            }

            this.execute(_action);
            return;
        }

        //the command needs to be created with arguments
        view.setMenuState(new ActionCreationState(actionConstructor, providedArguments));
        view.repaint();
    }
}