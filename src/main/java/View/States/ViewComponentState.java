package View.States;

import Model.Ship.Coordinates;
import View.Client.Client;

import java.util.List;

public class ViewComponentState implements ViewState {
    private final String username;
    private final Coordinates coordinates;

    public ViewComponentState(String username, Coordinates coordinates) {
        this.username = username;
        this.coordinates = coordinates;
    }

    @Override
    public ViewState backToShip() {
        return new ViewShipBoardState(this.username);
    }

    @Override
    public List<String> getAvailableVisualizers(){
        return List.of("BackToShip");
    }

    @Override
    public void paint() {
        Client.client.getState().viewComponent(username, coordinates);
    }
}
