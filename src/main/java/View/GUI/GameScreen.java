package View.GUI;

import Controller.Enums.ComponentOrigin;
import Controller.Enums.MatchLevel;
import Model.Board.AdventureCards.AdventureCardFilip;
import Model.Board.CardDeck;
import Model.Board.FlightBoard;
import Model.Enums.Direction;
import Model.Game;
import Model.Player;
import Model.Ship.ShipBoard;
import Model.Ship.Components.SpaceshipComponent;
import View.Client.ClientState;
import View.Client.States.Connected.LoggedIn.GameSelected.LobbyState;
import View.Client.States.Connected.LoggedIn.GameSelected.Playing.BuildingState;
import View.Client.States.Connected.LoggedIn.GameSelected.Playing.FlightState;
import View.Client.States.Connected.LoggedIn.GameSelected.Playing.RewardState;
import View.Client.States.Connected.LoggedIn.GameSelectedState;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

import java.util.*;

/**
 * The in-game screen, used from the lobby to the final rewards.
 */
final class GameScreen implements GUI.Screen {

    private static final double CELL = 76;
    private static final double POOL_TILE = 50;
    private static final double HAND_TILE = 110;
    private static final double RESERVED_TILE = 60;

    private final GUI gui;
    private final BorderPane root = new BorderPane();

    private final Label header = new Label();
    private final Label phase = new Label();
    private final Label message = new Label();

    private final ComboBox<String> shipOwner = new ComboBox<>();
    private final ShipBoardPane shipBoard = new ShipBoardPane(CELL, this::cellClicked);
    private final VBox handBox = new VBox(8);
    private final Label hint = new Label();

    private final TabPane tabs = new TabPane();
    private final Tab tilesTab = new Tab("Tiles");
    private final Tab deckTab = new Tab("Card deck");
    private final Tab flightTab = new Tab("Flight board");
    private final Tab cardTab = new Tab("Card");
    private final Tab playersTab = new Tab("Players");

    private final CommandPanel commands;

    private Game game;
    private String me;
    private List<String> available = List.of();
    private Class<?> lastPhase;

    /** Where the tile to place comes from, and how it is rotated. */
    private ComponentOrigin origin = ComponentOrigin.HAND;
    private Direction orientation = Direction.UP;
    private String handTileKey;

    GameScreen(GUI gui) {
        this.gui = gui;
        this.commands = new CommandPanel(gui);

        header.getStyleClass().add("header-title");
        phase.getStyleClass().add("phase");
        message.getStyleClass().add("message");
        message.setWrapText(true);
        message.managedProperty().bind(message.visibleProperty());

        final HBox headerRow = new HBox(16, header, phase);
        headerRow.setAlignment(Pos.CENTER_LEFT);
        final VBox top = new VBox(6, headerRow, message);
        top.getStyleClass().add("top-bar");
        root.setTop(top);

        shipOwner.setOnAction(_ -> {
            if (game != null) {
                showShip();
            }
        });
        final HBox shipTitle = new HBox(8, sectionTitle("Ship of"), shipOwner);
        shipTitle.setAlignment(Pos.CENTER_LEFT);

        hint.getStyleClass().add("hint");
        hint.setWrapText(true);

        final VBox left = new VBox(10, shipTitle, shipBoard, handBox, hint);
        left.setPadding(new Insets(12));
        left.setMinWidth(8 * CELL + 60);

        for (Tab tab : List.of(tilesTab, deckTab, flightTab, cardTab, playersTab)) {
            tab.setClosable(false);
        }
        tabs.setMinWidth(380);

        final HBox center = new HBox(8, left, tabs);
        HBox.setHgrow(tabs, Priority.ALWAYS);
        root.setCenter(center);

        final ScrollPane bottom = new ScrollPane(commands);
        bottom.setFitToWidth(true);
        bottom.setPrefViewportHeight(110);
        root.setBottom(bottom);

        root.getStyleClass().add("screen");
    }

    private static Label sectionTitle(String text) {
        final Label label = new Label(text);
        label.getStyleClass().add("section-title");
        return label;
    }

    @Override
    public Parent root() {
        return root;
    }

