package View.States;

import View.Client.Client;

public class ViewTilesState implements ViewState {
    @Override
    public void paint() {
        Client.client.getState().viewTiles();
    }
}
