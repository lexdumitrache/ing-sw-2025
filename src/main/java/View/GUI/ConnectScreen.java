package View.GUI;

import View.Client.Client;
import View.Client.ClientState;
import View.Client.States.ConnectingState;
import View.Client.States.ProtocolChoiceState;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * First screen: choose RMI or TCP and connect to a server.
 */
final class ConnectScreen implements GUI.Screen {

    private static final String DEFAULT_RMI_PORT = "1099";
    private static final String DEFAULT_TCP_PORT = "1234";

    private final GUI gui;
    private final VBox root = new VBox(14);
    private final ToggleGroup protocol = new ToggleGroup();
    private final RadioButton tcp = new RadioButton("TCP (socket)");
    private final RadioButton rmi = new RadioButton("RMI");
    private final TextField host = new TextField("localhost");
    private final TextField port = new TextField(DEFAULT_TCP_PORT);
    private final Button connect = new Button("Connect");
    private final Label status = new Label();

    ConnectScreen(GUI gui) {
        this.gui = gui;

        final Label title = new Label("Galaxy Trucker");
        title.getStyleClass().add("title");

        tcp.setToggleGroup(protocol);
        rmi.setToggleGroup(protocol);
        tcp.setSelected(true);
        protocol.selectedToggleProperty().addListener((_, _, selected) -> {
            final String other = selected == rmi ? DEFAULT_TCP_PORT : DEFAULT_RMI_PORT;
            if (port.getText().isBlank() || port.getText().equals(other)) {
                port.setText(selected == rmi ? DEFAULT_RMI_PORT : DEFAULT_TCP_PORT);
            }
        });

        host.setPromptText("server address");
        port.setPromptText("port");
        port.setPrefColumnCount(6);

        connect.setDefaultButton(true);
        connect.setOnAction(_ -> connect());

        status.getStyleClass().add("status");

        final HBox protocols = new HBox(16, tcp, rmi);
        protocols.setAlignment(Pos.CENTER);
        final HBox address = new HBox(8, new Label("Server"), host, new Label("Port"), port);
        address.setAlignment(Pos.CENTER);

        final VBox card = new VBox(14, title, new Label("Connect to a game server"), protocols, address, connect, status);
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
        // once a protocol has been chosen (e.g. with --useTCP) it cannot be changed
        final boolean canChoose = state instanceof ProtocolChoiceState;
        tcp.setDisable(!canChoose);
        rmi.setDisable(!canChoose);
        connect.setDisable(false);
    }

    private void connect() {
        final String hostname = host.getText().trim();
        final String portText = port.getText().trim();
        if (hostname.isEmpty() || portText.isEmpty()) {
            status.setText("Please fill in the server address and port.");
            return;
        }

        connect.setDisable(true);
        status.setText("Connecting to " + hostname + ":" + portText + "…");

        final boolean chooseProtocol = Client.client.getState() instanceof ProtocolChoiceState;
        final String chosen = rmi.isSelected() ? "RMI" : "TCP";

        gui.submit(() -> {
            if (chooseProtocol) {
                Client.client.createAction(chosen, new String[0]);
            }
            Client.client.createAction("Connect", new String[]{hostname, portText});

            final boolean failed = Client.client.getState() instanceof ConnectingState;
            javafx.application.Platform.runLater(() -> {
                connect.setDisable(false);
                status.setText(failed ? "Could not connect to " + hostname + ":" + portText + ". Is the server running?" : "");
            });
        });
    }
}
