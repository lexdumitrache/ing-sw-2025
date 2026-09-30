package View.States;

import View.Client.Client;

public class ViewCardState implements ViewState {
    @Override
    public void paint() {
        Client.client.getState().viewCard();
    }
}
