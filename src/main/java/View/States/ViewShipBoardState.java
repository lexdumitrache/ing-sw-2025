package View.States;

import Model.Ship.Coordinates;
import View.Client.Client;

import java.util.List;

public class ViewShipBoardState implements ViewState{
    private final String username;

    public ViewShipBoardState(String username) {
        this.username = username;
    }

    @Override
    public ViewState viewComponent(Coordinates coordinates) {
        return new ViewComponentState(this.username, coordinates);
    }

    @Override
    public List<String> getAvailableVisualizers(){
        return List.of("ViewComponent");
    }

    @Override
    public void paint() {
        Client.client.getState().viewShipBoard(username);
    }
}
