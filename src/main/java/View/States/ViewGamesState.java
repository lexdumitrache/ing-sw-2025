package View.States;

import View.Client.Client;

public class ViewGamesState implements ViewState {
    @Override
    public void paint() {
        Client.client.getState().list();
    }
}
