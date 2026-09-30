package View.States;

import View.Client.Client;

public class ViewCardDeckState implements ViewState {
    @Override
    public void paint() {
        Client.client.getState().viewCardDeck();
    }
}
