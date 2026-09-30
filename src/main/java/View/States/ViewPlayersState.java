package View.States;

import View.Client.Client;

public class ViewPlayersState implements ViewState {
    @Override
    public void paint() {
        Client.client.getState().viewPlayers();
    }
}
