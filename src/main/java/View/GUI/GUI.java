package View.GUI;

import View.Client.Client;
import View.Client.ClientState;
import View.Client.States.ConnectingState;
import View.Client.States.Connected.LoggedIn.GameSelectedState;
import View.Client.States.Connected.LoggedIn.GameSelectionState;
import View.Client.States.Connected.LoggedIn.UnconfirmedSelectionState;
import View.Client.States.Connected.LoginState;
import View.Client.States.Connected.UnconfirmedLoginState;
import View.Client.States.ProtocolChoiceState;
import View.View;
import View.Client.States.ConnectedState;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * JavaFX implementation of the client View.
 * It reads the current ClientState to decide which screen to show, and drives the game by
 * creating the same named actions the TUI uses (e.g. "Login", "PlaceComponent").
 */
public final class GUI extends View {

    private static GUI instance;

    private final CountDownLatch started = new CountDownLatch(1);

    /** Actions run off the JavaFX thread, one at a time and in order, so the window never freezes on network calls. */
    private final ExecutorService actions = Executors.newSingleThreadExecutor(runnable -> {
        final Thread thread = new Thread(runnable, "GUI-actions");
        thread.setDaemon(true);
        return thread;
    });

    private Stage stage;
    private String message;

    /** Screens contain JavaFX controls, so they are created once the JavaFX toolkit has started. */
    private ConnectScreen connectScreen;
    private LoginScreen loginScreen;
    private GamesScreen gamesScreen;
    private GameScreen gameScreen;

    public GUI() {
        instance = this;
    }

    /**
     * The JavaFX entry point. It is kept separate from Client so the shaded jar can start JavaFX
     * without the main class extending Application.
     */
    public static final class App extends Application {
        @Override
        public void start(Stage stage) {
            instance.onStart(stage);
        }
    }

    @Override
    public void run() {
        final Thread launcher = new Thread(() -> Application.launch(App.class), "JavaFX-launcher");
        launcher.start();
        try {
            started.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void onStart(Stage stage) {
        this.connectScreen = new ConnectScreen(this);
        this.loginScreen = new LoginScreen(this);
        this.gamesScreen = new GamesScreen(this);
        this.gameScreen = new GameScreen(this);

        this.stage = stage;
        stage.setTitle("Galaxy Trucker");
        stage.setMinWidth(1100);
        stage.setMinHeight(720);
        stage.setOnCloseRequest(_ -> {
            try {
                Client.client.createAction("Stop", new String[0]);
            } finally {
                Platform.exit();
                System.exit(0);
            }
        });

        final Scene scene = new Scene(connectScreen.root(), 1280, 820);
        final var css = GUI.class.getClassLoader().getResource("gui/style.css");
        if (css != null) {
            scene.getStylesheets().add(css.toExternalForm());
        }
        stage.setScene(scene);
        stage.show();

        // once a second: notice a lost connection, and keep the hourglass countdown moving
        final Timeline clock = new Timeline(new KeyFrame(Duration.seconds(1), _ -> tick()));
        clock.setCycleCount(Animation.INDEFINITE);
        clock.play();

        started.countDown();
    }

    private void tick() {
        final ClientState state = Client.client.getState();
        final boolean lost = state.isDone()
                || (state instanceof ConnectedState connected && connected.getNetwork().isDone());
        if (lost) {
            Client.client.restart();
            log("The connection to the server was lost. Connect again and log in with the same name to get back into your game.");
            refresh();
            return;
        }
        if (stage.getScene().getRoot() == gameScreen.root()) {
            gameScreen.tick();
        }
    }

    /**
     * Runs a named client action (the same names the TUI accepts) with its arguments, in order.
     */
    void send(String action, String... args) {
        actions.submit(() -> {
            try {
                Client.client.createAction(action, args);
            } catch (RuntimeException e) {
                log(e.getMessage());
                repaint();
            }
        });
    }

    /**
     * Runs arbitrary work on the action thread (used to chain actions that depend on each other).
     */
    void submit(Runnable work) {
        actions.submit(work);
    }

    /**
     * @return the last message (error or information) to show to the player, or null
     */
    /**
     * @return true if the message shown is an information notice rather than an error
     */
    boolean isNotice() {
        return message == null && notice != null && System.currentTimeMillis() < noticeUntil;
    }

    String getMessage() {
        if (message == null && notice != null && System.currentTimeMillis() < noticeUntil) {
            return notice;
        }
        return message;
    }

    @Override
    public void repaint() {
        if (stage != null) {
            Platform.runLater(this::refresh);
        }
    }

    /** An information message shown for a few seconds, even if game updates clear the error messages. */
    private String notice;
    private long noticeUntil;

    @Override
    public void notify(String message) {
        this.notice = message;
        this.noticeUntil = System.currentTimeMillis() + 6000;
    }

    @Override
    public void log(String message) {
        this.message = (message == null || message.isBlank()) ? null : Text.stripAnsi(message).trim();
    }

    @Override
    public void showOptions(String prompt, List<String> options) {
        // the GUI shows options with its own controls
    }

    @Override
    public void showArguments(List<String> arguments, Map<String, String> providedArguments) {
        // the GUI shows arguments with its own controls
    }

    private void refresh() {
        final ClientState state = Client.client.getState();
        final Screen screen;

        if (state instanceof ProtocolChoiceState || state instanceof ConnectingState) {
            screen = connectScreen;
        } else if (state instanceof LoginState || state instanceof UnconfirmedLoginState) {
            screen = loginScreen;
        } else if (state instanceof GameSelectionState || state instanceof UnconfirmedSelectionState) {
            screen = gamesScreen;
        } else if (state instanceof GameSelectedState) {
            screen = gameScreen;
        } else {
            screen = connectScreen;
        }

        final Parent root = screen.root();
        if (stage.getScene().getRoot() != root) {
            stage.getScene().setRoot(root);
        }
        screen.update(state);
    }

    /**
     * A screen of the GUI: built once, then updated every time the client state changes.
     */
    interface Screen {
        Parent root();

        void update(ClientState state);
    }
}
