package View.GUI;

import Controller.Enums.MatchLevel;
import View.Client.ClientState;
import View.Client.States.Connected.LoggedIn.GameSelectionState;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.Arrays;

/**
 * Third screen: join an existing game or create a new one.
 */
final class GamesScreen implements GUI.Screen {

    private final GUI gui;
    private final VBox root = new VBox();
    private final Label welcome = new Label();
    private final ListView<Integer> games = new ListView<>();
    private final Button join = new Button("Join");
    private final Button refresh = new Button("Refresh");
    private final ComboBox<MatchLevel> level = new ComboBox<>();
    private final Button create = new Button("Create game");
    private final Label status = new Label();

    GamesScreen(GUI gui) {
        this.gui = gui;

        final Label title = new Label("Galaxy Trucker");
        title.getStyleClass().add("title");

        games.setPlaceholder(new Label("No open games yet: create one!"));
        games.setPrefHeight(220);
        games.setCellFactory(_ -> new ListCell<>() {
            @Override
            protected void updateItem(Integer id, boolean empty) {
                super.updateItem(id, empty);
                setText(empty || id == null ? null : "Game #" + id);
            }
        });
        games.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                join();
            }
        });

        join.setOnAction(_ -> join());
        join.disableProperty().bind(games.getSelectionModel().selectedItemProperty().isNull());
        refresh.setOnAction(_ -> gui.send("UpdateList"));

        level.getItems().setAll(MatchLevel.values());
        level.setValue(MatchLevel.TRIAL);
        level.setCellFactory(_ -> levelCell());
        level.setButtonCell(levelCell());
        create.setOnAction(_ -> {
            status.setText("Creating game…");
            gui.send("Create", level.getValue().name());
        });

        status.getStyleClass().add("status");

        final HBox joinRow = new HBox(8, join, refresh);
        joinRow.setAlignment(Pos.CENTER);
        final HBox createRow = new HBox(8, new Label("New game:"), level, create);
        createRow.setAlignment(Pos.CENTER);

        final VBox card = new VBox(14, title, welcome, new Label("Open games"), games, joinRow, new Separator(), createRow, status);
        card.setAlignment(Pos.CENTER);
        card.getStyleClass().add("panel");
        card.setMaxWidth(460);

        root.getChildren().add(card);
        root.setAlignment(Pos.CENTER);
        root.getStyleClass().add("screen");
    }

    private static ListCell<MatchLevel> levelCell() {
        return new ListCell<>() {
            @Override
            protected void updateItem(MatchLevel item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : switch (item) {
                    case TRIAL -> "Trial flight";
                    case LEVEL2 -> "Level II";
                });
            }
        };
    }

    @Override
    public Parent root() {
        return root;
    }

    @Override
    public void update(ClientState state) {
        welcome.setText("Welcome, " + state.getUsername() + "!");

        if (state instanceof GameSelectionState selection) {
            final Integer selected = games.getSelectionModel().getSelectedItem();
            games.getItems().setAll(Arrays.asList(selection.getGamesList()));
            if (selected != null && games.getItems().contains(selected)) {
                games.getSelectionModel().select(selected);
            }
            create.setDisable(false);
            status.setText(gui.getMessage() == null ? "" : gui.getMessage());
        } else {
            // waiting for the server to confirm the game we joined or created
            create.setDisable(true);
            status.setText("Joining…");
        }
    }

    private void join() {
        final Integer id = games.getSelectionModel().getSelectedItem();
        if (id != null) {
            status.setText("Joining game #" + id + "…");
            gui.send("Join", id.toString());
        }
    }
}