    @Override
    public void update(ClientState state) {
        if (!(state instanceof GameSelectedState selected) || selected.getGame() == null) {
            return;
        }
        this.game = selected.getGame();
        this.me = state.getUsername();
        this.available = state.getAvailableCommands();

        header.setText("Galaxy Trucker · " + (game.getLevel() == MatchLevel.TRIAL ? "Trial flight" : "Level II") + " · " + me);
        phase.setText(phaseName(state));

        final String text = gui.getMessage();
        message.setText(text == null ? "" : text);
        message.setVisible(text != null);

        updateShipOwners();
        showShip();
        updateTabs(state);
        commands.update(available);

        // on a phase change, bring the most useful tab to the front
        if (state.getClass() != lastPhase) {
            lastPhase = state.getClass();
            if (state instanceof BuildingState) {
                tabs.getSelectionModel().select(tilesTab);
            } else if (state instanceof FlightState) {
                tabs.getSelectionModel().select(cardTab);
            } else {
                tabs.getSelectionModel().select(playersTab);
            }
        }
    }

    private String phaseName(ClientState state) {
        final String serverPhase = game.getState() == null ? "" : Text.humanize(game.getState().getClass().getSimpleName());
        if (state instanceof LobbyState) {
            return "Waiting in the lobby";
        }
        return serverPhase;
    }

    /* ---------------------------------------------------------------- ship */

    private void updateShipOwners() {
        final List<String> names = game.getPlayers().stream().map(Player::getName).toList();
        final String selected = shipOwner.getValue();
        if (!shipOwner.getItems().equals(names)) {
            shipOwner.getItems().setAll(names);
        }
        shipOwner.setValue(selected != null && names.contains(selected) ? selected : me);
    }

    private boolean viewingOwnShip() {
        return Objects.equals(shipOwner.getValue(), me);
    }

    private void showShip() {
        final Player owner = game.getPlayer(shipOwner.getValue() == null ? me : shipOwner.getValue());
        shipBoard.show(owner == null ? null : owner.getShipBoard(), game.getLevel());
        updateHand();
    }

    private void updateHand() {
        handBox.getChildren().clear();
        hint.setText("");

        final Player player = game.getPlayer(me);
        if (player == null || !viewingOwnShip() || !(available.contains("PlaceComponent") || available.contains("GetComponent"))) {
            return;
        }

        final ShipBoard ship = player.getShipBoard();
        final SpaceshipComponent hand = ship.getActiveComponent();
        final List<SpaceshipComponent> reserved = ship.getReservedComponents();

        // a new tile in hand starts unrotated
        final String key = hand == null ? null : hand.getImagePath() + "@" + System.identityHashCode(hand);
        if (!Objects.equals(key, handTileKey)) {
            handTileKey = key;
            orientation = hand == null ? Direction.UP : hand.getOrientation();
            if (hand != null) {
                origin = ComponentOrigin.HAND;
            }
        }
        if (origin == ComponentOrigin.HAND && hand == null) {
            origin = reserved.isEmpty() ? ComponentOrigin.HAND : ComponentOrigin.FIRST_RESERVED;
        }
        if (origin == ComponentOrigin.SECOND_RESERVED && reserved.size() < 2
                || origin == ComponentOrigin.FIRST_RESERVED && reserved.isEmpty()) {
            origin = ComponentOrigin.HAND;
        }

        final VBox handSlot = slot("In hand", hand, HAND_TILE, ComponentOrigin.HAND);

        final Button left = new Button("⟲");
        final Button right = new Button("⟳");
        left.setTooltip(new Tooltip("Rotate left"));
        right.setTooltip(new Tooltip("Rotate right"));
        left.setOnAction(_ -> rotate(-1));
        right.setOnAction(_ -> rotate(1));
        final boolean canRotate = selectedTile(hand, reserved) != null;
        left.setDisable(!canRotate);
        right.setDisable(!canRotate);

        final Button reserve = new Button("Reserve");
        reserve.setDisable(hand == null || !available.contains("ReserveComponent") || reserved.size() >= 2);
        reserve.setOnAction(_ -> gui.send("ReserveComponent"));

        final VBox handControls = new VBox(6, new HBox(6, left, right), reserve);
        handControls.setAlignment(Pos.CENTER_LEFT);

        final HBox reservedRow = new HBox(6,
                slot("Reserved 1", reserved.isEmpty() ? null : reserved.get(0), RESERVED_TILE, ComponentOrigin.FIRST_RESERVED),
                slot("Reserved 2", reserved.size() < 2 ? null : reserved.get(1), RESERVED_TILE, ComponentOrigin.SECOND_RESERVED));
        reservedRow.setAlignment(Pos.BOTTOM_LEFT);

        final HBox row = new HBox(16, handSlot, handControls, reservedRow);
        row.setAlignment(Pos.CENTER_LEFT);
        handBox.getChildren().add(row);

        if (selectedTile(hand, reserved) != null) {
            hint.setText("Click an empty cell next to your ship to place the selected tile. Use ⟲ ⟳ to rotate it.");
        } else if (available.contains("GetComponent")) {
            hint.setText("Pick a tile from the Tiles tab.");
        }
    }

