package View.Client.Visualizes;

import View.Client.Actions.ActionConstructor;
import View.States.ViewShipBoardState;
import View.States.ViewState;

import java.util.List;
import java.util.Map;

public class ViewShipBoard implements Visualize {
    final String username;

    public ViewShipBoard(String username) {
        this.username = username;
    }

    @Override
    public ViewState getViewState() {
        return new ViewShipBoardState(this.username);
    }

    public static ActionConstructor getConstructor() {
        return new ActionConstructor() {
            @Override
            public ViewShipBoard create(Map<String, String> args) throws IllegalArgumentException {
                final String username = args.get("PlayerName");

                if(username == null) {
                    throw new IllegalArgumentException("Player's name is required");
                }

                return new ViewShipBoard(username);
            }

            @Override
            public List<String> getArguments() {
                return List.of("PlayerName");
            }
        };
    }
}
