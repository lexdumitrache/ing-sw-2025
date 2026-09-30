package View.GUI;

import View.Client.ClientState;
import View.Client.States.Connected.UnconfirmedLoginState;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * Second screen: choose a username.
 */
final class LoginScreen implements GUI.Screen {

    private final GUI gui;
    private final VBox root = new VBox();
    private final TextField username = new TextField();
    private final Button login = new Button("Log in");
    private final Label status = new Label();

    /** true while waiting for the server to accept the username */
    private boolean pending = false;

    LoginScreen(GUI gui) {
        this.gui = gui;

        final Label title = new Label("Galaxy Trucker");
        title.getStyleClass().add("title");

        username.setPromptText("your name");
        username.setPrefColumnCount(16);

        login.setDefaultButton(true);
        login.setOnAction(_ -> login());

        status.getStyleClass().add("status");

        final HBox row = new HBox(8, username, login);
        row.setAlignment(Pos.CENTER);

        final VBox card = new VBox(14, title, new Label("Choose a name to play with"), row, status);
        card.setAlignment(Pos.CENTER);
        card.getStyleClass().add("panel");
        card.setMaxWidth(460);

        root.getChildren().add(card);
        root.setAlignment(Pos.CENTER);
        root.getStyleClass().add("screen");
    }

    @Override
    public Parent root() {
        return root;
    }

    @Override
    public void update(ClientState state) {
        final boolean waiting = state instanceof UnconfirmedLoginState;
        login.setDisable(waiting);
        username.setDisable(waiting);

        if (waiting) {
            status.setText("Logging in…");
        } else if (pending) {
            // the server sent us back to the login screen: the name was refused
            status.setText("That name is already taken, please choose another one.");
        }
        pending = waiting;
    }

    private void login() {
        final String name = username.getText().trim();
        if (name.isEmpty() || name.contains(" ")) {
            status.setText("The name cannot be empty or contain spaces.");
            return;
        }
        pending = true;
        gui.send("Login", name);
    }
}