    private SpaceshipComponent selectedTile(SpaceshipComponent hand, List<SpaceshipComponent> reserved) {
        return switch (origin) {
            case HAND -> hand;
            case FIRST_RESERVED -> reserved.isEmpty() ? null : reserved.get(0);
            case SECOND_RESERVED -> reserved.size() < 2 ? null : reserved.get(1);
        };
    }

    private VBox slot(String title, SpaceshipComponent component, double size, ComponentOrigin slotOrigin) {
        final StackPane tile = new StackPane();
        tile.setPrefSize(size, size);
        tile.setMinSize(size, size);
        tile.setMaxSize(size, size);
        tile.getStyleClass().add("slot");

        if (component != null) {
            final ImageView view = Images.tileView(component, true, size - 6);
            if (slotOrigin == origin) {
                view.setRotate(Images.rotation(orientation));
                tile.getStyleClass().add("slot-selected");
            }
            tile.getChildren().add(view);
            tile.setOnMouseClicked(_ -> {
                origin = slotOrigin;
                orientation = component.getOrientation();
                updateHand();
            });
            Tooltip.install(tile, new Tooltip(Text.enumName(component.getType())));
        }

        final Label label = new Label(title);
        label.getStyleClass().add("slot-label");
        final VBox box = new VBox(4, tile, label);
        box.setAlignment(Pos.CENTER);
        return box;
    }

    private void rotate(int steps) {
        final Direction[] clockwise = {Direction.UP, Direction.RIGHT, Direction.DOWN, Direction.LEFT};
        int index = Arrays.asList(clockwise).indexOf(orientation);
        index = Math.floorMod(index + steps, clockwise.length);
        orientation = clockwise[index];
        updateHand();
    }

    private void cellClicked(int row, int col, SpaceshipComponent component) {
        if (commands.isOpen() && commands.fillCoordinates(row, col)) {
            return;
        }

        final Player player = game.getPlayer(me);
        if (player == null || !viewingOwnShip()) {
            return;
        }

        final ShipBoard ship = player.getShipBoard();
        if (component == null && available.contains("PlaceComponent")
                && selectedTile(ship.getActiveComponent(), ship.getReservedComponents()) != null) {
            gui.send("PlaceComponent", origin.name(), Integer.toString(row), Integer.toString(col), orientation.name());
        }
    }

    /* ---------------------------------------------------------------- tabs */

    private void updateTabs(ClientState state) {
        final List<Tab> visible = new ArrayList<>();

        if (state instanceof BuildingState) {
            tilesTab.setContent(tilePool());
            visible.add(tilesTab);

            final CardDeck deck = game.getFlightBoard().getBookedDecks().get(me);
            if (deck != null) {
                deckTab.setContent(cards(deck.peekCards()));
                visible.add(deckTab);
            }
        }

        if (state instanceof BuildingState || state instanceof FlightState || state instanceof RewardState) {
            flightTab.setContent(flightBoard());
            visible.add(flightTab);
        }

        if (state instanceof FlightState) {
            cardTab.setContent(currentCard());
            visible.add(cardTab);
        }

        playersTab.setText(state instanceof RewardState ? "Final ranking" : "Players");
        playersTab.setContent(players(state instanceof RewardState));
        visible.add(playersTab);

        if (!tabs.getTabs().equals(visible)) {
            final Tab selected = tabs.getSelectionModel().getSelectedItem();
            tabs.getTabs().setAll(visible);
            if (selected != null && visible.contains(selected)) {
                tabs.getSelectionModel().select(selected);
            }
        }
    }

