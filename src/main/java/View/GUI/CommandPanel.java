package View.GUI;

import Controller.Enums.ComponentOrigin;
import Controller.Enums.MatchLevel;
import Model.Enums.Direction;
import View.Client.Actions.ActionConstructor;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.*;

/**
 * Shows one button per command the server currently allows. Commands with arguments open a small
 * form; clicking the ship board or the tile pool fills coordinates and indexes into it.
 */
final class CommandPanel extends VBox {

    /** Commands the GUI shows in other ways, or that only make sense in the TUI. */
    private static final Set<String> HIDDEN = Set.of("Stop", "UpdateList", "BackToShip");

    /** Arguments whose values come from a fixed set. */
    private static final Map<String, List<String>> CHOICES = Map.of(
            "origin", names(ComponentOrigin.values()),
            "orientation", names(Direction.values()),
            "MatchLevel", names(MatchLevel.values()));

    private final GUI gui;
    private final FlowPane buttons = new FlowPane(6, 6);
    private final HBox form = new HBox(8);

    private String openCommand;
    private final Map<String, Control> fields = new LinkedHashMap<>();

    CommandPanel(GUI gui) {
        this.gui = gui;
        setSpacing(8);
        getStyleClass().add("command-panel");
        form.setAlignment(Pos.CENTER_LEFT);
        form.getStyleClass().add("command-form");
        final Label title = new Label("Actions");
        title.getStyleClass().add("section-title");
        getChildren().addAll(title, buttons);
    }

    private static List<String> names(Enum<?>[] values) {
        return Arrays.stream(values).map(Enum::name).toList();
    }

    /**
     * @param commands the commands currently available to the player
     */
    void update(List<String> commands) {
        buttons.getChildren().clear();

        for (String command : commands) {
            if (HIDDEN.contains(command) || command.startsWith("View")) {
                continue;
            }
            final Button button = new Button(Text.humanize(command));
            button.setOnAction(_ -> open(command));
            if (command.equals(openCommand)) {
                button.getStyleClass().add("selected");
            }
            buttons.getChildren().add(button);
        }

        if (openCommand != null && !commands.contains(openCommand)) {
            close();
        }
    }

    private void open(String command) {
        final ActionConstructor constructor = ActionConstructor.getActionConstructor(command);
        final List<String> arguments = constructor == null ? null : constructor.getArguments();

        if (arguments == null || arguments.isEmpty()) {
            close();
            gui.send(command);
            return;
        }

        openCommand = command;
        fields.clear();
        form.getChildren().clear();
        form.getChildren().add(new Label(Text.humanize(command) + ":"));

        for (String argument : arguments) {
            final Control control;
            if (CHOICES.containsKey(argument)) {
                final ComboBox<String> box = new ComboBox<>();
                box.getItems().setAll(CHOICES.get(argument));
                box.getSelectionModel().selectFirst();
                control = box;
            } else {
                final TextField field = new TextField();
                field.setPrefColumnCount(5);
                field.setOnAction(_ -> submit());
                control = field;
            }
            fields.put(argument, control);
            form.getChildren().addAll(new Label(Text.humanize(argument).toLowerCase()), control);
        }

        final Button send = new Button("Send");
        send.setDefaultButton(true);
        send.setOnAction(_ -> submit());
        final Button cancel = new Button("Cancel");
        cancel.setOnAction(_ -> close());
        form.getChildren().addAll(send, cancel);

        if (!getChildren().contains(form)) {
            getChildren().add(form);
        }
        for (var node : buttons.getChildren()) {
            node.getStyleClass().remove("selected");
            if (((Button) node).getText().equals(Text.humanize(command))) {
                node.getStyleClass().add("selected");
            }
        }
    }

    private void close() {
        openCommand = null;
        fields.clear();
        form.getChildren().clear();
        getChildren().remove(form);
        buttons.getChildren().forEach(node -> node.getStyleClass().remove("selected"));
    }

    private void submit() {
        if (openCommand == null) {
            return;
        }
        final List<String> values = new ArrayList<>();
        for (Map.Entry<String, Control> entry : fields.entrySet()) {
            final String value = value(entry.getValue());
            if (value == null || value.isBlank()) {
                entry.getValue().requestFocus();
                return;
            }
            values.add(value.trim());
        }
        final String command = openCommand;
        close();
        gui.send(command, values.toArray(new String[0]));
    }

    @SuppressWarnings("unchecked")
    private static String value(Control control) {
        if (control instanceof ComboBox<?> box) {
            return ((ComboBox<String>) box).getValue();
        }
        return ((TextField) control).getText();
    }

    /**
     * @return true if a command form is open
     */
    boolean isOpen() {
        return openCommand != null;
    }

    /**
     * Puts a clicked cell into the open form: row/column, or for moves the first empty pair
     * (oldRow/oldColumn, then newRow/newColumn).
     *
     * @return true if the coordinates were used
     */
    boolean fillCoordinates(int row, int col) {
        for (String prefix : List.of("", "old", "new")) {
            final String rowName = prefix.isEmpty() ? "row" : prefix + "Row";
            final String colName = prefix.isEmpty() ? "column" : prefix + "Column";
            if (fields.containsKey(rowName) && fields.containsKey(colName) && isEmpty(rowName)) {
                fill(rowName, Integer.toString(row));
                fill(colName, Integer.toString(col));
                return true;
            }
        }
        // every pair is filled: overwrite the last one
        for (String prefix : List.of("new", "")) {
            final String rowName = prefix.isEmpty() ? "row" : prefix + "Row";
            final String colName = prefix.isEmpty() ? "column" : prefix + "Column";
            if (fields.containsKey(rowName) && fields.containsKey(colName)) {
                fill(rowName, Integer.toString(row));
                fill(colName, Integer.toString(col));
                return true;
            }
        }
        return false;
    }

    /**
     * Puts a clicked tile index into the open form.
     *
     * @return true if the form had an index field
     */
    boolean fillIndex(int index) {
        if (fields.containsKey("index")) {
            fill("index", Integer.toString(index));
            return true;
        }
        return false;
    }

    private boolean isEmpty(String argument) {
        final String value = value(fields.get(argument));
        return value == null || value.isBlank();
    }

    @SuppressWarnings("unchecked")
    private void fill(String argument, String value) {
        final Control control = fields.get(argument);
        if (control instanceof ComboBox<?> box) {
            ((ComboBox<String>) box).setValue(value);
        } else if (control instanceof TextField field) {
            field.setText(value);
        }
    }
}
