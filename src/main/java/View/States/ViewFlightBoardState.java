package View.States;

import View.Client.Client;

public class ViewFlightBoardState implements ViewState {
    @Override
    public void paint() {
        Client.client.getState().viewFlightBoard();
    }
}
