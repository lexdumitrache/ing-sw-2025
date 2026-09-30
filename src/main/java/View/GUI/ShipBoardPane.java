package View.GUI;

import Controller.Enums.MatchLevel;
import Model.Ship.Coordinates;
import Model.Ship.ShipBoard;
import Model.Ship.Components.SpaceshipComponent;
import Model.Enums.Crewmates;
import Model.Enums.Good;
import Model.Ship.Components.BatteryCompartment;
import Model.Ship.Components.Cabin;
import Model.Ship.Components.CargoHold;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

/**
 * Draws a ship on the real ship board artwork, with the tile pictures on it. Rows 5-9 and columns 4-10
 * use the same numbering as the game commands, and clicking a cell reports its coordinates.
 */
final class ShipBoardPane extends Pane {

    static final int FIRST_ROW = 5, LAST_ROW = 9, FIRST_COL = 4, LAST_COL = 10;

    /** Geometry of pics/cardboard/cardboard-1.jpg and -1b.jpg (937 x 679): cell (5, 4) starts at (37, 31). */
    private static final double ART_ORIGIN_X = 37, ART_ORIGIN_Y = 31, ART_PITCH = 124, ART_CELL = 120;

    interface CellHandler {
        void clicked(int row, int col, SpaceshipComponent component, Node cell);
    }

    private final double cellSize;
    private final CellHandler handler;

    /**
     * @param cellSize size of one cell on screen, in pixels
     */
    ShipBoardPane(double cellSize, CellHandler handler) {
        this.cellSize = cellSize;
        this.handler = handler;
        getStyleClass().add("ship-board");
    }

    /**
     * @param ship  the ship to show
     * @param level the match level, which decides the board picture and shape
     */
    void show(ShipBoard ship, MatchLevel level) {
        getChildren().clear();

        final double scale = cellSize / ART_CELL;
        final Image art = Images.load(level == MatchLevel.TRIAL ? "pics/cardboard/cardboard-1.jpg" : "pics/cardboard/cardboard-1b.jpg");
        if (art != null) {
            final ImageView background = new ImageView(art);
            background.setFitWidth(art.getWidth() * scale);
            background.setPreserveRatio(true);
            background.setSmooth(true);
            getChildren().add(background);
            setPrefSize(art.getWidth() * scale, art.getHeight() * scale);
            setMinSize(art.getWidth() * scale, art.getHeight() * scale);
        }

        for (int row = FIRST_ROW; row <= LAST_ROW; row++) {
            for (int col = FIRST_COL; col <= LAST_COL; col++) {
                final StackPane cell = cell(ship, level, row, col);
                if (cell != null) {
                    cell.relocate((ART_ORIGIN_X + (col - FIRST_COL) * ART_PITCH) * scale,
                            (ART_ORIGIN_Y + (row - FIRST_ROW) * ART_PITCH) * scale);
                    getChildren().add(cell);
                }
            }
        }
    }

    /**
     * @return the cell at (row, col), or null if it is outside the ship
     */
    private StackPane cell(ShipBoard ship, MatchLevel level, int row, int col) {
        final SpaceshipComponent component = ship == null ? null : ship.getComponent(new Coordinates(row, col));
        if (component == null && !ShipBoard.isValidCell(level, row, col)) {
            return null;
        }

        final StackPane cell = new StackPane();
        cell.setPrefSize(cellSize, cellSize);
        cell.setMinSize(cellSize, cellSize);
        cell.setMaxSize(cellSize, cellSize);

        if (component != null) {
            cell.getStyleClass().add("cell-filled");
            cell.getChildren().add(Images.tileView(component, true, cellSize));
            final Node badge = badge(component);
            if (badge != null) {
                StackPane.setAlignment(badge, Pos.BOTTOM_CENTER);
                cell.getChildren().add(badge);
            }
            Tooltip.install(cell, new Tooltip(Text.enumName(component.getType()) + " (" + row + ", " + col + ")"));
        } else {
            cell.getStyleClass().add("cell-empty");
            Tooltip.install(cell, new Tooltip("(" + row + ", " + col + ")"));
        }

        cell.setOnMouseClicked(_ -> handler.clicked(row, col, component, cell));
        return cell;
    }

    /**
     * @return a small overlay showing what the component holds (batteries, crew, goods), or null
     */
    private static Node badge(SpaceshipComponent component) {
        if (component instanceof BatteryCompartment battery) {
            return chip("⚡ " + battery.getBatteries(), "badge-battery");
        }
        if (component instanceof Cabin cabin) {
            final Crewmates crew = cabin.getOccupants();
            if (crew == null || crew == Crewmates.EMPTY) {
                return null;
            }
            return switch (crew) {
                case DOUBLE_HUMAN -> chip("crew 2", "badge-crew");
                case SINGLE_HUMAN -> chip("crew 1", "badge-crew");
                case PURPLE_ALIEN -> chip("alien", "badge-purple");
                case BROWN_ALIEN -> chip("alien", "badge-brown");
                case EMPTY -> null;
            };
        }
        if (component instanceof CargoHold hold) {
            final HBox goods = new HBox(2);
            goods.setAlignment(Pos.CENTER);
            goods.getStyleClass().add("badge");
            goods.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
            for (Good good : hold.getGoods()) {
                final Rectangle square = new Rectangle(10, 10, good == null ? Color.TRANSPARENT : goodColor(good));
                square.setStroke(good == null ? Color.web("#8a93c0") : Color.BLACK);
                goods.getChildren().add(square);
            }
            return goods;
        }
        return null;
    }

    private static Label chip(String text, String style) {
        final Label label = new Label(text);
        label.getStyleClass().addAll("badge", style);
        return label;
    }

    static Color goodColor(Good good) {
        return switch (good) {
            case RED -> Color.web("#e8453c");
            case YELLOW -> Color.web("#f2c230");
            case GREEN -> Color.web("#3fbf5f");
            case BLUE -> Color.web("#3b8cf0");
        };
    }
}
