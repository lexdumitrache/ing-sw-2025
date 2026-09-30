package View.GUI;

import Controller.Enums.MatchLevel;
import Model.Ship.Coordinates;
import Model.Ship.ShipBoard;
import Model.Ship.Components.SpaceshipComponent;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;

/**
 * Draws a ship board with the real tile pictures. Rows 5-9 and columns 4-10 use the same numbering
 * as the game commands, and clicking a cell reports its coordinates.
 */
final class ShipBoardPane extends GridPane {

    static final int FIRST_ROW = 5, LAST_ROW = 9, FIRST_COL = 4, LAST_COL = 10;

    interface CellHandler {
        void clicked(int row, int col, SpaceshipComponent component);
    }

    private final double cellSize;
    private final CellHandler handler;

    ShipBoardPane(double cellSize, CellHandler handler) {
        this.cellSize = cellSize;
        this.handler = handler;
        setHgap(3);
        setVgap(3);
        setAlignment(Pos.CENTER);
        getStyleClass().add("ship-board");
    }

    /**
     * @param ship  the ship to show
     * @param level the match level, which decides the shape of the board
     */
    void show(ShipBoard ship, MatchLevel level) {
        getChildren().clear();

        for (int col = FIRST_COL; col <= LAST_COL; col++) {
            final Label label = new Label(Integer.toString(col));
            label.getStyleClass().add("board-label");
            GridPane.setHalignment(label, HPos.CENTER);
            add(label, col - FIRST_COL + 1, 0);
        }

        for (int row = FIRST_ROW; row <= LAST_ROW; row++) {
            final Label label = new Label(Integer.toString(row));
            label.getStyleClass().add("board-label");
            add(label, 0, row - FIRST_ROW + 1);

            for (int col = FIRST_COL; col <= LAST_COL; col++) {
                add(cell(ship, level, row, col), col - FIRST_COL + 1, row - FIRST_ROW + 1);
            }
        }
    }

    private StackPane cell(ShipBoard ship, MatchLevel level, int row, int col) {
        final StackPane cell = new StackPane();
        cell.setPrefSize(cellSize, cellSize);
        cell.setMinSize(cellSize, cellSize);
        cell.setMaxSize(cellSize, cellSize);

        final SpaceshipComponent component = ship == null ? null : ship.getComponent(new Coordinates(row, col));

        if (component != null) {
            cell.getStyleClass().add("cell-filled");
            cell.getChildren().add(Images.tileView(component, true, cellSize - 4));
            Tooltip.install(cell, new Tooltip(Text.enumName(component.getType()) + " (" + row + ", " + col + ")"));
        } else if (ShipBoard.isValidCell(level, row, col)) {
            cell.getStyleClass().add("cell-empty");
            Tooltip.install(cell, new Tooltip("(" + row + ", " + col + ")"));
        } else {
            cell.getStyleClass().add("cell-outside");
            return cell;
        }

        cell.setOnMouseClicked(_ -> handler.clicked(row, col, component));
        return cell;
    }
}