    private Node tilePool() {
        final FlowPane pool = new FlowPane(4, 4);
        pool.setPadding(new Insets(8));
        final SpaceshipComponent[] tiles = game.getTiles();

        for (int i = 0; i < tiles.length; i++) {
            final SpaceshipComponent tile = tiles[i];
            if (tile == null) {
                continue;
            }
            final int index = i;
            final StackPane cell = new StackPane(Images.tileView(tile, tile.isVisible(), POOL_TILE));
            cell.getStyleClass().add("pool-tile");
            Tooltip.install(cell, new Tooltip((tile.isVisible() ? Text.enumName(tile.getType()) : "Face-down tile") + " #" + index));
            cell.setOnMouseClicked(_ -> {
                if (commands.isOpen() && commands.fillIndex(index)) {
                    return;
                }
                if (available.contains("GetComponent")) {
                    gui.send("GetComponent", Integer.toString(index));
                }
            });
            pool.getChildren().add(cell);
        }

        final Label help = new Label("Click a tile to take it. Face-down tiles are revealed when taken.");
        help.getStyleClass().add("hint");
        final VBox box = new VBox(6, help, pool);
        box.setPadding(new Insets(8));
        final ScrollPane scroll = new ScrollPane(box);
        scroll.setFitToWidth(true);
        return scroll;
    }

    private Node cards(List<AdventureCardFilip> cards) {
        final FlowPane pane = new FlowPane(8, 8);
        pane.setPadding(new Insets(10));
        for (AdventureCardFilip card : cards) {
            final Image image = Images.fromModelPath(card.getImagePath());
            if (image != null) {
                final ImageView view = new ImageView(image);
                view.setFitWidth(150);
                view.setPreserveRatio(true);
                pane.getChildren().add(view);
            }
        }
        final ScrollPane scroll = new ScrollPane(pane);
        scroll.setFitToWidth(true);
        return scroll;
    }

    private Node flightBoard() {
        final FlightBoard board = game.getFlightBoard();
        final GridPane table = new GridPane();
        table.setHgap(18);
        table.setVgap(6);
        table.setPadding(new Insets(12));

        final String[] headers = {"Order", "Player", "Position", "Distance", "Credits"};
        for (int i = 0; i < headers.length; i++) {
            final Label label = new Label(headers[i]);
            label.getStyleClass().add("table-header");
            table.add(label, i, 0);
        }

        final Player[] order = board.getTurnOrder();
        final Player inTurnPlayer = game.getState() == null ? null : game.getState().getPlayerInTurn();
        final String inTurn = inTurnPlayer == null ? null : inTurnPlayer.getName();
        for (int i = 0; i < order.length; i++) {
            final Player player = order[i];
            final boolean playing = player.getName().equals(inTurn);
            final Label name = new Label((playing ? "▶ " : "") + player.getName() + (player.getName().equals(me) ? " (you)" : ""));
            if (playing) {
                name.getStyleClass().add("in-turn");
            }
            table.add(new Label(Integer.toString(i + 1)), 0, i + 1);
            table.add(name, 1, i + 1);
            table.add(new Label(Integer.toString(board.getPosition(player))), 2, i + 1);
            table.add(new Label(Integer.toString(board.getTotalDistance(player))), 3, i + 1);
            table.add(new Label(Integer.toString(player.getCredits())), 4, i + 1);
        }

        final Label info = new Label(order.length == 0
                ? "Nobody is on the flight board yet. Players are placed when they finish building."
                : "The board has " + board.getCellNumber() + " cells.");
        info.getStyleClass().add("hint");

        final VBox box = new VBox(8, table, info);
        box.setPadding(new Insets(8));
        return box;
    }

    private Node currentCard() {
        final TextArea text = new TextArea();
        text.setEditable(false);
        text.getStyleClass().add("mono");
        final List<String> lines = game.renderCard();
        text.setText(lines == null || lines.isEmpty()
                ? "No card is being resolved. The leader draws the next card."
                : Text.stripAnsi(String.join("\n", lines)));
        return text;
    }

    private Node players(boolean ranking) {
        final List<Player> players = new ArrayList<>(game.getPlayers());
        if (ranking) {
            players.sort(Comparator.comparingInt(Player::getCredits).reversed());
        }

        final GridPane table = new GridPane();
        table.setHgap(18);
        table.setVgap(6);
        table.setPadding(new Insets(12));
        final String[] headers = ranking ? new String[]{"Rank", "Player", "Credits"} : new String[]{"", "Player", "Credits"};
        for (int i = 0; i < headers.length; i++) {
            final Label label = new Label(headers[i]);
            label.getStyleClass().add("table-header");
            table.add(label, i, 0);
        }
        for (int i = 0; i < players.size(); i++) {
            final Player player = players.get(i);
            table.add(new Label(ranking ? Integer.toString(i + 1) : "•"), 0, i + 1);
            table.add(new Label(player.getName() + (player.getName().equals(me) ? " (you)" : "")), 1, i + 1);
            table.add(new Label(Integer.toString(player.getCredits())), 2, i + 1);
        }
        return table;
    }
}
