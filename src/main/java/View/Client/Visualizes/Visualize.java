package View.Client.Visualizes;

import View.Client.Actions.Action;
import View.Client.Client;
import View.Client.ClientState;
import View.States.ViewState;

public interface Visualize extends Action {
    ViewState getViewState();

    @Override
    default ClientState execute(ClientState state) {
        try {
            Client.view.setState(getViewState());
        } catch (UnsupportedOperationException e) {
            Client.view.log(e.getMessage());
        }
        return state;
    }

    @Override
    default boolean isVisualize() {
        return true;
    }
}
