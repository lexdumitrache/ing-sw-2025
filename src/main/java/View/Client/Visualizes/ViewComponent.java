package View.Client.Visualizes;

import Model.Ship.Coordinates;
import View.Client.Actions.ActionConstructor;
import View.Client.Client;
import View.States.ViewState;

import java.util.List;
import java.util.Map;

public class ViewComponent implements Visualize {
    private final Coordinates coordinates;

    public ViewComponent(Coordinates coordinates) {
        this.coordinates = coordinates;
    }

    @Override
    public ViewState getViewState() {
        return Client.view.getState().viewComponent(coordinates);
    }

    public static ActionConstructor getConstructor() {
        return new ActionConstructor() {
            @Override
            public ViewComponent create(Map<String, String> args) throws IllegalArgumentException {
                final int row, column;

                try{
                    row = Integer.parseInt(args.get("row"));
                } catch (NumberFormatException e){
                    throw new IllegalArgumentException("Could not parse the row. Did you provide an Integer?");
                }

                try{
                    column = Integer.parseInt(args.get("column"));
                } catch (NumberFormatException e){
                    throw new IllegalArgumentException("Could not parse the column. Did you provide an Integer?");
                }

                return new ViewComponent(new Coordinates(row, column));
            }

            @Override
            public List<String> getArguments() {
                return List.of("row", "column");
            }
        };
    }
}
